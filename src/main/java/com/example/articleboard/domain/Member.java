package com.example.articleboard.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Member(String email, String password, String nick, String imgUrl) {
        this.email = email;
        this.password = password;
        this.nick = nick;
        this.imgUrl = imgUrl;
    }
    //== 생성 메서드 ==//
    public static Member createMember(String email, String password, String nick, String imgUrl) {
        return new Member(email, password, nick, imgUrl);
    }
}
