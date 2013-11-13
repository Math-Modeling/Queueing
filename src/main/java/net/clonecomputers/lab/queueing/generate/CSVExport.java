package net.clonecomputers.lab.queueing.generate;

import java.io.*;

//import javax.swing.*;
import org.apache.commons.csv.*;

public class CSVExport {
	private Queueing q;
	private CSVPrinter csv;
	
	public static void printHeader(CSVPrinter csv) throws IOException {
		csv.printRecord("delta t","shopping","in line","at checkout","lambda","mu","number of cashiers","how long to run");
	}
	
	public static void printSimulationWideDataLine(CSVPrinter csv,
			double lambda, double mu, int numCashiers, long dataLength) throws IOException {
		csv.printRecord(null,null,null,null,lambda,mu,numCashiers,dataLength);
	}
	
	public static CSVFormat getFormat(){
		return CSVFormat.EXCEL.withSkipHeaderRecord(true).withHeader().withIgnoreEmptyLines(true);
	}
	
	public CSVExport(Queueing q){
		this.q = q;
	}
	
	public void startCSV(Appendable output) throws IOException {
		//JFileChooser chooser = new JFileChooser();
		//chooser.showSaveDialog(null);
		//filepath = chooser.getSelectedFile();
		csv = new CSVPrinter(output, CSVFormat.EXCEL);
		CSVExport.printHeader(csv);
		CSVExport.printSimulationWideDataLine(csv, q.lambda, q.mu, q.cashiers.length, q.maxIterations);
	}
	
	public void finishCSV() throws IOException {
		csv.flush();
		csv.close();
	}
	
	public void record(double timeInterval) throws IOException{
		int shopping = 0, inLine = 0, atCheckout = 0;
		for(Customer c: q.customers){
			if(c.inSupermarket) shopping++;
			if(c.inQueue) inLine++;
			if(c.atCheckout) atCheckout++;
		}
		if(shopping + inLine + atCheckout != q.customers.size()) csv.printComment("customer numbers don't add up");
		if(inLine != q.customersInQueue.size()) csv.printComment("wrong number of customers in queue");
		if(atCheckout > q.cashiers.length) csv.printComment("more people checking out than cashiers");
		csv.printRecord(timeInterval,shopping,inLine,atCheckout);
	}
}
