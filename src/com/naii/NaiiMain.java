package com.naii;

import javax.swing.UIManager;

import com.naii.ctr.NaiiControl;

public class NaiiMain {

	
	public static void main(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					NaiiControl.getControl().load();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		
	}
}
