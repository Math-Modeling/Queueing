package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;

public class NewCustomerFilter extends AbstractSimpleFilter {

	@Override
	public boolean acceptEvent(DataSnapshot e) {
		return e.getEvent() == DataSnapshot.QueueingEvent.SUPERMARKET_ARRIVE;
	}

}
