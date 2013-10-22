package net.clonecomputers.lab.queueing.calculate;

public class DataSnapshot {
	
	private final double t;
	private final int shopping;
	private final int queue;
	private final int checkout;
	
	public DataSnapshot(double time, int customersShopping, int queueLength, int cashiersBusy) {
		t = time;
		shopping = customersShopping;
		queue = queueLength;
		checkout = cashiersBusy;
	}

	public double getTime() {
		return t;
	}

	public int getCustomersShopping() {
		return shopping;
	}

	public int getQueueLength() {
		return queue;
	}

	public int getCashiersBusy() {
		return checkout;
	}
	
}
