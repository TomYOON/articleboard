package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Comment;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    @Test
    public void 댓글작성() throws Exception {
        //given
        Member member = Member.createMember("testEmail", "1234", "ts", null);
        Article article = Article.createArticle(member, "제목", "내용");

        em.persist(member);
        em.persist(article);

        String content = "댓글 내용";

        //when
        Long commentId = commentService.write(member.getId(), article.getId(), null, null, content);
        Comment comment = commentRepository.findOne(commentId);

        //then
        assertEquals(member, comment.getMember());
        assertEquals(article, comment.getArticle());
        assertEquals(content, comment.getContent());
        assertTrue(comment.getArticle().getComments().contains(comment));
    }

    @Test
    public void 멤버_댓글조회() throws Exception {
        //given
        Member member1 = Member.createMember("testEmail", "1234", "first", null);
        Member member2 = Member.createMember("testEmail2", "1234", "second", null);
        Article article1 = Article.createArticle(member1, "member1의 작성글", "member1의 작성내용");
        Article article2 = Article.createArticle(member2, "member2의 작성글", "member2의 작성내용");

        em.persist(member1);
        em.persist(member2);
        em.persist(article1);
        em.persist(article2);

        String content1 = "댓글 내용";
        String content2 = "댓글 내용2";

        //when
        Long commentId1 = commentService.write(member1.getId(), article1.getId(), null, null, content1);
        Long commentId2 = commentService.write(member2.getId(), article2.getId(), null, null, content2);

        Comment comment1 = commentRepository.findOne(commentId1);
        Comment comment2 = commentRepository.findOne(commentId2);
        List<Comment> member1Comments = commentService.findCommentByMemberId(member1.getId(), 0, 10);
        List<Comment> member2Comments = commentService.findCommentByMemberId(member2.getId(), 0, 10);

        //then
        assertEquals(member1Comments.get(0), comment1);
        assertEquals(member2Comments.get(0), comment2);
    }

    @Test()
    @DisplayName("대댓글이나 대대댓글이 없는 댓글만 삭제할 수 있다.")
    public void 댓글_삭제() throws Exception {
        //given
        Member member = Member.createMember("testEmail", "1234", "ts", null);
        Article article = Article.createArticle(member, "제목", "내용");

        em.persist(member);
        em.persist(article);

        String content1 = "자식이 있는 댓글 내용";
        String content2 = "멤버가 테그된 댓글 내용";
        String content3 = "삭제 가능한 댓글 내용";

        //when
        Long commentId1 = commentService.write(member.getId(), article.getId(), null, null, content1);
        Comment comment1 = commentRepository.findOne(commentId1);
        Long commentId2 = commentService.write(member.getId(), article.getId(), commentId1, null, content2);
        Comment comment2 = commentRepository.findOne(commentId2);
        Long commentId3 = commentService.write(member.getId(), article.getId(), comment2.getParentComment().getId(), comment2.getMember().getId(), content3);


        commentService.deleteComment(member.getId(), commentId1);
        commentService.deleteComment(member.getId(), commentId2);
        commentService.deleteComment(member.getId(), commentId3);

        Comment comment3 = commentRepository.findOne(commentId3);
        //then
        assertEquals(null, comment1.getContent());
        assertEquals(null, comment2.getContent());
        assertEquals(null, comment3);
    }



}