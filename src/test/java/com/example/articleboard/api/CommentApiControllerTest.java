package com.example.articleboard.api;

import com.example.articleboard.domain.Member;
import com.example.articleboard.dto.LoginDto;
import com.example.articleboard.security.JwtUtils;
import com.example.articleboard.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentApiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private static MemberService memberService;
    @Autowired
    private static ObjectMapper objectMapper;
    @Autowired
    private static MockMvc mvc;
    @Autowired
    private static JwtUtils jwtUtils;

    private static final String BASE_URL = "/api/comments";
    private static String adminAccessToken;
    private static String userAccessToken;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {


        Member user = Member.createMember("member1", "123456", "일반 회원", null);
        Member admin = Member.createMember("admin2", "123456", "관리자", null);
        admin.setRoleToAdmin();
        memberService.join(user);
        memberService.join(admin);

        userAccessToken = getAccessTokenWithLogin(user.getUsername(), "123456");
        adminAccessToken = getAccessTokenWithLogin(admin.getUsername(), "123456");

    }
    @BeforeEach
    public void setUpBeforeEach() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("댓글 등록 성공")
    public void 댓글_등록() throws Exception {
        //given
        String content = "댓글 내용입니다.";

        //when

        //then

    }

    public static String getAccessTokenWithLogin(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new LoginDto(username, password));
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JwtUtils.ACCESS_TOKEN_KEY))
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie(JwtUtils.ACCESS_TOKEN_KEY);
        return cookie.getValue();
    }

}