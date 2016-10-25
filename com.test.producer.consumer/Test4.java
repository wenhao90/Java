package test;

import java.util.concurrent.Semaphore;

/**
 * 生产者消费者模型的特点： 
 * 1. 同一时间内只能有一个生产者生产 
 * 2. 同一时间内只能有一个消费者消费 
 * 3. 生产者生产的同时消费者不能消费 
 * 4. 消费者消费的同时生产者不能生产
 * 5. 共享空间空时消费者不能继续消费 
 * 6. 共享空间满时生产者不能继续生产
 * 
 * 使用信号灯(Semaphore)方法实现生产者消费者模型
 * 
 * 设置3个信号：
 * 1. 空位置EMPTY：初始10, 当EMPTY为10时, 标识仓库为空, 生产一个产品, 此信号减1
 * 2. 满位置FULL：初始0, 当FULL为0时, 标识仓库为空, 生产一个产品, 此信号加1
 * 3. 互斥量MUTEX：根据模型的特点同一时间只能有一个线程访问共享区, 因此需要互斥
 * 
 * EMPTY和FULL一一对应
 * 
 * 测试: 分别开始四个线程去调用生产者和消费者
 */
public class Test4 {
	
	/**
	 * 信号类
	 */
	static class Signs {
		// 仓库数量
		static volatile int COUNT = 0;
		// 信号量：记录仓库空的位置(空了10个, 没有产品, 为0时仓库满)
		static final Semaphore EMPTY = new Semaphore(10);
		// 信号量：记录仓库满的位置(装了0个, 没有产品, 为10时仓库满)
		static final Semaphore FULL = new Semaphore(0);
		// 临界区互斥访问信号量(二进制信号量), 相当于互斥锁, 有锁时才能生产和消费
		static final Semaphore MUTEX = new Semaphore(1);
	}

	/**
	 * 生产者
	 */
	class Producer implements Runnable {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			while (true) {
				try {
					Signs.EMPTY.acquire();// 递减仓库空信号量，将消费计数器减1
					Signs.MUTEX.acquire();// 进入临界区
					Signs.COUNT++;
					System.out.println(Thread.currentThread().getName()
							+ "生产者生产，目前总共有" + Signs.COUNT);
					Signs.MUTEX.release();
					Signs.FULL.release();
					
					Thread.currentThread().sleep(100);
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
				}
			}
		}
	}

	/**
	 * 消费者
	 */
	class Consumer implements Runnable {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			while (true) {
				try {
					Signs.FULL.acquire();
					Signs.MUTEX.acquire();
					Signs.COUNT--;
					System.out.println(Thread.currentThread().getName()
							+ "消费者消费，目前总共有" + Signs.COUNT);
					Signs.MUTEX.release();
					Signs.EMPTY.release();
					
					Thread.currentThread().sleep(120);
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
				}
			}
		}
	}

	public static void main(String[] args) {
		Test4 test = new Test4();

		for (int i = 0; i < 4; i++) {
			Producer producer = test.new Producer();
			new Thread(producer).start();
			
			Consumer consumer = test.new Consumer();
			new Thread(consumer).start();
		}
	}
}