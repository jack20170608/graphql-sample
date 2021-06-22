package com.jack.graphql.utils;

import com.jack.graphql.application.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JmsSendHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsSendHelper.class);

    public static void sendStringMessage(String queue, String message){
        Session session = null;
        try {
            Connection connection = JmsConnectionFactory.getSharedConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue jmsQueue = session.createQueue(queue);
            TextMessage textMessage = session.createTextMessage();
            MessageProducer messageProducer = session.createProducer(jmsQueue);

            messageProducer.send(jmsQueue, textMessage, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

            LOGGER.info("send successfully.");
        }catch (Throwable throwable){
            LOGGER.error("Message send failure", throwable);
            throw new RuntimeException("message send failure", throwable);
        }finally {
            if (null != session){
                try {
                    session.close();
                }catch (JMSException e){
                    LOGGER.warn("Session close failure.");
                }
            }
        }
    }
}
