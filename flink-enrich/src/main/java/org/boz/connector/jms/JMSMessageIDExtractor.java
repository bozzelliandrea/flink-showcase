package org.boz.connector.jms;

import java.io.Serializable;

@FunctionalInterface
public interface JMSMessageIDExtractor<T> extends Serializable {

    String getKey(T entry);
}
