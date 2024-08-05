package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.example.demo.KafkaConfig.TOPIC_NAME;

@Component
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Transaction send(@NonNull Transaction request, Integer partition, @NonNull String source) throws JsonProcessingException {
        logger.info("Request received: {}", request.toString());

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, partition, request.getId(), mapper.writeValueAsString(request));
        record.headers().add("Source", source.getBytes(StandardCharsets.UTF_8));

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("Request fail: {}", ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("Request sent with success: {}", result);
            }
        });
        request.sent();
        return request;
    }

    public static class Transaction implements Serializable {
        private String username;
        private Integer total;
        private Boolean sent;
        private final String id = UUID.randomUUID().toString();

        public Transaction() {
        }

        public Transaction(String username, Integer total) {
            this.username = username;
            this.total = total;
        }

        public void sent() {
            this.sent = true;
        }

        public String getId() {
            return this.id;
        }

        public Boolean getSent() {
            return sent;
        }

        public void setSent(Boolean sent) {
            this.sent = sent;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "username='" + username + '\'' +
                    ", total=" + total +
                    ", sent=" + sent +
                    '}';
        }
    }

}


