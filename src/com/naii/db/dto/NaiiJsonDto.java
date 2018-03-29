package com.naii.db.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class NaiiJsonDto extends NaiiDto{

	protected Map<String, String> JSON_MAP;
	
	public NaiiJsonDto() {
		this(null);
	}
	
	public NaiiJsonDto(String src) {
		super(src);
	}
	
	@Override
	public void parseJSON(String src) {
		JSON_MAP = new HashMap<String, String>();
		if(src == null || src.length() < 5)
			return;
		
		String[] ss = src.split("\n");
		if(ss.length > 0){
			super.parseJSON(ss[0]);
			src = ss[1];
		}
		if(src == null || src.length() < 5)
			return;
		if(src.indexOf("{") == 0 && 
				src.lastIndexOf("}") == src.length()-1){
			src = src.substring(2, src.length()-2);
			String[] items = src.split("\",\"");
			for(String s : items){
				String[] kv = s.split("\":\"");
				JSON_MAP.put(kv[0], kv[1]);
			}
		}
	}
	
	public void push(String key, String value) {
		JSON_MAP.put(key, value);
	}
	
	public String remove(String key){
		return JSON_MAP.remove(key);
	}
	
	public Map<String, String> getJSON_MAP() {
		return JSON_MAP;
	}
	
	@Override
	public String toString() {
		return "JSON_MAP DTO";
	}
	
	@Override
	public String toJSON() {
		String ss1 = null;
		if(JSON_MAP != null){
			StringBuffer json = new StringBuffer("{");
			for(Entry<String, String> ent : JSON_MAP.entrySet()){
				json.append("\""+ent.getKey()+"\":\""+ent.getValue()+"\",");
			}
			if(json.length() > 1)
				json.setLength(json.length()-1);
			json.append("}");
			ss1 = json.toString();
		}else{
			ss1 = "{}";
		}
		return super.toJSON()+"\n"+ss1;
	}
}
