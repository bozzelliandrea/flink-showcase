package org.boz.connector.jms.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import java.io.Serializable;

public class JMSQueueSourceFunction<OUT extends Serializable> implements SourceFunction<OUT> {

    private final static Logger LOGGER = LoggerFactory.getLogger(JMSQueueSourceFunction.class);

    private final String queueName;
    private final QueueConnectionFactory connectionFactory;
    private final JMSDeserializer<OUT> deserializer;

    private boolean isRunning = false;
    private QueueConnection connection;
    private QueueSession session;
    private Queue destination;
    private QueueReceiver consumer;

    public JMSQueueSourceFunction(String queueName, QueueConnectionFactory connectionFactory, JMSDeserializer<OUT> deserializer) {
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;
        this.deserializer = deserializer;
    }

    @Override
    public void run(SourceContext<OUT> sourceContext) throws Exception {
        final String username = "admin";
        final String password = "password";
        connection = connectionFactory.createQueueConnection(username, password);
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
        consumer = session.createReceiver(destination);
        connection.start();

        while (isRunning) {
            sourceContext.collect(deserializer.deserialize(consumer.receive()));
        }
    }

    @Override
    public void cancel() {
        if (isRunning) {
            try {
                consumer.close();
                session.close();
                connection.close();
            } catch (JMSException exception) {
                LOGGER.error("Failed closing JMS Queue channel", exception);
            }
        }
        isRunning = false;
    }
}
