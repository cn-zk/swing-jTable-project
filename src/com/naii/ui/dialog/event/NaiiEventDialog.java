package com.naii.ui.dialog.event;

import java.awt.Dimension;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.ui.dialog.NaiiDialog;

@SuppressWarnings("serial")
public class NaiiEventDialog extends NaiiDialog{

	
	@Override
	public void init() {
		setSize(new Dimension(455,300));
	}
	
	@Override
	public void saveFormObject(NaiiDto naiiDto) {
		 NaiiControl.getControl().saveObject(naiiDto);
	}
	
}
