package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class DemoApplication {

    private static final Faker faker = Faker.instance();

    @Autowired
    private TransactionManager transactionManager;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        LoggerFactory.getLogger(DemoApplication.class).info("Server Ready at: {}", "http://localhost:8080/");
    }


    @Scheduled(fixedDelay = 10)
    public void scheduleFixedDelayTask_0() throws JsonProcessingException {
        transactionManager.send(new TransactionManager
                .Transaction(faker.name().username(), faker.number().randomDigit()),
                0,
                "SCHEDULED_JOB");
    }


    @Scheduled(fixedDelay = 10)
    public void scheduleFixedDelayTask_1() throws JsonProcessingException {
        transactionManager.send(new TransactionManager
                .Transaction(faker.name().username(), faker.number().randomDigit()),
                1,
                "SCHEDULED_JOB");
    }


    @Scheduled(fixedDelay = 10)
    public void scheduleFixedDelayTask_2() throws JsonProcessingException {
        transactionManager.send(new TransactionManager
                .Transaction(faker.name().username(), faker.number().randomDigit()),
                2,
                "SCHEDULED_JOB");
    }


    @Scheduled(fixedDelay = 10)
    public void scheduleFixedDelayTask_3() throws JsonProcessingException {
        transactionManager.send(new TransactionManager
                .Transaction(faker.name().username(), faker.number().randomDigit()),
                3,
                "SCHEDULED_JOB");
    }
}
