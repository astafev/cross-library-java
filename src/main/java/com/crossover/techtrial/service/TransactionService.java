package com.crossover.techtrial.service;

import com.crossover.techtrial.exceptions.NotAllowedException;
import com.crossover.techtrial.exceptions.NotFoundException;
import com.crossover.techtrial.model.Transaction;

public interface TransactionService {

    /**
     * Issues the book to the member.
     * <p>
     * If book id or member id is not present in the db, throws {@link NotFoundException}.
     * If the member has 5 or more books on him, {@link NotAllowedException} is thrown.
     * If the member tries to issue a book which is already in use, {@link NotAllowedException} is thrown.
     */
    Transaction issueBook(Long bookId, Long memberId) throws NotAllowedException, NotFoundException;

    Transaction returnBook(Long transactionId);
}
