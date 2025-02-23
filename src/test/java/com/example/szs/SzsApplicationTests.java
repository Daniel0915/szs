package com.example.szs;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.repository.stock.LargeHoldingsDetailRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
class SzsApplicationTests {

    @Autowired
    EntityManager em;

    @Autowired
    LargeHoldingsDetailRepositoryCustom largeHoldingsDetailRepositoryCustom;

    @Test
    void contextLoads() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        LargeHoldingsDetailDTO dto1 = LargeHoldingsDetailDTO.builder()
                                                                 .seq(1L)
                                                                 .rceptNo("123456")
                                                                 .corpCode(987654L)
                                                                 .corpName("Example Corp")
                                                                 .largeHoldingsName("John Doe")
                                                                 .birthDateOrBizRegNum("19900101")
                                                                 .tradeDt("20250101")
                                                                 .tradeReason("Acquisition")
                                                                 .stockType("Preferred")
                                                                 .beforeStockAmount(1000L)
                                                                 .changeStockAmount(100L)
                                                                 .afterStockAmount(1100L)
                                                                 .unitStockPrice(5000L)
                                                                 .currencyType("KRW")
                                                                 .totalStockPrice(5500000L)
                                                                 .build();

        LargeHoldingsDetailDTO dto2 = LargeHoldingsDetailDTO.builder()
                                                            .seq(0L)
                                                            .rceptNo("49303301L")
                                                            .corpCode(987654L)
                                                            .corpName("Example Corp")
                                                            .largeHoldingsName("John Doe")
                                                            .birthDateOrBizRegNum("19900101")
                                                            .tradeDt("20250101")
                                                            .tradeReason("Acquisition")
                                                            .stockType("Preferred")
                                                            .beforeStockAmount(1000L)
                                                            .changeStockAmount(100L)
                                                            .afterStockAmount(1100L)
                                                            .unitStockPrice(5000L)
                                                            .currencyType("KRW")
                                                            .totalStockPrice(5500000L)
                                                            .build();

        List<LargeHoldingsDetailDTO> list = new ArrayList<>(Arrays.asList(dto1, dto2));
        largeHoldingsDetailRepositoryCustom.saveLargeHoldingsDetail(list);
    }

}
