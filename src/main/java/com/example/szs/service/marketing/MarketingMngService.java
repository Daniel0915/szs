package com.example.szs.service.marketing;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.marketing.MarketingMng;
import com.example.szs.model.dto.MarketingMngDetailDTO;
import com.example.szs.model.dto.MarketingMngDTO;
import com.example.szs.repository.marketing.MarketingMngRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MarketingMngService {
    private final MarketingMngRepository marketingMngRepository;

    @Transactional
    public void save(MarketingMngDTO marketingMngDTO) {
        String userToken = marketingMngDTO.getUserToken();
        Optional<MarketingMng> optionalMarketingMng = marketingMngRepository.getMarketingMngBy(userToken);

        // 업데이트
        if (optionalMarketingMng.isPresent()) {
            MarketingMng marketingMng = optionalMarketingMng.get();
            marketingMng.updateParent(marketingMngDTO.getTitle());

            List<MarketingMng> marketingMngList = marketingMngRepository.getMarketingMngByPSeq(marketingMng.getSeq());

            for (MarketingMngDetailDTO marketingMngAnswerImgReq : marketingMngDTO.getListContent()) {
                Integer page = marketingMngAnswerImgReq.getPage();
                Optional<MarketingMng> findMarketingMngChild = marketingMngList.stream().filter(a -> Objects.equals(a.getPage(), page)).findFirst();
                if (findMarketingMngChild.isPresent()) {
                    MarketingMng marketingMngChild = findMarketingMngChild.get();
                    marketingMngChild.updateChild(
                            marketingMngAnswerImgReq.getQuestion(),
                            marketingMngAnswerImgReq.getImg(),
                            marketingMngAnswerImgReq.getAnswer1(),
                            marketingMngAnswerImgReq.getAnswer1Score(),
                            marketingMngAnswerImgReq.getAnswer2(),
                            marketingMngAnswerImgReq.getAnswer2Score()
                    );
                } else {
                    // 새로운 insert
                    marketingMngRepository.save(MarketingMng.createMarketingMngChild(marketingMng.getSeq(), marketingMngAnswerImgReq));
                }
            }
            return;
        }

        Long seq = marketingMngRepository.save(MarketingMng.builder()
                                                           .title(marketingMngDTO.getTitle())
                                                           .userToken(marketingMngDTO.getUserToken())
                                                           .time(new Time())
                                                           .build()).getSeq();

        List<MarketingMng> marketingMngList = new ArrayList<>();
        for (MarketingMngDetailDTO marketingMngAnswerImgReq : marketingMngDTO.getListContent()) {
            marketingMngList.add(MarketingMng.createMarketingMngChild(seq, marketingMngAnswerImgReq));
        }

        marketingMngRepository.saveAll(marketingMngList);
    }

    public MarketingMngDTO getMarketingMng(int seq) {
        if (seq < 0) {
            return null;
        }

        Optional<MarketingMng> optionalMarketingMng = marketingMngRepository.getMarketingMngBy(seq);
        if (optionalMarketingMng.isEmpty()) {
            return null;
        }

        MarketingMng parentMarketingMngEntity = optionalMarketingMng.get();
        long pSeq = parentMarketingMngEntity.getSeq();
        List<MarketingMng> childMarketingMngEntity = marketingMngRepository.getMarketingMngByPSeq(pSeq);

        MarketingMngDTO marketingMngDTO = parentMarketingMngEntity.toDtoByParent();
        List<MarketingMngDetailDTO> marketingMngDetailDTOList = childMarketingMngEntity.stream()
                                                                                       .map(MarketingMng::toDtoByChild)
                                                                                       .toList();


        marketingMngDTO = marketingMngDTO.toBuilder().listContent(marketingMngDetailDTOList).build();
        return marketingMngDTO;
    }
}
