package com.naii.ui.swing;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.naii.db.dto.NaiiValue;
import com.naii.tools.NaiiTools;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings({ "rawtypes", "serial" })
public class NaiiComboBox extends JComboBox implements NaiiSwingInterface{
	
	
	private NaiiValue[] data;
	
	@SuppressWarnings("unchecked")
	public void setData(NaiiValue[] data) {
		this.data = data;
		setModel(new DefaultComboBoxModel(data));
	}

	@Override
	public void renovation() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setValue(Object obj) {
		int index = NaiiTools.getIdIndex(data, NaiiTools.string(obj));
		if(index != -1)
		setSelectedIndex(index);
	}

	@Override
	public Object getValue() {
		return ((NaiiValue) getSelectedItem()).getId();
	}

}
