package com.jack.graphql.cache.helper;

import com.google.common.collect.Lists;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jack.graphql.App;
import com.jack.graphql.domain.Order;
import com.jack.graphql.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HazelcastCacheFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastCacheFactory.class);

    private volatile static HazelcastInstance instance = null;

    public static HazelcastInstance getHazelcast(){
        if (instance == null){
            synchronized (HazelcastCacheFactory.class){
                if (instance == null){
                    LOGGER.info("init the cache");

                    if (App.APP_CONTEXT.isLocal()) {
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
                    }else {
                        String groupName = App.APP_CONTEXT.getConfig().getString("hazelcast.group-name");
                        String password = App.APP_CONTEXT.getConfig().getString("hazelcast.password");
//                        String [] groupMembers = App.APP_CONTEXT.getConfig().getStringList("hazelcast.group-member-list").toArray(new String[0]);
                        List<String> groupMembers = App.APP_CONTEXT.getConfig().getStringList("hazelcast.group-member-list");

                        LOGGER.info("--------------------------------------------------------------------------------------");
                        LOGGER.info("groupName={}.", groupName);
                        LOGGER.info("password={}.", password);
                        LOGGER.info("groupMembers={}.", groupMembers);
                        LOGGER.info("--------------------------------------------------------------------------------------");



                        GroupConfig groupConfig = new GroupConfig(groupName, password);
                        ClientConfig clientConfig = new ClientConfig();
                        clientConfig.setProperty("hazelcast.logging.type", "slf4j");
                        clientConfig.setInstanceName(App.APP_NAME);


                        clientConfig.setGroupConfig(groupConfig);
                        clientConfig.getSerializationConfig().addPortableFactory(Constants.HAZELCAST_FACTORY_ID, classId -> {
                            if (classId == Constants.HAZELCAST_ORDER_OBJECT_ID){
                                return new Order();
                            }
                            return null;
                        });

                        clientConfig.getNetworkConfig()
                            .setAddresses(groupMembers)
                            .setConnectionTimeout(5000)
                            .setConnectionAttemptLimit(10)
                            .setConnectionAttemptPeriod(5000);
//                            .addAddress(groupMembers);

                        instance = HazelcastClient.newHazelcastClient(clientConfig);
                    }
                }
            }
        }
        return instance;
    }
}
