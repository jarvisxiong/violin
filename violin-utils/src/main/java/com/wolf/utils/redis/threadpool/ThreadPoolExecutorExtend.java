/**
 * Description: ThreadPoolExecutorExtend.java
 * All Rights Reserved.

 */
package com.wolf.utils.redis.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  
 * <br/> Created on 2013-12-9 下午3:31:49

 * @since 3.2 
 */
 public class ThreadPoolExecutorExtend extends ThreadPoolExecutor {

	/**
	 * 需要处理的任务数
	 */
	final AtomicInteger submittedTasksCount = new AtomicInteger();
	
	ThreadPoolExecutorExtend(int corePoolSize, int maximumPoolSize,
                             long keepAliveTime, TimeUnit unit,
                             BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                             RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		
	}
	/**
	 * 获取当前线程池正在执行的任务数
	 * @return
	 */
	public AtomicInteger getSubmittedTasksCount(){
		
		return this.submittedTasksCount ;
	}
	
	//重写execute方法
	@Override
	public void execute(Runnable command) {
		submittedTasksCount.incrementAndGet();
		super.execute(command);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		//执行完毕减1
		submittedTasksCount.decrementAndGet();
		
		if(r instanceof CommonFutureTask){
			IAsynchronousHandler handler = ((CommonFutureTask) r).getR();
			if(handler == null){
				throw new NullPointerException("线程池参数对象为null!");
			}
				
			handler.executeAfter(t);
			
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		
		if(r instanceof CommonFutureTask){
			IAsynchronousHandler handler = ((CommonFutureTask) r).getR();
			if(handler == null){
				throw new NullPointerException("线程池参数对象为null!");
			}
			
			handler.executeBefore(t);
			
		}
	}
	
	@Override
	protected <T> RunnableFuture<T> newTaskFor(
			Callable<T> callable) {
		// TODO Auto-generated method stub
		return new CommonFutureTask<T>(callable);
	}
	@Override
	public String toString() {
		return "ThreadPoolExecutor: ActiveCount = "+this.getActiveCount() + " CompletedTaskCount = "+ this.getCompletedTaskCount() + " CorePoolSize = "+ this.getCorePoolSize()
		+ " LargestPoolSize = "+this.getLargestPoolSize() + " MaximumPoolSize = "+ this.getMaximumPoolSize() + " PoolSize = "+this.getPoolSize() + " queueSize = "+this.getQueue().size()
		+" queueString=[" + this.getQueue().toString() + "]";
	}
	
	
}
