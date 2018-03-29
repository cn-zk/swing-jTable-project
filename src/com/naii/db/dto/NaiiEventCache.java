package com.naii.db.dto;

import com.naii.db.NaiiConfig;
import com.naii.db.annotation.NaiiRetention;
import com.naii.tools.NaiiLog;


public class NaiiEventCache extends NaiiJsonDto {

	@NaiiRetention(name="事件类型")
	public String event;

	@NaiiRetention(name="依赖ID")
	public String dependId;
	
	public NaiiEventCache() {
		// TODO Auto-generated constructor stub
	}
	
	public NaiiEventCache(String src) {
		super(src);
	}
	
	public NaiiEventCache(String src, boolean isDependId) {
		if(isDependId){
			id = src;
		}else{
			parseJSON(src);
		}
	}

	public NaiiEventCache(NaiiEvent ne) {
		dependId = ne.dependId;
		event = ne.event;
		id = NaiiConfig.KEY_EVENT + "_" + ne.event;
	}

	public void push(String key, float i) {
		if(JSON_MAP.containsKey(key)){
			Float in = 0f;
			try{
				in = Float.parseFloat(JSON_MAP.get(key));
			}catch(Exception e){}
			JSON_MAP.put(key, String.valueOf(in + i));
		}else{
			JSON_MAP.put(key, String.valueOf(i));
		}
	}
	
	public String remove(String key, Float i){
		if(i == null){
			return JSON_MAP.remove(key);
		}else{
			Float in = 0f;
			String val = JSON_MAP.get(key);
			if(val != null){
				try{
					in = Float.parseFloat(JSON_MAP.get(key));
				}catch(Exception e){
					NaiiLog.error("[NaiiEventCache.remove] "+key +", " + JSON_MAP.get(key));
				}
			}
			in -= i;
			if(in <= 0){
				JSON_MAP.remove(key);
			}else{
				JSON_MAP.put(key, String.valueOf(in));
			}
		}
		return key;
	}
	
	public String remove(String key){
		return remove(key, null);
	}

	public Float getNumber(String key) {
		String str = JSON_MAP.get(key);
		try {
			if(str != null){
				return Float.parseFloat(str);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0f;
	}
	
//	public static void main(String[] args) {
//		System.out.println(new NaiiEventCache("3333", true).toJSON());
//		System.out.println(new NaiiEventCache("{\"id\":\"9.98\"}\n{\"201605\":\"2.2\"}").toJSON());
//	}
	
}


