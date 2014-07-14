Cache4Query 是为查询做缓存的小工具
========================================
使用方法：
---------------------------------------
spring config
---------------------------------------
### 	
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
