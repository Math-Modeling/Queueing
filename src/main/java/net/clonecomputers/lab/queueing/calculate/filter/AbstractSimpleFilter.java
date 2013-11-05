package net.clonecomputers.lab.queueing.calculate.filter;

import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractSimpleFilter extends AbstractFilter {
	public abstract boolean acceptEvent(DataSnapshot e);
	
	double timeInProgress = 0;
	List<DataSnapshot.QueueingEvent> eventsInProgress = new ArrayList<DataSnapshot.QueueingEvent>();
	
	public List<DataSnapshot> processEvent(DataSnapshot e) {
		timeInProgress += e.getTime();
		eventsInProgress.add(e.getEvent());
		if(acceptEvent(e)) {
			List<DataSnapshot> ret =  Collections.singletonList(new DataSnapshot(timeInProgress + e.getTime(),
					e.getCustomersShopping(), e.getQueueLength(), e.getCashiersBusy(), eventsInProgress));
			timeInProgress = 0;
			eventsInProgress.clear();
			return ret;
		} else {
			return null;
		}
	}
}
