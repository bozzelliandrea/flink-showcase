package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionManager transactionManager;

    @PostMapping
    public ResponseEntity<TransactionManager.Transaction> post(@RequestBody TransactionManager.Transaction request) throws JsonProcessingException {
        return ResponseEntity.ok(transactionManager.send(request, "API"));
    }
}
