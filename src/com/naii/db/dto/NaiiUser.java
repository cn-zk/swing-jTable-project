package com.naii.db.dto;

import java.util.Date;

import com.naii.db.annotation.NaiiRetention;

public class NaiiUser extends NaiiDto {

	@NaiiRetention(name="姓名")
	public String name;		// 名称
	@NaiiRetention(name="电话", type="phone")
	public String phone;		// 年龄
	@NaiiRetention(name="年龄")
	public String age;		// 年龄
	@NaiiRetention(name="性别", type="combobox")
	public String sex;		// 年龄
	@NaiiRetention(name="级别", type="combobox")
	public String level;	// 级别
	@NaiiRetention(name="技能", type="combobox")
	public String skill;	// 技能
	@NaiiRetention(name="工时账号")
	public String work;
	@NaiiRetention(name="资源池", type="date")
	public Date resource;	// 资源池
	@NaiiRetention(name="组长", type="combobox")
	public String group;	// 分组/
	@NaiiRetention(name="所在分组", type="group")
	public String groupId;	// 分组/
	@NaiiRetention(name="工龄", type="number")
	public Float worktime;	
	@NaiiRetention(name="入职时间", type="date")
	public Date	entry;		// 入职
	@NaiiRetention(name="转正时间", type="date")
	public Date	worker;		// 入职
	@NaiiRetention(name="离职时间", type="date")
	public Date	quit;		// 离职
//	@NaiiRetention(name="项目编号", temp=true)
//	public String item;		// 项目/
//	@NaiiRetention(name="项目名称")
//	public String itemname;	// 项目名称/
	@NaiiRetention(name="工位IP")
	public String ip;
	@NaiiRetention(name="备注", type="textarea")
	public String remark;
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * 
	 * @param src
	 * @throws Exception 
	 */
	public NaiiUser(String src)  {
		super(src);
	}

	public NaiiUser() {
		// TODO Auto-generated constructor stub
	}
}
