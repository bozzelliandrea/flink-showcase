package org.boz.job;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.boz.config.IBMQueue;
import org.boz.connector.jms.source.JMSQueueSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MqToFileSystem implements JobDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqToFileSystem.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_hhmmss");

    public static void build(StreamExecutionEnvironment environment) throws JMSException {
        new MqToFileSystem().setup(environment);
    }

    @Override
    public void setup(StreamExecutionEnvironment environment) throws JMSException {
        environment
                .addSource(
                        JMSQueueSourceBuilder.<String>builder()
                                .setFactory(IBMQueue.connectionFactory())
                                .setQueueName("DEV.QUEUE.1")
                                .setDeserializer(message -> {
                                    try {
                                        return message.getBody(String.class);
                                    } catch (JMSException e) {
                                        LOGGER.error("Deserialization failed!", e);
                                        throw new RuntimeException(e);
                                    }
                                })
                                .build(),
                        "JMSQueueSource"
                )
                .returns(String.class)
                .writeAsText("file:///" + System.getenv("HOME")
                        + "/Downloads/transactions_processed" + formatter.format(new Date()) + ".jsonl",
                        FileSystem.WriteMode.OVERWRITE
                )
                .name("WriteToFileSink")
                .uid(UUID.randomUUID().toString());
    }
}
