package com.example.szs.domain.subscribe;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPushSubs is a Querydsl query type for UserPushSubs
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPushSubs extends EntityPathBase<UserPushSubs> {

    private static final long serialVersionUID = 682543315L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPushSubs userPushSubs = new QUserPushSubs("userPushSubs");

    public final StringPath channelType = createString("channelType");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final com.example.szs.domain.embedded.QTime time;

    public final com.example.szs.domain.user.QUserInfo userInfo;

    public QUserPushSubs(String variable) {
        this(UserPushSubs.class, forVariable(variable), INITS);
    }

    public QUserPushSubs(Path<? extends UserPushSubs> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPushSubs(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPushSubs(PathMetadata metadata, PathInits inits) {
        this(UserPushSubs.class, metadata, inits);
    }

    public QUserPushSubs(Class<? extends UserPushSubs> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.time = inits.isInitialized("time") ? new com.example.szs.domain.embedded.QTime(forProperty("time")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new com.example.szs.domain.user.QUserInfo(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

