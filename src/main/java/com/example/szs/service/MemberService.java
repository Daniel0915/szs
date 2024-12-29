//package com.example.szs.service;
//
//import com.example.szs.domain.embedded.Time;
//import com.example.szs.domain.member.Member;
//import com.example.szs.exception.CustomException;
//import com.example.szs.model.dto.MemberReq;
//import com.example.szs.model.eNum.ResStatus;
//import com.example.szs.repository.member.MemberRepository;
//import com.example.szs.utils.encryption.EncryptDecryptUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//@Slf4j
//public class MemberService {
//    private final MemberRepository memberRepository;
//
//    @Transactional
//    public void join(MemberReq memberReq) throws Exception {
//        validateDuplicateMember(memberReq.getUserId());
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String password = passwordEncoder.encode(memberReq.getPassword());
//        String regNo = EncryptDecryptUtils.encrypt(memberReq.getRegNo());
//
//        memberRepository.save(Member.builder()
//                                    .userId(memberReq.getUserId())
//                                    .password(password)
//                                    .name(memberReq.getName())
//                                    .regNo(regNo)
//                                    .time(new Time())
//                                    .build());
//    }
//
//    private void validateDuplicateMember(String userId) throws Exception {
//        if (memberRepository.findByUserId(userId).isPresent()) {
//            throw new CustomException(ResStatus.JOIN_DUPLICATION_ERROR);
//        }
//    }
//}
