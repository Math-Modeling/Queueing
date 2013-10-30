package net.clonecomputers.lab.queueing.calculate;

public class DataSnapshot {
	
	private final double dt;
	private final int shopping;
	private final int queue;
	private final int checkout;
	private final QueueingEvent event;
	
	public DataSnapshot(double deltaTime, int customersShopping, int queueLength, int cashiersBusy, QueueingEvent shoppingEvent) {
		dt = deltaTime;
		shopping = customersShopping;
		queue = queueLength;
		checkout = cashiersBusy;
		event = shoppingEvent;
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
	
	public QueueingEvent getEvent() {
		return event;
	}
	
	@Override
	public String toString(){
		return "("+dt+","+shopping+","+queue+","+checkout+")";
	}
	
	public enum QueueingEvent {
		/**
		 * / S \    / S + 1 \
		 * | L | -> |   L   |
		 * \ C /    \   C   /
		 */
		SUPERMARKET_ARRIVE,
		
		/**
		 * / S \    / S - 1 \
		 * | L | -> | L + 1 |
		 * \ C /    \   C   /
		 */
		ENTER_QUEUE,
		
		/**
		 * / S \    / S - 1 \
		 * | 0 | -> |   0   |
		 * \ C /    \ C + 1 /
		 */
		SKIP_QUEUE,
		
		/**
		 * / S \    /   S   \
		 * | L | -> | L - 1 |
		 * \ C /    \   C   /
		 */
		ENTER_CHECKOUT,
		
		/**
		 * / S \    /   S   \
		 * | 0 | -> |   0   |
		 * \ C /    \ C - 1 /
		 */
		LEAVE_CHECKOUT,
		OTHER,
	}
	
}
