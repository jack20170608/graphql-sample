package com.jack.graphql.interfaces.helper;


import com.google.common.collect.Lists;

import java.util.List;


public class CommonPage<T> {

    private int pageNum ;

    private int pageSize ;

    private int totalPage;

    private int total;

    private String queryKey;

    private List<T> data;


    public static <T> CommonPage<T> ofEmpty() {
        return of(null, 0,  100, 1, Lists.newArrayList());
    }

    public static <T> CommonPage<T> of(String queryKey, int total,List<T> data){
        return of(queryKey, total,  100, 1, data);
    }

    public static <T> CommonPage<T> of(String queryKey, int total, int pageSize, int pageNum, List<T> data) {
        CommonPage<T> page = new CommonPage<>();
        page.queryKey = queryKey;
        page.data = data;
        page.pageNum = pageNum;
        page.totalPage = total / pageSize + (total % pageSize == 0 ? 0 : 1);
        page.pageSize = pageSize;
        page.total = total;
        return page;
    }


    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }

    public String getQueryKey() {
        return queryKey;
    }
}
