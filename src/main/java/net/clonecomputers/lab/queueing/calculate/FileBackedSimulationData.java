package net.clonecomputers.lab.queueing.calculate;

import java.io.*;
import java.util.*;

import net.clonecomputers.lab.queueing.calculate.DataSnapshot.QueueingEvent;
import net.clonecomputers.lab.queueing.generate.*;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.apache.commons.io.output.*;

public class FileBackedSimulationData implements SimulationData {
	public static class Generator {
		private final File f;
		private final CSVPrinter csv;
		private long length;
		private static final int MAX_HEADER_LENGTH_IN_BYTES = 300;
		public Generator() throws IOException {
			f = File.createTempFile(
					"net.clonecomputers.lab.queueing.calculate.FileBackedSimulationData.Generator", ".csv");
			f.deleteOnExit();
			CSVPrinter tmpcsv = new CSVPrinter(new PrintStream(f), CSVExport.getFormat());
			CSVExport.printHeader(tmpcsv);
			tmpcsv.flush();
			tmpcsv.close();
			//RandomAccessFile rf = new RandomAccessFile(f, "rw");
			//rf.setLength(rf.length()-CSVExport.getFormat().getRecordSeparator().getBytes().length); // delete last char
			//rf.close();
			FileOutputStream fos = new FileOutputStream(f,true);
			for(int i = 0; i < MAX_HEADER_LENGTH_IN_BYTES; i+=" ".getBytes().length){
				fos.write(" ".getBytes()); // extra spaces are set to be ignored
			}
			fos.write(CSVExport.getFormat().getRecordSeparator().getBytes());
			fos.close();
			csv = new CSVPrinter(new PrintStream(new FileOutputStream(f,true)), CSVExport.getFormat());
		}
		public void add(DataSnapshot event) {
			length++;
			try {
				csv.printRecord(event.getTime(),event.getCustomersShopping(),
						event.getQueueLength(),event.getCashiersBusy());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		public void addAll(Collection<DataSnapshot> events){
			for(DataSnapshot event: events) add(event);
		}
		public void addAll(DataSnapshot[] events){
			for(DataSnapshot event: events) add(event);
		}
		public void addAll(Iterable<DataSnapshot> events){
			for(DataSnapshot event: events) add(event);
		}
		public FileBackedSimulationData finish(double lambda, double mu, int numberOfCashiers){
			//FIXME: might not work (needs testing)
			try{
				RandomAccessFile rf = new RandomAccessFile(f, "rw");
				rf.seek(headerLength());
				StringBuilderWriter output = new StringBuilderWriter();
				CSVPrinter csv = new CSVPrinter(output, CSVExport.getFormat());
				CSVExport.printSimulationWideDataLine(csv, lambda, mu, numberOfCashiers, length);
				csv.flush();
				csv.close();
				rf.write(output.toString().getBytes(),0,
						output.toString().getBytes().length -
						CSVExport.getFormat().getRecordSeparator().getBytes().length);
				rf.close();
				normalizeCSV(f);
				return new FileBackedSimulationData(f);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		
		private static long headerLength() {
			StringBuilderWriter output = new StringBuilderWriter();
			CSVPrinter csv = new CSVPrinter(output, CSVExport.getFormat());
			try{
				CSVExport.printHeader(csv);
				csv.flush();
				csv.close();
			}catch(IOException e){
				throw new RuntimeException(e);
			}
			return output.toString().getBytes().length;
		}
	}
	
	private static void normalizeCSV(File f) throws IOException {
		File f2 = File.createTempFile(
				"net.clonecomputers.lab.queueing.calculate.FileBackedSimulationData.Generator", ".csv");
		f2.deleteOnExit();
		CSVPrinter printer = new CSVPrinter(new PrintStream(f2), CSVFormat.EXCEL); // intentionally CSVFormat.EXCEL
		CSVParser parser = new CSVParser(new FileReader(f), CSVExport.getFormat()
				.withSkipHeaderRecord(false).withHeader((String[])null));
		printer.printRecords(parser);
		printer.close();
		FileUtils.copyFile(f2, f);
		f2.delete();
	}

	private final File f;
	private final double lambda;
	private final double mu;
	private final int numberOfCashiers;
	private final long length;
	private CSVParser currentCSV;
	
	public FileBackedSimulationData(File f) throws IOException,FileNotFoundException {
		this.f = f;
		currentCSV = newCSV();
		CSVRecord firstLine = currentCSV.iterator().next();
		lambda = Double.parseDouble(firstLine.get("lambda"));
		mu = Double.parseDouble(firstLine.get("mu"));
		numberOfCashiers = Integer.parseInt(firstLine.get("number of cashiers"));
		length = Long.parseLong(firstLine.get("how long to run"));
	}
	
	private CSVParser newCSV() {
		try {
			return new CSVParser(new BufferedReader(new FileReader(f)), CSVExport.getFormat());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Iterator<DataSnapshot> iterator() {
		return new FileBackedSimulationDataIterator(newCSV().iterator());
	}
	
	private class FileBackedSimulationDataIterator implements Iterator<DataSnapshot> {
		private final Iterator<CSVRecord> i;
		DataSnapshot lastSnap = null;
		public FileBackedSimulationDataIterator(Iterator<CSVRecord> i){
			this.i = i;
			i.next();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public DataSnapshot next() {
			return getSnap(i.next(), lastSnap);
		}
		
		@Override
		public boolean hasNext() {
			return i.hasNext();
		}
	}

	/*@Override
	public DataSnapshot get(int index) {
		if(index >= length) throw new ArrayIndexOutOfBoundsException(index);
		if(currentCSV.getRecordNumber() >= index) currentCSV = newCSV();
		Iterator<CSVRecord> i = currentCSV.iterator();
		if(index == 0) return getSnap(i.next(),(DataSnapshot)null);
		if(index == 1) return getSnap(i.next(),i.next());
		while(i.hasNext() && i.next().getRecordNumber() < index-1);
		return getSnap(i.next(),i.next());
	}*/
	
	private static DataSnapshot getSnap(CSVRecord pastLine, CSVRecord data){
		return getSnap(data,getSnap(pastLine,QueueingEvent.OTHER));
	}
	
	private static DataSnapshot getSnap(CSVRecord data, QueueingEvent e){
		return new DataSnapshot(Double.parseDouble(data.get("delta t")),Integer.parseInt(data.get("shopping")),
				Integer.parseInt(data.get("in line")),Integer.parseInt(data.get("at checkout")),e);
	}
	
	private static DataSnapshot getSnap(CSVRecord data, DataSnapshot last){
		return new DataSnapshot(Double.parseDouble(data.get("delta t")),Integer.parseInt(data.get("shopping")),
				Integer.parseInt(data.get("in line")),Integer.parseInt(data.get("at checkout")),last);
	}

	@Override
	public double getLambda() {
		return lambda;
	}

	@Override
	public double getMu() {
		return mu;
	}

	@Override
	public int getNumberOfCashiers() {
		return numberOfCashiers;
	}

	@Override
	public long length() {
		return length;
	}

	@Override
	public void saveData(File dataFile) throws IOException {
		FileUtils.copyFile(f, dataFile);
	}

}
