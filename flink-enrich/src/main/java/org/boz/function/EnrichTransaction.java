package org.boz.function;

import com.github.javafaker.Faker;
import org.apache.flink.api.common.functions.MapFunction;
import org.boz.model.Transaction;

import java.util.Date;


public class EnrichTransaction implements MapFunction<Transaction, Transaction> {

    private final static Faker faker = new Faker();

    @Override
    public Transaction map(Transaction transaction) throws Exception {
        System.out.println("Input transaction: " + transaction);
        transaction.setReceiver(faker.name().fullName());
        transaction.setValid(faker.bool().bool());
        transaction.setHasError(faker.bool().bool());
        transaction.setEnrichedDate(new Date());
        System.out.println("Output transaction: " + transaction);
        return transaction;
    }
}
