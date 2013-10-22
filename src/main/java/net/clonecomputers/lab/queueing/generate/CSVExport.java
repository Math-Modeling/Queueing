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
	
	public void startCSV(BufferedReader stdin) throws IOException {
		//JFileChooser chooser = new JFileChooser();
		//chooser.showSaveDialog(null);
		String filepath;
		//filepath = chooser.getSelectedFile();
		System.out.println("Input filepath");
		filepath = stdin.readLine();
		filepath.replace("~", System.getProperty("user.home")+"/");
		if(!filepath.startsWith("/")) filepath = System.getProperty("user.home") + "/" + filepath;
		File f = new File(filepath);
		csv = new CSVPrinter(new BufferedWriter(new FileWriter(f)), CSVFormat.EXCEL);
		csv.printRecord("delta t","shopping","in line","at checkout","lambda","mu","number of cashiers","how long to run");
		csv.printRecord(null,null,null,null,q.lambda,q.mu, q.cashiers.length,q.maxIterations);
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
		csv.printRecord(timeInterval, shopping, inLine, atCheckout);
	}
}
