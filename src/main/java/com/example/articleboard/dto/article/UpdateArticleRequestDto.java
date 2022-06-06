package com.example.articleboard.dto.article;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateArticleRequestDto {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
}