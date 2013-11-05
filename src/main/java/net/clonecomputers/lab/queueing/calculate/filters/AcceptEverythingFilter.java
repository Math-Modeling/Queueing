package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;
import net.clonecomputers.lab.queueing.calculate.filter.*;

public class AcceptEverythingFilter extends AbstractSimpleFilter {

	@Override
	public boolean acceptEvent(DataSnapshot e) {
		return true;
	}

}
