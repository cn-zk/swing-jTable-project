package com.rdum.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextField;

public class RdumDate extends JTextField{

	SimpleDateFormat fm, fm1;
	public RdumDate() {
		fm = new SimpleDateFormat("yyyy-MM-dd");
		fm1 = new SimpleDateFormat("yyyy/MM/dd");
	}
	
	public void setValue(Date obj){
		setText(obj == null ? null : fm.format(obj));
	}

	public Date getValue() {
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
