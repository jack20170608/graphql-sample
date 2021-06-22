package com.jack.graphql.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jack.graphql.dao.OrderDao;
import com.jack.graphql.domain.Order;
import com.jack.graphql.domain.OrderBuilder;
import com.jack.graphql.domain.Status;
import com.jack.graphql.utils.BigDecimalUtils;
import com.jack.graphql.utils.IdGenerator;
import com.jack.graphql.utils.LocalDateUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jack.graphql.utils.LocalDateUtils.toLocalDateTime;
import static com.jack.graphql.utils.StringConvertUtils.toEnum;
import static com.jack.graphql.utils.StringConvertUtils.toStr;

public class OrderDaoImpl implements OrderDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderDaoImpl.class);

    private static final String INSERT_SQL = " insert into t_order (" +
        "  id" +
        ", sequence_no" +
        ", customer_id" +
        ", product_id" +
        ", order_dt" +
        ", status" +
        ", create_dt" +
        ", last_update_dt" +
        ", raw_string " +
        " ) values ( " +
        " nextval('seq_t_order_id') " +
        ", :o.sequenceNo " +
        ", :o.customerId " +
        ", :o.productId " +
        ", :o.orderDatetime " +
        ", :o.status" +
        ", :o.createDt" +
        ", :o.lastUpdateDt" +
        ", :o.rawString" +
        " ) ";

    private static final String UPDATE_SQL = " update t_order set " +
        " sequence_no    = :o.sequenceNo" +
        ", customer_id   = :o.customerId" +
        ", product_id    = :o.productId " +
        ", order_dt      = :o.orderDatetime" +
        ", status        = :o.status" +
        ", create_dt     = :o.createDt" +
        ", last_update_dt= :o.lastUpdateDt" +
        ", raw_string    = :o.rawString" +
        " where 1 =1 " +
        " and id = :o.id";

    private static final String GET_ALL = "" +
        " select id " +
        ", sequence_no" +
        ", customer_id" +
        ", product_id" +
        ", order_dt" +
        ", status" +
        ", create_dt" +
        ", last_update_dt" +
        ", raw_string" +
        " from t_order " +
        " where 1 = 1 "
//        " where 1 = 1 limit 10000 offset 0"
        ;

    private final Jdbi jdbi;

    private static final List<Long> CUSTOMER_TABLE = Lists.newArrayList(1L, 2L, 3L, 4L);
    private static final List<Long> PRODUCT_TABLE = Lists.newArrayList(1L, 2L, 3L, 5L, 6L, 7L, 8L, 9L, 10L);

    private void initData(int dataSize){
        List<Order> orderList = Lists.newArrayList();
        for (long i = 0; i < dataSize ; i++) {
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
                .withId(i)
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

            if (i % 1000 == 0){
                batchInsert(orderList);
                LOGGER.info("complete {}.", i);
                orderList.clear();
            }
        }

        batchInsert(orderList);
        orderList.clear();

    }

    public OrderDaoImpl(Jdbi jdbi) {
        this.jdbi = jdbi;

        this.jdbi.registerRowMapper(Order.class, (rs, ctx) -> {
            return OrderBuilder.anOrder()
                .withId(rs.getLong("id"))
                .withSequenceNo(rs.getString("sequence_no"))
                .withCustomerId(rs.getLong("customer_id"))
                .withProductId(rs.getLong("product_id"))
                .withOrderDatetime(toLocalDateTime(rs.getTimestamp("order_dt")))
                .withOrderStatus(toEnum(Status.class, rs.getString("status")))
                .withCreateDt(toLocalDateTime(rs.getTimestamp("create_dt")))
                .withLastUpdateDt(toLocalDateTime(rs.getTimestamp("last_update_dt")))
                .withRawString(rs.getString("raw_string").split("\\|"))
                .build();
        });
//        initData(1000000);
    }

    @Override
    public Long insert(Order order) {
        return jdbi.withHandle(handle -> handle.createUpdate(INSERT_SQL)
            .bindBean("o", order)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Long.class)
            .one());
    }

    @Override
    public void update(Order order) {
        jdbi.useHandle(handle -> handle.createUpdate(UPDATE_SQL)
            .bindBean("o", order)
            .execute());
    }

    @Override
    public Optional<Order> getById(Long id) {
        String sql = GET_ALL
            + " AND id = :id  ";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
            .bind("id", id)
            .mapTo(Order.class)
            .findOne());
    }

    @Override
    public Collection<Order> getAll() {
        return jdbi.withHandle(handle -> handle.createQuery(GET_ALL)
            .mapTo(Order.class)
            .list());
    }

    @Override
    public void batchInsert(List<Order> orderList) {
        jdbi.useHandle(handle -> {
            PreparedBatch insertBatch = handle.prepareBatch(INSERT_SQL);
            orderList.forEach(order -> insertBatch.bindBean("o", order).add());
            insertBatch.execute();
        });
    }


}
