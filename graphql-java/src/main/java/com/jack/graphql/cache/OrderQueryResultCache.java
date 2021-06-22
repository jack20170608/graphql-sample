package com.jack.graphql.cache;

import com.hazelcast.core.IMap;
import com.jack.graphql.cache.helper.HazelcastCacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class OrderQueryResultCache implements Cache<String, List<Long>>{

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderQueryResultCache.class);

    private final String cacheName;
    private final IMap<String, List<Long>> queryResultCache;

    public OrderQueryResultCache() {
        this.cacheName = "OrderQueryResultCache";
        queryResultCache = HazelcastCacheFactory.getHazelcast().getMap(cacheName);
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public IMap<String, List<Long>> getNativeCache() {
        return queryResultCache;
    }

    @Override
    public void init() {
        LOGGER.info("Query result cache init done.");
    }

    @Override
    public void refresh() {}

    @Override
    public Optional<List<Long>> get(String s) {
        return Optional.ofNullable(queryResultCache.get(s));
    }

    @Override
    public boolean isExists(String s) {
        return queryResultCache.containsKey(s);
    }

    @Override
    public void put(String s, List<Long> orderQueryResult) {
        queryResultCache.put(s, orderQueryResult, 3600, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String s) {
        queryResultCache.remove(s);
    }

    @Override
    public void clear() {
        queryResultCache.clear();
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
