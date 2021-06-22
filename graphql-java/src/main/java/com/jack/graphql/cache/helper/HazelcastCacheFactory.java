package com.jack.graphql.cache.helper;

import com.google.common.collect.Lists;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastCacheFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastCacheFactory.class);

    private volatile static HazelcastInstance instance = null;

    public static HazelcastInstance getHazelcast(){
        if (instance == null){
            synchronized (HazelcastCacheFactory.class){
                if (instance == null){
                    LOGGER.info("init the cache");
                    Config hzConfig = new Config();
                    hzConfig.setProperty("hazelcast.logging.type", "slf4j");

                    String serverName = "Order_Cache";
                    hzConfig.getGroupConfig().setName(serverName);
                    NetworkConfig networkConfig = hzConfig.getNetworkConfig();
                    JoinConfig joinConfig = networkConfig.getJoin();
                    joinConfig.getMulticastConfig().setEnabled(false);
                    joinConfig.getDiscoveryConfig().setDiscoveryStrategyConfigs(Lists.newArrayList());
                    joinConfig.getTcpIpConfig().setEnabled(false);
                    instance = Hazelcast.newHazelcastInstance();
                    LOGGER.info("hzcast init with server name {}", serverName);
                }
            }
        }
        return instance;
    }
}
