package com.example.articleboard.dto.member;

import com.example.articleboard.domain.Member;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MemberDto {
    @NonNull
    private Long memberId;
    @NonNull
    private String username;
    private String nick;
    @Nullable
    private String imgUrl;

    public MemberDto(Member member) {
        memberId = member.getId();
        username = member.getUsername();
        nick = member.getNick();
        imgUrl = member.getImgUrl();
    }


    @Builder
    public MemberDto(@NonNull Long memberId, @NonNull String username, @NonNull String nick, @Nullable String imgUrl) {
        this.memberId = memberId;
        this.username = username;
        this.nick = nick;
        this.imgUrl = imgUrl;
    }
}
