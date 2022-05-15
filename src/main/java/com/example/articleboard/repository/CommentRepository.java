package com.example.articleboard.repository;

import com.example.articleboard.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public void save(Comment comment) {
        em.persist(comment);
    }

    public Comment findOne(Long id) {
        return em.find(Comment.class, id);
    }

    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c",Comment.class).getResultList();
    }

    public List<Comment> findByMemberId(long memberId, int offset, int limit) {
        return em.createQuery("select c from Comment c" +
                            " join fetch c.member m" +
                            " where m.id = :memberId", Comment.class)
                .setParameter("memberId", memberId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }
}
