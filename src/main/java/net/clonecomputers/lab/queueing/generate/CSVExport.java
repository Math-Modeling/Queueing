package net.clonecomputers.lab.queueing.generate;

import java.io.*;

//import javax.swing.*;

import org.apache.commons.csv.*;

public class CSVExport {
	private Queueing q;
	private CSVPrinter csv;
	
	public CSVExport(Queueing q){
		this.q = q;
	}
	
	public void startCSV(Appendable output) throws IOException {
		//JFileChooser chooser = new JFileChooser();
		//chooser.showSaveDialog(null);
		//filepath = chooser.getSelectedFile();
		csv = new CSVPrinter(output, CSVFormat.EXCEL);
		csv.printRecord("delta t","shopping","in line","at checkout","lambda","mu","number of cashiers","how long to run");
		csv.printRecord(null,null,null,null,q.lambda,q.mu, q.cashiers.length,q.maxIterations);
	}
	
	public void finishCSV() throws IOException {
		csv.flush();
		csv.close();
	}
	
	public void record(double timeInterval) throws IOException{
		if(partial != null){
			csv.printRecord(timeInterval, partial[0], partial[1], partial[2]);
		}
		int shopping = 0, inLine = 0, atCheckout = 0;
		for(Customer c: q.customers){
			if(c.inSupermarket) shopping++;
			if(c.inQueue) inLine++;
			if(c.atCheckout) atCheckout++;
		}
		if(shopping + inLine + atCheckout != q.customers.size()) csv.printComment("customer numbers don't add up");
		if(inLine != q.customersInQueue.size()) csv.printComment("wrong number of customers in queue");
		if(atCheckout > q.cashiers.length) csv.printComment("more people checking out than cashiers");
		partial = new int[]{shopping, inLine, atCheckout};
	}
	
	int[] partial;
}
