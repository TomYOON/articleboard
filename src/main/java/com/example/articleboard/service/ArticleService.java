package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long write(Long memberId, String subject, String content) {

        Member member = memberRepository.findOne(memberId);
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
    public Long updateArticle(Long articleId, String subject, String content) {
        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return null;
        }
        article.updateArticle(subject, content);
        return article.getId();
    }
}
