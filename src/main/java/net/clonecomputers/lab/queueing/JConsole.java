package net.clonecomputers.lab.queueing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.text.*;

import org.apache.commons.io.input.*;

@SuppressWarnings("serial")
public class JConsole extends JPanel{
	private JTextPane textArea;
	private JTextField inputField;
	private BufferedInputStream externalIn;
	private PrintStream externalOut;
	private PrintStream externalErr;
	private PrintStream internalIn;
	private BufferedInputStream internalOut;
	private BufferedInputStream internalErr;

	public static void main(String[] args) throws IOException{
		final JConsole guiConsole = new JConsole();
		//System.setIn(guiConsole.getIn());
		//System.setOut(guiConsole.getOut());
		//System.setErr(guiConsole.getErr());
		JFrame consoleWindow = new JFrame("Console");
		consoleWindow.pack();
		consoleWindow.setSize(800, 600);
		consoleWindow.add(guiConsole);
		consoleWindow.setResizable(false);
		consoleWindow.setVisible(true);
		consoleWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		guiConsole.getOut().println("Hello world!");
		guiConsole.getErr().println("Goodbye world?");
	}
	
	public JConsole() throws IOException{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override public void run(){
					JConsole.this.initGUI();
				}
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		PipedInputStream tmpIOut = new PipedInputStream();
		PipedInputStream tmpIErr = new PipedInputStream();
		externalOut = new PrintStream(new PipedOutputStream(tmpIOut));
		externalErr = new PrintStream(new PipedOutputStream(tmpIErr));
		internalOut = new BufferedInputStream(tmpIOut);
		internalErr = new BufferedInputStream(tmpIErr);
		new Thread(new StreamWatcher(new InputStreamReader(internalOut), textArea, Color.BLACK)).start();
		new Thread(new StreamWatcher(new InputStreamReader(internalErr), textArea, Color.RED)).start();
		
		PipedInputStream tmpEIn = new PipedInputStream();
		PipedOutputStream tmpIIn = new PipedOutputStream();
		externalIn = new BufferedInputStream(tmpEIn);
		internalIn = new PrintStream(tmpIIn);
		TeeInputStream inViewer = new TeeInputStream(new PipedInputStream(tmpIIn), new PipedOutputStream(tmpEIn));
		new Thread(new StreamWatcher(new InputStreamReader(inViewer), textArea, Color.GREEN)).start();
	}

	public void initGUI() {
		textArea = new JTextPane();
		textArea.setEditable(false);
		inputField = new JTextField(80);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(textArea),BorderLayout.CENTER);
		this.add(inputField,BorderLayout.SOUTH);
		inputField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == '\n' || e.getKeyChar() == '\r'){
					inputLine();
				}
			}
		});
	}

	private class StreamWatcher implements Runnable {
		private Reader stream;
		private JTextPane output;
		private Color color;

		private StreamWatcher(Reader stream, JTextPane output, Color color) {
			this.stream = stream;
			this.output = output;
			this.color = color;
		}

		@Override
		public void run() {
			char[] buff = new char[100];
			int numChars;
			try {
				while((numChars = stream.read(buff)) != -1){
					String text = new String(buff, 0, numChars);
					appendToPane(output, text, color);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void appendToPane(JTextPane tp, String msg, Color c) {
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
			aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
			synchronized(tp){
				int len = tp.getDocument().getLength();
				tp.setCaretPosition(len);
				tp.setCharacterAttributes(aset, false);
				tp.replaceSelection(msg);
			}
		}

	}

	public InputStream getIn(){
		return externalIn;
	}

	public PrintStream getOut(){
		return externalOut;
	}

	public PrintStream getErr(){
		return externalErr;
	}

	protected void inputLine() {
		String input = inputField.getText();
		inputField.setText("");
		internalIn.print(input);
	}
}
