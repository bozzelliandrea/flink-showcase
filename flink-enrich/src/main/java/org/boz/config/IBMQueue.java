package org.boz.config;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;

import javax.jms.JMSException;

public class IBMQueue {

    public static MQQueueConnectionFactory connectionFactory() throws JMSException {
        MQQueueConnectionFactory ibmFactory = new MQQueueConnectionFactory();
        ibmFactory.setHostName("localhost");
        ibmFactory.setPort(1414);
        ibmFactory.setChannel("DEV.ADMIN.SVRCONN");
        ibmFactory.setQueueManager("MANAGER");
        ibmFactory.setObjectProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        ibmFactory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
        return ibmFactory;
    }
}
