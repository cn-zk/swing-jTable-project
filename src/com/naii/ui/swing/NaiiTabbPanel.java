package com.naii.ui.swing;

import java.awt.Component;

import javax.swing.JTabbedPane;

import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiTabbPanel extends JTabbedPane implements NaiiSwingInterface{

	public NaiiTabbPanel(int arg0) {
		super(arg0);
	}

	public NaiiTabbPanel() {
		super();
	}

	@Override
	public void renovation() {
		Component c = getComponent(getSelectedIndex());
		if(c instanceof NaiiSwingInterface){
			((NaiiSwingInterface) c).renovation();
		}
	}

	@Override
	public void setValue(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
