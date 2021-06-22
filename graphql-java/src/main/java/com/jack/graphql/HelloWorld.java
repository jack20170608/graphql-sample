package com.jack.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class HelloWorld {

    public static void main(String[] args) {
        String schema = "type " +
            "Query" +
            " { hello: String} " +
            " schema " +
            " { query: Query }";
        SchemaParser schemaParser = new SchemaParser();
        //直接加载schema，初始化GraphQL
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
        //加载一份服务端数据
        RuntimeWiring runtimeWiring = newRuntimeWiring()
            .type("Query", builder -> builder.dataFetcher("hello"
                , new StaticDataFetcher("world")))
            .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        // 构建一个GraphQL实例，执行graphql脚本
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute("{hello}");

        System.out.println(executionResult.getData().toString());
        // Prints: {hello=world}
    }
}