package com.example.szs.repository.stock;

import com.example.szs.domain.stock.ExecOwnershipDetailEntity;
import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.utils.jpa.ListDivider;
import com.example.szs.utils.jpa.Param;
import com.example.szs.utils.time.TimeUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QExecOwnershipDetailEntity.execOwnershipDetailEntity;

@Repository
@Slf4j
public class ExecOwnershipDetailRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final ExecOwnershipDetailRepository execOwnershipDetailRepository;

    public ExecOwnershipDetailRepositoryCustom(EntityManager em, ExecOwnershipDetailRepository execOwnershipDetailRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.execOwnershipDetailRepository = execOwnershipDetailRepository;
    }

    public void saveAll(List<ExecOwnershipDetailDTO> execOwnershipDetailDTOList) {
        if (CollectionUtils.isEmpty(execOwnershipDetailDTOList)) {
            return;
        }

        Map<Boolean, List<ExecOwnershipDetailDTO>> partitioned = execOwnershipDetailDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        update(partitioned.get(false));
        insert(partitioned.get(true));
    }

    private void insert(List<ExecOwnershipDetailDTO> insertDTOList) {
        if (CollectionUtils.isEmpty(insertDTOList)) {
            return;
        }

        List<ExecOwnershipDetailEntity> insertEntityList = new ArrayList<>();

        for (ExecOwnershipDetailDTO dto : insertDTOList) {
            Optional<ExecOwnershipDetailEntity.ExecOwnershipDetailEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, new ExecOwnershipDetailEntity(), new ExecOwnershipDetailEntity().toBuilder());
            optional.ifPresent(value -> insertEntityList.add(value.build()
                                                                  .toBuilder()
                                                                  .regDt(TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                                  .build()));
        }

        execOwnershipDetailRepository.saveAll(insertEntityList);
    }

    private void update(List<ExecOwnershipDetailDTO> updateDTOList) {
        if (CollectionUtils.isEmpty(updateDTOList)) {
            return;
        }

        List<Long> distinctSeqList = updateDTOList.stream()
                                                  .map(ExecOwnershipDetailDTO::getSeq)
                                                  .collect(Collectors.toSet())
                                                  .stream()
                                                  .collect(Collectors.toList());

        Map<Long, ExecOwnershipDetailEntity> findUpdateEntityMap = new HashMap<>();

        for (List<Long> seqList : ListDivider.getDivisionList(distinctSeqList, 300)) {
            findUpdateEntityMap.putAll(queryFactory.selectFrom(execOwnershipDetailEntity)
                                                   .where(execOwnershipDetailEntity.seq.in(seqList))
                                                   .fetch()
                                                   .stream()
                                                   .collect(Collectors.toMap(ExecOwnershipDetailEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue)));

        }

        List<ExecOwnershipDetailEntity> updateEntityList = new ArrayList<>();

        for (ExecOwnershipDetailDTO dto : updateDTOList) {
            ExecOwnershipDetailEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

            if (findUpdateEntity == null) {
                continue;
            }

            Optional<ExecOwnershipDetailEntity.ExecOwnershipDetailEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, findUpdateEntity, findUpdateEntity.toBuilder());
            optional.ifPresent(value -> updateEntityList.add(value.build()));
        }

        execOwnershipDetailRepository.saveAll(updateEntityList);
    }
}
