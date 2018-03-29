package com.naii.ui.dialog.form;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiConfig;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.NaiiLog;
import com.naii.ui.graphics.NaiiRadar;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiView extends JPanel implements NaiiSwingInterface{
	
	private NaiiRadar nr ;
	
	public NaiiView() {
		setBackground(Color.white);
		setLayout(new BorderLayout());
		add(nr = new NaiiRadar(new String[]{
			"过去12个月",
			"最近3个月",
			"当前月"
		}), BorderLayout.CENTER);
	}
	
	public void resetData(NaiiDto formObj) {
		String id = formObj != null ? formObj.id : "";
		NaiiLog.log("event="+id);
		nr.resetData(
				NaiiProperty.getProperty().getFormats(NaiiConfig.KEY_EVENT, NaiiProperty.CACHE_EVENT),
				NaiiControl.getControl().loadEventCache(
				id,
				12),
				NaiiControl.getControl().loadEventCache(
				id,
				3),
				NaiiControl.getControl().loadEventCache(
				id,
				1));
	}
	
	@Override
	public void renovation() {
		// TODO Auto-generated method stub
		
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
