package com.example.articleboard;

import com.example.articleboard.domain.Member;
import com.example.articleboard.service.ArticleService;
import com.example.articleboard.service.MemberService;
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
        Long adminId = initService.createAdmin();
        Long articleId = initService.createAdminArticle();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        final private MemberService memberService;
        final private ArticleService articleService;

        public Long createAdmin() {
            Member admin = Member.createMember("admin", "admin", "admin", null);
			admin.setRoleToAdmin();
            Long id = memberService.join(admin);

            return id;
        }

        public Long createAdminArticle() {
            String subject = "공지 사항";
            String content = "공지 사항";

            Long id = articleService.write("admin", subject, content);

            return id;
        }
    }
}
