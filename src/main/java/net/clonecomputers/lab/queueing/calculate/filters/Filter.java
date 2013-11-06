package net.clonecomputers.lab.queueing.calculate.filters;

import net.clonecomputers.lab.queueing.calculate.*;

public interface Filter {
	public SimulationData processEvents(SimulationData history);
}
