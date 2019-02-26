package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.exceptions.NotFoundException;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    public static final int TOP_DRIVERS_LIMIT = 5;
    @Autowired
    MemberRepository memberRepository;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("can't find member with id " + memberId));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public List<TopMemberDTO> topDrivers(LocalDateTime startTime, LocalDateTime endTime) {
        List<TopMemberDTO> ridesForDates = memberRepository.getRidesForDates(startTime, endTime,
                PageRequest.of(0, 5));
        return ridesForDates;
    }

}
