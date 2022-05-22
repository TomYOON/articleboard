package com.example.articleboard.service;

import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateUsername(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateUsername(Member member) {
        List<Member> findMember = memberRepository.findByUsername(member.getUsername());

        if (!findMember.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findALlMembers() {
        return memberRepository.findAll();
    }
}
