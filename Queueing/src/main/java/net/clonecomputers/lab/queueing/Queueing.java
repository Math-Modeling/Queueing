package net.clonecomputers.lab.queueing;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class Queueing {
	private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	private Cashier[] cashiers;
	private double timeToNextCustomer;
	private Set<Customer> customers;

	public void setup() throws IOException {
		System.out.println("Input how many cashiers: ");
		cashiers = new Cashier[Integer.parseInt(in.readLine().trim())];
		for(int i = 0; i < cashiers.length; i++) cashiers[i] = new Cashier();
		timeToNextCustomer = 0;
		customers = new HashSet<Customer>();
	}
	
	public static <T> T e(Set<T> set){
		return set.iterator().next();
	}
	
	public void run() throws IOException {
		System.out.println("type \"quit\" to quit");
		while(!in.ready() && !in.readLine().trim().equalsIgnoreCase("quit")){
			
		}
		
	}

	public static void main(String[] args) throws IOException {
		Queueing q = new Queueing();
		q.setup();
		q.run();
	}
}
