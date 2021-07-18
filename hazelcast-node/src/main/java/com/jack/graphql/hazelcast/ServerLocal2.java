package com.jack.graphql.hazelcast;

public class ServerLocal2 {

    public static void main(String[] args) {
        System.setProperty("env", "local");
        System.setProperty("port", "5556");
        Server.main(args);
    }
}
