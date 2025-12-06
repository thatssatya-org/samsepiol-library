package com.samsepiol.library.ai.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(value = "spring.ai.chat.client.enabled", havingValue = "true")
public @interface AIEnabled {
}
