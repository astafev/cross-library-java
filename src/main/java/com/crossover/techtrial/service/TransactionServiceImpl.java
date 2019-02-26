package com.crossover.techtrial.service;

import com.crossover.techtrial.exceptions.NotAllowedException;
import com.crossover.techtrial.exceptions.NotFoundException;
import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.model.Transaction;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.MemberRepository;
import com.crossover.techtrial.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final MemberRepository memberRepository;

    @Override
    public Transaction issueBook(Long bookId, Long memberId) {
        if (transactionRepository.findTransactionByBookId(bookId).isPresent()) {
            //Member is not allowed to issue a book which is already issued
            // to someone and should return HTTP Status code 403.
            throw new NotAllowedException("the book " + bookId + " is already issued");
        }
        // Member trying to issue a book which does not exist in our database, API should return HTTP Status code 404.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("can't find book with the id " + bookId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("can't find member with the id " + memberId));
        if (transactionRepository.countCurrentTransactionsByMember(member) >= 5) {
            throw new NotAllowedException("You can't take more than 5 books");
        }

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setMember(member);
        transaction.setDateOfIssue(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction returnBook(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("can't find transaction with the id"));
        if (transaction.getDateOfReturn() != null) {
            // After returning the book and completing the transaction by updating date of return,
            // Any subsequent request to return for the same transaction-id should return HTTP Status Code 403.
            // Valid value of Date Of Return field means books are returned.
            throw new NotAllowedException("The transaction is already completed");
        }
        transaction.setDateOfReturn(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}
