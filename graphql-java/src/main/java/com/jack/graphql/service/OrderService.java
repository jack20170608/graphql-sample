package com.jack.graphql.service;

import com.hazelcast.query.Predicate;
import com.jack.graphql.domain.Order;
import com.jack.graphql.interfaces.dto.OrderVO;
import com.jack.graphql.interfaces.dto.OrderQueryDto;
import com.jack.graphql.interfaces.helper.CommonPage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {

    void initDataWithSize(int dataSize);

    void batchInsert(List<Order> orders);

    Optional<OrderVO> getById(Long id);

    List<OrderVO> queryAll();

    List<Order> queryByKeys(Set<Long> keys);

    List<Order> query(Predicate predicate);

    List<String> incrementalQuery(String code, int maxDataSize);

    List<Long> queryAndReturnKeys(Predicate predicate);

    CommonPage<String> queryContentOnly(OrderQueryDto orderQueryDto);

    CommonPage<OrderVO> query(OrderQueryDto orderQueryDto);

    int updateCounter(String code, Long newCounter);


}
