package test1;

import java.util.concurrent.Semaphore;

public class ReaderWriterWithSemaphore implements ReaderWriter {
	// 共享数据, 只能有一个线程能写该数据, 但可以有多个线程同时读该数据
	private Object data = 0;
	// 写信号
	private Semaphore wmutex = new Semaphore(1);
	// 读信号
	private Semaphore rmutex = new Semaphore(2);
	// 读线程计数
	public volatile int count = 0;

	/**
	 * 获取数据
	 */
	public synchronized void read() {
		try {
			rmutex.acquire();
			if (count == 0) {
				wmutex.acquire();// 当第一读进程欲读数据库时，阻止写进程写
			}
			count++;
			System.out.println(Thread.currentThread().getName() + " be ready to read data!");
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName()	+ "have read data :" + data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			count--;
			if (count == 0) {
				wmutex.release();
			}
			rmutex.release();
		}
	}

	/**
	 * 写入数据
	 */
	public synchronized void write(Object data) {
		try {
			wmutex.acquire();
			while (count != 0){
				continue;
			}
			System.out.println(Thread.currentThread().getName()	+ " be ready to write data!");
			Thread.sleep(50);
			this.data = data;
			System.out.println(Thread.currentThread().getName() + " have write data: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wmutex.release();
		}
	}
}