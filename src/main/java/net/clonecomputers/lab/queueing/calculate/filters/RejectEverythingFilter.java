package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;

public class RejectEverythingFilter extends AbstractSimpleFilter {

	@Override
	public boolean acceptEvent(DataSnapshot e) {
		return false;
	}

}
