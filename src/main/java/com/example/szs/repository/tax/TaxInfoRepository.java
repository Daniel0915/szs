package com.example.szs.repository.tax;

import com.example.szs.domain.member.Member;
import com.example.szs.domain.tax.TaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaxInfoRepository extends JpaRepository<TaxInfo, Long> {
    Optional<TaxInfo> findByMember(Member member);
    Optional<TaxInfo> findByMemberAndYear(Member member, int year);
}
