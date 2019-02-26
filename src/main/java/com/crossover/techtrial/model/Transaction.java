package com.crossover.techtrial.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
public class Transaction implements Serializable {

    private static final long serialVersionUID = 8951221480021840448L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    Book book;

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    Member member;

    /**
     * Date and time of issuance of this book
     */
    @Column(name = "date_of_issue")
    LocalDateTime dateOfIssue;

    /**
     * Date and time of return of this book
     */
    @Column(name = "date_of_return")
    LocalDateTime dateOfReturn;

}
