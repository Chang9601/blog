package com.whooa.blog.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostEntity is a Querydsl query type for PostEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostEntity extends EntityPathBase<PostEntity> {

    private static final long serialVersionUID = -644276831L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostEntity postEntity = new QPostEntity("postEntity");

    public final com.whooa.blog.common.entity.QCoreEntity _super = new com.whooa.blog.common.entity.QCoreEntity(this);

    public final com.whooa.blog.category.entity.QCategoryEntity category;

    public final ListPath<com.whooa.blog.comment.entity.CommentEntity, com.whooa.blog.comment.entity.QCommentEntity> comments = this.<com.whooa.blog.comment.entity.CommentEntity, com.whooa.blog.comment.entity.QCommentEntity>createList("comments", com.whooa.blog.comment.entity.CommentEntity.class, com.whooa.blog.comment.entity.QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final ListPath<com.whooa.blog.file.value.File, com.whooa.blog.file.value.QFile> files = this.<com.whooa.blog.file.value.File, com.whooa.blog.file.value.QFile>createList("files", com.whooa.blog.file.value.File.class, com.whooa.blog.file.value.QFile.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.whooa.blog.user.entity.QUserEntity user;

    public QPostEntity(String variable) {
        this(PostEntity.class, forVariable(variable), INITS);
    }

    public QPostEntity(Path<? extends PostEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostEntity(PathMetadata metadata, PathInits inits) {
        this(PostEntity.class, metadata, inits);
    }

    public QPostEntity(Class<? extends PostEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.whooa.blog.category.entity.QCategoryEntity(forProperty("category")) : null;
        this.user = inits.isInitialized("user") ? new com.whooa.blog.user.entity.QUserEntity(forProperty("user")) : null;
    }

}

