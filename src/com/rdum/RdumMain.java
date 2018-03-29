package com.rdum;

import javax.swing.UIManager;

import com.rdum.tools.RdumTools;
import com.rdum.ui.RdumUI;

public class RdumMain {

	public static void main(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		RdumTools.load();
		
		RdumTools.ui = new RdumUI();
		
	}
}
