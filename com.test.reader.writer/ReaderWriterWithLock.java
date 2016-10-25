package test1;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaderWriterWithLock implements ReaderWriter {
	// 共享数据, 只能有一个线程能写该数据, 但可以有多个线程同时读该数据
	private Object data = 0;
	// 读写锁
	private ReadWriteLock rwl = new ReentrantReadWriteLock();

	/**
	 * 获取数据
	 */
	public void read() {
		// 上读锁，其他线程只能读不能写
		rwl.readLock().lock();
		System.out.println(Thread.currentThread().getName()	+ " be ready to read data!");
		try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 释放读锁，最好放在finally里面
			rwl.readLock().unlock();
			System.out.println(Thread.currentThread().getName()	+ "have read data :" + data);
		}
	}

	/**
	 * 写入数据
	 */
	public void write(Object data) {
		// 上写锁，不允许其他线程读也不允许写
		rwl.writeLock().lock();
		System.out.println(Thread.currentThread().getName()	+ " be ready to write data!");
		try {
			Thread.sleep((long) (Math.random() * 1000));
			this.data = data;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rwl.writeLock().unlock();// 释放写锁
			System.out.println(Thread.currentThread().getName()	+ " have write data: " + data);
		}
	}
}