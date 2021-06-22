package com.jack.graphql.service;

import com.jack.graphql.interfaces.dto.OrderVO;
import com.jack.graphql.interfaces.dto.OrderQueryDto;
import com.jack.graphql.interfaces.helper.CommonPage;

import java.util.List;
import java.util.Optional;

public interface OrderService {


    Optional<OrderVO> getById(Long id);

    CommonPage<String> queryContentOnly(OrderQueryDto orderQueryDto);

    CommonPage<OrderVO> query(OrderQueryDto orderQueryDto);

}
