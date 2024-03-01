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
    public void scheduleFixedDelayTask() throws JsonProcessingException {
        transactionManager.send(new TransactionManager
                .Transaction(faker.name().username(), faker.number().randomDigit()),
                "SCHEDULED_JOB");
    }

}
