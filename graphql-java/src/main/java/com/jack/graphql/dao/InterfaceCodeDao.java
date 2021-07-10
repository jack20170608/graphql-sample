package com.jack.graphql.dao;

import com.jack.graphql.domain.InterfaceCode;

import java.util.Collection;
import java.util.Optional;

public interface InterfaceCodeDao {

    Collection<InterfaceCode> listAll();

    InterfaceCode create(InterfaceCode interfaceCode);

    Optional<InterfaceCode> getByCode(String code);

    int updateCounter(String code, Long counter);
}
