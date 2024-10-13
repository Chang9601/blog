package com.whooa.blog.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoreEntity is a Querydsl query type for CoreEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QCoreEntity extends EntityPathBase<CoreEntity> {

    private static final long serialVersionUID = 196198827L;

    public static final QCoreEntity coreEntity = new QCoreEntity("coreEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCoreEntity(String variable) {
        super(CoreEntity.class, forVariable(variable));
    }

    public QCoreEntity(Path<? extends CoreEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoreEntity(PathMetadata metadata) {
        super(CoreEntity.class, metadata);
    }

}

