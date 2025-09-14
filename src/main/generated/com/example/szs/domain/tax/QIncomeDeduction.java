package com.example.szs.domain.tax;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIncomeDeduction is a Querydsl query type for IncomeDeduction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIncomeDeduction extends EntityPathBase<IncomeDeduction> {

    private static final long serialVersionUID = -799697278L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIncomeDeduction incomeDeduction = new QIncomeDeduction("incomeDeduction");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final QTaxInfo taxInfo;

    public final EnumPath<com.example.szs.model.eNum.IncomeDeductionType> type = createEnum("type", com.example.szs.model.eNum.IncomeDeductionType.class);

    public final StringPath yearMonth = createString("yearMonth");

    public QIncomeDeduction(String variable) {
        this(IncomeDeduction.class, forVariable(variable), INITS);
    }

    public QIncomeDeduction(Path<? extends IncomeDeduction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIncomeDeduction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIncomeDeduction(PathMetadata metadata, PathInits inits) {
        this(IncomeDeduction.class, metadata, inits);
    }

    public QIncomeDeduction(Class<? extends IncomeDeduction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.taxInfo = inits.isInitialized("taxInfo") ? new QTaxInfo(forProperty("taxInfo"), inits.get("taxInfo")) : null;
    }

}

