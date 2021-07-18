package com.jack.graphql.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            com.typesafe.config.Config defaultConfig = ConfigFactory.parseResources("application.conf");
            String env = System.getProperty("env");
            com.typesafe.config.Config specConfig = ConfigFactory.parseResources("application-" + env + ".conf");
            com.typesafe.config.Config ownConfig = specConfig.withFallback(defaultConfig);

            int port = Integer.parseInt(System.getProperty("port"));

            String serverName = "Jack007HazelcastNode" + env;
            String password = "1";
            String managementServerUrl = ownConfig.getString("hazelcast.management-center.url");

            LOGGER.info("hzcluster={}", serverName);
            LOGGER.info("port={}", port);

            Config hzConfig = new Config();
            hzConfig.setProperty("hazelcast.logging.type", "slf4j");
            hzConfig.getGroupConfig().setName(serverName)
                .setPassword(password);

            NetworkConfig networkConfig = hzConfig.getNetworkConfig();
            networkConfig
                .getJoin()
                .getMulticastConfig()
                .setEnabled(false);

            networkConfig.setPortAutoIncrement(false)
                .setPort(port);

            List<String> clusterMembers = ownConfig.getStringList("hazelcast.members");
            networkConfig
                .getJoin()
                .getTcpIpConfig()
                .setEnabled(true)
                .setMembers(clusterMembers);

            hzConfig.getManagementCenterConfig().setEnabled(true).setUrl(managementServerUrl);

            HazelcastInstance instance = Hazelcast.newHazelcastInstance(hzConfig);

            LOGGER.info("Server bring up successfully.");
        } catch (Throwable t) {
            LOGGER.error("Server bring up failure.", t);
        }
    }


}
