package com.jack.graphql.hazelcast;

public class ServerLocal {

    public static void main(String[] args) {
        System.setProperty("env", "local");
        System.setProperty("port", "5555");
        Server.main(args);
    }
}
