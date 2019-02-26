package com.crossover.techtrial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopMemberDTO {

    private Long memberId;
    private String name;
    private String email;
    private Integer bookCount;

    public TopMemberDTO(Long memberId, String name, String email, Long bookCount) {
        this(memberId, name, email, bookCount.intValue());
    }
}
