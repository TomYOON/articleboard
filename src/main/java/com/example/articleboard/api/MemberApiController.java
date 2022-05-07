package com.example.articleboard.api;

import com.example.articleboard.domain.Member;
import com.example.articleboard.dto.MemberDto;
import com.example.articleboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/members")
    public List<MemberDto> members() {

        List<Member> allMembers = memberService.findALlMembers();
        return allMembers.stream()
                .map(m -> new MemberDto(m))
                .collect(Collectors.toList());
    }

    @PostMapping("api/member")
    public Long saveMember(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return id;
    }
}
