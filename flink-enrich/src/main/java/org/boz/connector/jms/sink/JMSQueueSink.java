package org.boz.connector.jms.sink;

import org.apache.flink.annotation.Experimental;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Objects;

@Experimental
public class JMSQueueSink<IN extends Serializable> extends RichSinkFunction<IN> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMSQueueSink.class);

    private final String queueName;
    private final QueueConnectionFactory connectionFactory;
    private final String username;
    private final String password;

    private QueueConnection connection;
    private QueueSession session;
    private Queue destination;
    private QueueSender producer;

    public JMSQueueSink(final QueueConnectionFactory connectionFactory,
                        final String queueName,
                        final String username,
                        final String password) {
        Objects.requireNonNull(connectionFactory, "QueueConnectionFactory must not be null");
        Objects.requireNonNull(queueName, "Queue name must not be null");
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.username = username;
        this.password = password;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = connectionFactory.createQueueConnection(username, password);
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        final String clientId = parameters.getString("jms_client_id", null);
        if (clientId != null)
            connection.setClientID(clientId);
        producer = session.createSender(destination);
        destination = session.createQueue(queueName);
        connection.start();
    }

    @Override
    public void close() throws Exception {
        producer.close();
        session.close();
        connection.close();
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {
        try {
            MessageProducer producer = session.createProducer(destination);
            ObjectMessage message = session.createObjectMessage(value);

            producer.send(message);

            /*
            producer.send(destination,
                    message,
                    Message.DEFAULT_DELIVERY_MODE,
                    Message.DEFAULT_PRIORITY,
                    Message.DEFAULT_TIME_TO_LIVE);
             */
        } catch (JMSException e) {
            LOGGER.error("Error sending message to [{}]: {}", destination.getQueueName(), e.getLocalizedMessage());
            throw e;
        }
    }
}
