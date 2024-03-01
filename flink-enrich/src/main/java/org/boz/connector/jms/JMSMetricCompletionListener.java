package org.boz.connector.jms;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.CompletionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.Serializable;

public class JMSMetricCompletionListener implements CompletionListener, Serializable {

    public static final String METRIC_COUNTER_SUCCESS = "successCounter";
    public static final String METRIC_COUNTER_ERROR = "errorCounter";

    private static final long serialVersionUID = 5123078687130322971L;

    private final transient Logger logger;
    private final transient Counter successCounter;
    private final transient Counter errorCounter;
    private final transient boolean logKey;

    private transient boolean enableLog = true;

    public JMSMetricCompletionListener(final RuntimeContext runtimeContext,
                                       final boolean logKey) {
        this(runtimeContext.getMetricGroup().counter(METRIC_COUNTER_SUCCESS),
                runtimeContext.getMetricGroup().counter(METRIC_COUNTER_ERROR),
                logKey);
    }

    public JMSMetricCompletionListener(final Logger logger,
                                       final RuntimeContext runtimeContext,
                                       final boolean logKey) {
        this(logger,
                runtimeContext.getMetricGroup().counter(METRIC_COUNTER_SUCCESS),
                runtimeContext.getMetricGroup().counter(METRIC_COUNTER_ERROR),
                logKey);
    }

    public JMSMetricCompletionListener(final Logger logger,
                                       final Counter successCounter,
                                       final Counter errorCounter,
                                       final boolean logKey) {
        this.logger = logger;
        this.successCounter = successCounter;
        this.errorCounter = errorCounter;
        this.logKey = logKey;
    }

    public JMSMetricCompletionListener(final Counter successCounter,
                                       final Counter errorCounter,
                                       final boolean logKey) {
        this.logger = LoggerFactory.getLogger(JMSMetricCompletionListener.class);
        this.successCounter = successCounter;
        this.errorCounter = errorCounter;
        this.logKey = logKey;
    }

    @Override
    public void onCompletion(Message message) {
        if (logKey && enableLog) {
            try {
                logger.debug("Message {} sent", message.getJMSMessageID());
            } catch (JMSException e) {
                logger.warn("Logger was turned off due to jmsException");
                enableLog = false;
            }
        }

        successCounter.inc();
    }

    @Override
    public void onException(Message message, Exception e) {
        if (logKey && enableLog) {
            try {
                logger.error("Message error for id " + message.getJMSMessageID(), e);
            } catch (JMSException jmsException) {
                logger.warn("Logger was turned off due to jmsException");
                enableLog = false;
            }
        } else {
            logger.error("Error sending message ", e);
        }

        errorCounter.inc();
    }
}
