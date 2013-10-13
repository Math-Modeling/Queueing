package net.clonecomputers.lab.queueing;

public class Cashier {
	private static int globalID = 0;
	public final int id = globalID++;
	public Customer currentCustomer = null;
	public double howLongUntilDone = 0;
}
