package com.jack.graphql.dao.impl;

import com.google.common.collect.Lists;
import com.jack.graphql.application.PostgresDataSourceFactory;
import com.jack.graphql.cache.Cache;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import com.jack.graphql.domain.OrderBuilder;
import com.jack.graphql.domain.Status;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jack.graphql.utils.LocalDateUtils.toLocalDateTime;
import static com.jack.graphql.utils.StringConvertUtils.toEnum;

public class OrderDaoImpl implements OrderDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderDaoImpl.class);

    private static final String INSERT_SQL = " insert into t_order (" +
        " sequence_no" +
        ", customer_id" +
        ", product_id" +
        ", order_dt" +
        ", status" +
        ", create_dt" +
        ", last_update_dt" +
        ", raw_string " +
        " ) values ( " +
        " :o.sequenceNo " +
        ", :o.customerId " +
        ", :o.productId " +
        ", :o.orderDatetime " +
        ", :o.status" +
        ", :o.createDt" +
        ", :o.lastUpdateDt" +
        ", :o.rawString" +
        " ) ";

    private static final String UPDATE_SQL = " update t_order set " +
        " sequence_no    = :o.sequenceNo" +
        ", customer_id   = :o.customerId" +
        ", product_id    = :o.productId " +
        ", order_dt      = :o.orderDatetime" +
        ", status        = :o.status" +
        ", create_dt     = :o.createDt" +
        ", last_update_dt= :o.lastUpdateDt" +
        ", raw_string    = :o.rawString" +
        " where 1 =1 " +
        " and id = :o.id";

    private static final String GET_ALL = "" +
        " select id " +
        ", sequence_no" +
        ", customer_id" +
        ", product_id" +
        ", order_dt" +
        ", status" +
        ", create_dt" +
        ", last_update_dt" +
        ", raw_string" +
        " from t_order " +
        " where 1 = 1 ";

    private static final String GET_MIN_ID = "" +
        " select min(id) maxId" +
        " from t_order " +
        " where 1 = 1 ";

    private static final String GET_MAX_ID = "" +
        " select max(id) maxId" +
        " from t_order " +
        " where 1 = 1 ";

    private final Jdbi jdbi;

    public static final List<Long> CUSTOMER_TABLE = Lists.newArrayList(1L, 2L, 3L, 4L);
    public static final List<Long> PRODUCT_TABLE = Lists.newArrayList(1L, 2L, 3L, 5L, 6L, 7L, 8L, 9L, 10L);



    public OrderDaoImpl(Jdbi jdbi) {
        this.jdbi = jdbi;

        this.jdbi.registerRowMapper(Order.class, (rs, ctx) -> OrderBuilder.anOrder()
            .withId(rs.getLong("id"))
            .withSequenceNo(rs.getString("sequence_no"))
            .withCustomerId(rs.getLong("customer_id"))
            .withProductId(rs.getLong("product_id"))
            .withOrderDatetime(toLocalDateTime(rs.getTimestamp("order_dt")))
            .withOrderStatus(toEnum(Status.class, rs.getString("status")))
            .withCreateDt(toLocalDateTime(rs.getTimestamp("create_dt")))
            .withLastUpdateDt(toLocalDateTime(rs.getTimestamp("last_update_dt")))
            .withRawString(rs.getString("raw_string").split("\\|"))
            .build());
    }

    @Override
    public Long insert(Order order) {
        return jdbi.withHandle(handle -> handle.createUpdate(INSERT_SQL)
            .bindBean("o", order)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Long.class)
            .one());
    }

    @Override
    public void update(Order order) {
        jdbi.useHandle(handle -> handle.createUpdate(UPDATE_SQL)
            .bindBean("o", order)
            .execute());
    }

    @Override
    public Optional<Order> getById(Long id) {
        String sql = GET_ALL
            + " AND id = :id  ";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
            .bind("id", id)
            .mapTo(Order.class)
            .findOne());
    }

    @Override
    public Collection<Order> getAll() {
        return jdbi.withHandle(handle -> handle.createQuery(GET_ALL)
            .mapTo(Order.class)
            .list());
    }

    @Override
    public Collection<Order> getByIdRange(Long minId, Long maxId) {
        String sql = GET_ALL
            + " and id >= :minId"
            + " and id < :maxId ";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
            .bind("minId", minId)
            .bind("maxId", maxId)
            .mapTo(Order.class)
            .list());
    }

    //Not work
    @Override
    public int loadingDataToCacheStream(Cache<Long, Order> cache){
        AtomicInteger counter = new AtomicInteger(0);
        String sql = GET_ALL + " and id > :id ";
        jdbi.useHandle(handle -> {
            handle.createQuery(sql)
                .bind("id", 100)
                .mapTo(Order.class)
                .useStream(stream -> {
                    stream.forEach(order -> {
                        counter.incrementAndGet();
                        cache.put(order.getId(), order);
                        if (counter.get() % 1000 == 0){
                            LOGGER.info("Done {}.", counter.get());
                        }
                    });
                });
        });
        LOGGER.info("Total size {}.", counter.get());
        return counter.get();
    }


    @Override
    public int loadingDataToCache(Cache<Long, Order> cache) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String sql = GET_ALL + " and id > ? ";
        int totalSize = 0;
        try {
            conn = PostgresDataSourceFactory.getInstance().getHikariDataSource().getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(1000);
            preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
            preparedStatement.setLong(1, 0);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Order order = OrderBuilder.anOrder()
                    .withId(rs.getLong("id"))
                    .withSequenceNo(rs.getString("sequence_no"))
                    .withCustomerId(rs.getLong("customer_id"))
                    .withProductId(rs.getLong("product_id"))
                    .withOrderDatetime(toLocalDateTime(rs.getTimestamp("order_dt")))
                    .withOrderStatus(toEnum(Status.class, rs.getString("status")))
                    .withCreateDt(toLocalDateTime(rs.getTimestamp("create_dt")))
                    .withLastUpdateDt(toLocalDateTime(rs.getTimestamp("last_update_dt")))
                    .withRawString(rs.getString("raw_string").split("\\|"))
                    .build();
                cache.put(order.getId(), order);
                if (++totalSize % 1000 == 0) {
                    LOGGER.info("Done {}.", totalSize);
                }
            }
            LOGGER.info("Done all {}.", totalSize);
        } catch (SQLException e) {
            LOGGER.error("Loading error, ", e);
            throw new RuntimeException("Data loading failure...", e);
        } finally {
            try {
                if (rs != null){
                    rs.close();
                }
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (null != conn) {
                    conn.close();
                }
            }catch (SQLException ignore) {}
        }
        return totalSize;
    }

    @Override
    public Optional<Long> getMinId() {
        return jdbi.withHandle(handle -> handle.createQuery(GET_MIN_ID)
            .mapTo(Long.class)
            .findOne());
    }

    @Override
    public Optional<Long> getMaxId() {
        return jdbi.withHandle(handle -> handle.createQuery(GET_MAX_ID)
            .mapTo(Long.class)
            .findOne());
    }

    @Override
    public void batchInsert(List<Order> orderList) {
        jdbi.useHandle(handle -> {
            PreparedBatch insertBatch = handle.prepareBatch(INSERT_SQL);
            orderList.forEach(order -> insertBatch.bindBean("o", order).add());
            insertBatch.execute();
        });
    }


}
