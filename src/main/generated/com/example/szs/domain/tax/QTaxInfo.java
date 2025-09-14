package com.example.szs.domain.tax;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTaxInfo is a Querydsl query type for TaxInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTaxInfo extends EntityPathBase<TaxInfo> {

    private static final long serialVersionUID = -1767728449L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaxInfo taxInfo = new QTaxInfo("taxInfo");

    public final ListPath<IncomeDeduction, QIncomeDeduction> incomeDeductionList = this.<IncomeDeduction, QIncomeDeduction>createList("incomeDeductionList", IncomeDeduction.class, QIncomeDeduction.class, PathInits.DIRECT2);

    public final com.example.szs.domain.member.QMember member;

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final NumberPath<java.math.BigDecimal> taxDeductionAmount = createNumber("taxDeductionAmount", java.math.BigDecimal.class);

    public final com.example.szs.domain.embedded.QTime time;

    public final NumberPath<java.math.BigDecimal> totalInComeAmount = createNumber("totalInComeAmount", java.math.BigDecimal.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QTaxInfo(String variable) {
        this(TaxInfo.class, forVariable(variable), INITS);
    }

    public QTaxInfo(Path<? extends TaxInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTaxInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTaxInfo(PathMetadata metadata, PathInits inits) {
        this(TaxInfo.class, metadata, inits);
    }

    public QTaxInfo(Class<? extends TaxInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.szs.domain.member.QMember(forProperty("member"), inits.get("member")) : null;
        this.time = inits.isInitialized("time") ? new com.example.szs.domain.embedded.QTime(forProperty("time")) : null;
    }

}

