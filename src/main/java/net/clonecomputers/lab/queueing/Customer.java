package net.clonecomputers.lab.queueing;

public class Customer {
	private static int globalID = 0;
	public final int id = globalID++;
	//public boolean inSupermarket = true;
	public boolean inQueue = false;
	public boolean atCheckout = false;
	public double totalTimeSoFar = 0;
}
