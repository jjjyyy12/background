package auth.background.service.runnable;

/*
 * 为queueService定义的 委托接口
 * */
@FunctionalInterface
public interface RunnableQueueSucc<T,M> { void Handle(T in,M reply); }
