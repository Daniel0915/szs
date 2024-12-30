package com.example.szs.repository.stock;

import com.example.szs.domain.stock.ExecOwnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecOwnershipRepository extends JpaRepository<ExecOwnershipEntity, Long> {
}
