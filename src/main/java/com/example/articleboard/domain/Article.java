package com.example.articleboard.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Article {

    @Id
    @Column(name = "article_id")
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String subject;
    private String content;

    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();

    //== 생성 메서드 ==//
    public static Article createArticle(Member member, String subject, String content) {
        Article article = new Article();
        article.setMember(member);
        article.setSubject(subject);
        article.setContent(content);

        return article;
    }
}
