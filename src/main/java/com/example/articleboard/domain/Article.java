package com.example.articleboard.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
public class Article {

    @Id
    @Column(name = "article_id")
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String subject;
    private String content;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //== 변경 메서드 ==//
    public void updateArticle(String subject, String content) {
        setSubject(subject);
        setContent(content);
    }

    //== 생성 메서드 ==//
    public static Article createArticle(Member member, String subject, String content) {
        Article article = new Article();
        article.setMember(member);
        article.setSubject(subject);
        article.setContent(content);

        return article;
    }
}
