package com.naii.db.dto;

import com.naii.tools.NaiiLog;

public class NaiiValue{

	private String id;
	private String name;

	private static final String ID		="id";
	private static final String NAME	="name";
	
	public NaiiValue() {
		// TODO Auto-generated constructor stub
	}
	
	public NaiiValue(String s) {
 		String[] ms = s.split("\",\"");
 		
 		for(String v : ms){
 			String [] vs = v.split("\":\"");
 			if(ID.equals(vs[0])){
 				id = vs[1];
 			}else if(NAME.equals(vs[0])){
 				name = vs[1];
 			}
 		}
 		NaiiLog.log("[property] "+toJSON());
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String toJSON(){
		return "{\""+id+"\":\""+name+"\"}";
	}
	
	@Override
	public String toString() {
		return name;
	}
}