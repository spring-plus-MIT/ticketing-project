package com.example.ticketingproject.domain.castmember.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CastMemberRequest {

    @Length(min = 1, max = 50, message = "출연자명은 1자 이상 50자 이하만 가능합니다.")
    private String name;

    @Length(min = 1, max = 50, message = "출연자명은 1자 이상 50자 이하만 가능합니다.")
    private String roleName;
}