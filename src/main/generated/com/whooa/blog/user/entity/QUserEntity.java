package com.whooa.blog.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 1691296503L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final com.whooa.blog.common.entity.QCoreEntity _super = new com.whooa.blog.common.entity.QCoreEntity(this);

    public final BooleanPath active = createBoolean("active");

    public final ListPath<com.whooa.blog.comment.entity.CommentEntity, com.whooa.blog.comment.entity.QCommentEntity> comments = this.<com.whooa.blog.comment.entity.CommentEntity, com.whooa.blog.comment.entity.QCommentEntity>createList("comments", com.whooa.blog.comment.entity.CommentEntity.class, com.whooa.blog.comment.entity.QCommentEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath name = createString("name");

    public final EnumPath<com.whooa.blog.common.security.oauth2.OAuth2Provider> oAuth2Provider = createEnum("oAuth2Provider", com.whooa.blog.common.security.oauth2.OAuth2Provider.class);

    public final StringPath oAuth2ProviderId = createString("oAuth2ProviderId");

    public final StringPath oAuth2ProviderRefreshToken = createString("oAuth2ProviderRefreshToken");

    public final StringPath password = createString("password");

    public final StringPath passwordResetToken = createString("passwordResetToken");

    public final DateTimePath<java.time.LocalDateTime> passwordResetTokenExpiration = createDateTime("passwordResetTokenExpiration", java.time.LocalDateTime.class);

    public final ListPath<com.whooa.blog.post.entity.PostEntity, com.whooa.blog.post.entity.QPostEntity> posts = this.<com.whooa.blog.post.entity.PostEntity, com.whooa.blog.post.entity.QPostEntity>createList("posts", com.whooa.blog.post.entity.PostEntity.class, com.whooa.blog.post.entity.QPostEntity.class, PathInits.DIRECT2);

    public final StringPath refreshToken = createString("refreshToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<com.whooa.blog.user.type.UserRole> userRole = createEnum("userRole", com.whooa.blog.user.type.UserRole.class);

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

