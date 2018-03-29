package com.naii.tools;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.naii.ctr.NaiiControl;
import com.naii.db.annotation.NaiiRetention;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEquipment;
import com.naii.db.dto.NaiiUser;
import com.naii.db.dto.NaiiValue;
import com.naii.tools.convert.ConvertChar;

public class NaiiTools {
	
	public static final String[] USER_DATAS		=new String[]{
		"entry"	, "quit"
	};
	public static final String[] FILTER_USERS	=new String[]{
		"id", "age", "sex", "item", "resource","quit"
	};
	public static final String[] FILTER_RESOURCE=new String[]{
		"id", "age", "sex", "item", "entry", "quit","group"
	};
	public static final String[] FILTER_QUIT	=new String[]{
		"id", "age", "sex", "item", "resource","entry"
	};
	public static final String[] FILTER_EVENTS	=new String[]{
		"id", "dependId"
	};
	
	public static final String[] FILTER_IN_USERS	=new String[]{
		"name", "phone", "level", "entry"
	};
	public static String string(Object value) {
		String val = null;
		if(value == null || "null".equals(value)){
		}else if(value instanceof String){
			val = (String) value;
		}else{
			val = String.valueOf(value);
		}
		return val;
	}
	public static String unicodeString(Object value) {
		return ConvertChar.toUnicode(string(value));
	}
	public static String gbkString(Object value) {
		return ConvertChar.UnicodeDecode(string(value));
	}
	
	public static Object[] getRetentionNames(Class<?> c) {
		return getRetentionNames(c.getFields());
	}
	public static Object[] getRetentionNames(Field[] fd) {
		List<Object> h = new ArrayList<Object>(fd.length);
        for (Field f : fd) {
        	if(f.getModifiers() == 25){
        		continue;
        	}
			String hr = f.getAnnotation(NaiiRetention.class).name();
			if(hr == null){
				hr = "";
			}
			h.add(hr);
        }
        Object[] ha = new Object[h.size()];
        h.toArray(ha);
        return ha;
	}
	
	public static Object[][] getRetentionNames(boolean isUser) {
		Field[] fd;
		if(!isUser){
			fd= NaiiEquipment.class.getDeclaredFields();
		}else{
			fd= NaiiUser.class.getDeclaredFields();
		}
		Object[][] h = new Object[fd.length][2]; 
		int i=0;
		for (Field f : fd) {  
			h[i][0] = f.getName();
			
			h[i][1]=f.getAnnotation(NaiiRetention.class).name();
			h[i][1] = "";
			if(h[i][1] == null){
				h[i][1] = "";
			}
			
			i++;
		} 
		return h;
	}
	
	public static String convertData(Object obj, String format){
		if(obj instanceof Date){
			return new SimpleDateFormat(format == null ? "yyyy/MM/dd" : format).format((Date)obj);
		}
		return String.valueOf((obj != null ? obj : ""));
	}
	
	public static String getUserName(String str) {
		if(str == null){
			return "";
		}
		try {
			for(NaiiDto u : NaiiControl.getControl().queryNaiiDto(new NaiiUser(), null)){
				if(u.id != null ? u.id.equals(str) : u.id == str){
					return ((NaiiUser)u).name;
				}
			}
		} catch (Exception e) {
			// TODO NaiiTools.getUserName
			e.printStackTrace();
		}
		return str;
	}
	public static int getIdIndex(NaiiValue[] datas, String id) {
		int i=0;
		for(NaiiValue v : datas){
			if(v.getId().equals(id)){
				return i;
			}
			i++;
		}
		return -1;
	}
	public static boolean removeEmptyDir(File dfile) {
		if(dfile.isFile()){
			return true;
		}else{
			for(File f : dfile.listFiles()){
				removeEmptyDir(f);
			}
		}
		NaiiLog.log("[removeEmptyDir] "+ dfile.toString());
		return dfile.delete();
	}
	
