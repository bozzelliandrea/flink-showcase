package org.boz.connector.jms.sink;

import javax.jms.QueueConnectionFactory;
import java.io.Serializable;
import java.util.Objects;

public class JMSQueueSinkBuilder<IN extends Serializable> {

    private String queueName;
    private QueueConnectionFactory factory;

    private JMSQueueSinkBuilder() {}

    public static <IN extends Serializable> JMSQueueSinkBuilder<IN> builder() {
        return new JMSQueueSinkBuilder<>();
    }

    public JMSQueueSinkBuilder<IN> setFactory(final QueueConnectionFactory connectionFactory) {
        Objects.requireNonNull(connectionFactory, "QueueConnectionFactory must not be null");
        this.factory = connectionFactory;
        return this;
    }

    public JMSQueueSinkBuilder<IN> setQueueName(String name) {
        Objects.requireNonNull(name, "Queue name must not be null");
        this.queueName = name;
        return this;
    }

    public JMSQueueSink<IN> build() {
        return new JMSQueueSink<>(factory, queueName);
    }
}
