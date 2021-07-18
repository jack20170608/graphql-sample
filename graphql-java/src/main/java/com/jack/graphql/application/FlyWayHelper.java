package com.jack.graphql.application;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class FlyWayHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlyWayHelper.class);


    public static void runFlyway(AppContext appContext){
        boolean flywayEnabled = appContext.getConfig().getBoolean("flyway.enabled");
        String url = appContext.getConfig().getString("flyway.url");
        String user = appContext.getConfig().getString("flyway.user");
        String password = appContext.getConfig().getString("flyway.password");
        String metaTable = appContext.getConfig().getString("flyway.table");
        List<String> locations = appContext.getConfig().getStringList("flyway.locations");

        LOGGER.info("=====================================================");
        LOGGER.info("url=[{}].", url);
        LOGGER.info("user=[{}], password=[{}].", user, password);
        LOGGER.info("schemaHisTable=[{}].", metaTable);
        LOGGER.info("locations=[{}]", String.join(",", locations));
        LOGGER.info("=====================================================");

        if (flywayEnabled) {
            Flyway flyway = Flyway.configure()
                .encoding("utf-8")
                .dataSource(url, user, password)
                .table(metaTable)
                .locations(locations.toArray(new String[locations.size()]))
                .schemas("public")
                .load();

            flyway.baseline();
            flyway.repair();

            flyway.migrate();
        }else {
            LOGGER.info("Flyway not enabled.");
        }

    }

}
