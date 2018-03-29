package com.rdum.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.rdum.db.RdumUser;
import com.rdum.tools.RdumTools;
import com.rdum.ui.swing.JFixedTable;

public class RdumRecycle extends RdumView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFixedTable table;
	private JList list; 
	
	public RdumRecycle() {
		
		setLayout(new BorderLayout());
		
		add(new JScrollPane(list = new JList(RdumTools.getRecycleDirList())){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
				setPreferredSize(new Dimension(100, 1));
				setBorder(BorderFactory.createLineBorder(Color.white, 0));
			}
		}, BorderLayout.WEST);
		
		add(table = new JFixedTable(), BorderLayout.CENTER);
		add(new JLabel(" "), BorderLayout.SOUTH);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					List<RdumUser> users = null;
					try {
						users = RdumTools.db.loadRecycleUser(
								(String)list.getSelectedValue());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					table.setModel(2, RdumTools.converTable(users),
							RdumTools.converTableHead());
					table.revalidate();
				}
			}
		});
	}
	
	@Override
	public String getState() {
		return null;
	}

	@Override
	public void search(String text) {
		table.selectRow(2, text);
	}

}
