package com.jack.graphql.cache;

import com.hazelcast.core.IMap;
import com.jack.graphql.cache.helper.HazelcastCacheFactory;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

public class OrderCacheImpl implements Cache<Long, Order> {

    private static final Logger LOGGER  = LoggerFactory.getLogger(OrderCacheImpl.class);

    private final OrderDao orderDao;
    private final transient Object refreshLock = new Object();
    private final String cacheName ;
    private final IMap<Long, Order> orderCache ;
    private boolean isReady;

    public OrderCacheImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
        this.cacheName = "order-cache";
        orderCache = HazelcastCacheFactory.getHazelcast().getMap(cacheName);
        init();
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public IMap<Long, Order> getNativeCache() {
        return orderCache;
    }

    @Override
    public void init() {
        orderCache.addIndex("sequenceNo", false);
        orderCache.addIndex("customerId", true);
        orderCache.addIndex("productId", true);
        orderCache.addIndex("orderDatetime", true );
        orderCache.addIndex("createDt", true );
        orderCache.addIndex("lastUpdateDt", true );
        orderCache.addIndex("status", false);

        LOGGER.info("Loading data into cache.");
        new Thread(this::refresh, "orderCacheLoadingThread").start();
    }

    @Override
    public void refresh() {
        synchronized (refreshLock){
            isReady = false;
            clear();
            Collection<Order> allOrders = orderDao.getAll();
            allOrders.forEach(o -> put(o.getId(), o));

            LOGGER.info("Done the cache loading with size {}.", allOrders.size());
            isReady = true;
        }
    }

    @Override
    public Optional<Order> get(Long id) {
        return Optional.ofNullable(orderCache.get(id));
    }

    @Override
    public boolean isExists(Long id) {
        return orderCache.containsKey(id);
    }

    @Override
    public void put(Long id, Order order) {
        orderCache.put(id, order);
    }

    @Override
    public void remove(Long id) {
        orderCache.remove(id);
    }

    @Override
    public void clear() {
        orderCache.clear();
    }

    @Override
    public boolean isReady() {
        return isReady;
    }
}
