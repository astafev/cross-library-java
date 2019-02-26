package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.MemberRepository;
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
public class MemberControllerTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TestRestTemplate template;

    private Member member;

    @Before
    public void setup() throws Exception {
        member = new Member();
        member.setName("Test Name");
        member.setEmail("abc@mail");
        memberRepository.save(member);
    }

    @After
    public void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    public void testMemberRegsitrationsuccessful() {
        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", getHttpEntity(
                        "{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\"," +
                                "\"membershipStatus\": \"ACTIVE\"," +
                                "\"membershipStartDate\":\"2018-08-08T12:12:12\" }"),
                Member.class);

        Assert.assertEquals("test 1", response.getBody().getName());
        Assert.assertEquals(200, response.getStatusCode().value());

        //cleanup the user
        memberRepository.deleteById(response.getBody().getId());
    }

    @Test
    public void testMemberRegsitrationErrorOnNotAlphabetic() {
        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", getHttpEntity(
                        "{\"name\": \" test 1\", \"email\": \"test10000000000001@gmail.com\"," +
                                "\"membershipStatus\": \"ACTIVE\"," +
                                "\"membershipStartDate\":\"2018-08-08T12:12:12\" }"),
                Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testMemberRegsitrationErrorOnTooShort() {
        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", getHttpEntity(
                        "{\"name\": \"a\", \"email\": \"test10000000000001@gmail.com\"," +
                                "\"membershipStatus\": \"ACTIVE\"," +
                                "\"membershipStartDate\":\"2018-08-08T12:12:12\" }"),
                Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testMemberRegsitrationErrorOnTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("a");
        }
        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", getHttpEntity(
                        "{\"name\": \"" + sb.toString() + "\", \"email\": \"test10000000000001@gmail.com\"," +
                                "\"membershipStatus\": \"ACTIVE\"," +
                                "\"membershipStartDate\":\"2018-08-08T12:12:12\" }"),
                Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testGetAll() throws Exception {
        ResponseEntity<List<Member>> response = template.exchange("/api/member/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Member>>() {
                });

        List<Member> members = response.getBody();
        Assert.assertEquals(Collections.singletonList(member), members);
    }

    @Test
    public void testGetMemberById() {
        ResponseEntity<Member> response = template.exchange("/api/member/" + member.getId(), HttpMethod.GET, null,
                new ParameterizedTypeReference<Member>() {
                });

        Assert.assertEquals(member, response.getBody());
    }

    @Test
    public void testGetMemberWrongId() {
        ResponseEntity<Member> response = template.exchange("/api/member/-500", HttpMethod.GET, null,
                new ParameterizedTypeReference<Member>() {
                });

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }

}
