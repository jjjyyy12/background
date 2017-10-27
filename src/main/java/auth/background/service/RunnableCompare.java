package auth.background.service;

/*
 * 为redisService定义的 委托接口
 * */
@FunctionalInterface
public interface RunnableCompare<T> { boolean Compare(T t); }