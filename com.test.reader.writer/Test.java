package test1;

import java.util.Random;

/**
 * 读者写者问题的测试类
 */
public class Test {

	public static void main(String[] args) {
		// 采用ReentrantReadWriteLock的实现类
		//final ReaderWriter readWriter = new ReaderWriterWithLock();
		
		// 采用ReadWriterWithSemaphore的实现类
		final ReaderWriter readWriter = new ReaderWriterWithSemaphore();
		
		
		// 读写分别起3个线程
		for (int i = 0; i < 3; i++) {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						readWriter.read();// 读操作
					}
				}
			}).start();
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						// 写操作
						readWriter.write(new Random().nextInt(10000));
					}
				}
			}).start();
		}
	}
}
