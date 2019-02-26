/**
 *
 */
package com.crossover.techtrial.repositories;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Person repository for basic operations on Person entity.
 *
 * @author crossover
 */
@RestResource(exported = false)
public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {
    Optional<Member> findById(Long id);

    List<Member> findAll();

    @Query("SELECT new com.crossover.techtrial.dto.TopMemberDTO(m.id, m.name, m.email, count(t.id)) from Transaction t JOIN t.member m " +
            " WHERE t.dateOfIssue >= :startTime AND t.dateOfReturn <= :endTime" +
            " GROUP BY m" +
            " ORDER BY COUNT(t.id) DESC, m.name, m.email ")
    List<TopMemberDTO> getRidesForDates(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        Pageable pageRequest);
}
