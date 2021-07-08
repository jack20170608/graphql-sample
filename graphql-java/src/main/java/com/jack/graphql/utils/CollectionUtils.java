package com.jack.graphql.utils;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils(){}

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.size() == 0;
    }

    public static int size(Collection collection){
        return collection == null ? 0 :  collection.size() ;
    }
}
