package com.jack.graphql.interfaces.api;

import com.hazelcast.query.Predicate;
import com.jack.graphql.interfaces.dto.CommonQueryDto;
import com.jack.graphql.interfaces.dto.filter.Filter;
import com.jack.graphql.interfaces.dto.filter.HazelCastFilterHelper;
import com.jack.graphql.interfaces.helper.RestResponse;
import com.jack.graphql.interfaces.helper.RestResponseHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("filter")
public class FilterHandler{


    @GET
    @Path("testCommonQueryObj")
    @Produces(MediaType.APPLICATION_JSON)
    public CommonQueryDto testReturnQueryDto(){
        CommonQueryDto commonQueryDto = CommonQueryDto.dummyQueryObj();
        System.out.println(commonQueryDto);
        return commonQueryDto;
    }


    @POST
    @Path("postQueryDto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> postQueryDto(CommonQueryDto queryDto){
        System.out.println("---------------------------------------------------");
        System.out.println(queryDto);
        System.out.println("---------------------------------------------------");
        Predicate predicate = HazelCastFilterHelper.toHazelCastPredicate(queryDto.getFilter());
        System.out.println(predicate);
        System.out.println("---------------------------------------------------");
        return RestResponseHelper.success("success");
    }


    @GET
    @Path("testGetFilterj")
    @Produces(MediaType.APPLICATION_JSON)
    public Filter testReturnFilter(){
        Filter filter = CommonQueryDto.dummyFilter();
        System.out.println(filter);
        return filter;
    }

    @GET
    @Path("testGetSimpleFilter")
    @Produces(MediaType.APPLICATION_JSON)
    public Filter testGetSimpleFilter(){
        Filter filter = CommonQueryDto.simpleFilter();
        System.out.println(filter);
        return filter;
    }


    @POST
    @Path("postFilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> postFilter(Filter filter){
        System.out.println("---------------------------------------------------");
        System.out.println(filter);
        System.out.println("---------------------------------------------------");
        return RestResponseHelper.success("success");
    }




}
