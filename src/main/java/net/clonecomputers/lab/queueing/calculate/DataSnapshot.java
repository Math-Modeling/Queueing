package net.clonecomputers.lab.queueing.calculate;

public class DataSnapshot {
	
	private final double dt;
	private final int shopping;
	private final int queue;
	private final int checkout;
	
	public DataSnapshot(double deltaTime, int customersShopping, int queueLength, int cashiersBusy) {
		dt = deltaTime;
		shopping = customersShopping;
		queue = queueLength;
		checkout = cashiersBusy;
	}

	public double getTime() {
		return dt;
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
	
	@Override
	public String toString(){
		return "("+dt+","+shopping+","+queue+","+checkout+")";
	}
	
}
