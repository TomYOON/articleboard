package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Comment;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ArticleServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentService commentService;
    private Member member;

    @BeforeEach
    public void initialize() {
        member = Member.createMember("Email@email.com", "12345", "hi", null);
        em.persist(member);
    }

    @Test
    public void 게시글_등록() throws Exception {
        //given
        String subject = "글 제목입니다.";
        String content = "글 내용입니다.";

        //when
        Long articleId = articleService.write(member.getId(), subject, content);
        Article article = articleRepository.findOne(articleId);

        //then
        assertEquals(member, article.getMember());
        assertEquals(subject, article.getSubject());
        assertEquals(content, article.getContent());
    }

    @Test
    @DisplayName("게시글 삭제 성공시 해당 게시글의 댓글들은 전부 deleted = true여야 한다.")
    public void 게시글_삭제() throws Exception {
        //given
        String subject = "글 제목입니다.";
        String content = "글 내용입니다.";

        Long articleId = articleService.write(member.getId(), subject, content);
        Long commentId1 = commentService.write(member.getId(), articleId, null, null, "첫 번째 댓글");
        Long commentId2 = commentService.write(member.getId(), articleId, null, null, "두 번째 댓글");
        Comment comment1 = em.find(Comment.class, commentId1);
        Comment comment2 = em.find(Comment.class, commentId2);

        //when
        articleService.deleteArticle(articleId);
        Article deletedArticle = articleRepository.findOne(articleId);

        //then
        assertEquals(null, deletedArticle);
        assertTrue(comment1.getIsDeleted());
        assertTrue(comment2.getIsDeleted());
    }

    @Test
    public void 게시글_수정() throws Exception {
        //given
        String subject = "글 제목입니다.";
        String content = "글 내용입니다.";
        String updatedSubject = "수정된 제목입니다.";
        String updatedContent = "수정된 내용입니다.";

        Long articleId = articleService.write(member.getId(), subject, content);
        Article article = articleRepository.findOne(articleId);

        //when
        articleService.updateArticle(articleId, member.getId(), updatedSubject, updatedContent);

        //then
        assertEquals(updatedSubject, article.getSubject());
        assertEquals(updatedContent, article.getContent());
    }

    @Test
    @DisplayName("게시글의 작성자가 아닌 회원이 수정할 경우 실패")
    public void 게시글_수정_실패() throws Exception {
        //given
        String subject = "글 제목입니다.";
        String content = "글 내용입니다.";
        String updatedSubject = "수정된 제목입니다.";
        String updatedContent = "수정된 내용입니다.";

        Long articleId = articleService.write(member.getId(), subject, content);
        Article article = articleRepository.findOne(articleId);

        //when then
        Assertions.assertThrows(ResourceAccessException.class, () -> {
            articleService.updateArticle(articleId, member.getId() + 1, updatedSubject, updatedContent);
        });
    }
}