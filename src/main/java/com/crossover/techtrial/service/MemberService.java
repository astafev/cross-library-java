/**
 *
 */
package com.crossover.techtrial.service;

import com.crossover.techtrial.model.Member;

import java.util.List;

/**
 * RideService for rides.
 *
 * @author crossover
 */
public interface MemberService {

    public Member save(Member member);

    public Member findById(Long memberId);

    public List<Member> findAll();

}
