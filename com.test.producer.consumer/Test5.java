package test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * 生产者消费者模型的特点： 
 * 1. 同一时间内只能有一个生产者生产 
 * 2. 同一时间内只能有一个消费者消费 
 * 3. 生产者生产的同时消费者不能消费 
 * 4. 消费者消费的同时生产者不能生产
 * 5. 共享空间空时消费者不能继续消费 
 * 6. 共享空间满时生产者不能继续生产
 * 
 * 使用PipedInputStream/PipedOutputStream实现生产者消费者模型
 * 
 * 使用PipedInputStream读入, 使用PipedOutputStream写出, PipedInputStream与PipedOutputStream一一对应
 * 
 * 测试: 分别开始四个线程去调用生产者和消费者
 */
public class Test5 {

	/**
	 * 信号类
	 */
	static class Signs {
		static volatile int COUNT = 0;

		static final PipedInputStream pis = new PipedInputStream();
		static final PipedOutputStream pos = new PipedOutputStream();
		static {
			try {
				pis.connect(pos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生产者
	 */
	class Producer implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					int b = (int) (Math.random() * 255);
					System.out.println("Producer: a byte, the value is " + b);
					Signs.pos.write(b);
					Signs.pos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Signs.pos.close();
					Signs.pis.close();
				} catch (IOException e) {
					System.out.println(e);
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
			try {
				while (true) {
					int b = Signs.pis.read();
					System.out.println("Consumer: a byte, the value is " + b);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Signs.pos.close();
					Signs.pis.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}

	}

	public static void main(String[] args) {
		Test5 test = new Test5();

		for (int i = 0; i < 4; i++) {
			Producer producer = test.new Producer();
			new Thread(producer).start();
			
			Consumer consumer = test.new Consumer();
			new Thread(consumer).start();
		}
	}
}