package com.depan.cache4Result.aspect;
import com.depan.cache4Result.annotation.Cache4Result;
import com.depan.cache4Result.util.MethodParamNamesScaner;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import ognl.Ognl;
import ognl.OgnlException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by zhangdengpan
 * cache manager!.
 */
public class Cache4ResultAspect {
    private Cache cache;
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        Object result;
        MethodSignature jpo = (MethodSignature) jp.getSignature();
        Cache4Result annotation = jpo.getMethod().getAnnotation(Cache4Result.class);//获取方法上的annotation实例
        String cacheKeyName = annotation.cacheKeyName();//从实例中获得注解中的cacheKeyName属性
        int liveSecond = annotation.liveSecond();//缓存多长时间
        String cacheKey = getCacheKey(cacheKeyName, jp);//构造一个缓存key的名字，并利用ognl解析${}中的方法参数
        Element element = cache.get(cacheKey);
        if (element == null) {
            result = jp.proceed();
            element = new Element(cacheKey, (Serializable) result);
            if(liveSecond>0){
                element.setTimeToLive(liveSecond);
            }
            cache.put(element);
            cache.flush();//持久化到硬盘
        }
        return element.getValue();
    }
    /**
     * 通过解析cacheKeyName 获得缓存的key名
     * @param key
     * @param jp
     * @return
     */
    private String getCacheKey(String key, ProceedingJoinPoint jp) {
        //不包含${}表达式则直接返回该名字不做替换
        if (!key.contains("$")) {
            return key;
        }
        String regexp = "\\$\\{[^\\}]+\\}";
        //获得匹配器
        Pattern pattern = Pattern.compile(regexp);
        //开始匹配
        Matcher matcher = pattern.matcher(key);
        List<String> names = new ArrayList<String>();
        try {
            while (matcher.find()) {
                //如果匹配
                names.add(matcher.group());
            }
            //执行替换
            key = executeNames(key, names, jp);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(("在解析cacheKeyName的时候，正则表达式解析错误!"));
        }
        return key;
    }
    /**
     * 对KEY中的参数进行替换
     *  例如： ${user.uid} 则执行user.getUid = 2 则将${user.uid}替换成2
     * @param key
     * @param names
     * @param jp
     * @return string key
     * @throws ognl.OgnlException
     */
    private String executeNames(String key, List<String> names, ProceedingJoinPoint jp) throws OgnlException {
        //拦截点中获取方法和参数
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        List<String> param = MethodParamNamesScaner.getParamNames(method);
        if (names == null || names.size() == 0) {
            return key;
        }
        Object[] params = jp.getArgs();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < param.size(); i++) {
            map.put(param.get(i), params[i]);
        }
        for (String name : names) {
            //将${}中的表达式挑选出来
            String temp = name.substring(2);
            temp = temp.substring(0, temp.length() - 1);
            //执行ognl表达式 获得参数的的get方法的值
            String ognlString = String.valueOf(Ognl.getValue(temp, map));
            key = myReplace(key, name, ognlString);
        }
        return key;
    }
    /**
     * 不依赖Regex的替换，避免$符号、{}等在String.replaceAll方法中当做Regex处理时候的问题。
     * @param src
     * @param from
     * @param to
     * @return
     */
    private String myReplace(String src, String from, String to) {
        int index = src.indexOf(from);
        if (index == -1) {
            return src;
        }
        return src.substring(0, index) + to + src.substring(index + from.length());
    }
    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
