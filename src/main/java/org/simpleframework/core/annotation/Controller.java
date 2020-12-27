package org.simpleframework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring @Controller注解
 * 被该注解标注的类为controller,需要被springIoC管理
 */

//表示注解作用在类上
@Target(ElementType.TYPE)
//表示注解作用在运行期（因为需要反射）
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
