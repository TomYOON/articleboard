package com.example.articleboard.api;

import com.example.articleboard.domain.Member;
import com.example.articleboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/members")
    public List<Member> members() {
        return memberService.findALlMembers();
    }

    @PostMapping("api/member")
    public Long saveMember(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return id;
    }
}
