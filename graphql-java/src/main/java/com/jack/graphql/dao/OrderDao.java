package com.jack.graphql.dao;

import com.jack.graphql.cache.Cache;
import com.jack.graphql.domain.Order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Long insert(Order order);

    void update(Order order);

    Optional<Order> getById(Long id);

    Optional<Long> getMinId();

    Optional<Long> getMaxId();

    Collection<Order> getAll();

    int loadingDataToCache(Cache<Long, Order> cache);

    Collection<Order> getByIdRange(Long minId, Long maxId);

    int loadingDataToCacheStream(Cache<Long, Order> cache);

    void batchInsert(List<Order> orderList);
}
