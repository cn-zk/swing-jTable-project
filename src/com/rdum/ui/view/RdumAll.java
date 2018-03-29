package com.rdum.ui.view;

import java.awt.BorderLayout;

import com.rdum.db.RdumUser;
import com.rdum.tools.RdumTools;
import com.rdum.ui.swing.JFixedTable;

public class RdumAll extends RdumView{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFixedTable fx;
	
	public RdumAll() {
		setLayout(new BorderLayout());
		add(fx = new JFixedTable());
		refush();
	}

	@Override
	public String getState() {
		return "rows= "+RdumTools.db.getUsers().size()+", cols= "+(RdumUser.class.getFields().length-1);
	}

	public JFixedTable getFixedTable() {
		return fx;
	}

	@Override
	public void search(String text) {
		fx.selectRow(2, text);
	}
	
	@Override
	public void refush() {
		fx.setModel(2,
				RdumTools.converTable(), 
				RdumTools.converTableHead());
	}
}
