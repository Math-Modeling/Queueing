package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;
import net.clonecomputers.lab.queueing.calculate.filter.*;

public class NewCustomerFilter extends AbstractSimpleFilter {

	@Override
	public boolean acceptEvent(DataSnapshot e) {
		// TODO Auto-generated method stub
		return e.getEvent() == DataSnapshot.QueueingEvent.SUPERMARKET_ARRIVE;
	}

}
