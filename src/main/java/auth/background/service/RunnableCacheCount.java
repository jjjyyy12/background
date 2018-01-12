package auth.background.service;

/*
 * 为redisService定义的 委托接口,得到int总数
 * */
@FunctionalInterface
public interface RunnableCacheCount { int getCount(); }