package com.whooa.blog.user.mapper;

import com.whooa.blog.user.dto.UserDto;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-14T20:44:01+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto.UserResponse fromEntity(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDto.UserResponse userResponse = new UserDto.UserResponse();

        userResponse.setId( userEntity.getId() );
        userResponse.setEmail( userEntity.getEmail() );
        userResponse.setName( userEntity.getName() );
        userResponse.setUserRole( userEntity.getUserRole() );

        return userResponse;
    }

    @Override
    public UserEntity toEntity(UserDto.UserCreateRequest userCreate) {
        if ( userCreate == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail( userCreate.getEmail() );
        userEntity.setName( userCreate.getName() );
        userEntity.setPassword( userCreate.getPassword() );
        if ( userCreate.getUserRole() != null ) {
            userEntity.setUserRole( Enum.valueOf( UserRole.class, userCreate.getUserRole() ) );
        }

        return userEntity;
    }
}
