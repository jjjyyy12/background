package auth.background.service.runnable;
 
/*
 * 为CacheService定义的 委托接口,得到单个可缓存实体
 * */
@FunctionalInterface
public interface RunnableCacheSignel<T,M> { T get(M id); }
