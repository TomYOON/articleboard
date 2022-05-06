package com.example.articleboard.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String nick;
    private String imgUrl;

//    @OneToMany(mappedBy = "member")
//    private List<Article> articles = new ArrayList<>();
//
//    @OneToMany(mappedBy = "writer")
//    private List<Comment> comments = new ArrayList<>();
}
