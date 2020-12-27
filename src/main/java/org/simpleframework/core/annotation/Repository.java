package org.simpleframework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring @Repository注解
 * 被该注解标注的类为Repository,需要被springIoC管理
 */
//表示注解作用在类上
@Target(ElementType.TYPE)
//表示注解在运行期获取（因为需要反射）
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
}
