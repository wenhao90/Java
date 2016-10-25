package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 生产者消费者模型的特点： 
 * 1. 同一时间内只能有一个生产者生产 
 * 2. 同一时间内只能有一个消费者消费 
 * 3. 生产者生产的同时消费者不能消费 
 * 4. 消费者消费的同时生产者不能生产
 * 5. 共享空间空时消费者不能继续消费 
 * 6. 共享空间满时生产者不能继续生产
 * 
 * 使用阻塞队列(BlockingQueue)方法实现生产者消费者模型
 * 
 * BlockingQueue是有界阻塞队列, 初始化后大小不可变, 自带阻塞功能, 同步功能
 * 如果队列已满, 将当前插入线程等待(当然还有其他用法)
 * 如果队列已空, 将当前取值线程等待
 * 
 * 测试: 分别开始四个线程去调用生产者和消费者
 */
public class Test3 {

	/**
	 * 信号类
	 */
	static class Signs {
		// 仓库数量
		static volatile int COUNT = 0;
		// 初始容量为10, 此容量不可变
		static volatile BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(10);
	}

	/**
	 * 生产者
	 */
	class Producer implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
					Signs.blockingQueue.put(Signs.COUNT);// 放入一个数据, 如果阻塞, 则等待
					Signs.COUNT++;
					System.out.println(Thread.currentThread().getName()
							+ "生产者生产，目前总共有" + Signs.COUNT);
				} catch (Exception e) {
					// TODO: handle exception
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
			while (true) {
				try {
					Thread.sleep(120);
					Signs.blockingQueue.take();// 从队列头部取出数据, 如果阻塞, 则等待
					Signs.COUNT--;
					System.out.println(Thread.currentThread().getName()
							+ "消费者消费，目前总共有" + Signs.COUNT);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	public static void main(String[] args) {
		Test3 test = new Test3();

		for (int i = 0; i < 4; i++) {
			Producer producer = test.new Producer();
			new Thread(producer).start();

			Consumer consumer = test.new Consumer();
			new Thread(consumer).start();
		}
	}
}