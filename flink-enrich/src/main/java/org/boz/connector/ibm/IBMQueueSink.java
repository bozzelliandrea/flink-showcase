package org.boz.connector.ibm;

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Hashtable;

public class IBMQueueSink<IN extends Serializable> extends RichSinkFunction<IN> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IBMQueueSink.class);

    private MQQueueManager mqQueueManager;
    private MQQueue queue;

    @Override
    public void open(Configuration parameters) throws Exception {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(WMQConstants.WMQ_HOST_NAME, "localhost");
        //properties.put(WMQConstants.WMQ_HOST_NAME, "ibm-mq");
        properties.put(WMQConstants.WMQ_PORT, "1414");
        properties.put(WMQConstants.WMQ_CHANNEL, "DEV.ADMIN.SVRCONN");
        properties.put(WMQConstants.USERID, "admin");
        properties.put(WMQConstants.PASSWORD, "password");

        mqQueueManager = new MQQueueManager("MANAGER", properties);
        queue = mqQueueManager.accessQueue("DEV.QUEUE.1", MQC.MQOO_OUTPUT);

    }

    @Override
    public void close() throws Exception {
        if (queue.isOpen())
            queue.close();
        if (mqQueueManager.isOpen())
            mqQueueManager.close();
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {
        try {
            MQMessage mqMsg = new MQMessage();
            mqMsg.writeObject(value);

            queue.put(mqMsg, new MQPutMessageOptions());
        } catch (MQException e) {
            LOGGER.error("Error sending message to [{}]: {}", "QUEUE", e.getLocalizedMessage());
            throw e;
        }
    }
}
