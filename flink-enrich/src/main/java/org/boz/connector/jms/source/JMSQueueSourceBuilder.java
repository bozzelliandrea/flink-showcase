package org.boz.connector.jms.source;

import javax.jms.QueueConnectionFactory;
import java.io.Serializable;
import java.util.Objects;

public class JMSQueueSourceBuilder<OUT extends Serializable> implements Serializable {

    private String queueName;
    private QueueConnectionFactory factory;
    private JMSDeserializer<OUT> deserializer;
    private String username;
    private String password;

    private JMSQueueSourceBuilder() {
    }

    public static <OUT extends Serializable> JMSQueueSourceBuilder<OUT> builder() {
        return new JMSQueueSourceBuilder<>();
    }

    public JMSQueueSourceBuilder<OUT> setQueueName(String queueName) {
        Objects.requireNonNull(queueName, "Queue name must not be null");
        this.queueName = queueName;
        return this;
    }

    public JMSQueueSourceBuilder<OUT> setFactory(QueueConnectionFactory factory) {
        Objects.requireNonNull(factory, "QueueConnectionFactory must not be null");
        this.factory = factory;
        return this;
    }

    public JMSQueueSourceBuilder<OUT> setDeserializer(JMSDeserializer<OUT> deserializer) {
        Objects.requireNonNull(deserializer, "Deserializer must not be null");
        this.deserializer = deserializer;
        return this;
    }


    public JMSQueueSourceBuilder<OUT> setUsername(String username) {
        Objects.requireNonNull(username, "Connection username must not be null");
        this.username = username;
        return this;
    }

    public JMSQueueSourceBuilder<OUT> setPassword(String password) {
        Objects.requireNonNull(password, "Connection password must not be null");
        this.password = password;
        return this;
    }

    public JMSQueueSource<OUT> build() {
        if(!validator())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " config is invalid!");
        return new JMSQueueSource<>(queueName, factory, deserializer, username, password);
    }

    private boolean validator() {
        return factory != null && deserializer != null && username != null && password != null;
    }
}
