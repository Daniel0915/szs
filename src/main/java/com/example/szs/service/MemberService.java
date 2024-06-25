package com.example.szs.service;

import com.example.szs.domain.member.Member;
import com.example.szs.model.dto.MemberReq;
import com.example.szs.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public String join(MemberReq memberReq) {
        validateDuplicateMember(memberReq.getUserId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(memberReq.getPassword());

        Member member = memberRepository.save(Member.builder()
                                                    .userId(memberReq.getUserId())
                                                    .password(password)
                                                    .name(memberReq.getName())
                                                    .regNo(memberReq.getRegNo())
                                                    .build());
        return member.getUserId();
    }

    // TODO :
    //  1. spring security 에서 filter 사용하여 토큰 발행
    //  2. 로그인 filter accessToken 확인
    //  3. url 접근 권한 부여
    //  4. JWT 정보에 member_seq 저장
    public String login(String userId, String password) {
        String accessToken = "";
        loginCheck(userId, password);
        // TODO : token 발행 코드 작성
        accessToken = "121212";
        return accessToken;
    }

    private void validateDuplicateMember(String userId) {
        // TODO : 예외 상황별로 custom exception 필요
        if (memberRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private void loginCheck(String userId, String password) {
        // TODO : 예외 상황별로 custom exception 필요
        if (!memberRepository.findByUserIdAndPassword(userId, password).isPresent()) {
            throw new IllegalStateException("로그인 정보가 틀렸습니다.");
        }
    }
}
