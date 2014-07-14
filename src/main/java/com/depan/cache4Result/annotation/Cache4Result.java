package com.depan.cache4Result.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Created by zhangdengpan
 * cache manager!
 * 请保证cacheKeyName的名字唯一
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache4Result {
    String cacheKeyName();//想要存入缓存的key名称
    int liveSecond() default 0;// 单位: 秒 -- 缓存存在的时间 0 代表默认的ehcache.xml中配置的时间
}
