package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Transaction;
import com.crossover.techtrial.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author kshah
 */
@RestController
@RequiredArgsConstructor
public class TransactionController {

    @Autowired
    private final TransactionService service;

    /*
     * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
     * Example Post Request :  { "bookId":1,"memberId":33 }
     */
    @PostMapping(path = "/api/transaction")
    public ResponseEntity<Transaction> issueBookToMember(@RequestBody Map<String, Long> params) {
        Long bookId = params.get("bookId");
        Long memberId = params.get("memberId");
        return ResponseEntity.ok().body(service.issueBook(bookId, memberId));
    }

    /*
     * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @PatchMapping(path = "/api/transaction/{transaction-id}/return")
    public ResponseEntity<Transaction> returnBookTransaction(@PathVariable(name = "transaction-id") Long transactionId) {
        Transaction transaction = service.returnBook(transactionId);
        return ResponseEntity.ok(transaction);
    }

}