	public static boolean isEmpty(String[] kv) {
		return kv.length < 2 || Assist.isEmpty(kv[1]) || "null".equals(kv[1]);
	}
	
	public static boolean isEmpty(Object obj){
		if(obj == null){
			return true;
		}
		if(obj instanceof String){
			return "null".equals((String)obj) || String.valueOf(obj).trim().length() < 1;
		}
		return false;
	}
	
	public static String getDayString() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	
	public static String getMonthString() {
		return new SimpleDateFormat("yyyy-MM").format(new Date());
	}
	
	public static String getYearString() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}
	
	
	public static String[] getMonths(int month) {
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");

		String[] ds = new String[month];
		for(int i=0;i< month;i++){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, i*-1);
			ds[i] = sf.format(c.getTime());
		}
		return ds;
	}
	
	public static boolean containMonth(Date d , int month){
		if(d == null){
			return false;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, month*-1);
		return c.getTime().getTime() <= d.getTime();
	}
	
	public static NaiiDto compareNaiiDto(NaiiDto dto) throws Exception {
		Class<?> clas = dto.getClass(); 
		List<NaiiDto> list = NaiiControl.getControl().queryNaiiDto(dto, null);
		Iterator<NaiiDto> iter = list.iterator();
		while(iter.hasNext()){
			NaiiDto d = iter.next();
			for(Field fd : clas.getFields()){
				if(compare(fd.get(dto) , fd.get(d))){
					return d;
				}
			}
		}
		
		return null;
	}
	private static boolean compare(Object val, Object obj) {
		if(isEmpty(val) || isEmpty(obj)){
			return false;
		}
		
		if(val instanceof String){
			if(val.equals(obj)){
				return true;
			}
		}
		if(val == obj){
			return true;
		}
		
		return false;
	}
	
	public static float filterNumber(String str){
		if(str == null){
			return 0;
		}

		float num = 0;
		String unit = "天";
		boolean flag;
		if(str.indexOf(unit) != -1){
			String[] ss = str.split(",");
			for(String s : ss){
				flag = false;
				char[] cs = s.toCharArray();
				StringBuffer buf = new StringBuffer();
				for(int i=s.indexOf(unit)-1; i > 0 ; i--){
					if(cs[i] >= '0' && cs[i] <= '9'){
						buf.append(cs[i]);
					}else if(!flag && cs[i] == '.'){
						buf.append(cs[i]);
					}else{
						break;
					}
				}
				cs = buf.toString().toCharArray();
				buf.setLength(0);
				for(int i=cs.length-1;i>=0;i--){
					buf.append(cs[i]);
				}
				num += Float.parseFloat(buf.toString());
			}
		}else{
			flag = false;
			StringBuffer ints = new StringBuffer();
			for(char c : str.toCharArray()){
				if(c >='0' && c <= '9'){
					ints.append(c);
				}else if(!flag && c == '.'){
					ints.append(c);
					flag = true;
				}
			}
			num = ints.length() > 0 ? Float.parseFloat(ints.toString()): 0;
		}
		return num;
	}
	public static String getId() {
		return System.nanoTime()+"";
	}
	public static Field[] getDtoFields(Class<? extends NaiiDto> clas) {
		Field[] fd = clas.getFields();
		int i = 0;
		for(Field f : fd){
			if(f.getModifiers() == 25){
        		i ++;
        	}
		}
		if(i > 0){
			Field[] fds = new Field[fd.length-i];
			i=0;
			for(Field f : fd){
				if(f.getModifiers() == 25){
	        		continue;
	        	}
				fds[i++] = f;
			}
			return fds;
		}else
			return fd;
	}
	
//	public static int getFixedNumber(String type) {
//		if(TYPE_EQUIPMENT.equals(type)){
//			return 2;
//		}else{
//			return 2;
//		}
//	}
	
	public static void main(String[] args) {
		System.out.println(filterNumber("20号~25号加班2天,27号~30号加班2天"));
	}
}
