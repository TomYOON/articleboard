package com.example.articleboard.dto.comment;

import lombok.Data;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Getter
public class CreateCommentRequest {
    @NotNull
    private Long memberId;
    @NotNull
    private Long articleId;
    @Nullable
    private Long parentId;
    @Nullable
    private Long tagMemberId;
    @NotEmpty
    private String content;
}