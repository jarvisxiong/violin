package com.wolf.test.concurrent.threadpool;

import com.wolf.test.concurrent.thread.runnable.MyTask;

import java.util.concurrent.*;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/23
 * Time: 9:01
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class FutureTest {

    public static void main(String[] args) {

//        testCommon();
        testInterrupted();
    }

    private static void testCommon() {
        ExecutorService exec = Executors.newCachedThreadPool();

        testTask(exec, 15); // 成功
        testTask(exec, 5); //  失败

        exec.shutdown();
    }

    public static void testTask(ExecutorService exec, int timeout) {
        MyTask task = new MyTask();
        Future<Boolean> future = exec.submit(task);
        Boolean taskResult = null;
        String failReason = null;
        try {
            // 等待计算结果，最长等待timeout秒，timeout秒后中止任务
            taskResult = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            failReason = "InterruptedException";
        } catch (ExecutionException e) {
            failReason = "ExecutionException";
        } catch (TimeoutException e) {
            failReason = "TimeoutException";
            exec.shutdownNow();
        }

        System.out.println("\ntaskResult : " + taskResult);
        System.out.println("failReason : " + failReason);
    }


    private static void testInterrupted() {
        ExecutorService exec = Executors.newCachedThreadPool();
        final MyTask task = new MyTask();

        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task.stop();
            }
        });


        Future<Boolean> future = exec.submit(task);
        try {
            Boolean taskResult = future.get();
            System.out.println(taskResult);
        } catch (InterruptedException e) {
            //这部分应该视具体情况而定
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the task too
            future.cancel(true);
        } catch (ExecutionException e) {
            System.out.println("ExecutionException");
            System.out.println(Thread.currentThread().getName());
            e.printStackTrace();
        }

        exec.shutdownNow();
    }
}