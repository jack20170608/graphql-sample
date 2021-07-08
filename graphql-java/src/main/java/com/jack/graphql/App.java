package com.jack.graphql;

import com.jack.graphql.application.AppContext;
import com.jack.graphql.application.HttpServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.muserver.MuServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static final String APP_NAME = "OrderService";
    public static AppContext APP_CONTEXT;

    private static void initAppContext(){
        LOGGER.info("Init app....");
        Config defaultConfig = ConfigFactory.parseResources("application.conf");
        String env = System.getProperty("ENV");
        Config specConfig = ConfigFactory.parseResources("application-" + env + ".conf");

        Config config = specConfig.withFallback(defaultConfig);

        APP_CONTEXT = new AppContext(config);
        APP_CONTEXT.init();
    }

    public static void main(String[] args) {
        initAppContext();
        try {
            MuServer muServer = HttpServer.startMuServer();
        }catch (Throwable t){
            LOGGER.error("Server start failed!", t);
            System.exit(1);
        }
    }
}
