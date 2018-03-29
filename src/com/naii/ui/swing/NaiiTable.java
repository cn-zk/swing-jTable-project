package com.naii.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.Assist;
import com.naii.tools.NaiiTools;
import com.naii.tools.SwingTools;
import com.naii.ui.inf.NaiiClickListener;
import com.naii.ui.inf.NaiiSwingInterface;

public class NaiiTable extends JPanel implements NaiiSwingInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NaiiFixedTable left, cent;

	private JScrollPane leftSp, centSp;
	private List<NaiiDto> modelData;
	private NaiiClickListener l;
	
	private String dateFormat = null;
	
	public NaiiTable() {
		this(false);
	}
	public NaiiTable(boolean main) {
		setLayout(new BorderLayout());
		
		left = new NaiiFixedTable(main);
		cent = new NaiiFixedTable(main, left);
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
				
				getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
					
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						centSp.getVerticalScrollBar().setValue(e.getValue());
					}
				});
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
	
	public void setNaiiDtoModel(int fixed, List<NaiiDto> data, String... filter) throws Exception{
		setNaiiDtoModel(fixed, data, false, filter);
	}
	public void setNaiiDtoModel(int fixed, List<NaiiDto> data, boolean in, String... filter) throws Exception{
		NaiiDto row = getSelectRowObject();
		int selectIndex = -1;
		
		Vector<Object>			leftH=new Vector<Object>(0);
		Vector<Object>			leftH0=new Vector<Object>(0);
		Vector<Vector<Object>>	leftD=new Vector<Vector<Object>>(0);
		
		Vector<Object>			centH=new Vector<Object>(0);
		Vector<Object>			centH0=new Vector<Object>(0);
		Vector<Vector<Object>>	centD=new Vector<Vector<Object>>(0);
		
		Object[] names = null;
		Field[] fields = null;
		
		this.modelData = data;
		boolean tflag = false;
		if(data != null && data.size() > 0){
			int num=0;
			for(NaiiDto obj:data){
				if(fields == null){
					fields = NaiiTools.getDtoFields(obj.getClass());
					names = NaiiTools.getRetentionNames(fields);
					
					leftH=new Vector<Object>(5);
					leftH0=new Vector<Object>(5);
					leftD=new Vector<Vector<Object>>(data.size());
					
					centH=new Vector<Object>(fields.length);
					centH0=new Vector<Object>(fields.length);
					centD=new Vector<Vector<Object>>(data.size());
				}
				Vector<Object> v = new Vector<Object>();
				for(int i=0,j=0;i<fixed && j < fields.length;j++){
					tflag = Assist.contains(filter, fields[j].getName());
					if(in ? !tflag : tflag){
						continue;
					}
					v.add(NaiiTools.convertData(fields[j].get(obj), dateFormat));
					i++;
				}
				v.add(0, ""+(num+1));
				leftD.add(v);
				
				if(row != null && row.id.equals(obj.id)){
					selectIndex = num;
				}
				
				++num;
			}
			int x=0;
			for(int i=0;i<fixed && x < fields.length;x++){
				tflag = Assist.contains(filter, fields[x].getName());
				if(in ? !tflag : tflag){
					continue;
				}
				leftH.add(names[x]);
				leftH0.add(fields[x].getName());
				i++;
			}
			
			for(NaiiDto obj:data){
				Vector<Object> v = new Vector<Object>();
				for(int i=fixed,j=x;i<names.length && j < fields.length;j++){
					tflag = Assist.contains(filter, fields[j].getName());
					if(in ? !tflag : tflag){
						continue;
					}
					v.add(NaiiTools.convertData(fields[j].get(obj), dateFormat));
					i++;
				}
				centD.add(v);
			}
			for(;x<names.length && x < fields.length;x++){
				tflag = Assist.contains(filter, fields[x].getName());
				if(in ? !tflag : tflag){
					continue;
				}
				centH.add(names[x]);
				centH0.add(fields[x].getName());
			}
		}
		leftH0.add(0, "NUMBER");
		String[] arr = new String[leftH0.size()];
		leftH0.toArray(arr);
		leftH.add(0, "序号");
		
		left.setData(leftD, leftH, arr);
		arr = new String[centH0.size()];
		centH0.toArray(arr);
		cent.setData(centD, centH, arr, true);
		
		fitTableColumns(left);
		fitTableColumns(cent);
		
		left.revalidate();
		this.revalidate();
		
		if(selectIndex != -1){
			selectRow(selectIndex);
		}else if(data.size() > 0){
			selectRow(0);
		}
		cent.requestFocus();
	}
	
	public void searchRowField(String text){
		int li = left.searchFieldRowIndex(text),
			ci = cent.searchFieldRowIndex(text);
		
		if(li != -1 && ci != -1){
			selectRow(li > ci ? ci : li);
		}else if(li != -1){
			selectRow(li);
		}else if(ci != -1){
			selectRow(ci);
		}else {
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
			leftSp.getVerticalScrollBar().setValue(index * 28);
		}
	}
	
	public int getSelectRow() {
		return left.getSelectedRow();
	}
	
	public NaiiDto getSelectRowObject(){
		return modelData == null || cent.getSelectedRow() == -1 ? null : modelData.get(cent.getRowSorter().convertRowIndexToModel(cent.getSelectedRow()));
	}

	public void fitTableColumns(JTable myTable) {
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

	public NaiiDto[] getSelectRowIds() {
		int[] rows = cent.getSelectedRows();
		NaiiDto[] dtos = new NaiiDto[rows.length];
		int n =0;
		for(int i : rows){
			dtos[n ++ ] = modelData.get(cent.getRowSorter().convertRowIndexToModel(i));
		}
		return dtos;
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

	public void setClickListener(NaiiClickListener naiiClickListener) {
		l = naiiClickListener;
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1){
					l.dblClickRow(NaiiTable.this);
				}
			}
		});
		cent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1){
					l.dblClickRow(NaiiTable.this);
				}
			}
		});
	}
	public void setForamt(String format) {
		dateFormat = format;
	}
}
class NaiiFixedTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NaiiFixedTable table;
	private String[] fields;
	private Vector<Object> head;
	private Vector<Vector<Object>> data;
	
	public NaiiFixedTable(boolean main) {

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setReorderingAllowed(false);
		
		setRowHeight(28);
		
		SwingTools.removeTableEnterEventHandle(this);
		
		if(main)
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(32 == e.getKeyCode() || 10 == e.getKeyCode()){
					NaiiControl.getControl().getNaiiUI().getButtons().clickEdit();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(table != null && (38 == e.getKeyCode() || 40 == e.getKeyCode())){
					table.setRowSelectionInterval(NaiiFixedTable.this.getSelectedRow(),NaiiFixedTable.this.getSelectedRow());
				}
			}
		});
	}
	
	public NaiiFixedTable(boolean main, NaiiFixedTable tb) {
		this(main);
		setLinkTable(tb);
	}
	
	public int searchFieldRowIndex(String text) {
		if(data == null){
			return -1;
		}
		Iterator<Vector<Object>> iter = data.iterator();
		int index = getSelectedRow();
		int rowIndex = 0;
		while(iter.hasNext()){
			if(rowIndex > index){
				Iterator<Object> row = iter.next().iterator();
				while(row.hasNext()){
					String str = (String) row.next();
					if(str != null && str.indexOf(text) != -1){
						if(getRowSorter() != null){
							return getRowSorter().convertRowIndexToView(rowIndex);
						}else
						return rowIndex;
					}
				}
			}else{
				iter.next();
			}
			rowIndex ++;
		}
		return -1;
	}

	public void setData(Vector<Vector<Object>> d, Vector<Object> h, String[] h0) {
		setData(d, h, h0, false);
	}
	public void setData(Vector<Vector<Object>> d, Vector<Object> h, String[] h0, boolean sort) {
		this.fields = h0;
		this.data = d;
		this.head = h;
		setModel(new DefaultTableModel(d, h), sort);
	}
	
	public Vector<Vector<Object>> getData(){
		return this.data;
	}
	
	public void updateData(Vector<Vector<Object>> data){
		this.data = data;
		setModel(new DefaultTableModel(data, this.head));
	}

	public void setLinkTable(NaiiFixedTable table1){
		this.table = table1;
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting() && NaiiFixedTable.this.getSelectedRow() != -1){
					table.setRowSelectionInterval(NaiiFixedTable.this.getSelectedRow(),NaiiFixedTable.this.getSelectedRow());
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
				
				String colField = column >= fields.length ? "": fields[column];
				
				String str0 = str;
				// convert name.
				if("groupId".equals(colField) &&!Assist.isEmpty(str0)){
					str0 = NaiiTools.getUserName(str0)+" 小组";
				}else if(Assist.contains(new String[]{"userId", "useId"}, colField)){
					str0 = NaiiTools.getUserName(str0);
				}
				
				JLabel la = new JLabel(NaiiProperty.getProperty().formatter(colField, str0));
				
				if("ip".equals(colField)){
					la.setHorizontalAlignment(JLabel.LEFT);
				}else{
					la.setHorizontalAlignment(JLabel.CENTER);
				}
				
				if("resource".equals(colField) && !Assist.isEmpty(str)){
					la.setBackground(new Color(51, 150, 51));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if("group".equals(colField) && "1".equals(str)){
					la.setBackground(new Color(151, 151, 250));
					la.setForeground(Color.white);
					la.setOpaque(true);
				}else if("quit".equals(colField) && !Assist.isEmpty(str)){
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
	
	public void setModel(TableModel dataModel, boolean sort) {
		setModel(dataModel);

		if(sort){
			setAutoCreateRowSorter(true);
			getRowSorter().addRowSorterListener(new RowSorterListener() {
				@Override
				public void sorterChanged(RowSorterEvent e) {
					if(RowSorterEvent.Type.SORTED.equals(e.getType()) && table.getColumnCount() > 1){
						Vector<Vector<Object>> d = table.getData();
						Vector<Vector<Object>> dd = new Vector<Vector<Object>>(d.size());
						int i=0;
						for(;i<d.size();i++){
							dd.add(new Vector<Object>(0));
						}
						i=0;
						Iterator<Vector<Object>> iter = d.iterator();
						while(iter.hasNext()){
							int index = e.getSource().convertRowIndexToView(i++);
							Vector<Object> v = iter.next();
							v.set(0, index + 1 + "");
							dd.set(index, v);
						}
						table.updateData(dd);
					}
				}
			});
		}
	}
}
