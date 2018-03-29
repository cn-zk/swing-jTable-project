package com.naii.ui.dialog.exp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiProperty;
import com.naii.db.annotation.NaiiRetention;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiUser;
import com.naii.tools.Assist;
import com.naii.tools.NaiiTools;

@SuppressWarnings("serial")
public class NaiiExportDialog extends JDialog{

	private HSSFWorkbook wb;
	private NaiiExportImg img;

	private JButton exp, cancle;
	
	private JFileChooser fc;
	
	private JLabel la;
	private JPanel cent;
	
	private String[] FILTER_FIELDS = new String[]{
		"name",
		"phone",
		"level",
		"skill",
		"work",
		"resource",
		"groupId",
		"entry"
	};
	
	public NaiiExportDialog() {
		
		super(NaiiControl.getControl().currentWondow());
		setModal(true);
		setSize(new Dimension(320, 280));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		cent = new JPanel();
		cent.setBackground(Color.white);
		cent.setLayout(new FlowLayout(FlowLayout.LEFT));
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		cent.add(la = new JLabel(){
			{
				setPreferredSize(new Dimension(300, 25));
				setBorder(BorderFactory.createLineBorder(Color.gray));
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				addMouseListener(new MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e) {
						if(fc.showSaveDialog(NaiiExportDialog.this) == 0){
							((JLabel) e.getSource()).setText(fc.getSelectedFile().getPath());
						}
					};
				});
			}
		});
		
		for(final Field f : NaiiUser.class.getFields()){
			JPanel p = new JPanel();
			p.setBackground(Color.white);
			p.setPreferredSize(new Dimension(90, 25));
			p.setLayout(new BorderLayout());
			
			p.add(new JLabel(f.getAnnotation(NaiiRetention.class).name()){
				{
					setPreferredSize(new Dimension(50, 25));
					addMouseListener(new MouseAdapter() {
						public void mousePressed(java.awt.event.MouseEvent e) {
							JComponent c = (JComponent) e.getSource();
							JCheckBox cbo = (JCheckBox) c.getParent().getComponent(1);
							cbo.setSelected(!cbo.isSelected());
						};
					});
				}
				public String toString() {
					return f.getName();
				};
			}, BorderLayout.CENTER);
			p.add(new JCheckBox(){
				{
					setBackground(Color.white);
					setSelected(Assist.contains(FILTER_FIELDS, f.getName()));
				}
			}, BorderLayout.WEST);
			cent.add(p);
		}
		
		add(cent);
		
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		south.add(exp = new JButton("export"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							exportData();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
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
		
		setResizable(false);
//		south.getComponent(0).setVisible(false);
		img = new NaiiExportImg();
	}
	
	private short[] bgs = new short[]{
		HSSFColor.CORAL.index,
		HSSFColor.LIME.index,
		HSSFColor.PALE_BLUE.index,
		HSSFColor.LIGHT_YELLOW.index,
		HSSFColor.GREY_25_PERCENT.index
	};
	
	private int tn;
	private HSSFCellStyle getHSSFCellStyle(int it){
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		
        font.setFontHeightInPoints((short) 11); //字体高度
        font.setColor(HSSFFont.COLOR_NORMAL); //字体颜色
        font.setFontName("微软雅黑"); //字体
//        font.setItalic(true); //是否使用斜体
//        font.setStrikeout(true); //是否使用划线

        // 设置单元格类型
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //水平布局：居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平布局：居中
        cellStyle.setWrapText(false);
        
        switch (it) {
		case 0:
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //宽度
	        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
	        tn = 0;
			break;
		case 1:
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    cellStyle.setFillForegroundColor(bgs[tn ++]);
		    if(tn >= bgs.length){
		    	tn = 0;
		    }
			break;
		default:
			break;
		}
        
        cellStyle.setFont(font);
        return cellStyle;
	}
	
	private String[] getSelectFields(){
		List<String> list = new ArrayList<String>(25);
	
		for(Component c: cent.getComponents()){
			if(!(c instanceof JLabel)){
				Component[] cs = ((JComponent)c).getComponents();
				JCheckBox box = (JCheckBox) cs[1];
				if(box.isSelected()){
					list.add(cs[0].toString());
				}
			}
		}
		
		String[] fs = new String[list.size()];
		list.toArray(fs);
		return fs;
	}
	
	@SuppressWarnings("deprecation")
	public void exportData() throws Exception{
		if("".equals(la.getText())){
			return;
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(la.getText()+File.separator+sf.format(new Date())+"_export.xls"));
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(NaiiExportDialog.this, e1.getMessage().replace("(", "\n("));
			return;
		}
		wb = new HSSFWorkbook();
		
		HSSFSheet sheet = wb.createSheet("分组明细导出");
		HSSFRow title = sheet.createRow(0);
		
		String[] filter = getSelectFields();
        
		int cels=0, groupIndex=0;
		Field[] fds = NaiiUser.class.getFields();
		// set title cells.
		for(Field f : fds){
			if(!Assist.contains(filter, f.getName())){
				continue;
			}
			HSSFCell cell = title.createCell(cels++);
			cell.setCellStyle(getHSSFCellStyle(0));
			cell.setCellValue(f.getAnnotation(NaiiRetention.class).name());
		}
		
		title.setHeightInPoints(23.25f);
		
        List<NaiiDto> dtos = NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_ENTRY);
        
        Map<String, List<String[]>> groups = new HashMap<String, List<String[]>>(15);
        
        // get group index.
        for(Field f : fds){
        	if(!Assist.contains(filter, f.getName())){
				continue;
			}
        	if("groupId".equals(f.getName())){
        		break;
        	}
        	groupIndex ++;
        }
        
        // sort group user.
		for(NaiiDto u : dtos){
			int i=1;
			String[] vs = new String[filter.length+1];
			for(Field f : fds){
				
				Object obj = f.get(u);
				if("id".equals(f.getName())){
					vs[0] = (String) obj;
				}
				if(!Assist.contains(filter, f.getName())){
					continue;
				}
				if(obj == null){
					obj = "";
				}else if(obj instanceof Date){
					obj = new SimpleDateFormat("yyyy-MM-dd").format((Date)obj);
				}else {
					obj = String.valueOf(obj);
					obj = NaiiProperty.getProperty().formatter(f.getName(), (String) obj);
				}
				vs[i++] = (String) obj;
			}
			List<String[]> list = groups.get(vs[groupIndex+1]);
			if(list == null){
				list = new ArrayList<String[]>();
				list.add(vs);
				groups.put(vs[groupIndex+1], list);
			}else{
				list.add(vs);
			}
		}
		
		int begin =1, end = 1;
		// set group value.
		for(List<String[]> e : groups.values()){
			
			String groupName = NaiiTools.getUserName(e.get(0)[groupIndex+1]) +" 小组";
			Iterator<String[]> iter = e.iterator();
			
			HSSFCellStyle cellGroupStyle = getHSSFCellStyle(1);
			while(iter.hasNext()){
				HSSFRow row = sheet.createRow(end++);
				
				String[] vs = iter.next();
				HSSFCellStyle cellStyle;
				if(vs[0].equals(vs[groupIndex+1])){
					cellStyle = cellGroupStyle;
				}else{
					cellStyle = getHSSFCellStyle(2);
				}
				
				vs[groupIndex+1] = groupName;
				
				for(int i=1;i<vs.length;i++){
					HSSFCell cell = row.createCell(i-1);
					if(i == groupIndex +1){
						cell.setCellStyle(cellGroupStyle);
					}else{
						cell.setCellStyle(cellStyle);
					}
					cell.setCellValue(vs[i]);
				}
			}
			sheet.addMergedRegion(new Region(     
	                 begin, //first row (0-based)       
	                (short)groupIndex, //first column  (0-based)       
	                 end-1, //last row (0-based)    
	                (short)groupIndex  //last column  (0-based)       
	        ));
			
			begin = end;
		}
		
		// column auto size.
		for(int i=0;i<cels;i++ ){
			sheet.autoSizeColumn((short) i);
		}
		sheet.autoSizeColumn((short) groupIndex, true);
		
		img.drawImage(wb);

		wb.write(out);
		out.close();
		closeForm();
	}
	
	public void closeForm(){
		this.dispose();
	}
	
	
	public void showDialog() throws Exception{
		la.setText(Assist.getDesktopPath().getPath());
		setLocationRelativeTo(NaiiControl.getControl().currentWondow());
		setVisible(true);
	}
	
	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		NaiiControl.getControl().testLoad();
		
		String path = "C:\\Users\\TouchWin\\Desktop\\2016-07-01_naii\\2016-04 考勤统计\\2016-04考勤统计.xls";
		new NaiiExportDialog().showDialog();
	}
}
