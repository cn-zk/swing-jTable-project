package com.naii.db.dto;

import java.lang.reflect.Field;

import com.naii.db.annotation.NaiiRetention;

public class NaiiRadar extends NaiiDto{

	@NaiiRetention(name="项目", link="NaiiUser")
	public Integer item ;
	@NaiiRetention(name="请假", link="NaiiUser")
	public Integer leave ;
	@NaiiRetention(name="加班", link="NaiiUser")
	public Integer overtime;
	@NaiiRetention(name="出差", link="NaiiUser")
	public Integer evection;
	@NaiiRetention(name="迟到", link="NaiiUser")
	public Integer late ;
	@NaiiRetention(name="其他", link="NaiiUser")
	public Integer other;
	
	public NaiiRadar() {
		Field[] fds = getClass().getDeclaredFields();
		for(Field f : fds){
			try {
				f.set(this, 0);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public NaiiRadar(String src) {
		super(src);
	}
}
