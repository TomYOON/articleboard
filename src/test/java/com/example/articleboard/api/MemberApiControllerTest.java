package com.example.articleboard.api;

import com.example.articleboard.domain.Member;
import com.example.articleboard.dto.LoginDto;
import com.example.articleboard.dto.JoinDto;
import com.example.articleboard.repository.MemberRepository;
import com.example.articleboard.security.JwtTokenUtils;
import com.example.articleboard.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class MemberApiControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
//    @Autowired
//    private WebTestClient webClient;

    private String BASE_URL = "/api/member";

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Member member = Member.createMember("testEmail", "123456", "ㅎㅇ", null);
        memberService.join(member);
    }

    @Test
    @DisplayName("회원가입이 정상적으로 되는 것과 비밀번호가 암호화 되어 저장되는지 확인한다.")
    public void 회원가입() throws Exception {
        //given
        String username = "test@test.com";
        String password = "123456";
        JoinDto form = JoinDto.builder()
                        .username(username)
                        .password(password)
                        .nick("테스트 유저").build();
        String body = mapper.writeValueAsString(form);

        //when then
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Member member = memberRepository.findByUsername(username);
        assertFalse(password.equals(member.getPassword()), "비밀번호가 암호화되어 저장된다.");
    }

    @Test
    @DisplayName("로그인 성공")
    public void 로그인() throws Exception {
        //given
        String username = "testEmail";
        String password = "123456";
        String body = mapper.writeValueAsString(new LoginDto(username, password));

        //when then
        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JwtTokenUtils.ACCESS_TOKEN_KEY));
    }
}