package com.jack.graphql.interfaces.api;

import com.google.common.collect.Maps;
import com.jack.graphql.cache.OrderCacheImpl;
import com.jack.graphql.domain.Order;
import com.jack.graphql.interfaces.dto.OrderQueryDto;
import com.jack.graphql.interfaces.dto.OrderVO;
import com.jack.graphql.interfaces.helper.CommonPage;
import com.jack.graphql.interfaces.helper.RestResponse;
import com.jack.graphql.interfaces.helper.RestResponseHelper;
import com.jack.graphql.service.OrderService;
import com.jack.graphql.utils.JmsSendHelper;
import com.jack.graphql.utils.ThreadUtils;
import io.muserver.rest.Description;
import io.muserver.rest.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/operation/api/v1")
@Description(value = "Operation API", details = "Provide the production support entrypoint.")
public class OperationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationHandler.class);

    private final OrderService orderService;
    private final OrderCacheImpl orderCache;

    private volatile boolean stopFlag;

    public OperationHandler(OrderService orderService, OrderCacheImpl orderCache) {
        this.orderService = orderService;
        this.orderCache = orderCache;
    }

    @GET
    @Path("getById")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "hahaha", details = "ddfsdfsd")
    public OrderVO getById(
        @Description("The key for the data")
        @Required
        @QueryParam("id") Long id) {
        Optional<OrderVO> optionalOrderDto = orderService.getById(id);
        return optionalOrderDto.orElse(null);
    }

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Query the ", details = "")
    public RestResponse<CommonPage<OrderVO>> query(OrderQueryDto orderQueryDto) {
        LOGGER.info("Query order with qry =[{}].", orderQueryDto);
        return RestResponseHelper.success(orderService.query(orderQueryDto));
    }


    @POST
    @Path("queryContent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Query the ", details = "")
    public RestResponse<CommonPage<String>> queryContent(OrderQueryDto orderQueryDto) {
        LOGGER.info("Query order with qry =[{}].", orderQueryDto);
        return RestResponseHelper.success(orderService.queryContentOnly(orderQueryDto));
    }

    @GET
    @Path("queryFull")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Query the ", details = "")
    public RestResponse<List<OrderVO>> queryAll() {
        return RestResponseHelper.success(orderService.queryAll().subList(0, 20));
    }


    @GET
    @Path("performanceTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Boolean> performanceTest() {
        Map<String, Boolean> result = Maps.newHashMap();
        Collection<Order> orderCollection1 = orderCache.getNativeCache().values();
        Collection<Order> orderCollection2 = orderCache.getNativeCache().values();

        if (orderCollection1 == orderCollection2) {
            result.put("equal", true);
            LOGGER.info("O1 is same reference to O2");
        } else {
            result.put("equal", false);
        }

        while (!stopFlag) {
            for (Order order : orderCollection1) {
                LOGGER.info(order.toString());
                ThreadUtils.quietSleep(100);
                if (stopFlag) {
                    break;
                }
            }

        }

        return result;
    }


    @GET
    @Path("toggle")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean toggle() {
        this.stopFlag = !stopFlag;
        return stopFlag;
    }

    @GET
    @Path("jmsTesting")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Query the ", details = "")
    public RestResponse<String> jmsTesting() {
        for (int i =1; i < 100000; i++){
            JmsSendHelper.sendStringMessage("test-queue", "dummy message");
            ThreadUtils.quietSleep(500);
        }
        return RestResponseHelper.success("success");
    }
}
