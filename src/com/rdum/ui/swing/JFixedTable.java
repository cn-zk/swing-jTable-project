package com.rdum.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.rdum.tools.RdumTools;

public class JFixedTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object[][] tableData;
	@SuppressWarnings("unused")
	private Object[]	tableHead;
	
	private FixedTable left, cent;

	int hiddenIndex = 0;

	JScrollPane leftSp, centSp;
	public JFixedTable() {
		setLayout(new BorderLayout());
		
		left = new FixedTable();
		cent = new FixedTable(left);
		left.setLinkTable(cent);

		add(leftSp = new JScrollPane(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			{
				setBorder(BorderFactory.createLineBorder(Color.white, 0));
				getViewport().setView(left);
				setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL){
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void paint(Graphics g) {
						g.setColor(Color.lightGray);
						g.fillRect(0, 0, getWidth(), getHeight());
					};
				});
				setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
				setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
			}
			
			@Override
			public void paint(Graphics g) {
				if(left.getWidth() != getWidth()){
					getParent().getParent().revalidate();
				}
				super.paint(g);
			}
			
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width = left.getWidth();
				return d;
			}
		}, BorderLayout.WEST);
		add(centSp = new JScrollPane(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				setBorder(BorderFactory.createLineBorder(Color.white, 0));
				setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
				getViewport().setView(cent);
				
				getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
					
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						leftSp.getVerticalScrollBar().setValue(e.getValue());
					}
				});
				
			}
		}, BorderLayout.CENTER);
	}

	public JFixedTable(int fixed, Object[][] tableData, Object[] tableHead) {
		this();
		setModel(fixed, tableData, tableHead);
	}
	
	
	public void setModel(int fixed, Object[][] tableData, Object[] tableHead){
		this.tableData = tableData ;
		this.tableHead = tableHead;
		Object[][] leftData = new Object[tableData.length][fixed];
		Object[]	leftHead = new Object[fixed];
		
		int hiddenCount = 1;
		
		Object[][] centData = new Object[tableData.length][tableHead.length-fixed-hiddenCount];
		Object[]	centHead = new Object[tableHead.length-fixed-hiddenCount];
		
		
		for(int i=0;i<tableData.length;i++){
			for(int j=0;j<fixed;j++){
				leftData[i][j] = tableData[i][j+hiddenCount];
			}
		}
		for(int j=0;j<fixed;j++){
			leftHead[j] = tableHead[j+hiddenCount];
		}
		
		for(int i=0;i<tableData.length;i++){
			for(int j=0;j<tableData[i].length-fixed-hiddenCount;j++){
				centData[i][j] = tableData[i][j+fixed+hiddenCount];
			}
		}
		for(int j=0;j<tableHead.length-fixed-hiddenCount;j++){
			centHead[j] = tableHead[j+fixed+hiddenCount];
		}
		
		left.setModel(new DefaultTableModel(leftData, leftHead));
		cent.setModel(new DefaultTableModel(centData, centHead));
		
		FitTableColumns(left);
		FitTableColumns(cent);
		
	}
	
	public void selectRow(int index, String value){
		if(value == null){
			return ;
		}
		int row = getSelectRow();
		for(int i= row != -1 ? row : 0; i< tableData.length ;i++){
			if(tableData[i][index] != null && 
					String.valueOf(tableData[i][index]).indexOf(value) != -1){
				if(i != row){
					if(row != -1 && 
							String.valueOf(tableData[row][index]).indexOf(value) != -1 &&
							row > i){
						continue;
					}else{
						selectRow(i);
						return;
					}
				}
			}
			selectRow(-1);
		}
	}
	
	public void selectRow(int index){
		if(index == -1){
			left.clearSelection();
			cent.clearSelection();
		}else{
			left.setRowSelectionInterval(index, index);
			cent.setRowSelectionInterval(index, index);
			centSp.getVerticalScrollBar().setValue(index * 28);
		}
	}
	
	public int getSelectRow() {
		return left.getSelectedRow();
	}

	public void FitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		Enumeration<?> columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) myTable.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(width + myTable.getIntercellSpacing().width + 20);
		}
	}
}

class FixedTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	FixedTable table;
	
	public FixedTable() {

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setReorderingAllowed(false);
		
		setRowHeight(28);
	}
	
	public FixedTable(FixedTable table) {
		this();
		setLinkTable(table);
	}
	
	public void setLinkTable(FixedTable table1){
		this.table = table1;
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting() && FixedTable.this.getSelectedRow() != -1){
					table.setRowSelectionInterval(FixedTable.this.getSelectedRow(),FixedTable.this.getSelectedRow());
				}
			}
		});
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return new TableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				String str = (String) value;
				if(str != null && str.length() > 30){
					str = str.substring(0, 30)+"...";
				}
				
				// convert name.
				if(column == 8 &&!RdumTools.isEmpty(str)){
					str = RdumTools.getUserName(str)+" 小组";
				}
				
				JLabel la = new JLabel(str);
				
				if(column == 12){
					la.setHorizontalAlignment(JLabel.LEFT);
				}else{
					la.setHorizontalAlignment(JLabel.CENTER);
				}
				
				if(column == 5 && RdumTools.isEmpty(str)){
					la.setBackground(new Color(220, 220, 81));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if(column == 6 && "是".equals(str)){
					la.setBackground(new Color(51, 150, 51));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if(column == 7 && "是".equals(str)){
					la.setBackground(new Color(151, 151, 250));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if(column == 10 && !RdumTools.isEmpty(str)){
					la.setBackground(new Color(255, 153, 51));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if(isSelected){
					la.setBackground(new Color(51, 153, 255));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}
				return la;
			}
		};
	}
}