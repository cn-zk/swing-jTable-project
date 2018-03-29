package com.naii.db.dto;

import java.util.Date;

import com.naii.db.annotation.NaiiRetention;

/**
 * 历史
 * @author TouchWin
 *
 */
public class NaiiHistory extends NaiiDto{

	@NaiiRetention(name="名称")
	public String name;
	@NaiiRetention(name="操作")
	public String option;
	@NaiiRetention(name="目录KEY")
	public String key;
	@NaiiRetention(name="记录")
	public String value;
	@NaiiRetention(name="日期", type="date")
	public Date date;
	@NaiiRetention(name="备注", type="textarea")
	public String remark;
	
	public NaiiHistory(String src) {
		super(src);
	}
	public NaiiHistory() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return name;
	}
	
//	public static void main(String[] args) {
//		System.out.println(new NaiiHistory().toJSON());
//	}

	public static final String OPTION_IMPORT = "import";
	
}
