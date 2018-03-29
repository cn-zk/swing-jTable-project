package com.naii.ui.dialog.exp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiEvent;
import com.naii.db.dto.NaiiValue;
import com.naii.ui.graphics.NaiiLine;

public class NaiiExportImg {

	NaiiLine[] lines;
	
	public NaiiExportImg() {
		lines = new NaiiLine[5];
		lines[0] = new NaiiLine("次", "项目人员分布明细");
		lines[1] = new NaiiLine("人", "近期3个月人员明细");
		lines[2] = new NaiiLine("次", "出差地人员明细");
		lines[3] = new NaiiLine("人", "人员级别明细");
		lines[4] = new NaiiLine("人", "人员技能明细");
		
		for (int i = 0; i < lines.length; i++) {
			lines[i].setDefaultWidth(500);
		}
		lines[0].setDefaultWidth(700);
	}
	
	public void drawImage(HSSFWorkbook wb) throws Exception {
		
		renovation();
		drawUserImg(wb);
		drawItemImg(wb);
	}
	
	private void drawItemImg(HSSFWorkbook wb) throws Exception {
		BufferedImage img= lines[0].createImage();
		HSSFSheet sheet = wb.createSheet("项目综合");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", outStream);
		
		HSSFPatriarch patri = sheet.createDrawingPatriarch();
		
		int h =  (int) ((float)img.getHeight() / rowHeight)+1;
		int w =  (int) (img.getWidth() / rowWidth);
		HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,
				(short) 1, 1, (short) w, h);
				patri.createPicture(anchor, wb.addPicture(
				outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
				
		patri.createPicture(anchor, wb.addPicture(
		outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
	}
	
	private void drawUserImg(HSSFWorkbook wb) throws Exception{
		HSSFSheet sheet = wb.createSheet("人员综合");
		
		HSSFPatriarch patri = sheet.createDrawingPatriarch();
		
		int oh = 0, space = 1;
		for(int i=1;i<lines.length; i++){
			BufferedImage img= lines[i].createImage();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ImageIO.write(img, "JPG", outStream);
			
			int h =  (int) ((float)img.getHeight() / rowHeight)+space + oh;
			int w =  (int) (img.getWidth() / rowWidth);
			
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,
					(short) 1, oh + 1, (short) w, h);
					patri.createPicture(anchor, wb.addPicture(
					outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
			oh = h;
		}
	}
	
	
	float rowHeight = 15f;
	float rowWidth = 55.555557f;
	public void renovation() {
		String[] tit;
		Float[] fl;
		Map<String, List<NaiiEvent>> map = NaiiControl.getControl().loadEventViewData("5");

		tit = new String[map.size()];
		fl = new Float[map.size()];

		int i=0;
		for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
			tit[i] = e.getKey();
			fl[i] = (float)e.getValue().size();
			i ++;
		}
		lines[0].resetData(tit, fl);
		
		tit = new String[]{
				"已招入职",
				"已经离职",
				"入资源池",
				"资源池入职"
		};
		fl = NaiiControl.getControl().loadUser3ViewData();
		lines[1].resetData(tit, fl);
		
		map = NaiiControl.getControl().loadEventViewData("2");

		tit = new String[map.size()];
		fl = new Float[map.size()];

		i =0;
		for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
			tit[i] = e.getKey();
			fl[i] = (float)e.getValue().size();
			i ++;
		}
		lines[2].resetData(tit, fl);
	
		
		NaiiValue[] vals = NaiiProperty.getProperty().getFormat(NaiiProperty.KEY_LEVEL);
		tit = new String[vals.length];
		String keys[] = new String[vals.length];
		i=0;
		for(NaiiValue val : vals){
			keys[i] = val.getId();
			tit[i++] = val.getName();
		}
		fl = NaiiControl.getControl().loadUserViewFloat(keys, NaiiProperty.KEY_LEVEL);
		lines[3].resetData(tit, fl);
		
		vals = NaiiProperty.getProperty().getFormat(NaiiProperty.KEY_SKILL);
		tit = new String[vals.length];
		keys = new String[vals.length];
		i=0;
		for(NaiiValue val : vals){
			keys[i] = val.getId();
			tit[i++] = val.getName();
		}
		fl = NaiiControl.getControl().loadUserViewFloat(keys, NaiiProperty.KEY_SKILL);
		lines[4].resetData(tit, fl);
	}
}
