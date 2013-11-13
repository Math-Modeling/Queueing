package net.clonecomputers.lab.queueing.generate;

import static java.lang.Math.*;

import java.io.*;

public class QueueingTest {

	public double mu;
	public double lambda;
	
	public static void main(String[] args) throws IOException{
		new QueueingTest().run();
	}
	
	public QueueingTest(){
		mu=.25;
		lambda=5;
	}
	
	public void run() throws IOException {
		while(System.in.available() == 0){
			System.out.println(randomCustomerInterval());
		}
	}
 
	private double randomCashierTime() {
		return -log(random())/mu;
	}

	private double randomCustomerInterval() {
		return -log(random())/lambda;
	}
}
