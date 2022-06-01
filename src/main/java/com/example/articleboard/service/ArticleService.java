package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long write(String username, String subject, String content) {

        Member member = memberRepository.findByUsername(username);
        Article article = Article.createArticle(member, subject, content);

        articleRepository.save(article);
        return article.getId();
    }

    public List<Article> findArticles(int offset, int limit) {
        return articleRepository.findAllWithMemberAndComment(offset,limit);
    }

    public List<Article> findMemberArticles(long memberId, int offset, int limit) {
        return articleRepository.findMemberArticles(memberId, offset, limit);
    }

    public Article getArticleDetail(Long articleId) {
        return articleRepository.findOne(articleId);
    }

    @Transactional
    public Long updateArticle(Long articleId, String username, String subject, String content) throws ResourceAccessException {
        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return null;
        }
        if (!article.getMember().getUsername().equals(username)) {
            throw new ResourceAccessException("게시글의 작성자가 아닙니다.");
        }

        article.updateArticle(subject, content);
        return article.getId();
    }
}
