package com.jack.graphql;

public class AppLocal {

    public static void main(String[] args) {
        System.setProperty("ENV", "dev");
        App.main(args);
    }
}
