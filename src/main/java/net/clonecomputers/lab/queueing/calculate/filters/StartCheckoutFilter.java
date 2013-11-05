package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;
import net.clonecomputers.lab.queueing.calculate.filter.*;

public class StartCheckoutFilter extends AbstractSimpleFilter {

	@Override
	public boolean acceptEvent(DataSnapshot e) {
		return e.getEvent() == DataSnapshot.QueueingEvent.ENTER_LEAVE_CHECKOUT ||
			   e.getEvent() == DataSnapshot.QueueingEvent.SKIP_QUEUE;
	}

}
