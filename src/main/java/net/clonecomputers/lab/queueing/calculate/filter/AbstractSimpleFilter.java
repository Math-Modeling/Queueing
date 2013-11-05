package net.clonecomputers.lab.queueing.calculate.filter;

import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractSimpleFilter extends AbstractFilter {
	public abstract boolean acceptEvent(DataSnapshot e);
	
	double timeInProgress = 0;
	
	public List<DataSnapshot> processEvent(DataSnapshot e) {
		if(acceptEvent(e)) {
			timeInProgress = 0;
			return Collections.singletonList(new DataSnapshot(timeInProgress + e.getTime(), e.getCustomersShopping(),
					e.getQueueLength(), e.getCashiersBusy(), (DataSnapshot.QueueingEvent)null));
		} else {
			timeInProgress += e.getTime();
			return null;
		}
	}
}
