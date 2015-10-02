package philosopherAndChopstick;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ChopStick {
	private boolean held;

	synchronized void pickUp() throws InterruptedException {
		while (held)
			wait();
		held = true;
	}

	synchronized void putDown() throws InterruptedException {
		held = false;
		notifyAll();
	}
}

class Philosopher implements Runnable {
	private static final int SPEED_FACTOR = 2;
	private ChopStick left, right;
	private int id;
	private Random r = new Random();
	private int sagacity, appetite;

	public Philosopher(ChopStick left, ChopStick right, int id) {
		this.id = id;
		this.left = left;
		this.right = right;
		sagacity = r.nextInt(3) + 1;
		appetite = r.nextInt(3) + 1;
	}

	public String toString() {
		return "Philosopher No. " + id + ": ";
	}

	private void think() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(r.nextInt(sagacity * SPEED_FACTOR));
	}

	private void eat() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(r.nextInt(appetite * SPEED_FACTOR));
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				System.out.println(this + "thinking");
				think();
				left.pickUp();
				System.out.println(this + "has 1 stick");
				right.pickUp();
				System.out.println(this + "has 2 sticks, eating");
				eat();
				left.putDown();
				right.putDown();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class PhilosopherAndChopstickTest {

	public static void main(String[] args) {
		final int N = 5;
		ExecutorService exec = Executors.newFixedThreadPool(N);
		// get 5 sticks
		ChopStick[] sticks = new ChopStick[N];
		for (int i=0; i < sticks.length; i++) {
			sticks[i] = new ChopStick();
		}
		// feed 5 philosophers
		for (int i=0; i < sticks.length - 1; i++) {
			exec.execute(new Philosopher(sticks[i], sticks[i+1], i + 1));
		}
		exec.execute(new Philosopher(sticks[0], sticks[sticks.length - 1], sticks.length));
	}

}
