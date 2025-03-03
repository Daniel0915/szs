package com.example.szs.repository.stock;

import com.example.szs.domain.stock.CorpInfoEntity;
import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.example.szs.utils.jpa.Param;
import com.example.szs.utils.time.TimeUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QCorpInfoEntity.corpInfoEntity;

@Repository
@Slf4j
public class CorpInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final CorpInfoRepository corpInfoRepository;

    public CorpInfoRepositoryCustom(EntityManager em, CorpInfoRepository corpInfoRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.corpInfoRepository = corpInfoRepository;
    }

    public List<CorpInfoDTO> getAllCorpInfoDTOList() {
        return queryFactory.selectFrom(corpInfoEntity)
                           .fetch()
                           .stream()
                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, CorpInfoDTO.class)
                                                               .stream()).collect(Collectors.toList());

    }

    public void saveAll(List<CorpInfoDTO> corpInfoDTOList) {
        if (CollectionUtils.isEmpty(corpInfoDTOList)) {
            return;
        }

        List<CorpInfoEntity> insertOrUpdateEntityList = new ArrayList<>();

        for (CorpInfoDTO dto : corpInfoDTOList) {
            Optional<CorpInfoEntity.CorpInfoEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, new CorpInfoEntity(), new CorpInfoEntity().toBuilder());
            optional.ifPresent(value -> insertOrUpdateEntityList.add(value.build()
                                                                          .toBuilder()
                                                                          .regDt(TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                                          .build()));
        }

        corpInfoRepository.saveAll(insertOrUpdateEntityList);
    }
}
