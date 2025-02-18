package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsStkrtEntity;
import com.example.szs.model.dto.user.LargeHoldingsStkrtDTO;
import com.example.szs.utils.jpa.Param;
import com.example.szs.utils.time.TimeUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QLargeHoldingsStkrtEntity.largeHoldingsStkrtEntity;

@Repository
@Slf4j
public class LargeHoldingsStkrtRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final LargeHoldingsStkrtRepository largeHoldingsStkrtRepository;

    public LargeHoldingsStkrtRepositoryCustom(EntityManager em, LargeHoldingsStkrtRepository largeHoldingsStkrtRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.largeHoldingsStkrtRepository = largeHoldingsStkrtRepository;
    }

    public void saveLargeHoldingsStkrt(List<LargeHoldingsStkrtDTO> largeHoldingsStkrtDTOList) {
        if (CollectionUtils.isEmpty(largeHoldingsStkrtDTOList)) {
            return;
        }
        Map<Boolean, List<LargeHoldingsStkrtDTO>> partitioned = largeHoldingsStkrtDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        // ############# update ############# [start]
        List<LargeHoldingsStkrtDTO> updateDTOList = partitioned.get(false);

        if (!CollectionUtils.isEmpty(updateDTOList)) {
            List<Long> seqList = updateDTOList.stream().map(LargeHoldingsStkrtDTO::getSeq).toList();

            Map<Long, LargeHoldingsStkrtEntity> findUpdateEntityMap = queryFactory.selectFrom(largeHoldingsStkrtEntity)
                                                                                   .where(largeHoldingsStkrtEntity.seq.in(seqList))
                                                                                   .fetch()
                                                                                   .stream()
                                                                                   .collect(Collectors.toMap(LargeHoldingsStkrtEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue));

            List<LargeHoldingsStkrtEntity> updateEntityList = new ArrayList<>();

            for (LargeHoldingsStkrtDTO dto : updateDTOList) {
                LargeHoldingsStkrtEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

                if (findUpdateEntity == null) {
                    continue;
                }

                Optional<LargeHoldingsStkrtEntity.LargeHoldingsStkrtEntityBuilder> optaional = Param.getSaveEntityToBuilder(dto, findUpdateEntity, findUpdateEntity.toBuilder());
                optaional.ifPresent(value -> updateEntityList.add(value.build()));
            }

            largeHoldingsStkrtRepository.saveAll(updateEntityList);
        }
        // ############# update ############# [end]

        // ############# insert ############# [start]
        List<LargeHoldingsStkrtDTO> insertDTOList = partitioned.get(true);
        List<LargeHoldingsStkrtEntity> insertEntityList = new ArrayList<>();

        for (LargeHoldingsStkrtDTO dto : insertDTOList) {
            Optional<LargeHoldingsStkrtEntity.LargeHoldingsStkrtEntityBuilder> optaional = Param.getSaveEntityToBuilder(dto, new LargeHoldingsStkrtEntity(), new LargeHoldingsStkrtEntity().toBuilder());
            optaional.ifPresent(value -> insertEntityList.add(value.build()
                                                                   .toBuilder()
                                                                   .regDt(TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                                   .build()));
        }

        largeHoldingsStkrtRepository.saveAll(insertEntityList);
        // ############# insert ############# [end]
    }
}
