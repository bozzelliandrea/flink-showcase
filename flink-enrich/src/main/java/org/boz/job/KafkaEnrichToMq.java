package org.boz.job;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.boz.config.IBMQueue;
import org.boz.config.Kafka;
import org.boz.connector.jms.sink.JMSQueueSinkBuilder;
import org.boz.function.EnrichTransaction;
import org.boz.model.Transaction;

import javax.jms.JMSException;
import java.util.UUID;

public class KafkaEnrichToMq implements JobDefinition {

    @Override
    public void setup(StreamExecutionEnvironment environment) throws JMSException {
        environment
                .addSource(
                        Kafka.sourceFunction("TRANSACTION_REGISTER", new SimpleStringSchema()),
                        "KafkaSourceFunction"
                )
                .map(new EnrichTransaction())
                .name("EnrichTransaction")
                .uid(UUID.randomUUID().toString())
                .addSink(
                        JMSQueueSinkBuilder.<Transaction>builder()
                                .setUsername("admin")
                                .setPassword("password")
                                .setFactory(IBMQueue.connectionFactory())
                                .setQueueName("DEV.QUEUE.1")
                                .setMessageIDExtractor(Transaction::getUuid)
                                .build()
                )
                .name("JMSQueueSink");
    }
}
