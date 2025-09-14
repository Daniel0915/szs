package com.example.szs.domain.marketing;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMarketingMng is a Querydsl query type for MarketingMng
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketingMng extends EntityPathBase<MarketingMng> {

    private static final long serialVersionUID = 452503295L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMarketingMng marketingMng = new QMarketingMng("marketingMng");

    public final StringPath answer1 = createString("answer1");

    public final StringPath answer1Score = createString("answer1Score");

    public final StringPath answer2 = createString("answer2");

    public final StringPath answer2Score = createString("answer2Score");

    public final StringPath imgPath = createString("imgPath");

    public final NumberPath<Integer> page = createNumber("page", Integer.class);

    public final NumberPath<Long> pSeq = createNumber("pSeq", Long.class);

    public final StringPath question = createString("question");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final com.example.szs.domain.embedded.QTime time;

    public final StringPath title = createString("title");

    public final StringPath userToken = createString("userToken");

    public QMarketingMng(String variable) {
        this(MarketingMng.class, forVariable(variable), INITS);
    }

    public QMarketingMng(Path<? extends MarketingMng> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMarketingMng(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMarketingMng(PathMetadata metadata, PathInits inits) {
        this(MarketingMng.class, metadata, inits);
    }

    public QMarketingMng(Class<? extends MarketingMng> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.time = inits.isInitialized("time") ? new com.example.szs.domain.embedded.QTime(forProperty("time")) : null;
    }

}

