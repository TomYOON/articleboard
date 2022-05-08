package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import junit.extensions.ActiveTestSuite;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void 게시글_등록() throws Exception {
        //given
        Member member = Member.createMember("Email@email.com", "12345", "hi", null);
        em.persist(member);

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
}