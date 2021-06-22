package com.jack.graphql.dao;

import com.jack.graphql.domain.Order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Long insert(Order order);

    void update(Order order);

    Optional<Order> getById(Long id);

    Collection<Order> getAll();

    void batchInsert(List<Order> orderList);
}
