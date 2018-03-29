package com.rdum.ui.view;

import javax.swing.JPanel;

public abstract class RdumView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract String getState();

	public abstract void search(String text);

	public void refush() {
		
	}
}
