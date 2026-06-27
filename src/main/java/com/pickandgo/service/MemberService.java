package com.pickandgo.service;

import com.pickandgo.domain.Member;
import com.pickandgo.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member register(String name, String address, String email, String password, String bank) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다: " + email);
        }
        Member member = new Member(name, address, email, password, bank);
        return memberRepository.save(member);
    }

    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }
}
