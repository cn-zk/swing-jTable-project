package com.rdum.event;

import java.awt.AWTEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class RdumEvent {

	public AWTEvent event;
	public JFrame fr;
	public JDialog dialog;
	
	public RdumEvent(AWTEvent arg0, JFrame fr) {
		event = arg0;
		this.fr = fr;
	}
	
	public RdumEvent(AWTEvent arg0, JDialog d) {
		event = arg0;
		this.dialog = d;
	}

	
}
