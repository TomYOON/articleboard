package com.example.articleboard.dto;

import com.example.articleboard.domain.Article;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ArticleDto {
    @NonNull
    private MemberDto memberDto;
    @NonNull
    private String subject;
    @NonNull
    private String content;

    private List<CommentDto> commentDtos;

    public ArticleDto(Article article) {
        memberDto = new MemberDto(article.getMember());
        subject = article.getSubject();
        content = article.getContent();
        commentDtos = article.getComments().stream()
                .map(c -> new CommentDto(c))
                .collect(Collectors.toList());
    }
}
