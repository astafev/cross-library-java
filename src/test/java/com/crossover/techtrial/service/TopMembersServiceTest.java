package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.model.Transaction;
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
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class TopMembersServiceTest {

    // year 2033, we need to limit possible time somehow
    public static final long LIMIT_DATE = 2_000_000_000L;
    // something about 6 years
    public static final long MAX_DIFFERENCE = 200_000_000L;
    @Autowired
    private MemberService memberService;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TestRestTemplate template;

    @Before
    public void setup() {
        clean();
        for (int i = 0; i < 10; i++) {
            saveMember(i);
            saveBook(i);
        }

        List<Member> members = memberRepository.findAll();
        List<Book> books = new ArrayList<>(10);
        bookRepository.findAll().forEach(books::add);

        Random random = new Random();
        for (Member member : members) {
            for (Book book : books) {
                long start = random.nextLong() % LIMIT_DATE;
                saveTransaction(book, member,
                        LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(start + (Math.abs(random.nextLong()) % MAX_DIFFERENCE), 0, ZoneOffset.UTC));
            }
        }
    }


    @After
    public void clean() {
        transactionRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Test
    public void check() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            LocalDateTime start = LocalDateTime.ofEpochSecond(random.nextLong() % LIMIT_DATE, 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.ofEpochSecond(random.nextLong() % LIMIT_DATE, 0, ZoneOffset.UTC);
            log.info("getting top members for the time {} - {}", start, end);
            List<TopMemberDTO> topMemberDTOS = memberService.topDrivers(
                    start,
                    end);
            log.info("api returned {}", topMemberDTOS);
            List<TopMemberDTO> correctAnswer = getTheCorrectValue(start, end);
            log.info("java api returned {}", correctAnswer);
            assertEquals(topMemberDTOS, correctAnswer);
        }
    }

    private void saveTransaction(Book book, Member member, LocalDateTime start, LocalDateTime finish) {
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setMember(member);
        transaction.setDateOfIssue(start);
        transaction.setDateOfReturn(finish);
        transactionRepository.save(transaction);
    }

    private void saveMember(int num) {
        Member member = new Member();
        member.setName("Test Name " + num);
        member.setEmail("email@test" + num);
        memberRepository.save(member);
    }

    private void saveBook(int num) {
        Book book = new Book();
        book.setTitle("Test Title " + num);
        bookRepository.save(book);
    }

    private List<TopMemberDTO> getTheCorrectValue(LocalDateTime start, LocalDateTime finish) {
        List<Member> members = memberRepository.findAll();
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        Map<Member, Integer> membersTransactionCout = StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
                .filter(transaction -> {
                    if (transaction.getDateOfReturn() == null || transaction.getDateOfIssue() == null) {
                        return false;
                    }
                    if (transaction.getDateOfReturn().isAfter(finish) || transaction.getDateOfReturn().isBefore(start)) {
                        return false;
                    }
                    if (transaction.getDateOfIssue().isAfter(finish) || transaction.getDateOfIssue().isBefore(start)) {
                        return false;
                    }
                    return true;
                }).map(Transaction::getMember)
                .reduce(new HashMap<Member, Integer>(), (map, val) -> {
                    map.put(val, map.getOrDefault(val, 0) + 1);
                    return map;
                }, (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                });

        List<TopMemberDTO> collect = membersTransactionCout.entrySet().stream().map(entry -> {
            Member member = entry.getKey();
            return new TopMemberDTO(member.getId(), member.getName(), member.getEmail(), entry.getValue());
        })
                .sorted(Comparator.comparing(TopMemberDTO::getEmail))
                .sorted(Comparator.comparing(TopMemberDTO::getName))
                .sorted(Comparator.comparing(TopMemberDTO::getBookCount, Comparator.reverseOrder()))
                .limit(5).collect(Collectors.toList());

        return collect;
    }

}
