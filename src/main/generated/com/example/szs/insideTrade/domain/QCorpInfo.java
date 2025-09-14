package com.example.szs.insideTrade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCorpInfo is a Querydsl query type for CorpInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCorpInfo extends EntityPathBase<CorpInfo> {

    private static final long serialVersionUID = 1074654825L;

    public static final QCorpInfo corpInfo = new QCorpInfo("corpInfo");

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final StringPath regDt = createString("regDt");

    public QCorpInfo(String variable) {
        super(CorpInfo.class, forVariable(variable));
    }

    public QCorpInfo(Path<? extends CorpInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCorpInfo(PathMetadata metadata) {
        super(CorpInfo.class, metadata);
    }

}

