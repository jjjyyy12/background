package auth.background.service;

import java.util.List;
/*
 * 为redisService定义的 委托接口
 * */
@FunctionalInterface
public interface Runnable<T> { List<T> getList(); }
