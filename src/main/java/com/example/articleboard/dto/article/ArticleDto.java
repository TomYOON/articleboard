package com.example.articleboard.dto.article;

import com.example.articleboard.domain.Article;
import com.example.articleboard.dto.comment.CommentDto;
import com.example.articleboard.dto.member.MemberDto;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ArticleDto {
    @NonNull
    Long articleId;
    @NonNull
    private MemberDto memberDto;
    @NonNull
    private String subject;
    @NonNull
    private String content;

    private List<CommentDto> commentDtos;

    public ArticleDto(Article article) {
        articleId = article.getId();
        memberDto = new MemberDto(article.getMember());
        subject = article.getSubject();
        content = article.getContent();
        commentDtos = article.getComments().stream()
                .map(c -> new CommentDto(c))
                .collect(Collectors.toList());
    }
}
