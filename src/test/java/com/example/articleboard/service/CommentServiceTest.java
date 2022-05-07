package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Comment;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.CommentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
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
        em.persist(member);
        Article article = Article.createArticle(member, "제목", "내용");
        em.persist(article);

        String content = "댓글 내용";

        //when
        Long commentId = commentService.write(member.getId(), article.getId(), null, null, content);
        Comment comment = commentRepository.findOne(commentId);

        //then
        assertEquals(member, comment.getMember());
        assertEquals(article, comment.getArticle());
        assertEquals(content, comment.getContent());
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
        assertEquals("memberId로 조회한 댓글목록의 첫 번째는 최근에 작성한 댓글과 같아야 한다.", member1Comments.get(0), comment1);
        assertEquals("memberId로 조회한 댓글목록의 첫 번째는 최근에 작성한 댓글과 같아야 한다.", member2Comments.get(0), comment2);
    }



}