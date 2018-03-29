package com.rdum.ui.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.rdum.db.RdumUser;
import com.rdum.tools.RdumTools;
import com.rdum.ui.swing.JFixedTable;

public class RdumTabs extends RdumView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabs;
	
	private JFixedTable group, source, quit;
	java.util.Map<String, List<RdumUser>> maps;
	
	public RdumTabs() {
		setLayout(new BorderLayout());
		
		add(tabs = new JTabbedPane());
		
		tabs.addTab("分组", group = new JFixedTable());
		tabs.addTab("资源池", source = new JFixedTable());
		tabs.addTab("离职", quit = new JFixedTable());
		
		refush();
		
		tabs.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				RdumTools.ui.flushState();
			}
		});
	}
	
	@Override
	public void refush(){
		maps = RdumTools.db.getTabsList();
		
		group.setModel(2, RdumTools.converTable(maps.get("group")),
				RdumTools.converTableHead());
		source.setModel(2, RdumTools.converTable(maps.get("source")),
				RdumTools.converTableHead());
		quit.setModel(2, RdumTools.converTable(maps.get("quit")),
				RdumTools.converTableHead());
	}
	
	@Override
	public String getState() {
		List<RdumUser> l = null;
		switch(tabs.getSelectedIndex()){
		case 0 :
			l = maps.get("group");
			break;
		case 1:
			l = maps.get("source");
			break;
		case 2:
			l = maps.get("quit");
			break;
		}
		return "ROWS= "+l.size();
	}

	@Override
	public void search(String text) {
		JFixedTable t = null;
		switch(tabs.getSelectedIndex()){
		case 0 :
			t = group;
			break;
		case 1:
			t = source;
			break;
		case 2:
			t = quit;
			break;
		}
		if(t != null)
		t.selectRow(2, text);
	}

}
