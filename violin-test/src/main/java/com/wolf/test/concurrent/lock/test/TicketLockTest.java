package com.wolf.test.concurrent.lock.test;

import com.wolf.test.concurrent.lock.TicketLock;
import com.wolf.utils.BaseUtils;

/**
 * Description:
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class TicketLockTest {

    public static void main(String[] args) {
        final TicketLock clhLock = new TicketLock();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    BaseUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    BaseUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    BaseUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });

        thread.start();
        thread2.start();
        thread3.start();
    }
}
