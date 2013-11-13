package net.clonecomputers.lab.queueing.calculate.filters;

import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractSimpleFilter extends AbstractFilter {
	public abstract boolean acceptEvent(DataSnapshot e);
	
	protected boolean discardsLastEvent = true; // set to false in constructor for subclass if you don't want this
	
	List<DataSnapshot> buffer = null;
	
	double timeInProgress = 0;
	List<DataSnapshot.QueueingEvent> eventsInProgress = new ArrayList<DataSnapshot.QueueingEvent>();
	
	public List<DataSnapshot> processEvent(DataSnapshot e) {
		List<DataSnapshot> ret = null;
		if(discardsLastEvent) ret = buffer;
		timeInProgress += e.getTime();
		eventsInProgress.add(e.getEvent());
		if(acceptEvent(e)) {
			buffer =  Collections.singletonList(new DataSnapshot(timeInProgress,
					e.getCustomersShopping(), e.getQueueLength(), e.getCashiersBusy(), eventsInProgress));
			timeInProgress = 0;
			eventsInProgress.clear();
			if(discardsLastEvent) return ret;
			else return buffer;
		} else {
			return null;
		}
	}
}
