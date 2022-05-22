package com.example.articleboard.repository;

import com.example.articleboard.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByUsername(String username) {
        List<Member> members = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username).getResultList();
        if (members.isEmpty()) {
            return null;
        }
        return members.get(0);

    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
