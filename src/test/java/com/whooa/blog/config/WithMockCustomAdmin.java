package com.whooa.blog.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
@WithMockCustomUser(email = "admin@naver", userRole = "ADMIN")
public @interface WithMockCustomAdmin {

}