package com.example.articleboard.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String nick;
    private String imgUrl;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Member(String username, String password, String nick, String imgUrl) {
        this.username = username;
        this.password = password;
        this.nick = nick;
        this.imgUrl = imgUrl;
    }

    @PrePersist
    private void prePersist() {
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    //== 변경 메서드 ==//
    public void encryptPassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    public void setRoleToAdmin() {
        this.role = Role.ADMIN;
    }
    //== 생성 메서드 ==//
    public static Member createMember(String username, String password, String nick, String imgUrl) {
        return new Member(username, password, nick, imgUrl);
    }
}
