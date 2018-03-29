package com.naii.ui.dialog.event;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.naii.ctr.NaiiChangeEvent;
import com.naii.ctr.NaiiChangeListener;
import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.NaiiTools;
import com.naii.ui.dialog.NaiiDialog;
import com.naii.ui.inf.NaiiSwingInterface;
import com.naii.ui.swing.NaiiListPanel;
import com.naii.ui.swing.NaiiTabbPanel;
import com.naii.ui.swing.NaiiTable;

@SuppressWarnings("serial")
public class NaiiEvent extends JPanel implements NaiiSwingInterface{

	NaiiTable table ;
	NaiiListPanel recyc;
	JPanel panel;
	String id;
	private NaiiTabbPanel tab;
	
	public NaiiEvent() {
		setLayout(new BorderLayout());
		add(getOptions(), BorderLayout.NORTH);
		
		tab = new NaiiTabbPanel();
		tab.setUI(new BasicTabbedPaneUI(){
			@Override
			protected int calculateTabHeight(int tabPlacement, int tabIndex,
					int fontHeight) {
				return 50;
			}
		});
		tab.setBorder(null);
		tab.addTab("生效", getViewPanel());
		tab.addTab("回收站", getRecyclePanel());
		tab.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(((NaiiTabbPanel) e.getSource()).getSelectedIndex() == 0){
					panel.getComponent(0).setVisible(true);
					panel.getComponent(1).setVisible(true);
					panel.getComponent(2).setVisible(true);
					panel.getComponent(3).setVisible(false);
				}else{
					panel.getComponent(0).setVisible(false);
					panel.getComponent(1).setVisible(false);
					panel.getComponent(2).setVisible(false);
					panel.getComponent(3).setVisible(true);
					
					recyc.renovation();
				}
			}
		});
		
		tab.setTabPlacement(JTabbedPane.RIGHT);
		tab.setFocusable(false);
		
		add(tab, BorderLayout.CENTER);
		
		dialog = new NaiiEventDialog();
		
		NaiiControl.getControl().addChangeListener(new NaiiChangeListener() {
			@Override
			public void changeData(NaiiChangeEvent ce) {
				if(ce.option == NaiiChangeEvent.OPTION_IMOROT){
					return;
				}
				try {
					table.setNaiiDtoModel(0, 
							NaiiControl.getControl().queryNaiiDto(
									new com.naii.db.dto.NaiiEvent(id, true), 
									NaiiControl.FILTER_EVENT),
							NaiiTools.FILTER_EVENTS);
					if(recyc.isShowing()){
						recyc.renovation();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	

	private Component getViewPanel() {
		table = new NaiiTable();
		return table;
	}
	
	private Component getRecyclePanel() {
		recyc = new NaiiListPanel(){
			@Override
			public void renovation() {
				setList(NaiiControl.getControl().readRecycleList(),
						new com.naii.db.dto.NaiiEvent(id, true));
			}
		};
		return recyc;
	}

	private Component getOptions() {
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2,2));
		panel.setPreferredSize(new Dimension(1, 25));
		
		panel.add(new JButton("add"){
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						showNaiiEventDialog(null);
					}
				});
			}
		});
		panel.add(new JButton("edit"){
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						showNaiiEventDialog(table.getSelectRowObject());
					}
				});
			}
		});
		panel.add(new JButton("remove"){
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						deleteNaiiEvent(table.getSelectRowIds());
					}
				});
			}
		});
		panel.add(new JButton("back"){
			{
				setVisible(false);
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						backNaiiEvent(recyc.getSelectRows());
					}
				});
			}
		});
		return panel;
	}

	NaiiDialog dialog ;
	
	public void showNaiiEventDialog(NaiiDto naiiDto){
		dialog.openForm(naiiDto == null ? new com.naii.db.dto.NaiiEvent(id, true) : naiiDto);
	}

	public void deleteNaiiEvent(NaiiDto[] selectRowIds) {
		NaiiControl.getControl().removeObject(selectRowIds);
	}
	
	public void backNaiiEvent(NaiiDto[] selectRowIds) {
		NaiiControl.getControl().backObject(selectRowIds, NaiiTools.getYearString());
	}
	
	public void resetData(NaiiDto formObj){
		id = formObj.id;
		try {
			table.setNaiiDtoModel(0, 
					NaiiControl.getControl().queryNaiiDto(
							new com.naii.db.dto.NaiiEvent(formObj.id, true),
							NaiiControl.FILTER_EVENT),
					NaiiTools.FILTER_EVENTS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

