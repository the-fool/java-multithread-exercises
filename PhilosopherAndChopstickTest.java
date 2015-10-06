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
	// constant speed factor alters the rate of the simulation
	private static final int SPEED_FACTOR = 200;
	private ChopStick left, right;
	private int id;
	// random thinking/eating times
	private Random r = new Random();
	// random coefficients making one philosopher with much different proclivities from another 
	private int sagacity, appetite;
	// if nourishment hits 0, then philosopher starves to death
	private volatile int nourishment;
	ExecutorService exec; 

	public Philosopher(ChopStick left, ChopStick right, int id) {
		this.id = id;
		this.left = left;
		this.right = right;
		sagacity = r.nextInt(3) + 1;
		appetite = r.nextInt(3) + 1;
		
		// a separate hunger thread for the philosopher
		// it ticks down on a timer, and gets incremented when the philosopher eats
		nourishment = 5;
		exec = Executors.newFixedThreadPool(1);
		exec.execute(new Stomach());
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
				
				nourishment++;
				if (nourishment > 0)
					System.out.println(this + "nourishment level: " + nourishment);
				else {
					throw new StarvedException(this + "***** STARVED TO DEATH ******");
				}
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (StarvedException e) {
			System.out.println(e.getMessage());
		}
	}
	private class Stomach implements Runnable {
		public void run() {
			try {
				while (!Thread.interrupted()) {
			// This sleep coefficient is critical for whether philosophers are too hungry to survive.
			// With multiplicative at 4, it's okay, but at 3, philosophers tend to starve
				TimeUnit.MILLISECONDS.sleep(SPEED_FACTOR * 4);
				nourishment--;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("serial")
	private class StarvedException extends RuntimeException {
		StarvedException() {
			super();
		}
		StarvedException(String message) {
			super(message);
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
		// possible deadlock UNLESS we switch up the order the philosophers reach for the sticks
		// if every philosopher reaches for a stick on the left first, 
		// then a circular deadlock is possible
		exec.execute(new Philosopher(sticks[0], sticks[sticks.length - 1], sticks.length));
	}

}
