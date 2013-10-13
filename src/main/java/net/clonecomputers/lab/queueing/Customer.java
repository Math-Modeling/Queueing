package net.clonecomputers.lab.queueing;

public class Customer implements Comparable<Customer> {
	private static int globalID = 0;
	public final int id = globalID++;
	public boolean inSupermarket = true;
	public boolean inQueue = false;
	public boolean atCheckout = false;
	public double timeLeftInSupermarket;
	public double timeSpentInSupermarket = 0;
	public double timeSpentInQueue = 0;
	public double timeSpentAtCheckout = 0;
	@Override public String toString(){
		return t(inSupermarket)+t(inQueue)+t(atCheckout);
	}
	public String t(boolean b){
		return b?"T":"F";
	}
	public int hashCode(){
		return id;
	}
	public int compareTo(Customer c){
		if(c.id > id) return 1;
		if(c.id == id) return 0;
		if(c.id < id) return -1;
		return -1;
	}
	public boolean equals(Object o){
		return o instanceof Customer && ((Customer)o).id == id;
	}
}
