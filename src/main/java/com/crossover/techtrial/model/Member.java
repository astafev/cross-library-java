package com.crossover.techtrial.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Data
@EqualsAndHashCode(of = {"id", "email", "name"})
@ToString(of = {"id", "email", "name"})
public class Member implements Serializable {

    private static final long serialVersionUID = 9045098179799205444L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    @NotNull
    String email;

    @Enumerated(EnumType.STRING)
    MembershipStatus membershipStatus;

    @Column(name = "membership_start_date")
    LocalDateTime membershipStartDate;

}
