package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLargeHoldingsStkrt is a Querydsl query type for LargeHoldingsStkrt
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLargeHoldingsStkrt extends EntityPathBase<LargeHoldingsStkrt> {

    private static final long serialVersionUID = 527679474L;

    public static final QLargeHoldingsStkrt largeHoldingsStkrt = new QLargeHoldingsStkrt("largeHoldingsStkrt");

    public final StringPath birthDateOrBizRegNum = createString("birthDateOrBizRegNum");

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath largeHoldingsName = createString("largeHoldingsName");

    public final StringPath rceptNo = createString("rceptNo");

    public final StringPath regDt = createString("regDt");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<Float> stkrt = createNumber("stkrt", Float.class);

    public final NumberPath<Long> totalStockAmount = createNumber("totalStockAmount", Long.class);

    public QLargeHoldingsStkrt(String variable) {
        super(LargeHoldingsStkrt.class, forVariable(variable));
    }

    public QLargeHoldingsStkrt(Path<? extends LargeHoldingsStkrt> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLargeHoldingsStkrt(PathMetadata metadata) {
        super(LargeHoldingsStkrt.class, metadata);
    }

}

