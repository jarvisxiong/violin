package com.wolf.utils.redis.threadpool;

import java.util.concurrent.Future;

/**
 * 
 *  实现任务的代理
 * <br/> Created on  2012-11-13 下午09:22:36

 * @since 1.0
 */
public class ThreadPoolAdaptor implements IAsynchronousHandler{
	
	private IAsynchronousHandler handler;
	
	private Future<Object> future ;
	
	private final long executeTime;
	
	public ThreadPoolAdaptor(IAsynchronousHandler handler, long time){
		this.handler = handler;
		executeTime = System.currentTimeMillis()+time;
	}
	
	//获取真实的任务对象
	public IAsynchronousHandler getHandler() {
		return handler;
	}

	
	
	 Future<Object> getFuture() {
		return future;
	}

	 void setFuture(Future<Object> future) {
		this.future = future;
	}
	 
	 long getExecuteTime(){
		 
		 return executeTime;
	 }
	
	@Override
	public Object call() throws Exception {
		
		return handler.call();
	}

	@Override
	public void executeAfter(Throwable t) {
		
		handler.executeAfter(t);
		
	}

	@Override
	public void executeBefore(Thread t) {
		
		handler.executeBefore(t);
		
	}
	
	
	
}
