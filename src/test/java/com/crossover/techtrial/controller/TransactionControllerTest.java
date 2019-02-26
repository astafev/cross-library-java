package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.model.Transaction;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.MemberRepository;
import com.crossover.techtrial.repositories.TransactionRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author kshah
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TestRestTemplate template;

    private Member member;
    private Book book;

    @Before
    public void setup() {
        member = new Member();
        member.setName("Test Name");
        member.setEmail("abc@mail");
        memberRepository.save(member);

        book = new Book();
        book.setTitle("The Tale of TransactionControllerTest");
        bookRepository.save(book);
    }

    @After
    public void clean() {
        transactionRepository.deleteAll();
        memberRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    public void testIssueABook() {
        LocalDateTime before = LocalDateTime.now();
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", getHttpEntity("{\"bookId\":" + book.getId() + "," +
                        "\"memberId\":" + member.getId() + "}"),
                Transaction.class);
        assertEquals(response.toString(), 200, response.getStatusCode().value());
        Transaction savedTransaction = response.getBody();
        assertEquals(book, savedTransaction.getBook());
        assertEquals(member, savedTransaction.getMember());

        assertTrue(!savedTransaction.getDateOfIssue().isBefore(before));
        assertTrue(!savedTransaction.getDateOfIssue().isAfter(LocalDateTime.now()));

        Assert.assertNull(savedTransaction.getDateOfReturn());
    }

    @Test
    public void testCanNotBeIssuedTwice() {
        HttpEntity<Object> request = getHttpEntity("{\"bookId\":" + book.getId() + "," +
                "\"memberId\":" + member.getId() + "}");
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", request,
                Transaction.class);
        assertEquals(response.toString(), 200, response.getStatusCode().value());
        response = template.postForEntity(
                "/api/transaction", request,
                Transaction.class);
        assertEquals(response.toString(), HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCanNotBeIssuedNotExistingBook() {
        HttpEntity<Object> request = getHttpEntity("{\"bookId\":500," +
                "\"memberId\":" + member.getId() + "}");
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", request,
                Transaction.class);
        assertEquals(response.toString(), HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testReturnABook() {
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", getHttpEntity("{\"bookId\":" + book.getId() + "," +
                        "\"memberId\":" + member.getId() + "}"),
                Transaction.class);
        Transaction savedTransaction = response.getBody();
        LocalDateTime before = LocalDateTime.now();
        ResponseEntity<Transaction> patch = patch("/api/transaction/" + savedTransaction.getId() + "/return");
        Transaction returnedTransaction = patch
                .getBody();

        assertTrue(!returnedTransaction.getDateOfReturn().isBefore(before));
        assertTrue(!returnedTransaction.getDateOfReturn().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testCanNotBeReturnedTwice() {
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", getHttpEntity("{\"bookId\":" + book.getId() + "," +
                        "\"memberId\":" + member.getId() + "}"),
                Transaction.class);
        Transaction savedTransaction = response.getBody();
        patch("/api/transaction/" + savedTransaction.getId() + "/return");
        ResponseEntity<Transaction> patch = patch("/api/transaction/" + savedTransaction.getId() + "/return");
        assertEquals(HttpStatus.FORBIDDEN, patch.getStatusCode());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private ResponseEntity<Transaction> patch(String url) {
        return template.exchange(
                url,
                HttpMethod.PATCH,
                null,
                Transaction.class);
    }
}
