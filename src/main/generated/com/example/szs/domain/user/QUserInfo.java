package com.example.szs.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserInfo is a Querydsl query type for UserInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInfo extends EntityPathBase<UserInfo> {

    private static final long serialVersionUID = 246252261L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserInfo userInfo = new QUserInfo("userInfo");

    public final com.example.szs.domain.embedded.QTime time;

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final ListPath<com.example.szs.domain.subscribe.UserPushSubs, com.example.szs.domain.subscribe.QUserPushSubs> userPushSubsList = this.<com.example.szs.domain.subscribe.UserPushSubs, com.example.szs.domain.subscribe.QUserPushSubs>createList("userPushSubsList", com.example.szs.domain.subscribe.UserPushSubs.class, com.example.szs.domain.subscribe.QUserPushSubs.class, PathInits.DIRECT2);

    public final StringPath userPwd = createString("userPwd");

    public QUserInfo(String variable) {
        this(UserInfo.class, forVariable(variable), INITS);
    }

    public QUserInfo(Path<? extends UserInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserInfo(PathMetadata metadata, PathInits inits) {
        this(UserInfo.class, metadata, inits);
    }

    public QUserInfo(Class<? extends UserInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.time = inits.isInitialized("time") ? new com.example.szs.domain.embedded.QTime(forProperty("time")) : null;
    }

}

