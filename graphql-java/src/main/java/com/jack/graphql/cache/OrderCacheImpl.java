package com.jack.graphql.cache;

import com.hazelcast.core.IMap;
import com.jack.graphql.application.AppContext;
import com.jack.graphql.cache.helper.HazelcastCacheFactory;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import com.jack.graphql.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

public class OrderCacheImpl implements Cache<Long, Order> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCacheImpl.class);

    private final OrderDao orderDao;
    private final transient Object refreshLock = new Object();
    private final String cacheName;
    private final IMap<Long, Order> orderCache;
    private boolean isReady;
    private final boolean needToRefresh;

    public OrderCacheImpl(OrderDao orderDao, AppContext appContext) {
        this.orderDao = orderDao;
        this.needToRefresh = appContext.getConfig().getBoolean("order.cache.refresh-when-start");
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
        orderCache.addIndex("orderDatetime", true);
        orderCache.addIndex("createDt", true);
        orderCache.addIndex("lastUpdateDt", true);
        orderCache.addIndex("status", false);

        LOGGER.info("Loading data into cache.");
        if (needToRefresh) {
            new Thread(this::refresh, "orderCacheLoadingThread").start();
        } else {
            isReady = true;
        }
    }

    @Override
    public void refresh() {
        synchronized (refreshLock) {
            isReady = false;
            clear();
//            long allDataSize = doRefreshThoughIds();
            long allDataSize = doRefreshThoughJdbcStream();
//            long allDataSize = doRefreshThoughJdbiStream();
            LOGGER.info("Done the cache loading with size {}.", allDataSize);
            isReady = true;
        }
    }

    private int doRefreshThoughJdbcStream(){
        return orderDao.loadingDataToCache(this);
    }

    private int doRefreshThoughJdbiStream(){
        return orderDao.loadingDataToCacheStream(this);
    }

    private int doRefreshThoughIds() {
        int allDataSize = 0;
        long minId = orderDao.getMinId().orElse(0L);
        long maxId = orderDao.getMaxId().orElse(0L);
        long batchSize = 1000L;
        long batchCount = (maxId - minId) / batchSize + 1;
        LOGGER.info("Total batch size= [{}].", batchCount);
        for (long i = 0; i < batchCount; i++) {
            LOGGER.info("loading batch {}.", i);
            long startIndex = minId + batchSize * i;
            long endIndex = startIndex + batchSize;
            Collection<Order> orders = orderDao.getByIdRange(startIndex, endIndex);
            allDataSize += CollectionUtils.size(orders);
            orders.forEach(o -> put(o.getId(), o));
        }
        return allDataSize;
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
