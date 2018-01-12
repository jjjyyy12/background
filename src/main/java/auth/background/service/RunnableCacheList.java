package auth.background.service;

import java.util.List;
/*
 * 为CacheService定义的 委托接口，得到列表
 * */
@FunctionalInterface
public interface RunnableCacheList<T> { List<T> getList(); }
