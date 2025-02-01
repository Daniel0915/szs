package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import com.example.szs.model.dto.LargeHoldingsDetailDTO;
import com.example.szs.utils.jpa.Param;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QLargeHoldingsDetailEntity.largeHoldingsDetailEntity;

@Repository
@Slf4j
public class LargeHoldingsDetailRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final LargeHoldingsDetailRepository largeHoldingsDetailRepository;

    public LargeHoldingsDetailRepositoryCustom(EntityManager em, LargeHoldingsDetailRepository largeHoldingsDetailRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.largeHoldingsDetailRepository = largeHoldingsDetailRepository;
    }

    public void saveLargeHoldingsDetail(List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (CollectionUtils.isEmpty(largeHoldingsDetailDTOList)) {
            return;
        }
        Map<Boolean, List<LargeHoldingsDetailDTO>> partitioned = largeHoldingsDetailDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        // ############# update ############# [start]
        List<LargeHoldingsDetailDTO> updateDTOList = partitioned.get(false);

        if (!CollectionUtils.isEmpty(updateDTOList)) {
            List<Long> seqList = updateDTOList.stream().map(LargeHoldingsDetailDTO::getSeq).toList();

            Map<Long, LargeHoldingsDetailEntity> findUpdateEntityMap = queryFactory.selectFrom(largeHoldingsDetailEntity)
                                                                                   .where(largeHoldingsDetailEntity.seq.in(seqList))
                                                                                   .fetch()
                                                                                   .stream()
                                                                                   .collect(Collectors.toMap(LargeHoldingsDetailEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue));

            List<LargeHoldingsDetailEntity> updateEntityList = new ArrayList<>();

            for (LargeHoldingsDetailDTO dto : updateDTOList) {
                LargeHoldingsDetailEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

                if (findUpdateEntity == null) {
                    continue;
                }

                Optional<LargeHoldingsDetailEntity> optaional = Param.getSaveEntity(dto, findUpdateEntity);
                optaional.ifPresent(updateEntityList::add);
            }

            largeHoldingsDetailRepository.saveAll(updateEntityList);
        }
        // ############# update ############# [end]

        // ############# insert ############# [start]
        List<LargeHoldingsDetailDTO> insertDTOList = partitioned.get(true);
        List<LargeHoldingsDetailEntity> insertEntityList = new ArrayList<>();
        for (LargeHoldingsDetailDTO dto : insertDTOList) {
            Optional<LargeHoldingsDetailEntity> optaional = Param.getSaveEntity(dto, new LargeHoldingsDetailEntity());
            optaional.ifPresent(insertEntityList::add);
        }

        largeHoldingsDetailRepository.saveAll(insertEntityList);
        // ############# insert ############# [start]
    }
}
