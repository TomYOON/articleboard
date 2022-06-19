package com.example.articleboard.api;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Member;
import com.example.articleboard.dto.LoginDto;
import com.example.articleboard.dto.article.*;
import com.example.articleboard.env.UriConfig;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.MemberRepository;
import com.example.articleboard.security.JwtTokenUtils;
import com.example.articleboard.service.ArticleService;
import com.example.articleboard.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ArticleApiControllerTest {

    Logger log = (Logger) LoggerFactory.getLogger(ArticleApiController.class);
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;
    private String userAccessToken;
    private String adminAccessToken;

    private final String subject = "게시글 제목 입니다.";
    private final String content = "게시글 내용 입니다.";

    @BeforeEach
    public void setUpBeforeEach() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Member user = Member.createMember("user", "123456", "일반 회원", null);
        Member admin = Member.createMember("admin_user", "123456", "관리자", null);
        admin.setRoleToAdmin();

        memberService.join(user);
        memberService.join(admin);
        log.info("admin: {}", admin.getRole());

        userAccessToken = getAccessTokenWithLogin("user", "123456");
        adminAccessToken = getAccessTokenWithLogin("admin_user", "123456");
    }

    @Test
    @DisplayName("로그인한 사용자가 게시글을 작성에 성공한다.")
    public void 게시글_등록() throws Exception {
        //given
        Member member = memberRepository.findByUsername("user");
        String body = objectMapper.writeValueAsString(CreateArticleRequestDto.builder().memberId(member.getId()).subject(subject).content(content).build());
        Cookie cookie = new Cookie(JwtTokenUtils.ACCESS_TOKEN_KEY, userAccessToken);

        //when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(UriConfig.Article.BASE)
                        .content(body)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        //then
        CreateArticleResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreateArticleResponseDto.class);
        long articleId = responseDto.getArticleId();
        Article article = articleRepository.findOne(articleId);

        assertEquals(subject, article.getSubject());
        assertEquals(content, article.getContent());
    }

    @Test
    @DisplayName("사용자가 본인의 게시글을 수정한다.")
    public void 게시글_수정() throws Exception {
        //given
        String updatedSubject = "수정된 게시글 제목";
        String updatedContent = "수정된 게시글 내용";

        Member member = memberRepository.findByUsername("user");
        long articleId = articleService.write(member.getId(), subject, content);

        String body = objectMapper.writeValueAsString(UpdateArticleRequestDto.builder().subject(updatedSubject).content(updatedContent).build());
        Cookie cookie = new Cookie(JwtTokenUtils.ACCESS_TOKEN_KEY, userAccessToken);

        //when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(UriConfig.Article.BASE + UriConfig.Article.ARTICLE_ID, articleId)
                        .content(body)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        UpdateArticleResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UpdateArticleResponseDto.class);
        long updatedArticleId = responseDto.getArticleId();
        Article article = articleRepository.findOne(updatedArticleId);

        assertEquals(updatedSubject, article.getSubject());
        assertEquals(updatedContent, article.getContent());
    }

    @Test
    @DisplayName("사용자가 다른 사용자의 게시글을 수정하지 못한다.")
    public void 게시글_수정_권한_없음() throws Exception {
        //given
        String updatedSubject = "수정된 게시글 제목";
        String updatedContent = "수정된 게시글 내용";

        Member member = memberRepository.findByUsername("admin_user"); // admin으로 작성
        long articleId = articleService.write(member.getId(), subject, content);

        String body = objectMapper.writeValueAsString(UpdateArticleRequestDto.builder().subject(updatedSubject).content(updatedContent).build());
        Cookie cookie = new Cookie(JwtTokenUtils.ACCESS_TOKEN_KEY, userAccessToken);

        //when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(UriConfig.Article.BASE + UriConfig.Article.ARTICLE_ID, articleId)
                        .content(body)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        //then
        Article article = articleRepository.findOne(articleId);

        assertEquals(subject, article.getSubject());
        assertEquals(content, article.getContent());
    }

    @Test
    @DisplayName("작성자가 게시글을 삭제할 수 있다.")
    public void 게시글_삭제() throws Exception {
        //given
        Member member = memberRepository.findByUsername("user");
        long articleId = articleService.write(member.getId(), subject, content);

        Cookie cookie = new Cookie(JwtTokenUtils.ACCESS_TOKEN_KEY, userAccessToken);

        //when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(UriConfig.Article.BASE + UriConfig.Article.ARTICLE_ID, articleId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        DeleteArticleResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DeleteArticleResponseDto.class);

        assertEquals(articleId, responseDto.getArticleId());
        assertEquals(null, articleRepository.findOne(articleId));
    }

    @Test
    @DisplayName("관리자가 게시글을 삭제할 수 있다.")
    public void 게시글_삭제_관리자() throws Exception {
        //given
        Member member = memberRepository.findByUsername("user");
        long articleId = articleService.write(member.getId(), subject, content);
        Cookie cookie = new Cookie(JwtTokenUtils.ACCESS_TOKEN_KEY, adminAccessToken);

        //when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(UriConfig.Article.BASE + UriConfig.Article.ARTICLE_ID, articleId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        DeleteArticleResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DeleteArticleResponseDto.class);

        assertEquals(articleId, responseDto.getArticleId());
        assertEquals(null, articleRepository.findOne(articleId));
    }

    public String getAccessTokenWithLogin(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new LoginDto(username, password));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(UriConfig.LOGIN)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JwtTokenUtils.ACCESS_TOKEN_KEY))
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie(JwtTokenUtils.ACCESS_TOKEN_KEY);
        return cookie.getValue();
    }

}