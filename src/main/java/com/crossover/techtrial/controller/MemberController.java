package com.crossover.techtrial.controller;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    /*
     * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @PostMapping(path = "/api/member")
    public ResponseEntity<?> register(@RequestBody Member p) {
        // names should be between the length of 2 and 100 and should always start with an alphabet
        if (p.getName() != null) {
            int length = p.getName().length();
            if (length < 2 || length > 100) {
                return ResponseEntity.status(400).body(Collections.singletonMap("error",
                        "Please use name between 2 and 100 characters"));
            }
            if (!Character.isAlphabetic(p.getName().codePointAt(0))) {
                return ResponseEntity.status(400).body(Collections.singletonMap("error",
                        "The name should start from alphabetic character"));
            }
        }
        return ResponseEntity.ok(memberService.save(p));
    }

    /*
     * PLEASE DO NOT CHANGE API SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @GetMapping(path = "/api/member")
    public ResponseEntity<List<Member>> getAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    /*
     * PLEASE DO NOT CHANGE API SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @GetMapping(path = "/api/member/{member-id}")
    public ResponseEntity<Member> getMemberById(@PathVariable(name = "member-id", required = true) Long memberId) {
        return ResponseEntity.ok(memberService.findById(memberId));
    }


    /**
     * This API returns the top 5 members who issued the most books within the search duration.
     * Only books that have dateOfIssue and dateOfReturn within the mentioned duration should be counted.
     * Any issued book where dateOfIssue or dateOfReturn is outside the search, should not be considered.
     * <p>
     * DONT CHANGE METHOD SIGNATURE AND RETURN TYPES
     *
     * @return
     */
    @GetMapping(path = "/api/member/top-member")
    public ResponseEntity<List<TopMemberDTO>> getTopMembers(
            @RequestParam(value = "startTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
        return ResponseEntity.ok(memberService.topDrivers(startTime, endTime));

    }

}
