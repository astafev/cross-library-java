package com.crossover.techtrial.repositories;

import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestResource(exported = false)
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByBookId(long bookId);

    @Query("SELECT COUNT(t) from Transaction t WHERE t.dateOfReturn is null and t.member = ?1")
    int countCurrentTransactionsByMember(Member member);
}
