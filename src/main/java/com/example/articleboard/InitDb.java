package com.example.articleboard;

import com.example.articleboard.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.createAdmin();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        final private EntityManager em;

        public void createAdmin() {
            Member admin = Member.createMember("admin", "admin", "admin", null);
			admin.setRoleToAdmin();
            em.persist(admin);
        }
    }
}
