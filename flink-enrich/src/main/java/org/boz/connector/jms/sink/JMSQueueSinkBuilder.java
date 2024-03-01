package org.boz.connector.jms.sink;

import org.boz.connector.jms.JMSMessageIDExtractor;

import javax.jms.CompletionListener;
import javax.jms.QueueConnectionFactory;
import java.io.Serializable;
import java.util.Objects;

public class JMSQueueSinkBuilder<IN extends Serializable> implements Serializable {

    private static final long serialVersionUID = 6495712725284644669L;

    private String queueName;
    private QueueConnectionFactory factory;
    private String username;
    private String password;
    private String clientId;
    private CompletionListener completionListener;
    private JMSMessageIDExtractor<IN> messageIDExtractor;

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

    public JMSQueueSinkBuilder<IN> setUsername(String username) {
        Objects.requireNonNull(username, "Connection username must not be null");
        this.username = username;
        return this;
    }

    public JMSQueueSinkBuilder<IN> setPassword(String password) {
        Objects.requireNonNull(password, "Connection password must not be null");
        this.password = password;
        return this;
    }

    public JMSQueueSinkBuilder<IN> setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
        return this;
    }

    public JMSQueueSinkBuilder<IN> setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public JMSQueueSinkBuilder<IN> setMessageIDExtractor(JMSMessageIDExtractor<IN> messageIDExtractor) {
        this.messageIDExtractor = messageIDExtractor;
        return this;
    }

    public JMSQueueSink<IN> build() {
        if(!validator())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " config is invalid!");
        return new JMSQueueSink<>(factory, queueName, username, password, completionListener, clientId, messageIDExtractor);
    }

    private boolean validator() {
        return factory != null && username != null && password != null && queueName != null;
    }
}
