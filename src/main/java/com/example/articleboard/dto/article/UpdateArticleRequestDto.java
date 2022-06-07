package com.example.articleboard.dto.article;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UpdateArticleRequestDto {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
}