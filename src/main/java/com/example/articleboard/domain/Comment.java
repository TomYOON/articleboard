package com.example.articleboard.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Member tagMember;

    private String content;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    //== 연관관계 메서드 ==//
    private void setMember(Member member) {
        this.member = member;
    }

    private void setArticle(Article article) {
        this.article = article;
        article.getComments().add(this);
    }

    private void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        parentComment.getChildComments().add(this);
    }

    private void setTagMember(Member tagMember) {
        this.tagMember = tagMember;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //== 생성 메서드 ==//
    public static Comment createComment(Member member, Article article, Comment parentComment, Member tagMember, String content) {
        Comment comment = new Comment();
        comment.setMember(member);
        comment.setArticle(article);
        comment.setContent(content);

        if (parentComment != null) {
            comment.setParentComment(parentComment);
        } else {
            comment.setParentComment(comment);
        }

        if (tagMember != null) {
            comment.setTagMember(tagMember);
        }

        return comment;
    }

}
