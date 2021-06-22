package com.jack.graphql.interfaces.api;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class G1OrderDtoGraphqlHandler implements RouteHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(G1OrderDtoGraphqlHandler.class);


    @Override
    public void handle(MuRequest muRequest, MuResponse muResponse, Map<String, String> map) throws Exception {

    }
}
