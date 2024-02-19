package org.boz.connector.jms.source;

import javax.jms.Message;
import java.io.Serializable;

@FunctionalInterface
public interface JMSDeserializer<OUT> extends Serializable {

    OUT deserialize(Message message);
}
