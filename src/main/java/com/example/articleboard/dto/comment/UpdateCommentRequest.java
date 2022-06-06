package com.example.articleboard.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateCommentRequest {
    @NotEmpty
    private String content;
}