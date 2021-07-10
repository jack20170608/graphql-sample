package com.jack.graphql.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jack.graphql.dao.InterfaceCodeDao;
import com.jack.graphql.domain.InterfaceCode;
import com.jack.graphql.domain.OrderField;
import com.jack.graphql.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class InterfaceCodeDaoImpl implements InterfaceCodeDao {

    private final static Map<String, InterfaceCode> DB = Maps.newConcurrentMap();

    private final static Logger LOGGER = LoggerFactory.getLogger(InterfaceCodeDaoImpl.class);

    static {
        DB.put("AAA",  InterfaceCode.of("AAA",OrderField.ALL_FIELDS));
        DB.put("BBB", InterfaceCode.of("BBB", Lists.newArrayList(OrderField.customerId, OrderField.productId, OrderField.id, OrderField.price)));
    }

    @Override
    public Collection<InterfaceCode> listAll(){
        return DB.values();
    }

    @Override
    public InterfaceCode create(InterfaceCode interfaceCode){
        if (null == interfaceCode.getId()){
            interfaceCode.setId(IdGenerator.getNextIdLong());
        }
        DB.put(interfaceCode.getCode(), interfaceCode);
        return interfaceCode;
    }

    @Override
    public Optional<InterfaceCode> getByCode(String code){
        return Optional.ofNullable(DB.get(code));
    }

    @Override
    public int updateCounter(String code, Long counter) {
        return Optional.ofNullable(DB.get(code)).map(interfaceCode -> {
            interfaceCode.setCounter(counter);
            interfaceCode.setLastUpdateDt(LocalDateTime.now());
            DB.put(code, interfaceCode);
            return 1;
        }).orElse(0);
    }
}
