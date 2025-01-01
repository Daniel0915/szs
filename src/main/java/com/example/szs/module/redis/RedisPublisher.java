package com.example.szs.module.redis;

import com.example.szs.model.dto.LargeHoldingsDTO;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.PubType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {
    private final RedisTemplate<String, Object> template;

    /**
     * Object publish
     */
    public void publish(ChannelTopic topic, MessageDto dto) {
        template.convertAndSend(topic.getTopic(), dto);
    }

    /**
     * String publish
     */
    public void publish(ChannelTopic topic , String data) {
        template.convertAndSend(topic.getTopic(), data);
    }


    // TODO : 요청 변수 Puh 객체 만들어서 매개변수 사용하기
    public void pub(PubType pubType, List<LargeHoldingsDTO> largeHoldingsDTOList) {

        switch (pubType) {
            case STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS:
            case STOCK_CHANGE_EXECOWNERSHIP:


                break;
            default:
                log.info("not match pub type");
                break;
        }
    }

    private void pubStockChangeNotifyLargeHoldings(List<LargeHoldingsDTO> largeHoldingsDTOList) {
        if (CollectionUtils.isEmpty(largeHoldingsDTOList)) {
            return;
        }





    }

}
