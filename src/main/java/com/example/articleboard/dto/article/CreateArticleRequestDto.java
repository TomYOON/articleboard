package com.example.articleboard.dto.article;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateArticleRequestDto {
    @NotNull
    private Long memberId;
    @NotEmpty
    private String subject;
    private String content;
}