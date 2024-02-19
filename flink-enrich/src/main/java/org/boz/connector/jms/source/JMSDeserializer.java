package org.boz.connector.jms.source;

import javax.jms.Message;

@FunctionalInterface
public interface JMSDeserializer<OUT> {

    OUT deserialize(Message message);
}
