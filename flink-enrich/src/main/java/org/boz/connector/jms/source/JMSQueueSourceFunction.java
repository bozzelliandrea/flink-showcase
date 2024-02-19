package org.boz.connector.jms.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.Serializable;

public class JMSQueueSourceFunction<OUT extends Serializable> implements SourceFunction<OUT> {

    private final static Logger LOGGER = LoggerFactory.getLogger(JMSQueueSourceFunction.class);

    private boolean isRunning = false;

    @Override
    public void run(SourceContext<OUT> sourceContext) throws Exception {

        while (isRunning) {

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
