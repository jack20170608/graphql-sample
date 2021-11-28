package com.jack.graphql.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.LikePredicate;
import com.jack.graphql.cache.Cache;
import com.jack.graphql.dao.InterfaceCodeDao;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import com.jack.graphql.domain.OrderBuilder;
import com.jack.graphql.domain.OrderField;
import com.jack.graphql.domain.Status;
import com.jack.graphql.interfaces.dto.CommonQueryDto;
import com.jack.graphql.interfaces.dto.OrderQueryDto;
import com.jack.graphql.interfaces.dto.OrderVO;
import com.jack.graphql.interfaces.helper.CommonPage;
import com.jack.graphql.service.OrderService;
import com.jack.graphql.utils.*;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.jack.graphql.dao.impl.OrderDaoImpl.CUSTOMER_TABLE;
import static com.jack.graphql.dao.impl.OrderDaoImpl.PRODUCT_TABLE;
import static com.jack.graphql.utils.StringConvertUtils.toStr;

public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderDao orderDao;
    private final InterfaceCodeDao interfaceCodeDao;
    private final Cache<Long, Order> orderCache;
    private final Cache<String, List<Long>> orderQueryCache;

    public OrderServiceImpl(OrderDao orderDao
        , InterfaceCodeDao interfaceCodeDao
        , Cache<Long, Order> orderCache
        , Cache<String, List<Long>> orderQueryCache) {
        this.orderDao = orderDao;
        this.interfaceCodeDao = interfaceCodeDao;
        this.orderCache = orderCache;
        this.orderQueryCache = orderQueryCache;
    }


    @Override
    public void initDataWithSize(int dataSize) {
        orderQueryCache.clear();

        List<Order> orderList = Lists.newArrayList();
        for (long i = 0; i < dataSize; i++) {
            String[] rawString = new String[12];
            String sequenceNo = IdGenerator.getNextId();
            Long customerId = CUSTOMER_TABLE.get(RandomUtils.nextInt(0, 3));
            Long productId = PRODUCT_TABLE.get(RandomUtils.nextInt(0, 9));
            Status status = i % 11 == 0 ? Status.PAY : (i % 3 != 0 ? Status.FINISHED : Status.DELIVERING);
            LocalDateTime now = LocalDateUtils.plus(LocalDateTime.now(), RandomUtils.nextInt(0, 100) - 100, ChronoUnit.DAYS);

            String nowString = LocalDateUtils.format(now, LocalDateUtils.DATETIME_PATTERN);
            BigDecimal price = new BigDecimal(RandomUtils.nextInt(1, 1000) / 20);
            int quantity = RandomUtils.nextInt(1, 100);
            BigDecimal totalAmount = BigDecimalUtils.multiply(price, new BigDecimal(quantity));

            rawString[0] = toStr(i);
            rawString[1] = sequenceNo;
            rawString[2] = toStr(customerId);
            rawString[3] = toStr(productId);
            rawString[4] = toStr(price);
            rawString[5] = toStr(quantity);
            rawString[6] = toStr(totalAmount);
            rawString[7] = toStr(nowString);
            rawString[8] = toStr(nowString);
            rawString[9] = toStr(nowString);
            rawString[10] = toStr(status);
            rawString[11] = "some dummy comments for " + i;

            orderList.add(OrderBuilder.anOrder()
                .withSequenceNo(sequenceNo)
                .withCustomerId(customerId)
                .withProductId(productId)
                .withOrderDatetime(now)
                .withOrderStatus(status)
                .withCreateDt(now)
                .withLastUpdateDt(now)
                .withRawString(rawString)
                .build()
            );

            if (i % 1000 == 0) {
                batchInsert(orderList);
                LOGGER.info("complete {}.", i);
                orderList.clear();
            }
        }

        batchInsert(orderList);
        orderList.clear();

        orderDao.loadingDataToCache(orderCache);
    }

    @Override
    public void batchInsert(List<Order> orders) {
        orderDao.batchInsert(orders);
    }


    @Override
    public List<String> incrementalQuery(String code, int maxDataSize) {
        int maxSize = maxDataSize <= 0 || maxDataSize > 1000 ? 100 : maxDataSize;
        return interfaceCodeDao.getByCode(code).map(i -> {
            Long counter = i.getCounter();
            GreaterLessPredicate predicate = new GreaterLessPredicate(OrderField.id.name(), counter, false, false);
            List<Long> targetKeys = queryAndReturnKeys(predicate);
            int totalSize = targetKeys.size();
            LOGGER.info("Total size [{}].", totalSize);

            int startIndex = 0;
            int endIndex = Math.min(totalSize, maxSize);
            List<Order> targetOrders = queryByKeys(Sets.newHashSet(targetKeys.subList(startIndex, endIndex)));

            if (!CollectionUtils.isEmpty(targetOrders)) {
                Long newCounter = targetOrders.get(endIndex - 1).getId();
                LOGGER.info("New counter = {}.", newCounter);
                interfaceCodeDao.updateCounter(code, newCounter);
            }
            return targetOrders.stream().map(order -> MAPPER.apply(order, i.getOrderFieldList())).map(OrderVO::getContent).collect(Collectors.toList());
        }).orElse(Lists.newArrayList());
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
    public List<Order> queryByKeys(Set<Long> keys) {
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

    @Override
    public int updateCounter(String code, Long newCounter) {
        return interfaceCodeDao.updateCounter(code, newCounter);
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
