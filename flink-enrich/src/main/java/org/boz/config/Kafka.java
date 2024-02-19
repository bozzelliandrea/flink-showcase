package org.boz.config;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Properties;

public final class Kafka {

    private final static String host = "localhost:29092";
    private final static String groupId = "my-group";

    public static <T> KafkaSource<T> source(String topic, DeserializationSchema<T> schema) {
        return KafkaSource.<T>builder()
                .setBootstrapServers(host)
                .setTopics(topic)
                .setGroupId(groupId)
                .setValueOnlyDeserializer(schema)
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .build();
    }

    public static <T> FlinkKafkaConsumer<T> sourceFunction(String topic, DeserializationSchema<T> schema) {
        Properties kafkaProperties = new Properties(2);
        kafkaProperties.setProperty("bootstrap.servers", host);
        kafkaProperties.setProperty("group.id", groupId);
        return new FlinkKafkaConsumer<T>(topic,
                schema,
                kafkaProperties);

    }
}
