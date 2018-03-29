package com.naii.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.naii.ctr.NaiiControl;
import com.naii.ui.view.NaiiLoadView;

public class NaiiLog {

	private static boolean showLog = true;
	private static boolean showError = true;
	
	private static final SimpleDateFormat sf;

	static{
		sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	
	public static void log(String msg){
		NaiiLoadView load = NaiiControl.getControl().getLoad();
		String time = sf.format(new Date());
		if(load != null && load.isPrint()){
			load.setText("["+time + "] LOG: "+msg);
			return;
		}
		
		if(!showLog){
			return ;
		}
		System.out.println("["+time + "] LOG: "+msg);
	}
	public static void error(String msg){
//		if(NaiiControl.getControl().getNaiiUI().getLoad().isPrint()){
//			NaiiControl.getControl().getNaiiUI().getLoad().setText(msg);
//			return;
//		}
		if(!showError){
			return ;
		}
		String time = sf.format(new Date());
		System.err.println("["+time + "] ERROE: "+msg);
	}
}
