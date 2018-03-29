package com.naii.db.dto;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.naii.db.annotation.NaiiRetention;
import com.naii.tools.Assist;
import com.naii.tools.NaiiLog;
import com.naii.tools.NaiiTools;

public class NaiiDto {

	@NaiiRetention(name="主键")
	public String id ;		// id

	@NaiiRetention(name="删除时间", hide=true)
	public Date remove;
	
	public NaiiDto() {
		// TODO Auto-generated constructor stub
	}
	
	public NaiiDto(String str) {
		parseJSON(str);
	}
	
	public void parseJSON(String src){

		if(src == null || src.length() < 5)
			return;

		Class<?> c = this.getClass();
		JSONObject obj = new JSONObject(src.replace("\n", ";"));
		
		Iterator<String> iter = obj.keys();
		while(iter.hasNext()){
			try {
				String key = iter.next();
				Field fd = c.getField(key);
				Object val = obj.get(key);
				Class<?> type = fd.getType();
				
				if(NaiiTools.isEmpty(val)){
					continue;
				}
				
				if(type == Date.class){
					val = new Date(obj.getLong(key));
				}else if(type == Integer.class){
					val = obj.getInt(key);
				}else if(type == Float.class){
					val = obj.getBigDecimal(key).floatValue();
				}else if(type == Double.class){
					val = obj.getBigDecimal(key).doubleValue();
				}else {
					val = NaiiTools.gbkString(obj.getString(key));
				}
				fd.set(this, val);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
	
	public void parseJSON2(String src){
		if(src == null || src.length() < 5)
			return;
		if(src.indexOf("{") == 0 && 
				src.lastIndexOf("}") == src.length()-1){
			Class<?> c = this.getClass();
			src = src.substring(2, src.length()-2);
			String[] items = src.split("\",\"");
			for(String s : items){
				String[] kv = s.split("\":\"");
				try {
					// 兼容旧数据
					if(kv.length < 2){
						kv = s.split("\"=\"");
					}
//					if("resuce".equals(kv[0])){
//						kv[0] = "resource";
//						if("0".equals(kv[1])){
//							kv[1] = null;
//						}
//					}
//					if("group".equals(kv[0])){
//						kv[1] = "是".equals(NaiiTools.gbkString(kv[1])) ? "1" : "0";
//					}
					Field fd = c.getField(kv[0]);
					if(fd.getType() == Date.class){
						if("0".equals(kv[1])){
							
						}else{
							fd.set(this, Assist.isEmpty(kv[1]) ? null : new Date(Long.parseLong(kv[1])));
						}
					}else if(fd.getType() == Integer.class){
						try{
							fd.set(this, NaiiTools.isEmpty(kv)? 0 : Integer.parseInt(kv[1]));
						}catch(NumberFormatException e){
							NaiiLog.error(s);
							fd.set(this, 0);
						}
					}else if(fd.getType() == Float.class){
						try{
							fd.set(this, NaiiTools.isEmpty(kv)? 0 : Float.parseFloat(kv[1]));
						}catch(NumberFormatException e){
							NaiiLog.error(s);
							fd.set(this, 0);
						}
					}else{
						fd.set(this, kv.length > 1 ? NaiiTools.gbkString(kv[1]): null);
					}
				} catch (Exception e) {
					NaiiLog.error(this.getClass().getName()+ ", "+s);
				}
			}
		}
	}
	
	public String toCutJSON(){
		return "{\"id\":\""+id+"\"}";
	}
	
	public String toJSON(){
		JSONObject obj = new JSONObject();
		for(Field f : getClass().getFields()){
			if(f.getModifiers() == 25){
				continue;
			}
			NaiiRetention r = f.getAnnotation(NaiiRetention.class);
			try {
				if(r.temp()){
					continue;
				}else if(f.get(this) != null){
					if(f.getType() == Date.class){
						Date d = (Date) f.get(this);
						obj.put(f.getName(), d != null ? d.getTime() : null);
					}else if(f.getType() == String.class){
						obj.put(f.getName(), NaiiTools.unicodeString(f.get(this)));
					}else{
						obj.put(f.getName(), f.get(this));
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return obj.toString();
	}
	
//	public String toJSON(){
//		StringBuffer json = new StringBuffer("{");
//		for(Field f : getClass().getFields()){
//			if(f.getModifiers() == 25){
//				continue;
//			}
//			NaiiRetention r = f.getAnnotation(NaiiRetention.class);
//			if(r.temp()){
//				continue;
//			}else{
//				try {
//					json.append("\"").append(f.getName()).append("\":\"");
//					
//					if(f.getType() == Date.class){
//						Date d = (Date) f.get(this);
//						json.append(d != null ? d.getTime() : null);
//					}else{
//						json.append(NaiiTools.unicodeString(String.valueOf(f.get(this))));
//					}
//					
//					json.append("\",");
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		json.setLength(json.length()-1);
//		json.append("}");
//		return json.toString();
//	}
	
	public Map<String, Object> getDeclaredRetentionValueMap(){
		Field[] fds = getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>(fds.length);
		for(int i=0;i<fds.length;i++){
			try {
				NaiiRetention nr = fds[i].getAnnotation(NaiiRetention.class);
				if(nr.hide() || nr.temp()){
					continue;
				}
				map.put(nr.name(), 
						fds[i].get(this));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
	
//	public static void main(String[] args) throws IOException {
//		String str = Stream.readerString(new File("D:\\Users\\Administrator\\Desktop\\naii\\db\\data\\history\\rongda\\2016\\import\\event\\9023857617395"));
//		String str = Stream.readerString(new File("D:\\Users\\Administrator\\Desktop\\naii\\db\\data\\user\\rongda\\20465048297046"));
//		System.out.println(new NaiiUser(str).toJSON());
//	}
	
}
