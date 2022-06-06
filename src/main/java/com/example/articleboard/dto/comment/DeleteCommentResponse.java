package com.example.articleboard.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteCommentResponse {
    private Long commentId;
}