package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.repositories.BookRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

/**
 * @author kshah
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TestRestTemplate template;

    private Book book;

    @Before
    public void setup() throws Exception {
        book = new Book();
        book.setTitle("The Tale of Book Controller Test");
        bookRepository.save(book);
    }

    @After
    public void tearDown() {
        bookRepository.deleteAll();
    }
    
    @Test
    public void testSaveBook() {
        Book book2Save = new Book();
        book2Save.setTitle("My Fancy New Book");
        ResponseEntity<Book> response = template.postForEntity(
                "/api/book", book2Save,
                Book.class);

        Assert.assertEquals(response.toString(), 200, response.getStatusCode().value());
        Book savedBook = response.getBody();
        Assert.assertEquals(book2Save.getTitle(), savedBook.getTitle());

        ResponseEntity<Book> getResponse = template.getForEntity(
                "/api/book/" + savedBook.getId(), Book.class);
        Assert.assertEquals(getResponse.getBody(), savedBook);
    }

    @Test
    public void testGetBooks() {
        ResponseEntity<List<Book>> response = template.exchange("/api/book/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Book>>() {
                });


        List<Book> books = response.getBody();
        Assert.assertEquals(Collections.singletonList(book), books);
    }


    @Test
    public void testGetById() {
        ResponseEntity<Book> response = template.exchange("/api/book/" + book.getId(), HttpMethod.GET, null,
                new ParameterizedTypeReference<Book>() {
                });

        Assert.assertEquals(book, response.getBody());
    }

    @Test
    public void testGetWrongId() {
        ResponseEntity<Book> response = template.exchange("/api/book/-500", HttpMethod.GET, null,
                new ParameterizedTypeReference<Book>() {
                });

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
