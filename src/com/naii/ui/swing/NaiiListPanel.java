package com.naii.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiHistory;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiListPanel extends JPanel implements NaiiSwingInterface{

	private JList list;
	private NaiiTable tab;
	private File[] files;
	
	private int selectIndex = -1;
	private NaiiDto dto;
	
	private boolean recycle;
	
	public NaiiListPanel() {
		this(true);
	}
	public NaiiListPanel(boolean recycle) {
		this.recycle = recycle;
		setLayout(new BorderLayout());
		
		add(new JScrollPane(list = new JList()), BorderLayout.WEST);
		list.setPreferredSize(new Dimension(130, 1));
		list.setBorder(null);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(arg0.getValueIsAdjusting()){
					selectItem(selectIndex = list.getSelectedIndex());
				}
			}
		});
		
		add(tab = new NaiiTable(), BorderLayout.CENTER);
	}
	
	protected void selectItem(int index){
		try {
			
			if(dto instanceof NaiiHistory){
				((NaiiHistory) dto).date = new SimpleDateFormat("yyyy").parse(files[index].getName());
			}
			
			tab.setNaiiDtoModel(2, 
					recycle?
					NaiiControl.getControl().loadRecycleCompany(
							files[index].getName(),
							dto):
					NaiiControl.getControl().queryNaiiDto(dto, null), 
					"id");
			list.setSelectedIndex(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setNaiiTableForamt(String format){
		tab.setForamt(format);
	}
	public void setList(File[] files, NaiiDto dto){
		this.dto = dto;
		if(files == null){
			this.files = null;
			list.setListData(new Object[0]);
			return;
		}
		this.files = files;
		String[] strs = new String[files.length];
		int i=0;
		for(File f: files){
			strs[i++] = f.getName();
		}
		list.setListData(strs);
		
		list.setSelectedIndex(selectIndex);
		if(selectIndex != -1){
			selectItem(selectIndex);
		}
	}
	
	public NaiiDto[] getSelectRows(){
		return tab.getSelectRowIds();
	}
	
	public String getSelectDate(){
		return selectIndex != -1 && files.length > selectIndex? files[selectIndex].getName() : null;
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

	public NaiiTable getNaiiTable() {
		return tab;
	}

}
