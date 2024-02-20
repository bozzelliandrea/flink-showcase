package org.boz.connector.jms.source;

import org.apache.flink.annotation.Experimental;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
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

@Experimental
public class JMSQueueSource<OUT extends Serializable> extends RichParallelSourceFunction<OUT> implements SourceFunction<OUT> {

    private final static Logger LOGGER = LoggerFactory.getLogger(JMSQueueSource.class);

    private final String queueName;
    private final QueueConnectionFactory connectionFactory;
    private final JMSDeserializer<OUT> deserializer;
    private final String username;
    private final String password;

    private boolean isRunning = true;
    private QueueConnection connection;
    private QueueSession session;
    private Queue destination;
    private QueueReceiver consumer;

    public JMSQueueSource(final String queueName,
                          final QueueConnectionFactory connectionFactory,
                          final JMSDeserializer<OUT> deserializer,
                          final String username,
                          final String password) {
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;
        this.deserializer = deserializer;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(SourceContext<OUT> sourceContext) throws Exception {
        while (isRunning) {
            sourceContext.collect(deserializer.deserialize(consumer.receive()));
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = connectionFactory.createQueueConnection(username, password);
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(queueName);
        consumer = session.createReceiver(destination);
        connection.start();
    }

    @Override
    public void close() throws Exception {
        closeChannel();
    }

    @Override
    public void cancel() {
        closeChannel();
    }

    private void closeChannel() {
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
