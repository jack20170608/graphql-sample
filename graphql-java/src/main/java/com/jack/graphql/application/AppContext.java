package com.jack.graphql.application;

import com.jack.graphql.cache.Cache;
import com.jack.graphql.cache.OrderCacheImpl;
import com.jack.graphql.cache.OrderQueryResultCache;
import com.jack.graphql.dao.InterfaceCodeDao;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.dao.impl.InterfaceCodeDaoImpl;
import com.jack.graphql.dao.impl.OrderDaoImpl;
import com.jack.graphql.service.OrderService;
import com.jack.graphql.service.impl.OrderServiceImpl;
import com.typesafe.config.Config;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AppContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

    private final Config config;

    public AppContext(@NotNull Config config) {
        this.config = config;
    }

    private PostgresDataSourceFactory dataSourceFactory = null;

    private OrderDao orderDao;
    private InterfaceCodeDao interfaceCodeDao;

    private OrderCacheImpl orderCache;
    private Cache<String, List<Long>> orderQueryCache;

    private OrderService orderService;

    public void init() {
        LOGGER.info("Initing the application context..........");
        boolean flywayEnabled = config.getBoolean("flyway.enabled");
        LOGGER.info("flyway enabled={}", flywayEnabled);
        if (flywayEnabled) {
            FlyWayHelper.runFlyway(this);
        }
        dataSourceFactory = PostgresDataSourceFactory.getInstance();

        //dao
        orderDao = new OrderDaoImpl(dataSourceFactory.getJdbi());
        interfaceCodeDao = new InterfaceCodeDaoImpl();


        //cache
        orderCache = new OrderCacheImpl(orderDao, this);
        //query cache
        orderQueryCache = new OrderQueryResultCache();
        //service
        orderService = new OrderServiceImpl(orderDao, interfaceCodeDao, orderCache, orderQueryCache);
    }

    public boolean isLocal() {
        String env = System.getProperty("ENV");
        return StringUtils.contains(env, "local");
    }

    public Config getConfig() {
        return config;
    }

    public PostgresDataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }

    public OrderCacheImpl getOrderCache() {
        return orderCache;
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
