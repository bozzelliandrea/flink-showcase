/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.boz;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.boz.connector.jms.sink.JMSQueueSink;
import org.boz.connector.jms.sink.JMSQueueSinkBuilder;
import org.boz.connector.jms.source.JMSQueueSourceBuilder;
import org.boz.function.EnrichTransaction;
import org.boz.function.MapTransactionToJson;

import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.UUID;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        // Sets up the execution environment, which is the main entry point
        // to building Flink applications.
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_hhmmss");

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:29092")
                .setTopics("TRANSACTION_REGISTER")
                .setGroupId("my-group")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .build();


        Properties kafkaProperties = new Properties(2);
        kafkaProperties.setProperty("bootstrap.servers", "localhost:29092");
        kafkaProperties.setProperty("group.id", "my-group");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("TRANSACTION_REGISTER",
                new SimpleStringSchema(),
                kafkaProperties);


        MQQueueConnectionFactory ibmFactory = new MQQueueConnectionFactory();
        ibmFactory.setHostName("localhost");
        ibmFactory.setPort(1414);
        ibmFactory.setChannel("DEV.ADMIN.SVRCONN");
        ibmFactory.setQueueManager("MANAGER");
        ibmFactory.setObjectProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        ibmFactory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);


        JMSQueueSink<String> sink = JMSQueueSinkBuilder.<String>builder()
                //.setFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"))
                .setFactory(ibmFactory)
                .setQueueName("DEV.QUEUE.1")
                .build();

        env.addSource(consumer).addSink(sink);


        env.fromSource(source, WatermarkStrategy.noWatermarks(), "KafkaSource")
                .setParallelism(1)
                .map(new EnrichTransaction())
                .uid(UUID.randomUUID().toString())
                .map(new MapTransactionToJson())
                .uid(UUID.randomUUID().toString())
                .addSink(sink)
                .name("MqSink");
                /*
                .writeAsText("file:///" + System.getenv("HOME")
                        + "/Downloads/transactions_processed"
                        + formatter.format(new Date())
                        + ".jsonl", FileSystem.WriteMode.OVERWRITE);
                 */

        // Execute program, beginning computation.
        env.execute("Flink Transaction Enrich");
    }
}
