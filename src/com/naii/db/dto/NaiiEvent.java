package com.naii.db.dto;

import java.util.Date;

import com.naii.db.annotation.NaiiRetention;

/**
 * 事件
 * @author TouchWin
 *
 */
public class NaiiEvent extends NaiiDto{
	
	@NaiiRetention(name="事件名称")
	public String name;
	@NaiiRetention(name="事件类型", type="combobox")
	public String event;
	@NaiiRetention(name="事件日期", type="date")
	public Date date;
	@NaiiRetention(name="事件天数", type="number")
	public Float value;
	@NaiiRetention(name="事件备注", type="textarea")
	public String remark;
	@NaiiRetention(name="依赖主键")
	public String dependId;
	
	public NaiiEvent(String src) {
		this(src, false);
	}
	public NaiiEvent(String src, boolean isDependId) {
		if(isDependId){
			dependId = src;
		}else{
			parseJSON(src);
		}
	}
	
	public NaiiEvent() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String toCutJSON() {
		return "{\"id\":\""+id+"\",\"dependId\":\""+dependId+"\"}";
	}
}
