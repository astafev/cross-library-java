package com.crossover.techtrial.service;

import com.crossover.techtrial.exceptions.NotAllowedException;
import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.MemberRepository;
import com.crossover.techtrial.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TransactionServiceImplTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TestRestTemplate template;

    private Member member;

    @Before
    public void setup() {
        clean();
        for (int i = 0; i < 10; i++) {
            saveBook(i);
        }

        member = new Member();
        member.setName("Test Name ");
        member.setEmail("email@test");
        memberRepository.save(member);
    }


    @After
    public void clean() {
        transactionRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void canNotIssueMoreThan5Books() {
        Iterator<Book> books = bookRepository.findAll().iterator();

        for (int i = 0; i < 5; i++) {
            transactionService.issueBook(books.next().getId(), member.getId());
        }
        // up to that point everything should be ok
        try {
            transactionService.issueBook(books.next().getId(), member.getId());
            throw new AssertionError("should have been thrown Not allowed exception");
        } catch (NotAllowedException e) {
            // expected
        }
    }


    private void saveMember(int num) {

    }

    private void saveBook(int num) {
        Book book = new Book();
        book.setTitle("Test Title " + num);
        bookRepository.save(book);
    }
}
