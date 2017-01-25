package com.wolf.test.thread.runnable;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/23
 * Time: 11:45
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ThreadTesterA  implements Runnable {

	private int counter;

	@Override
	public void run() {
		while (counter <= 10) {
			System.out.print("Counter = " + counter + " ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}
		System.out.println();
	}
}
