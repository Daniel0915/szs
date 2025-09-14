package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExecOwnershipDetail is a Querydsl query type for ExecOwnershipDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExecOwnershipDetail extends EntityPathBase<ExecOwnershipDetail> {

    private static final long serialVersionUID = 544899134L;

    public static final QExecOwnershipDetail execOwnershipDetail = new QExecOwnershipDetail("execOwnershipDetail");

    public final NumberPath<Long> afterStockAmount = createNumber("afterStockAmount", Long.class);

    public final NumberPath<Long> beforeStockAmount = createNumber("beforeStockAmount", Long.class);

    public final NumberPath<Long> changeStockAmount = createNumber("changeStockAmount", Long.class);

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath execOwnershipName = createString("execOwnershipName");

    public final StringPath isuExctvOfcps = createString("isuExctvOfcps");

    public final StringPath isuExctvRgistAt = createString("isuExctvRgistAt");

    public final StringPath isuMainShrholdr = createString("isuMainShrholdr");

    public final StringPath rceptNo = createString("rceptNo");

    public final StringPath regDt = createString("regDt");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath stockType = createString("stockType");

    public final StringPath tradeDt = createString("tradeDt");

    public final StringPath tradeReason = createString("tradeReason");

    public final StringPath unitStockPrice = createString("unitStockPrice");

    public QExecOwnershipDetail(String variable) {
        super(ExecOwnershipDetail.class, forVariable(variable));
    }

    public QExecOwnershipDetail(Path<? extends ExecOwnershipDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExecOwnershipDetail(PathMetadata metadata) {
        super(ExecOwnershipDetail.class, metadata);
    }

}

