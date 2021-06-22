package com.jack.graphql;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestListSubset {

    @Test
    public void testListSplit(){
        List<String> data = Lists.newArrayList("1", "2", "3", "4", "5");


//        data.subList(0, 4).forEach(s -> {
//            System.out.println(s);
//        });


        System.out.println(data.subList(5, 5).isEmpty());
    }
}
