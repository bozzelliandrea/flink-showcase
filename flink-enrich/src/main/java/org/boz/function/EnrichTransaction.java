package org.boz.function;

import com.github.javafaker.Faker;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.boz.model.Transaction;

import java.util.Date;


public class EnrichTransaction implements MapFunction<String, Transaction> {

    private final static Faker faker = new Faker();

    @Override
    public Transaction map(String message) throws Exception {

        System.out.println("Input transaction: " + message);

        Transaction transaction = new ObjectMapper().readValue(message, Transaction.class);
        transaction.setReceiver(faker.name().fullName());
        transaction.setValid(faker.bool().bool());
        transaction.setHasError(faker.bool().bool());
        transaction.setEnrichedDate(new Date());

        System.out.println("Output transaction: " + transaction);
        return transaction;
    }
}
