package com.whooa.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
/* JPA 감사(auditing) 기능을 활성화한다. */
@EnableJpaAuditing
public class JpaConfig {

}