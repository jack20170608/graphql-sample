package com.jack.graphql.application;

import com.jack.graphql.App;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PostgresDataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDataSourceFactory.class);

    private static PostgresDataSourceFactory instance;

    private HikariDataSource hikariDataSource;

    private Jdbi jdbi;

    private PostgresDataSourceFactory() {}

    public static synchronized PostgresDataSourceFactory getInstance(){
        if (null == instance){
            instance = new PostgresDataSourceFactory();
            instance.init(App.APP_CONTEXT);
        }
        return instance;
    }

    private void init(AppContext appContext){
        LOGGER.info("Initial database factory");
        String driverClass = appContext.getConfig().getString("db.driver-class");
        String url = appContext.getConfig().getString("db.url");
        String user = appContext.getConfig().getString("db.user");
        String password = appContext.getConfig().getString("db.password");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClass);
        config.setUsername(user);
        config.setPassword(password);
        //disable the transaction support
        config.setAutoCommit(true);
        config.setValidationTimeout(10000L);
        config.setConnectionTestQuery("SELECT 1 ");
        config.setInitializationFailTimeout(120000L);
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setPoolName("graphql-sample");

        this.hikariDataSource = new HikariDataSource(config);

        this.jdbi = Jdbi.create(hikariDataSource);

        LOGGER.info("Db init successfully.");

    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }
}
