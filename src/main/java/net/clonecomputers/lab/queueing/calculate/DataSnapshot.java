package net.clonecomputers.lab.queueing.calculate;

import java.util.*;

public class DataSnapshot {
	
	private final double dt;
	private final int shopping;
	private final int queue;
	private final int checkout;
	private final List<QueueingEvent> events;
	
	public DataSnapshot(double deltaTime, int customersShopping, int queueLength, int cashiersBusy, List<QueueingEvent> shoppingEvents) {
		dt = deltaTime;
		shopping = customersShopping;
		queue = queueLength;
		checkout = cashiersBusy;
		events = Collections.unmodifiableList(shoppingEvents);
	}
	
	public DataSnapshot(double deltaTime, int customersShopping, int queueLength, int cashiersBusy, QueueingEvent shoppingEvent) {
		dt = deltaTime;
		shopping = customersShopping;
		queue = queueLength;
		checkout = cashiersBusy;
		events = Collections.singletonList(shoppingEvent);
	}
	
	public DataSnapshot(double deltaTime, int customersShopping, int queueLength, int cashiersBusy, DataSnapshot lastData) {
		int lastShopping = lastData==null? 0: lastData.getCustomersShopping();
		int lastQueueLength = lastData==null? 0: lastData.getQueueLength();
		int lastCashiersBusy = lastData==null? 0: lastData.getCashiersBusy();
		if(customersShopping == lastShopping + 1 && queueLength == lastQueueLength && cashiersBusy == lastCashiersBusy) {
			events = Collections.singletonList(QueueingEvent.SUPERMARKET_ARRIVE);
		} else if(customersShopping == lastShopping - 1 && queueLength == lastQueueLength + 1 && cashiersBusy == lastCashiersBusy) {
			events = Collections.singletonList(QueueingEvent.ENTER_QUEUE);
		} else if(customersShopping == lastShopping - 1 && queueLength == 0 && cashiersBusy == lastCashiersBusy + 1) {
			events = Collections.singletonList(QueueingEvent.SKIP_QUEUE);
		} else if(customersShopping == lastShopping && queueLength == lastQueueLength - 1 && cashiersBusy == lastCashiersBusy) {
			events = Collections.singletonList(QueueingEvent.ENTER_LEAVE_CHECKOUT);
		} else if(customersShopping == lastShopping && queueLength == 0 && cashiersBusy == lastCashiersBusy - 1) {
			events = Collections.singletonList(QueueingEvent.ONLY_LEAVE_CHECKOUT);
		} else {
			events = Collections.singletonList(QueueingEvent.OTHER);
		}
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
	
	/**
	 * @return the event if there is only one event, otherwise QueueingEvent.OTHER
	 */
	public QueueingEvent getEvent() {
		return events.size() == 1? events.get(0): QueueingEvent.OTHER;
	}
	
	/**
	 * @return the list of events
	 */
	public List<QueueingEvent> getEvents() {
		return events;
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
		ENTER_LEAVE_CHECKOUT,
		
		/**
		 * / S \    /   S   \
		 * | 0 | -> |   0   |
		 * \ C /    \ C - 1 /
		 */
		ONLY_LEAVE_CHECKOUT,
		OTHER,
	}
	
}
