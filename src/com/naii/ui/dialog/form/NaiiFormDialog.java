package com.naii.ui.dialog.form;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.naii.ctr.NaiiChangeEvent;
import com.naii.ctr.NaiiChangeListener;
import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiUser;
import com.naii.ui.dialog.NaiiDialog;
import com.naii.ui.dialog.event.NaiiEvent;

@SuppressWarnings("serial")
public class NaiiFormDialog extends NaiiDialog{
	
	private NaiiView view;
	
	@Override
	public void init() {
		getForm().add(view = new NaiiView(), BorderLayout.EAST);
		view.setPreferredSize(new Dimension(350,1));
		tab.addTab("    事      件    ", new NaiiEvent(){
			@Override
			public void renovation() {
				resetData(formObj);
			}
		});
		tab.addTab("    视      图    ", new NaiiItem(){
			@Override
			public void renovation() {
				resetData(formObj);
			}
		});
		NaiiControl.getControl().addChangeListener(new NaiiChangeListener() {
			@Override
			public void changeData(NaiiChangeEvent ce) {
				if(ce.option == NaiiChangeEvent.OPTION_IMOROT){
					return;
				}
				if(view.isVisible() && NaiiFormDialog.this.isShowing()){
					view.resetData(formObj);
				}
			}
		});
	}
	
	@Override
	public void saveFormObject(NaiiDto naiiDto) {
		NaiiControl.getControl().saveObject(naiiDto);
	}
	
	private void updateButtons(NaiiDto formObj){
		boolean flag = formObj.id != null && formObj instanceof NaiiUser;
		for(int i=1;i<tab.getTabCount(); i++){
			tab.setEnabledAt(i, flag);
		}
	}
	
	private void updateView(NaiiDto formObj){
		if(formObj instanceof NaiiUser){
			setSize(new Dimension(830, 480));
			view.setVisible(true);
			view.resetData(formObj);
		}else{
			setSize(new Dimension(460, 460));
			view.setVisible(false);
		}
	}
	
	@Override
	public void openForm(NaiiDto formObj) {
		updateButtons(formObj);
		
		updateView(formObj);
		
		super.openForm(formObj);
	}

}
