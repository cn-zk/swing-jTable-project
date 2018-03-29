package com.naii.ui.swing;

import javax.swing.JTextField;

import com.naii.tools.Assist;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiMoney extends JTextField implements NaiiSwingInterface{

	public NaiiMoney() {
		setText("0");
	}
	
	@Override
	public void renovation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Object obj) {
		if(obj == null){
			setText("0");
		}else{
			setText(obj+"");
		}
	}

	@Override
	public Object getValue() {
		return Assist.isEmpty(getText()) ? 0 : Float.parseFloat(getText());
	}

}
