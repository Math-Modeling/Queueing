package net.clonecomputers.lab.queueing.calculate;

import javax.swing.JPanel;

public abstract class InputField<E> extends JPanel {

	public abstract void setInputted(E o);
	public abstract E getInputted();
	
	public abstract void setLabelText(String t);
	public abstract String getLabelText();
	
}
