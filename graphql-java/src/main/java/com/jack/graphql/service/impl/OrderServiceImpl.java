package com.jack.graphql.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

import java.util.*;
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
    public List<Order> query(Predicate predicate) {
        return Optional.ofNullable(predicate).map(p -> {
            return orderCache.getNativeCache().values(p).stream()
                .sorted(Comparator.comparing(Order::getId))
                .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    @Override
    public List<Long> queryAndReturnKeys(Predicate predicate) {
        return Optional.ofNullable(predicate).map(p -> {
            return orderCache.getNativeCache().keySet(predicate)
                .stream().sorted()
                .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    @Override
    public List<Order> queryByKeys(Set<Long> keys){
        return orderCache.getNativeCache().getAll(keys).values().stream()
            .sorted(Comparator.comparing(Order::getId))
            .collect(Collectors.toList());
    }


    @Override
    public List<OrderVO> queryAll() {
        return orderCache.getNativeCache().values().stream()
            .sorted(Comparator.comparing(Order::getId))
            .map(t -> MAPPER.apply(t, OrderField.ALL_FIELDS))
            .collect(Collectors.toList());
    }

    @Override
    public CommonPage<OrderVO> query(OrderQueryDto orderQueryDto) {
        LOGGER.info("Query order with parameter {}.", orderQueryDto);
        if (!orderCache.isReady()) {
            return CommonPage.ofEmpty();
        }
        //1. find the key
        final String queryKey = orderQueryDto.getQueryKey();
        final int pageSize = orderQueryDto.getPageSize();
        final int pageNum = orderQueryDto.getPageNum();
        final boolean cacheQueryResult = orderQueryDto.isCacheQueryResult();

        final int beginIndex = (pageNum - 1) * pageSize;
        final int endIndex = pageNum * pageSize;

        CommonPage<OrderVO> result = null;
        //if the first time query
        if (StringUtil.isEmpty(queryKey) || !orderQueryCache.isExists(queryKey)) {
            AndPredicate predicate = new AndPredicate(toPredicate(orderQueryDto).toArray(new Predicate[0]));
            List<Long> candidateOrderKeys = queryAndReturnKeys(predicate);
            int innerEndIndex = endIndex;
            int total = candidateOrderKeys.size();
            //to avoid indexOutOfBound exception
            if (total < beginIndex) {
                return CommonPage.of(queryKey, 0, pageSize, pageNum, new ArrayList<>());
            }
            if (total < innerEndIndex) {
                innerEndIndex = total;
            }
            String newQueryKey = null;
            if (cacheQueryResult) {
                newQueryKey = IdGenerator.getNextId();
                LOGGER.info("New query key is [{}].", newQueryKey);
                orderQueryCache.put(newQueryKey, candidateOrderKeys);
            }
            result = CommonPage.of(newQueryKey, total, pageSize, pageNum,
                queryByKeys(Sets.newHashSet(candidateOrderKeys.subList(beginIndex, innerEndIndex)))
                    .stream()
                    .map(o -> MAPPER.apply(o, orderQueryDto.getOrderFieldList()))
                    .collect(Collectors.toList()));
        } else {
            result = orderQueryCache.get(queryKey).map(ids -> {
                int total = ids.size();
                int innerEndIndex = endIndex;
                if (total < beginIndex) {
                    return CommonPage.of(queryKey, total, pageSize, pageNum, new ArrayList<OrderVO>());
                }
                if (total < innerEndIndex) {
                    innerEndIndex = total;
                }
                Set<Long> candidateIds = Sets.newHashSet(ids.subList(beginIndex, innerEndIndex));
                List<Order> targetOrders = queryByKeys(candidateIds);

                return CommonPage.of(queryKey, total, pageSize, pageNum,
                    targetOrders.stream().map(o -> MAPPER.apply(o, orderQueryDto.getOrderFieldList()))
                        .collect(Collectors.toList()));
            }).orElse(CommonPage.of(queryKey, 0, pageSize, pageNum, new ArrayList<>()));
        }
        return result;
    }

    private List<Predicate<Long, Order>> toPredicate(OrderQueryDto orderQueryDto) {
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
        return allPredicates;
    }

    private static final BiFunction<Order, List<OrderField>, OrderVO> MAPPER = (order, fields) -> OrderVO.builder(order)
        .withContent(order.getByFields(fields))
        .build();

}
