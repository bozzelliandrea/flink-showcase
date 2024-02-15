package org.boz.connector.jms;

import javax.jms.QueueConnectionFactory;
import java.io.Serializable;
import java.util.Objects;

public class JmsQueueSinkBuilder<IN extends Serializable> {

    private String name;
    private QueueConnectionFactory factory;

    private JmsQueueSinkBuilder() {}

    public static <IN extends Serializable> JmsQueueSinkBuilder<IN> builder() {
        return new JmsQueueSinkBuilder<>();
    }

    public JmsQueueSinkBuilder<IN> setFactory(final QueueConnectionFactory connectionFactory) {
        Objects.requireNonNull(connectionFactory, "QueueConnectionFactory must not be null");
        this.factory = connectionFactory;
        return this;
    }

    public JmsQueueSinkBuilder<IN> setQueueName(String name) {
        Objects.requireNonNull(name, "Queue name must not be null");
        this.name = name;
        return this;
    }

    public JmsQueueSink<IN> build() {
        return new JmsQueueSink<>(factory, name);
    }
}
