package com.jack.graphql.service.impl;

import com.google.common.collect.Lists;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.LikePredicate;
import com.jack.graphql.cache.Cache;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import com.jack.graphql.domain.OrderField;
import com.jack.graphql.interfaces.dto.OrderQueryDto;
import com.jack.graphql.interfaces.dto.OrderVO;
import com.jack.graphql.interfaces.helper.CommonPage;
import com.jack.graphql.service.OrderService;
import com.jack.graphql.utils.CollectionUtils;
import com.jack.graphql.utils.IdGenerator;
import com.jack.graphql.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderDao orderDao;
    private final Cache<Long, Order> orderCache;
    private final Cache<String, List<Long>> orderQueryCache;

    public OrderServiceImpl(OrderDao orderDao
        , Cache<Long, Order> orderCache
        , Cache<String, List<Long>> orderQueryCache) {
        this.orderDao = orderDao;
        this.orderCache = orderCache;
        this.orderQueryCache = orderQueryCache;
    }

    @Override
    public Optional<OrderVO> getById(Long id) {
        LOGGER.info("Find order with id={}.", id);
        return orderCache.get(id).map(order -> OrderVO.builder(order)
            .withContent(order.getByFields(Arrays.stream(OrderField.values()).collect(Collectors.toList())))
            .build());
    }

    @Override
    public CommonPage<String> queryContentOnly(OrderQueryDto orderQueryDto) {
        CommonPage<OrderVO> voCommonPage = query(orderQueryDto);
        return CommonPage.of(voCommonPage.getQueryKey(), voCommonPage.getTotal(),
            voCommonPage.getPageSize(), voCommonPage.getPageNum(),
            voCommonPage.getData().stream().map(OrderVO::getContent).collect(Collectors.toList())
        );
    }

    @Override
    public CommonPage<OrderVO> query(OrderQueryDto orderQueryDto) {
        LOGGER.info("Query order with parameter {}.", orderQueryDto);
        if (!orderCache.isReady()) {
            return CommonPage.ofEmpty();
        }
        IMap<Long, Order> orderIMap = orderCache.getNativeCache();
        //1. find the key
        String queryKey = orderQueryDto.getQueryKey();
        int pageSize = orderQueryDto.getPageSize();

        CommonPage<OrderVO> result = null;
        //if the first time query
        if (StringUtil.isEmpty(queryKey) || !orderQueryCache.isExists(queryKey)) {
            List<Predicate<Long, Order>> allPredicates = Lists.newArrayList();
            if (!CollectionUtils.isEmpty(orderQueryDto.getCustomerIds())) {
                allPredicates.add(new InPredicate(OrderField.customerId.name(), orderQueryDto.getCustomerIds().toArray(new Long[0])));
            }
            if (!CollectionUtils.isEmpty(orderQueryDto.getProductIds())) {
                allPredicates.add(new InPredicate(OrderField.productId.name(), orderQueryDto.getProductIds().toArray(new Long[0])));
            }
            if (StringUtil.isNotBlank(orderQueryDto.getSequenceNo())) {
                allPredicates.add(new LikePredicate(OrderField.sequenceNo.name(), orderQueryDto.getSequenceNo()));
            }
            if (!CollectionUtils.isEmpty(orderQueryDto.getStatusSet())) {
                allPredicates.add(new InPredicate(OrderField.status.name(), orderQueryDto.getStatusSet().stream()
                    .map(Enum::name).toArray(String[]::new)));
            }
            AndPredicate predicate = new AndPredicate(allPredicates.toArray(new Predicate[0]));
            Collection<Order> candidateOrders = orderIMap.values(predicate);
            int total = candidateOrders.size();
            if (candidateOrders.size() <= pageSize) {
                result = CommonPage.of(queryKey, total, pageSize, 1,
                    candidateOrders.stream().sorted().map(o -> mapper.apply(o, orderQueryDto.getOrderFieldList()))
                        .collect(Collectors.toList())
                );
            } else {
                String newQueryKey = IdGenerator.getNextId();
                LOGGER.info("New query key is [{}].", newQueryKey);
                List<Order> targetOrders = candidateOrders.stream().sorted().collect(Collectors.toList()).subList(0, pageSize);
                orderQueryCache.put(newQueryKey, candidateOrders.stream().map(Order::getId).sorted().collect(Collectors.toList()));
                result = CommonPage.of(newQueryKey, total, pageSize, 1,
                    targetOrders.stream().sorted().map(o -> mapper.apply(o, orderQueryDto.getOrderFieldList()))
                        .collect(Collectors.toList()));
            }
        } else {
            result = orderQueryCache.get(queryKey).map(ids -> {
                int queryPageNum = orderQueryDto.getPageNum();
                int total = ids.size();
                boolean isLastPage = pageSize * queryPageNum >= total;
                int startIndex = (queryPageNum - 1) * pageSize;
                int endIndex = isLastPage ? total : queryPageNum * pageSize;

                List<Long> pageIds = Lists.newArrayList();
                if (startIndex <= endIndex) {
                    pageIds = ids.subList(startIndex, endIndex);
                }
                InPredicate predicate = new InPredicate(OrderField.id.name(), pageIds.toArray(new Long[0]));
                Collection<Order> targetOrders = orderCache.getNativeCache().values(predicate);

                return CommonPage.of(queryKey, total, pageSize, queryPageNum,
                    targetOrders.stream().sorted().map(o -> mapper.apply(o, orderQueryDto.getOrderFieldList()))
                        .collect(Collectors.toList()));
            }).orElse(CommonPage.ofEmpty());
        }
        return result;
    }

    private final BiFunction<Order, List<OrderField>, OrderVO> mapper = (order, fields) -> OrderVO.builder(order)
        .withContent(order.getByFields(fields))
        .build();

}
