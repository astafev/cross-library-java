package com.crossover.techtrial.service;

import com.crossover.techtrial.exceptions.NotFoundException;
import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public List<Book> getAll() {
        List<Book> personList = new ArrayList<>();
        bookRepository.findAll().forEach(personList::add);
        return personList;

    }

    public Book save(Book p) {
        return bookRepository.save(p);
    }

    @Override
    public Book findById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("can't find book with id " + bookId));
    }

}
