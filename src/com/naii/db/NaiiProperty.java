package com.naii.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.naii.db.dto.NaiiValue;
import com.naii.tools.Assist;
import com.naii.tools.NaiiLog;

public class NaiiProperty {

	public static final String[] CACHE_EVENT	=new String[]{
		"0", "1", "2", "3", "4", "5"
	};

	public static final String KEY_LEVEL = "level";

	public static final String KEY_SKILL = "skill";

	private static NaiiProperty property;
	
	private String companyCode, companyName;
	
	private Map<String, NaiiFormFormat> formatMap; 
	
	private NaiiProperty() {
		
	}
	
	public static NaiiProperty getProperty() {
		if(property == null){
			property = new NaiiProperty();
		}
		return property;
	}
	
	public String getCompanyCode() {
		return companyCode;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public void parseCompany(NaiiConfig config) throws Exception {
		
		try {
			formatMap = new HashMap<String, NaiiFormFormat>();

			companyCode = config.getCompanyCode();
			companyName = config.getCompanyName(companyCode);
			
			Map<String, String> defaultMap = config.getConfigMs().getValueMap("company");
			for(Entry<String, String> ent : defaultMap.entrySet()){
				formatMap.put(ent.getKey(), new NaiiFormFormat(ent.getValue()));
			}
		} catch (Exception e) {
			NaiiLog.error("[parseCompany.companyCode]"+companyCode);
			throw e;
		}
		
	}
	
	public String formatter(String key, String value){
		NaiiFormFormat fm = formatMap.get(key);
		if(fm != null){
			for(NaiiValue v : fm.getValues()){
				if(v.getId().equals(value)){
					return v.getName();
				}
			}
		}
		return value;
	}
	
	public NaiiValue[] getFormat(String key){
		NaiiFormFormat fm = formatMap.get(key);
		if(fm != null){
			return fm.getValues();
		}
		return null;
	}
	
	public NaiiValue[] getFormats(String key, String[] filter){
		NaiiValue[] vs = getFormat(key);
		NaiiValue[] vs2 = new NaiiValue[filter.length];
		for(int i=0;i<filter.length;i++){
			for(NaiiValue v : vs){
				if(v.getId().equals(filter[i])){
					vs2[i] = v;
					break;
				}
			}
		}
		return vs2;
	}
	
	class NaiiFormFormat {

		private static final String BEGIN = "[";
		private static final String END = "]";
		
		private boolean array;
		
		private NaiiValue[] values;
		
		private String value;
		
		public NaiiFormFormat(String value) {
			array = Assist.enclosed(value, BEGIN, END);
			
			if(array){
				value = value.substring(3, value.length()-3);
			 	String[] vs = value.split("\\\"\\},\\{\"");
			 	values = new NaiiValue[vs.length]; int i=0;
			 	for(String s: vs){
			 		values[i++] = new NaiiValue(s);
			 	}
			}
		}
		
		public boolean isArray() {
			return array;
		}
		
		public String getValue() {
			return value;
		}
		
		public NaiiValue[] getValues() {
			return values;
		}
		
		
	}
}

