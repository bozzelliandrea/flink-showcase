package org.boz.function;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.boz.model.Transaction;

public class MapTransactionToJson implements MapFunction<Transaction, String> {

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    @Override
    public String map(Transaction transaction) throws Exception {
        return jacksonMapper.writeValueAsString(transaction);
    }
}
