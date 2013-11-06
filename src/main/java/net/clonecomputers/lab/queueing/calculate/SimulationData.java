package net.clonecomputers.lab.queueing.calculate;

public interface SimulationData extends Iterable<DataSnapshot> {
	public DataSnapshot get(int index);
	public double getLambda();
	public double getMu();
	public int getNumberOfCashiers();
	public int length();
}
