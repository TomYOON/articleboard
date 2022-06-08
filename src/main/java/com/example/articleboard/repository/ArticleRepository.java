package com.example.articleboard.repository;

import com.example.articleboard.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleRepository {

    private final EntityManager em;

    public void save(Article article) {
        em.persist(article);
    }

    public Article findOne(Long id) {
        return em.find(Article.class, id);
    }

    public List<Article> findAllWithMemberAndComment(int offset, int limit) {
        return em.createQuery("select a from Article a" +
                        " join fetch a.member m", Article.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Article> findMemberArticles(Long memberId, int offset, int limit) {
        return em.createQuery("select a from Article a" +
                        " join fetch a.member m where m.id = :memberId", Article.class)
                .setParameter("memberId", memberId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public void deleteArticle(Article article) {
        em.remove(article);
    }
}
