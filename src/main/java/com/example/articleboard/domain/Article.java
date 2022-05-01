package com.example.articleboard.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Article {

    @Id
    @Column(name = "article_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_code")
    private Member member;

    private String subject;
    private String content;
}
