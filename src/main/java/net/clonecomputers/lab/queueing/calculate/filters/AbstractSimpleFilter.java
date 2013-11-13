package net.clonecomputers.lab.queueing.calculate.filters;

import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractSimpleFilter extends AbstractFilter {
	public abstract boolean acceptEvent(DataSnapshot e);
	
	double timeInProgress = 0;
	List<DataSnapshot.QueueingEvent> eventsInProgress = new ArrayList<DataSnapshot.QueueingEvent>();
	DataSnapshot snapshotInProgress = null;
	
	public List<DataSnapshot> processEvent(DataSnapshot e) {
		List<DataSnapshot> ret = null;
		if(acceptEvent(e)) {
			if(snapshotInProgress != null) ret =  Collections.singletonList(new DataSnapshot(
					timeInProgress, snapshotInProgress.getCustomersShopping(),
					snapshotInProgress.getQueueLength(),
					snapshotInProgress.getCashiersBusy(), eventsInProgress));
			timeInProgress = e.getTime();
			eventsInProgress.clear();
			eventsInProgress.addAll(e.getEvents());
			snapshotInProgress = e;
			return ret;
		} else {
			timeInProgress += e.getTime();
			eventsInProgress.addAll(e.getEvents());
			return null;
		}
	}
}
