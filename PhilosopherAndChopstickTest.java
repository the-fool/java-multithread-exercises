package philosopherAndChopstick;

import java.util.Random;

class DiningObject {
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
	private DiningObject[] pileOfSticks;
	private int id;
	private Random r = new Random();
	private int sagacity;
	
	public Philosopher(DiningObject[] pileOfSticks, int id) {
		this.id = id;
		this.pileOfSticks = pileOfSticks;
		sagacity = r.nextInt(3) + 1;
	}
	
	public void run() {
		
	}

}


public class PhilosopherAndChopstickTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
