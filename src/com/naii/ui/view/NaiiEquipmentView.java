package com.naii.ui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import com.naii.ctr.NaiiControl;
import com.naii.ui.graphics.NaiiLine;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiEquipmentView extends JPanel implements NaiiSwingInterface{

	private NaiiLine line;
	
	public NaiiEquipmentView() {
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		setBackground(Color.white);
		
		add(line = new NaiiLine("台", "设备信息"));
		line.setPreferredSize(new Dimension(400, 150));
	}
	
	@Override
	public void renovation() {
		String[] tit = new String[]{
				"未用设备",
				"笔记本",
				"个人设备",
				"台式机",
				"设备总数"
		};
		Float[] fl = NaiiControl.getControl().loadEquipmentViewFloat();
		line.resetData(tit, fl);
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
