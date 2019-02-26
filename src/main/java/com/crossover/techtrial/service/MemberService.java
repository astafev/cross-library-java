package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Member;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RideService for rides.
 *
 * @author crossover
 */
public interface MemberService {

    Member save(Member member);

    Member findById(Long memberId);

    List<Member> findAll();

    List<TopMemberDTO> topDrivers(LocalDateTime startTime, LocalDateTime endTime);

}
