package net.clonecomputers.lab.queueing.calculate;

import java.io.*;

public interface SimulationData extends Iterable<DataSnapshot> {
	//public DataSnapshot get(int index);
	public double getLambda();
	public double getMu();
	public int getNumberOfCashiers();
	public long length();
	public void saveData(File dataFile) throws IOException;
}
