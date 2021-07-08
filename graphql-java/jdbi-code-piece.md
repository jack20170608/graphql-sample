## sample code to batch execution 

```java
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Batch;

/**
 * Maintenance operations for the H2 database
 */
class H2DatabaseMaintenance implements DatabaseMaintenance {

    private final Handle handle;

    H2DatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    @Override
    public void dropTablesAndSequences() {
        // NOOP
    }

    @Override
    public void sweepData() {
        handle.useTransaction(h -> {
            Batch batch = h.createBatch();
            batch.add("set referential_integrity false");

            ResultIterable<String> tables = h.createQuery("show tables")
                    .mapTo(String.class);
            for (String table : tables) {
                batch.add(String.format("truncate table \"%s\"", table));
            }

            ResultIterable<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences")
                    .mapTo(String.class);
            for (String sequenceName : sequenceNames) {
                batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
            }
            batch.add("set referential_integrity true");
            batch.execute();
        });
    }
}

```

### BaseDao in Jdbi 

```java
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.result.ResultIterator;
import uk.ac.susx.tag.method51.core.data.store2.query.DatumQuery;
import uk.ac.susx.tag.method51.core.data.store2.query.SqlFragment;
import uk.ac.susx.tag.method51.core.data.store2.query.SqlQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BaseDAO<T, Q extends SqlQuery> implements DAO<T,Q> {

    private final Jdbi jdbi;
    private final RowMapper<T> mapper;

    public BaseDAO(Jdbi jdbi, RowMapper<T> mapper) {
        this.jdbi = jdbi;
        this.mapper = mapper;
    }

    public ResultIterator<T> iterator(Q query) {
        return jdbi.withHandle(handle -> handle.createQuery(query.sql())
                .map(mapper)
                .iterator()
        );
    }

    @Override
    public Stream<T> stream(Q query){
        return jdbi.withHandle(handle -> handle.createQuery(query.sql())
                .map(mapper)
                .stream()
        );
    }

    @Override
    public int update(Q query){

        String sql = query.sql();

        return jdbi.withHandle( handle -> handle.createUpdate(sql).execute() );
    }

    @Override
    public List<T> list(Q query)  {
        Stream<T> stream = stream(query);
        List<T> list = stream.collect(Collectors.toList());
        stream.close();
        return list;
    }
}

```

### PostgresDatabaseMaintenance
```java
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Batch;

/**
 * Maintenance operations for the PostgreSQL database
 */
class PostgresDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    PostgresDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        handle.useTransaction(h -> {
            Batch batch = h.createBatch();
            ResultIterable<String> tableForeignKeys = h.createQuery(
                    "select 'alter table \"' || relname || '\" drop constraint \"'|| conname ||'\"' " +
                    "from pg_constraint " +
                    "inner join pg_class on conrelid=pg_class.oid " +
                    "inner join pg_namespace on pg_namespace.oid=pg_class.relnamespace " +
                    "where pg_constraint.contype = 'f' " +
                    "order by nspname, relname, conname")
                    .mapTo(String.class);
            for (String alterTable : tableForeignKeys) {
                batch.add(alterTable);
            }
            ResultIterable<String> tableNames = h.createQuery("select tablename from pg_tables " +
                    "where tableowner = (select current_user) " +
                    "and schemaname = 'public'")
                    .mapTo(String.class);
            for (String tableName : tableNames) {
                batch.add(String.format("delete from \"%s\"", tableName));
            }
            ResultIterable<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences " +
                    "where sequence_schema='public' " +
                    "and sequence_catalog = (select current_catalog)")
                    .mapTo(String.class);
            for (String sequenceName : sequenceNames) {
                batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
            }
            batch.execute();
        });
    }

    public void dropTablesAndSequences() {
        handle.useTransaction(h -> {
            String currentUser = h.createQuery("select current_user")
                    .mapTo(String.class)
                    .findOnly();
            h.execute(String.format("drop owned by \"%s\"", currentUser));
        });
    }
}

```