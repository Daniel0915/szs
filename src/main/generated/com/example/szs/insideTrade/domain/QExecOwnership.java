package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExecOwnership is a Querydsl query type for ExecOwnership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExecOwnership extends EntityPathBase<ExecOwnership> {

    private static final long serialVersionUID = 2092145037L;

    public static final QExecOwnership execOwnership = new QExecOwnership("execOwnership");

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath isuExctvOfcps = createString("isuExctvOfcps");

    public final StringPath isuExctvRgistAt = createString("isuExctvRgistAt");

    public final StringPath isuMainShrholdr = createString("isuMainShrholdr");

    public final StringPath rceptDt = createString("rceptDt");

    public final StringPath rceptNo = createString("rceptNo");

    public final StringPath regDt = createString("regDt");

    public final StringPath repror = createString("repror");

    public final NumberPath<Long> spStockLmpCnt = createNumber("spStockLmpCnt", Long.class);

    public final NumberPath<Long> spStockLmpIrdsCnt = createNumber("spStockLmpIrdsCnt", Long.class);

    public final NumberPath<Float> spStockLmpIrdsRate = createNumber("spStockLmpIrdsRate", Float.class);

    public final NumberPath<Float> spStockLmpRate = createNumber("spStockLmpRate", Float.class);

    public QExecOwnership(String variable) {
        super(ExecOwnership.class, forVariable(variable));
    }

    public QExecOwnership(Path<? extends ExecOwnership> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExecOwnership(PathMetadata metadata) {
        super(ExecOwnership.class, metadata);
    }

}

