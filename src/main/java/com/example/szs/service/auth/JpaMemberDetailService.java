package com.example.szs.service.auth;


import com.example.szs.domain.member.Member;
import com.example.szs.model.auth.AuthMember;
import com.example.szs.repository.member.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class JpaMemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthMember user = memberRepository
                .findByUserId(username)
                .map(AuthMember::new)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found"));
        return user;
    }
}
