package com.jack.graphql.application;

import com.jack.graphql.App;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

import static com.jack.graphql.utils.StringConvertUtils.toEnum;

public class JmsConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConnectionFactory.class);

    private volatile static Connection sharedConnection;

    public enum JmsBrokerType{
        solace, activemq
    }

    public static Connection getSharedConnection() throws Exception {
        if (sharedConnection == null){
            synchronized (JmsConnectionFactory.class){
                if (sharedConnection == null){
                    AppContext appContext = App.APP_CONTEXT;
                    JmsBrokerType jmsBrokerType = toEnum(JmsBrokerType.class, appContext.getConfig().getString("jms.broker-type"));
                    if (jmsBrokerType == JmsBrokerType.solace){
                        String host = appContext.getConfig().getString("solace.host");
                        String vpn = appContext.getConfig().getString("solace.vpn");
                        String user = appContext.getConfig().getString("solace.user");
                        String password = appContext.getConfig().getString("solace.password");

                        boolean sslEnabled = appContext.getConfig().getBoolean("solace.ssl-enabled");
                        LOGGER.info("-------------------------------------------------------------------------");
                        LOGGER.info("host=[{}]", host);
                        LOGGER.info("vpn=[{}]", vpn);
                        LOGGER.info("user=[{}]", user);
                        LOGGER.info("ssl-enabled=[{}]", sslEnabled);
                        LOGGER.info("-------------------------------------------------------------------------");


                        SolConnectionFactory solConnectFactory = SolJmsUtility.createConnectionFactory();
                        solConnectFactory.setHost(host);
                        solConnectFactory.setVPN(vpn);
                        solConnectFactory.setUsername(user);
                        solConnectFactory.setPassword(password);
                        solConnectFactory.setDynamicDurables(false);
                        solConnectFactory.setRespectTTL(false);

                        //config for the auto recovery
                        solConnectFactory.setConnectRetries(5);
                        solConnectFactory.setConnectTimeoutInMillis(5000);
                        solConnectFactory.setReconnectRetries(5);
                        solConnectFactory.setReconnectRetryWaitInMillis(5000);
                        solConnectFactory.setConnectRetriesPerHost(10);

                        sharedConnection = solConnectFactory.createConnection();
                        sharedConnection.start();
                    }else {
                        throw new RuntimeException("Not suppored currently.");
                    }
                }
            }
        }
        return sharedConnection;
    }

    public static void releaseConnection(){
        if (null != sharedConnection){
            try {
                sharedConnection.stop();
                sharedConnection.close();
            }catch (JMSException e){
                LOGGER.error("Jms connection close failurel", e);
            }
        }
    }

}
