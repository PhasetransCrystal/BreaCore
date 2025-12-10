package com.phasetranscrystal.breacore.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 定义带参数的注解
@Target(ElementType.TYPE)  // 指定注解用于类
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留
public @interface BreaModule {

    String moduleId();

    String moduleName() default "";
}
