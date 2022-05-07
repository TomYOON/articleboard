package com.example.articleboard.dto;

import com.example.articleboard.domain.Member;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
public class MemberDto {
    @NonNull
    private Long memberId;
    @NonNull
    private String email;
    @NonNull
    private String nick;
    @Nullable
    private String imgUrl;

    public MemberDto(Member member) {
        memberId = member.getId();
        email = member.getEmail();
        nick = member.getNick();
        imgUrl = member.getImgUrl();
    }
}
