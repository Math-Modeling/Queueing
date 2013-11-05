package net.clonecomputers.lab.queueing.calculate.filter;

import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractFilter implements Filter {
	public abstract List<DataSnapshot> processEvent(DataSnapshot e);
	
	public DataSnapshot[] processEvents(DataSnapshot[] history){
		List<DataSnapshot> filteredHistory = new ArrayList<DataSnapshot>();
		for(DataSnapshot e: history){
			List<DataSnapshot> newE = processEvent(e);
			if(newE != null && newE.size() > 0) filteredHistory.addAll(newE);
		}
		return filteredHistory.toArray(new DataSnapshot[0]);
	}
}
