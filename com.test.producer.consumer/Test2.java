package test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 生产者消费者模型的特点： 
 * 1. 同一时间内只能有一个生产者生产 
 * 2. 同一时间内只能有一个消费者消费 
 * 3. 生产者生产的同时消费者不能消费 
 * 4. 消费者消费的同时生产者不能生产
 * 5. 共享空间空时消费者不能继续消费 
 * 6. 共享空间满时生产者不能继续生产
 * 
 * 使用condition.await()/condition.signalAll()方法实现生产者消费者模型
 * 
 * 使用ReentrantLock和Condition进行同步, 其中ReentrantLock为生产者消费者公用, 
 * 意味这同一时间只有一个能存在, 符合模型特点
 * 将仓库空和满分为两个不同的条件(condition), 达到条件时线程阻塞
 * 
 * 生产者生产时先检查仓库是否已满, 如果满, 则生产线程进入等待状态 
 * 消费者消费时先检查仓库是否已空, 如果空, 则消费线程进入等待状态
 * 
 * 测试: 分别开始四个线程去调用生产者和消费者
 */
public class Test2 {

	/**
	 * 信号类
	 */
	static class Signs {
		// 仓库数量
		static volatile int COUNT = 0;
		// 仓库满时数量
		static final int FULL_COUNT = 10;
		// 锁和条件
		static final Lock LOCK = new ReentrantLock();
		static final Condition FULL = LOCK.newCondition();
		static final Condition EMPTY = LOCK.newCondition();
	}
	
	/**
	 * 生产者
	 */
	class Producer implements Runnable {
		@Override
		public void run() {
			while (true) {
				Signs.LOCK.lock();
				try {
					Thread.sleep(100);
					while (Signs.COUNT == Signs.FULL_COUNT) {//如果仓库已满, 生产者进入等待
						System.out.println(Thread.currentThread().getName()
								+ "库存已满,停止生产");
						Signs.FULL.await();
					}
					Signs.COUNT++;
					System.out.println(Thread.currentThread().getName()
							+ "生产者生产，目前总共有" + Signs.COUNT);
					// 唤醒消费线程
					Signs.EMPTY.signalAll();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					Signs.LOCK.unlock();
				}
			}
		}
	}

	/**
	 * 消费者
	 */
	class Consumer implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				Signs.LOCK.lock();
				try {
					Thread.sleep(80);
					while (Signs.COUNT == 0) {// 如果仓库已空, 消费者进入等待
						try {
							System.out.println(Thread.currentThread().getName()
									+ "库存已空,开始生产");
							Signs.EMPTY.await();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Signs.COUNT--;
					System.out.println(Thread.currentThread().getName()
							+ "消费者消费，目前总共有" + Signs.COUNT);
					// 唤醒生产线程
					Signs.FULL.signalAll();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					Signs.LOCK.unlock();
				}
			}
		}
	}

	public static void main(String[] args) {
		Test2 test = new Test2();

		for (int i = 0; i < 4; i++) {
			Producer producer = test.new Producer();
			new Thread(producer).start();
			
			Consumer consumer = test.new Consumer();
			new Thread(consumer).start();
		}
	}
}