package com.example.articleboard.dto.comment;

import com.example.articleboard.domain.Comment;
import lombok.Data;

@Data
public class CommentDto {
    private String memberNick;
    private String articleSubject;
    private String tagMemberNick;
    private String content;
    private Boolean isDeleted;

    public CommentDto(Comment comment) {
        memberNick = comment.getMember().getNick();
        content = comment.getContent();
        articleSubject = comment.getArticle().getSubject();
        isDeleted = comment.getIsDeleted();

        if (comment.getTagMember() != null) {
            tagMemberNick = comment.getTagMember().getNick();
        }
    }
}
