package org.boz.job;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.boz.config.IBMQueue;
import org.boz.connector.jms.source.JMSQueueSourceBuilder;
import org.boz.function.MapTransactionToJson;
import org.boz.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MqToFileSystem implements JobDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqToFileSystem.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_hhmmss");

    @Override
    public void setup(StreamExecutionEnvironment environment) throws JMSException {
        environment
                .addSource(
                        JMSQueueSourceBuilder.<Transaction>builder()
                                .setFactory(IBMQueue.connectionFactory())
                                .setUsername("admin")
                                .setPassword("password")
                                .setQueueName("DEV.QUEUE.1")
                                .setDeserializer(message -> {
                                    try {
                                        return message.getBody(Transaction.class);
                                    } catch (JMSException e) {
                                        LOGGER.error("Deserialization failed!", e);
                                        throw new RuntimeException(e);
                                    }
                                })
                                .build(),
                        "JMSQueueSource"
                )
                .returns(Transaction.class)
                .map(new MapTransactionToJson())
                .name("MapTransactionToJson")
                .uid(UUID.randomUUID().toString())
                .writeAsText("file:///" + System.getenv("HOME")
                                + "/Downloads/flink_output/transactions_processed" + formatter.format(new Date()) + ".jsonl",
                        FileSystem.WriteMode.OVERWRITE
                )
                .name("WriteToFileSink")
                .uid(UUID.randomUUID().toString());
    }
}
