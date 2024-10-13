package com.whooa.blog.category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryEntity is a Querydsl query type for CategoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryEntity extends EntityPathBase<CategoryEntity> {

    private static final long serialVersionUID = -788711971L;

    public static final QCategoryEntity categoryEntity = new QCategoryEntity("categoryEntity");

    public final com.whooa.blog.common.entity.QCoreEntity _super = new com.whooa.blog.common.entity.QCoreEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath name = createString("name");

    public final ListPath<com.whooa.blog.post.entity.PostEntity, com.whooa.blog.post.entity.QPostEntity> posts = this.<com.whooa.blog.post.entity.PostEntity, com.whooa.blog.post.entity.QPostEntity>createList("posts", com.whooa.blog.post.entity.PostEntity.class, com.whooa.blog.post.entity.QPostEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCategoryEntity(String variable) {
        super(CategoryEntity.class, forVariable(variable));
    }

    public QCategoryEntity(Path<? extends CategoryEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategoryEntity(PathMetadata metadata) {
        super(CategoryEntity.class, metadata);
    }

}

