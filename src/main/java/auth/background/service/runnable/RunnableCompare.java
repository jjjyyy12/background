package auth.background.service.runnable;

/*
 * 为redisService定义的 委托接口，更新缓存时用，判断是否是同个id的实体
 * */
@FunctionalInterface
public interface RunnableCompare<T> { boolean Compare(T t); }