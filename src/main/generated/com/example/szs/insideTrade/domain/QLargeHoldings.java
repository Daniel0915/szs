package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLargeHoldings is a Querydsl query type for LargeHoldings
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLargeHoldings extends EntityPathBase<LargeHoldings> {

    private static final long serialVersionUID = -1649509862L;

    public static final QLargeHoldings largeHoldings = new QLargeHoldings("largeHoldings");

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath rceptDt = createString("rceptDt");

    public final StringPath rceptNo = createString("rceptNo");

    public final StringPath regDt = createString("regDt");

    public final StringPath reportResn = createString("reportResn");

    public final StringPath repror = createString("repror");

    public final NumberPath<Long> stkqy = createNumber("stkqy", Long.class);

    public final NumberPath<Long> stkqyIrds = createNumber("stkqyIrds", Long.class);

    public final NumberPath<Float> stkrt = createNumber("stkrt", Float.class);

    public final NumberPath<Float> stkrtIrds = createNumber("stkrtIrds", Float.class);

    public QLargeHoldings(String variable) {
        super(LargeHoldings.class, forVariable(variable));
    }

    public QLargeHoldings(Path<? extends LargeHoldings> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLargeHoldings(PathMetadata metadata) {
        super(LargeHoldings.class, metadata);
    }

}

