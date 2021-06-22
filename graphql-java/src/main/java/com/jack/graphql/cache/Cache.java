package com.jack.graphql.cache;

import com.hazelcast.core.IMap;

import java.util.Optional;

public interface Cache<K, V> {

    String getName();

    IMap<K, V> getNativeCache();

    void init();

    void refresh();

    Optional<V> get(K k);

    boolean isExists(K k);

    void put(K k, V v);

    void remove(K k);

    void clear();

    boolean isReady();


}
