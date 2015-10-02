package philosopherAndChopstick;

import java.util.Random;
import java.util.concurrent.TimeUnit;

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
	public String toString() {
		return "Philosopher No. " + id + ": ";
	}
	
	private void think() throws InterruptedException {
		TimeUnit.MILLISECONDS.wait(r.nextInt(sagacity * 200));
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			try {
				System.out.println(this + "thinking . . .");
				think();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}


public class PhilosopherAndChopstickTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
