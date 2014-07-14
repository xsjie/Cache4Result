Cache4Query 是为查询做缓存的小工具
========================================
使用方法：
---------------------------------------
spring config
---------------------------------------
###  ehcache.xml
	<cache name="cache4Query"
           maxElementsInMemory="10"
           eternal="false"
           timeToIdleSeconds="3600"
           timeToLiveSeconds="10000"
           diskPersistent="true"
           overflowToDisk="true"
            />
### 	
	<bean id="cacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean"/>
    <!-- 配置一个简单的缓存工厂bean对象 -->
    <bean id="simpleCache" class="org.springframework.cache.ehcache.EhCacheFactoryBean">
        <property name="cacheManager" ref="cacheManager"/>
        <!-- 使用缓存 关联ehcache.xml中的缓存配置 -->
        <property name="cacheName" value="cache4Query"/>
    </bean>
	<bean id="cache4QueryAspect" class="com.depan.cache4Query.aspect.Cache4QueryAspect"></bean> 
     
     <aop:config>
        <aop:aspect id="myaspectj" ref="cache4QueryAspect">
            <aop:pointcut expression="@annotation(com.depan.cache4Query.annotation.Cache4Query)" id="mypointcut"/>
            <aop:around method="doAround" pointcut-ref="mypointcut"/>
        </aop:aspect>
    </aop:config>

@Cache4Query 注解使用
------------------------------------------
### 
	public class UserServiceImpl implements UserService {
	...

	/**
	*  cacheKeyName cache的key的名称
	*  liveSecond 缓存的存活时间 0 或不写表示 默认ehcache.xml 中的配置
	*
	* @param department 
	* @result list
	*/

	@Cache4Query(cacheKeyName = "findUsersByDepartment_${department.id}_${department.name}" , liveSecond = 60*60 )
	public List<User> findUsersByDepartment(Department department){
		...
	}
}
