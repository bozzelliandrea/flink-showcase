package org.boz.job;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.boz.config.IBMQueue;
import org.boz.config.Kafka;
import org.boz.connector.jms.sink.JMSQueueSinkBuilder;
import org.boz.function.EnrichTransaction;
import org.boz.function.MapTransactionToJson;

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
                .setParallelism(1)
                .map(new EnrichTransaction())
                .name("EnrichTransaction")
                .uid(UUID.randomUUID().toString())
                .map(new MapTransactionToJson())
                .name("MapTransactionToJson")
                .uid(UUID.randomUUID().toString())
                .addSink(
                        JMSQueueSinkBuilder.<String>builder()
                                .setUsername("admin")
                                .setPassword("password")
                                .setFactory(IBMQueue.connectionFactory())
                                .setQueueName("DEV.QUEUE.1") //TODO: capire errore se queue specificata
                                .build()
                )
                .name("JMSQueueSink");
    }
}
