package com.naii.ui.swing;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextField;

import com.naii.tools.NaiiTools;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiDate extends JTextField implements NaiiSwingInterface{

	SimpleDateFormat fm, fm1;
	public NaiiDate() {
		fm = new SimpleDateFormat("yyyy-MM-dd");
		fm1 = new SimpleDateFormat("yyyy/MM/dd");
	}

	@Override
	public void renovation() {
		
	}

	@Override
	public void setValue(Object obj) {
		setText(NaiiTools.isEmpty(obj) ? null : fm.format((Date)obj));
	}

	@Override
	public Object getValue() {
		try{
			return fm.parse(getText());
		}catch(Exception e){
			try{
				return fm1.parse(getText());
			}catch(Exception e1){
				return null;
			}
		}
	}
}
