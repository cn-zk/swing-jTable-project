package com.naii.ui.dialog.imp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;

import com.naii.ctr.NaiiChangeEvent;
import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiConfig;
import com.naii.db.NaiiProperty;
import com.naii.db.annotation.NaiiRetention;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEvent;
import com.naii.db.dto.NaiiHistory;
import com.naii.db.dto.NaiiUser;
import com.naii.db.dto.NaiiValue;
import com.naii.tools.NaiiTools;

@SuppressWarnings("serial")
public class NaiiImportDialog extends JDialog{

	private HSSFWorkbook wb;

	private JComboBox box, month;
	private JLabel lab;
	private JTable tab;
	private JProgressBar bar;
	
	private JButton imp, cancle;
	
	private String[][] data;
	
	private static final String IMP_KEY = "1";
	private static final String IMP_EVENT = "2";
	
	public NaiiImportDialog() {
		
		super(NaiiControl.getControl().currentWondow());
		setModal(true);
		setSize(new Dimension(500, 400));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable( false);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		try {
			box = (JComboBox) this.createInfo(panel, "Sheet：", JComboBox.class);
			month = (JComboBox) this.createInfo(panel, "Month：", JComboBox.class);
			lab = (JLabel) this.createInfo(panel, "Number：", JLabel.class);
			
			month.setModel(new DefaultComboBoxModel(NaiiTools.getMonths(12)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(tab = new JTable(){
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
						String str = String.valueOf(value);
						if(column == 4){
							if(IMP_KEY.equals(str)){
								str = "key";
							}else if(IMP_EVENT.equals(str)){
								str = "event";
							}else{
								str = "unknown";
							}
						}
						JLabel la = new JLabel(str, JLabel.CENTER);
						if(isSelected){
							la.setOpaque(true);
							la.setBackground(new Color(51, 153, 255));
							la.setForeground(Color.white);
						}
						return la;
					}
				};
			}
		}));
//		tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tab.getTableHeader().setReorderingAllowed(false);
		tab.setRowHeight(28);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		try {
			bar = (JProgressBar) createInfo(south, "improt：", JProgressBar.class);
			bar.setPreferredSize(new Dimension(250, 25));
			bar.setStringPainted(true);
		} catch (Exception e1) {}
		south.add(imp = new JButton("import"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						importData();
					}
				});
			}
		});
		south.add(cancle = new JButton("cancle"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						closeForm();
					}
				});
			}
		});
		add(south, BorderLayout.SOUTH);
		south.getComponent(0).setVisible(false);
	}
	
	public void closeForm(){
		this.dispose();
	}
	
	public JComponent createInfo(JComponent panel, final String tit, Class<?> clas) throws Exception{
		
		final JComponent comp = (JComponent) clas.newInstance();
		if(comp instanceof JComboBox){
			
			((JComboBox)comp).addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						loadSheet(box.getSelectedIndex());
					}
				}
			});
		}
		
		panel.add(new JComponent() {
			{
				setLayout(new BorderLayout());
				add(new JLabel(tit, JLabel.RIGHT){
					{
						setPreferredSize(new Dimension(65, 23));
					}
				}, BorderLayout.WEST);
				add(comp, BorderLayout.CENTER);
			}
		});
		return comp;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void loadSheet(int index){
		String[][] rows;
		int number = 0;
		if(index == 0){
			rows = new String[0][3];
		}else{
			HSSFSheet sheet = wb.getSheetAt(index-1);
			HSSFRow row = sheet.getRow(0);
			if(row != null){
				rows = new String[row.getLastCellNum()][3];
				number = sheet.getPhysicalNumberOfRows();
				int i=0;
				Iterator<HSSFCell> iter = row.iterator();
				
				while(iter.hasNext()){
					String name = iter.next().getStringCellValue();
					String[] fields = getDtoFieldsByName(NaiiUser.class, name);
							 fields = getEventTypeByName(fields, name);
					rows[i++] = new String[]{(i+""), name, fields[0], fields[1], fields[2]};
				}
			}else{
				rows = new String[0][3];
			}
		}
		data = rows;
		lab.setText(number+"");
		tab.setModel(new DefaultTableModel(rows, new String[]{"number","cellName", "fieldName", "field", "key"}));
	}
	
	private String[] getDtoFieldsByName(Class<?> clas, String name){
		NaiiRetention nr;
		for(Field fd : clas.getDeclaredFields()){
			nr = fd.getAnnotation(NaiiRetention.class);
			if(name.indexOf(nr.name()) != -1){
				return new String[]{nr.name(), fd.getName(), "1"};
			}
		}
		
		return new String[]{"", "", "0"};
	}
	
	private String[] getEventTypeByName(String[] fields, String name){
		if("0".equals(fields[2])){
			NaiiValue[] vs = NaiiProperty.getProperty().getFormat(NaiiConfig.KEY_EVENT);
			for(NaiiValue v : vs){
				if(name.indexOf(v.getName()) != -1){
					return new String[]{v.getName(), v.getId(), IMP_EVENT};
				}
			}
		}
		return fields;
	}
	
	private void unLockBut(boolean flag){
		box.setEnabled(flag);
		month.setEnabled(flag);
		imp.setEnabled(flag);
		cancle.setEnabled(flag);
	}
	
	@SuppressWarnings({ "unchecked"})
	private void importData(){
		int index = box.getSelectedIndex(); 
		if(index == 0){
			unLockBut(true);
			return ;
		}
		unLockBut(false);
		final List<NaiiEvent> events = new ArrayList<NaiiEvent>();
		HSSFSheet sheet = wb.getSheetAt(index-1);
		Iterator<HSSFRow> iter = sheet.rowIterator();
		iter.next();
		StringBuffer errbuf = new StringBuffer();
		int rowIndex = 0;
		while(iter.hasNext()){
			++rowIndex;
			try {
				HSSFRow row = iter.next();
				
				NaiiDto dto = getNaiiObject(NaiiUser.class, row);
				if(NaiiTools.isEmpty(dto)){
					continue;
				}
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				for(String[] cell: data){
					if(IMP_EVENT.equals(cell[4])){
						List<String> str = map.get(cell[3]);
						if(str == null){
							str = new ArrayList<String>();
						}
						String val = row.getCell(Integer.parseInt(cell[0])-1).toString();
						str.add(val.trim().length() > 0 ? val: null);
						map.put(cell[3], str);
					}
				}
				
				addEvent(map, dto, events);
				
			} catch (Exception e) {
				errbuf.append("[row:"+rowIndex+"] " + e.getMessage());
			}
		}
		
		String msg = errbuf.toString();
		if(msg.length() > 60){
			msg = msg.substring(0, 60)+"...";
		}
		
		// error message.
		if(msg.length() > 0 && JOptionPane.showConfirmDialog(
				NaiiImportDialog.this, "错误信息：\n"+msg+"\n是否继续?", 
				"Warring", JOptionPane.YES_NO_OPTION) == 0){
			errbuf.setLength(0);
		}
		
		if(errbuf.length() < 1){
			// set JProgressBar.
			bar.getParent().setVisible(true);
			bar.setModel(new DefaultBoundedRangeModel(0, 0, 0, events.size()));
			
			// save thread.
			new Thread(new Runnable() {
				public void run() {
					int i=1;
					Iterator<NaiiEvent> diter = events.iterator();
					NaiiDto dto = null;
					JSONArray arr = new JSONArray();
					while(diter.hasNext()){
						try {
							dto = diter.next();
							NaiiControl.getControl().saveObject(dto, NaiiChangeEvent.OPTION_IMOROT);
						} catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(dto != null && dto.id != null){
								arr.put(dto.toCutJSON());
							}
							bar.setValue(i++);
						}
					}
					
					// set history property.
					NaiiHistory h = new NaiiHistory();
					h.name = "导入["+month.getSelectedItem()+"]";
					h.option = NaiiHistory.OPTION_IMPORT;
					h.key = NaiiConfig.KEY_EVENT;
					h.value = arr.toString();
					h.date = new Date();
					
					// create history remark.
					StringBuffer buf = new StringBuffer();
					for(String[] cell: data){
						buf.append(NaiiProperty.getProperty().formatter(NaiiConfig.KEY_EVENT, cell[3])+",");
					}
					buf.append(" \n引用类["+NaiiUser.class.getName()+"]");
					h.remark = buf.toString();
					
					// save history.
					NaiiControl.getControl().saveObject(h);	
					JOptionPane.showMessageDialog(NaiiImportDialog.this, "import event success "+events.size()+" !");
					unLockBut(true);
				}
			}).start();
		}else{
			unLockBut(true);
		}
	}
	
	/**
	 * add event.
	 * 
	 * @param map 
	 * @param dto
	 * @param events
	 * @throws Exception
	 */
	private void addEvent(Map<String, List<String>> map, NaiiDto dto, List<NaiiEvent> events) throws Exception {
		for(Entry<String, List<String>> entry : map.entrySet()){
			NaiiEvent e = new NaiiEvent(dto.id, true);
			e.event = entry.getKey();
			e.name = "[导入]"+ NaiiProperty.getProperty().formatter(NaiiConfig.KEY_EVENT, e.event);
			
			if(entry.getValue().size() < 2){
				// one entry.
				e.value = NaiiTools.filterNumber(entry.getValue().get(0));
				e.remark = entry.getValue().get(0);
			}else if("5".equals(e.event)){
				// item key.
				e.value = 1f;
				StringBuffer buf = new StringBuffer();
				for(String str: entry.getValue()){
					if(str == null){
						continue;
					}
					buf.append(str+"\n");
				}
				e.remark = buf.toString();
			}else{
				// entrys.
				float max = 0;
				StringBuffer buf = new StringBuffer();
				for(String str: entry.getValue()){
					if(str == null){
						continue;
					}
					int t = 0;
					try{
						t = Integer.parseInt(str);
					}catch(Exception ee){
					}
					if(t > max && t < 100){
						max = t;
					}
					buf.append(str+"\n");
				}
				e.value = max;
				e.remark = buf.toString();
			}
			try {
				e.date = sf.parse(String.valueOf(month.getSelectedItem()));
			} catch (ParseException e1) {
				throw e1;
			}
			if(e.value <= 0){
				continue;
			}
			events.add(e);
		}
	}
	
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
	
	private NaiiDto getNaiiObject(Class<? extends NaiiDto> clas, HSSFRow row) throws Exception{
		
		NaiiDto dto = clas.newInstance();
		
		for(String[] cell : data){
			if("1".equals(cell[4])){
				
				String val = row.getCell(Integer.parseInt(cell[0])-1).toString();
				if("name".equals(cell[3])){
					if(val.indexOf("(") != -1){
						val = val.substring(0, val.indexOf("("));
					}
					val = val.trim();
				}
				clas.getField(cell[3]).set(dto, val);
			}
		}
		return NaiiTools.compareNaiiDto(dto);
	}
	
	@SuppressWarnings("unchecked")
	public void showDialog(File f) throws Exception{

		bar.getParent().setVisible(false);
		InputStream is = new FileInputStream(f);  
		wb = new HSSFWorkbook(is);
		
		String[] sheets = new String[wb.getNumberOfSheets()+1];
		sheets[0] = "请选择";
		for(int i=1;i<sheets.length;i++){
			sheets[i] = wb.getSheetName(i-1);
		}
		box.setModel(new DefaultComboBoxModel(sheets));
		loadSheet(0);

		setLocationRelativeTo(NaiiControl.getControl().currentWondow());
		setVisible(true);
	}
	
//	public static void main(String[] args) throws Exception{
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		
//		NaiiControl.getControl().testLoad();
//		
//		String path = "C:\\Users\\TouchWin\\Desktop\\2016-07-01_naii\\2016-04 考勤统计\\2016-04考勤统计.xls";
//		new NaiiImportDialog().showDialog(new File(path));
//	}
}
