package com.jack.graphql;

import com.google.common.collect.Lists;
import com.jack.graphql.domain.Status;
import com.jack.graphql.utils.LocalDateUtils;
import com.jack.graphql.utils.StringConvertUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    @Test
    public void testDateTimeParse(){
        String pattern = "yyyy:MM:dd:HH:mm:ss:SSS";
        String str = LocalDateUtils.format(LocalDateTime.now(), pattern);
        System.out.println(str);
        LocalDateTime orderDatetime = LocalDateUtils.parseLocalDateTime(str , pattern);
        System.out.println(orderDatetime);

    }

    @Test
    public void testEnumParse(){
//        Status status = Enum.valueOf(Status.class, "DELIVERING");
//        System.out.println(status);


        Status status1 = StringConvertUtils.toEnum(Status.class, "FINISHED                        ");
        System.out.println(status1);
    }

}
