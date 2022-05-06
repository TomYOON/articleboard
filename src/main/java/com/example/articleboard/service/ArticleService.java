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

    public List<Article> findArticles(int offset, int limmit) {
        return articleRepository.findAllWithMemberAndComment(offset,limmit);
    }
}
