package test;

/**
 * 生产者消费者模型的特点： 
 * 1. 同一时间内只能有一个生产者生产 
 * 2. 同一时间内只能有一个消费者消费 
 * 3. 生产者生产的同时消费者不能消费 
 * 4. 消费者消费的同时生产者不能生产
 * 5. 共享空间空时消费者不能继续消费 
 * 6. 共享空间满时生产者不能继续生产
 * 
 * 使用wait()/notifyAll()实现生产者消费者模型
 * 
 * 使用synchronized进行同步, 根据模型特点可知, 不能使用两个锁去锁住数据区(仓库)
 * 
 * 生产者生产时先检查仓库是否已满, 如果满, 则生产线程进入等待状态 
 * 消费者消费时先检查仓库是否已空, 如果空, 则消费线程进入等待状态
 * 
 * 测试: 分别开始四个线程去调用生产者和消费者
 */
public class Test1 {

	/**
	 * 信号类
	 */
	static class Signs {
		// 仓库数量
		static volatile int COUNT = 0;
		// 仓库满时数量
		static final int FULL_COUNT = 10;
		// 锁和条件
		static final String LOCK = "LOCK";
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
					synchronized (Signs.LOCK) {
						while (Signs.COUNT == Signs.FULL_COUNT) {
							System.out.println(Thread.currentThread().getName()
									+ "库存已满,停止生产");
							Signs.LOCK.wait();
						}
						Signs.COUNT++;
						System.out.println(Thread.currentThread().getName()
								+ "生产者生产，目前总共有" + Signs.COUNT);
						// 唤醒消费线程
						Signs.LOCK.notifyAll();
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
					Thread.sleep(100);
					synchronized (Signs.LOCK) {
						while (Signs.COUNT == 0) {
							System.out.println(Thread.currentThread().getName()
									+ "库存已空,开始生产");
							Signs.LOCK.wait();
						}

						Signs.COUNT--;
						System.out.println(Thread.currentThread().getName()
								+ "消费者消费，目前总共有" + Signs.COUNT);
						// 唤醒生产线程
						Signs.LOCK.notifyAll();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		Test1 test = new Test1();

		for (int i = 0; i < 4; i++) {
			Producer producer = test.new Producer();
			new Thread(producer).start();

			Consumer consumer = test.new Consumer();
			new Thread(consumer).start();
		}
	}
}