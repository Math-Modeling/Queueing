package net.clonecomputers.lab.queueing;

public class Cashier {
	private static int globalID = 0;
	public final int id = globalID++;
	public boolean hasCustomer = false;
	public double howLongUntilDone = 0;
}
