package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.MemberRepository;
import com.example.articleboard.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
    public Long updateArticle(Long articleId, Long memberId, String subject, String content) throws ResourceAccessException {
        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return null;
        }
        if (!article.getMember().getId().equals(memberId)) {
            throw new ResourceAccessException("게시글의 작성자가 아닙니다.");
        }

        article.updateArticle(subject, content);
        return article.getId();
    }

    @Transactional
    public Long deleteArticle(Long articleId, Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return null;
        }

        Member writer = article.getMember();

        if (!AuthUtils.hasAdminRole(authentication) && writer.getId() != memberId) {
            throw new ResourceAccessException("게시글의 작성자가 아닙니다.");
        }

        article.delete();
        articleRepository.deleteArticle(article);

        return articleId;
    }
}
