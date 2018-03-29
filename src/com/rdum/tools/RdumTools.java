package com.rdum.tools;

import java.awt.Dimension;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.rdum.db.RdumDb;
import com.rdum.db.RdumRetention;
import com.rdum.db.RdumTempUser;
import com.rdum.db.RdumUser;
import com.rdum.tools.convert.ConvertChar;
import com.rdum.ui.RdumUI;

public class RdumTools {

	static String[][] convertMap = null;
	
	public static String[][] actions = new String[][]{
		{"add","添加"},
		{"edit","编辑"},
		{"del","删除"}
	};
	
	public static String[][] options = new String[][]{
		{"save","保存"}, 
		{"cancel","取消"}
	};
	
	public static String[][] exps = new String[][]{
		{"exp","导出"}, 
		{"cancel","取消"}
	};
	
	public static RdumDb db;

	public static String[] viewBoxItem = new String[]{
			"全部",
			"分类",
			"回收站"
	};
	
	public static final String[] EXP_BOX_ITEM = new String[]{
			"全部"
	};
	
	public static RdumUI ui;
	
	static{
		convertMap = new String[][]{
			{"<HOME>",System.getProperty("user.dir")}
		};
		
	}
	
	public static String convertString(String str){
		for(String[] item: convertMap){
			str = str.replace(item[0], item[1]);
		}
		return str;
	}
	
	public static String convertOption(String option){
		for(String[] o : actions){
			if(o[0].equals(option)){
				return o[1];
			}
		}
		return option;
	}
	
	public static void load() throws Exception{
		db = new RdumDb();
	}
	
	public static Dimension getDimension(){
		
		Dimension d = new Dimension();
		d.width = db.getMsInt("width");
		d.height = db.getMsInt("height");
		
		if(d.width < 1){
			d.width = 800;
		}
		
		if(d.height < 1){
			d.height = 600;
		}
		return d;
	}

	public static Object[][] converTable(List<RdumUser> users) {
		Field[] fds = RdumUser.class.getDeclaredFields();
		Object[][] arrs = new Object[users.size()][fds.length];
		int i=0, j;
		for(RdumUser u : users){
			j=0;
			for(Field f : fds){
				try {
					Object d = f.get(u);
					if(f.getType() == Date.class && d != null){
						arrs[i][j]=new SimpleDateFormat("yyyy/MM/dd").format((Date)d);
					}else
						arrs[i][j]=f.get(u);
					if(arrs[i][j] == null || "null".equals(arrs[i][j])){
						arrs[i][j] = "";
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				j++;
			}
			i++;
		}
		return arrs;
	}
	public static Object[][] converTable() {
		return converTable(db.getUsers());
	}

	public static Object[] converTableHead() {
		Field[] fd= RdumUser.class.getDeclaredFields();
		Object[] h = new Object[fd.length]; 
		int i=0;
        for (Field f : fd) {  
			h[i]=f.getAnnotation(RdumRetention.class).name();
			if(h[i] == null){
				h[i] = "";
			}
			i++;
        } 
        return h;
	}

	public static String string(Object value) {
		String val = null;
		if(value == null || "null".equals(value)){
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

	public static boolean contentUser(RdumUser user) {
		Iterator<RdumUser> iter = db.getUsers().iterator();
		while(iter.hasNext()){
			if(iter.next().name.equals(user.name)){
				return true;
			}
		}
		return false;
	}

	public static RdumUser getUser(String id) {
		if(id == null){
			return null;
		}
		Iterator<RdumUser> iter = db.getUsers().iterator();
		RdumUser u;
		while(iter.hasNext()){
			u = iter.next();
			if(u.id.equals(id)){
				return u;
			}
		}
		return null;
	}

	public static String getUserName(String id) {
		RdumUser u = getUser(id);
		return u != null ? u.name : null;
	}

	public static Object[] getGroupList() {
		List<RdumTempUser> list = new ArrayList<RdumTempUser>();
		Iterator<RdumUser> iter = db.getUsers().iterator();
		RdumUser u;
		while(iter.hasNext()){
			u = iter.next();
			if("是".equals(u.group)){
				list.add(new RdumTempUser(u));
			}
		}
		return list.toArray();
	}

	public static Object[] getRecycleDirList() {
		return db.getRecycleList();
	}

	public static void setUserItemByName(String name, String value) throws Exception {
		Iterator<RdumUser> iter = db.getUsers().iterator();
		RdumUser u;
		while(iter.hasNext()){
			u = iter.next();
			if(u.name.equals(name)){
				u.item = value;
				RdumTools.db.save(u);
				return;
			}
		}
	}
	
	/**
	 * 字符串数组包含空内容,如"",null,"  "等.
	 * @param val
	 * @return 有空返回true
	 */
	public static boolean isEmpty(String... val){
		if(val != null){
			int i = 0;
			while(i < val.length)
				if(isEmpty(val[i++])) 
					return true;
				return false;
		}
		return true;
	}
	
	/**
	 * 字符串等于空,如"",null,"  "等.
	 * @param str
	 * @return 是空返回true
	 */
	public static boolean isEmpty(String str){
		return str == null || str.trim().length() < 1;
	}
	
	
	public static boolean isNull(Object arg){
		return arg == null;
	}
	/**
	 * 是否包含为null的对象
	 * @param args
	 * @return []包含null：true/不包含null:false
	 */
	public static boolean isNull(Object... args){
		if(args != null){
			for(Object arg : args)
				if(isNull(arg))
					return true;
			return false;
		}
		return true;
	}
}


