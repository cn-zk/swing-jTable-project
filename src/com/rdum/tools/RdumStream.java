package com.rdum.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.rdum.db.RdumUser;

public class RdumStream {

	// *.xlsx，*.xls

	public static void saveAll(OutputStream out) throws IOException {
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		insertTab(wb, null, "全部");
		
		Map<String, List<RdumUser>> l = RdumTools.db.getTabsList();
		
		insertTab(wb, l.get("group"), "分组");
		insertTab(wb, l.get("source"), "资源");
		insertTab(wb, l.get("quit"), "离职");
		wb.write(out);
	
	}

	public static void insertTab(HSSFWorkbook wb, List<RdumUser> users, String name) {
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(name);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		

		HSSFCell cell = null;
		int i = 0;
		for (Object u : RdumTools.converTableHead()) {
			cell = row.createCell((short) i++);
			cell.setCellValue((String) u);
			cell.setCellStyle(style);
		}

		// 第五步，写入实体数据 实际应用中这些数据从数据库得到，

		i = 1;
		for (Object[] u : users != null ?
				RdumTools.converTable(users) :
				RdumTools.converTable()) {
			row = sheet.createRow((int) i);
			// 第四步，创建单元格，并设置值
			for (int j = 0; j < u.length; j++) {
				cell = row.createCell((short) j);
				if(j == 11){
					cell.setCellValue(RdumTools.getUserName((String) u[j]));
				}else{
					cell.setCellValue((String) u[j]);
				}
//				style.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);
//				switch(j){
//					case 7 :
//						if(Assist.isEmpty((String) u[j])){
//							
//						}else{
//							style.setFillBackgroundColor(HSSFColor.WHITE.index);
//						}
//					break;
//				}
				
				
				cell.setCellStyle(style);
			}
			sheet.autoSizeColumn((short)i);
			i++;
		}

	}

	public static void outDum(File f) {

		// 第六步，将文件存到指定位置
		try {
			OutputStream out;
			saveAll(out = new FileOutputStream(f.getPath() + "/all.xls"));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void inDum(File f) throws Exception {
		SimpleDateFormat fs =new SimpleDateFormat("yyyy-MM-dd");
		InputStream is = new FileInputStream(f);  
		HSSFWorkbook wb = new HSSFWorkbook(is);
		HSSFSheet st = wb.getSheet("Sheet1"); 
		
		Iterator<HSSFRow> iter = st.rowIterator();
		
		String tname= null, tvalue = null;
		while(iter.hasNext()){
			HSSFRow row = iter.next();
			String name = row.getCell(0).getStringCellValue().replace("(rd)", "");
			String value = row.getCell(2).getStringCellValue();
			if(name.equals(tname)){
				tvalue += ","+value;
			}else{
				if(tname != null){
					RdumTools.setUserItemByName(tname, tvalue);
				}
				tname = name;
				tvalue = value;
			}
		}
		
	}
}
