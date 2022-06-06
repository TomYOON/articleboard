package com.example.articleboard.dto.article;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
public class CreateArticleRequestDto {
    @NotNull
    private Long memberId;
    @NotEmpty
    private String subject;
    private String content;

    public CreateArticleRequestDto(Long memberId, String subject, String content) {
        this.memberId = memberId;
        this.subject = subject;
        this.content = content;
    }
}