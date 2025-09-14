package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLargeHoldingsDetail is a Querydsl query type for LargeHoldingsDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLargeHoldingsDetail extends EntityPathBase<LargeHoldingsDetail> {

    private static final long serialVersionUID = -1264844021L;

    public static final QLargeHoldingsDetail largeHoldingsDetail = new QLargeHoldingsDetail("largeHoldingsDetail");

    public final NumberPath<Long> afterStockAmount = createNumber("afterStockAmount", Long.class);

    public final NumberPath<Long> beforeStockAmount = createNumber("beforeStockAmount", Long.class);

    public final StringPath birthDateOrBizRegNum = createString("birthDateOrBizRegNum");

    public final NumberPath<Long> changeStockAmount = createNumber("changeStockAmount", Long.class);

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath currencyType = createString("currencyType");

    public final StringPath largeHoldingsName = createString("largeHoldingsName");

    public final StringPath rceptNo = createString("rceptNo");

    public final StringPath regDt = createString("regDt");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath stockType = createString("stockType");

    public final NumberPath<Long> totalStockPrice = createNumber("totalStockPrice", Long.class);

    public final StringPath tradeDt = createString("tradeDt");

    public final StringPath tradeReason = createString("tradeReason");

    public final NumberPath<Long> unitStockPrice = createNumber("unitStockPrice", Long.class);

    public QLargeHoldingsDetail(String variable) {
        super(LargeHoldingsDetail.class, forVariable(variable));
    }

    public QLargeHoldingsDetail(Path<? extends LargeHoldingsDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLargeHoldingsDetail(PathMetadata metadata) {
        super(LargeHoldingsDetail.class, metadata);
    }

}

