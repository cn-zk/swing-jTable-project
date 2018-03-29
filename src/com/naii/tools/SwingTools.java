package com.naii.tools;

import javax.swing.ActionMap;
import javax.swing.JTable;

public class SwingTools {
	
	
	/**
	 * 移除Swing JTable 回车事件
	 * @param table
	 */
	public static void removeTableEnterEventHandle(JTable table) { 
		ActionMap am = table.getActionMap(); 
		am.getParent().remove("selectNextRowCell"); 
		table.setActionMap(am); 
	} 

}
