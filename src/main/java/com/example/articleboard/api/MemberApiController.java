package com.example.articleboard.api;

import com.example.articleboard.domain.Member;
import com.example.articleboard.dto.member.MemberDto;
import com.example.articleboard.env.UriConfig;
import com.example.articleboard.dto.JoinDto;
import com.example.articleboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UriConfig.Member.BASE)
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/")
    @ResponseBody
    public List<MemberDto> members() {

        List<Member> allMembers = memberService.findALlMembers();
        return allMembers.stream()
                .map(m -> new MemberDto(m))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    public MemberDto saveMember(@RequestBody @Valid JoinDto form) {
        Long id = memberService.join(Member.createMember(form.getUsername(), form.getPassword(), form.getNick(), null));
        return MemberDto.builder().username(form.getUsername()).memberId(id).build();
    }

    @GetMapping(UriConfig.Member.PROFILE)
    @ResponseBody
    public MemberDto getProfile(@PathVariable("memberId") Long memberId) {
        Member member = memberService.findMember(memberId);
        return new MemberDto(member);
    }
}
