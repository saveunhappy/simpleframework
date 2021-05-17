package org.simpleframework.core.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
目前只支持成员变量注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    //这里就是多个的话，指定注入哪个名字的，当然，还有@Primary,这个类似于@Order接口了
//    @Qualifier("Chinese")
//    @Autowired
//    private Person person;
    String value() default "";
}
