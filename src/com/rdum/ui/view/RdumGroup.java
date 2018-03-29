package com.rdum.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.rdum.db.RdumTempUser;
import com.rdum.tools.RdumTools;

public class RdumGroup extends JLabel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String[][] options = new String[][]{
		{"save","确定"}, 
		{"cancel","取消"}
	};
	JDialog d ;
	
	JList list;
	String id;
	
	public RdumGroup() {
		
		setBorder(BorderFactory.createLineBorder(Color.gray));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		d = new JDialog(RdumTools.ui.getFrame(), true);
		d.setTitle("Group");
		d.setSize(new Dimension(300, 400));
		d.setResizable(false);
		d.setLocationRelativeTo(null);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setLayout(new BorderLayout());
		d.add(new JScrollPane(list = new JList()){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}
		}, BorderLayout.CENTER);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == 1){
					Object[] l = RdumTools.getGroupList();
					list.setListData(l);
					if(l.length > 0){
						list.setSelectedIndex(0);
					}
					d.setVisible(true);
				}else{
					setValue(null);
				}
			}
		});
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		addButtons(south, options);
		d.add(south, BorderLayout.SOUTH);
	}
	
	public void addButtons(JComponent panel, String[][] options){
		for(final String[] option: options){
			JButton b = new JButton(option[1]);
			b.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if("save".equals(option[0])){
						RdumTempUser tu = (RdumTempUser) list.getSelectedValue();
						RdumGroup.this.id = tu.getUser().id;
						RdumGroup.this.setText(tu.getUser().name);
					}
					d.dispose();
				}
			});
			panel.add(b);
		}
	}
	
	public void setValue(String id){
		this.id = id;
		setText(RdumTools.getUserName(id));
	}
	
	public String getValue(){
		return id;
	}
}
