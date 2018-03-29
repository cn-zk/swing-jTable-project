package com.rdum.db;

import java.lang.reflect.Field;
import java.util.Date;

import com.rdum.tools.RdumTools;

public class RdumUser {

	@RdumRetention(name="主键")
	public String id ;		// id
	@RdumRetention(name="公司", type="combobox")
	public String company ;		// id
	@RdumRetention(name="名称")
	public String name;		// 名称
	@RdumRetention(name="年龄")
	public String age;		// 年龄
	@RdumRetention(name="性别", type="combobox")
	public String sex;		// 年龄
	@RdumRetention(name="电话")
	public String phone;		// 年龄
	@RdumRetention(name="级别", type="combobox")
	public String level;	// 级别
	@RdumRetention(name="技能", type="combobox")
	public String skill;	// 技能
	@RdumRetention(name="工时账号")
	public String work;	
	@RdumRetention(name="资源池", type="combobox")
	public String resuce;	// 资源池
	@RdumRetention(name="是否组长", type="combobox")
	public String group;	// 分组/
	@RdumRetention(name="所在分组", type="user")
	public String groupId;	// 分组/
	@RdumRetention(name="入职时间", type="date")
	public Date	entry;		// 入职
	@RdumRetention(name="离职时间", type="date")
	public Date	quit;		// 离职
	@RdumRetention(name="项目编号")
	public String item;		// 项目/
//	@RdumRetention(name="项目名称")
//	public String itemname;	// 项目名称/
	@RdumRetention(name="工位IP")
	public String ip;
	@RdumRetention(name="备注", type="textarea")
	public String remark;
	
	@Override
	public String toString() {
		return "{\"id\"=\"" + id + 
				"\",\"company\"=\"" + RdumTools.unicodeString(company) + 
				"\",\"name\"=\"" + RdumTools.unicodeString(name) + 
				"\",\"age\"=\"" + RdumTools.unicodeString(age) +
				"\",\"sex\"=\"" + RdumTools.unicodeString(sex) +
				"\",\"phone\"=\"" + RdumTools.unicodeString(phone) +
				"\",\"level\"=\"" + RdumTools.unicodeString(level) +
				"\",\"skill\"=\"" + RdumTools.unicodeString(skill) +
				"\",\"work\"=\"" + RdumTools.unicodeString(work) + 
				"\",\"resuce\"=\"" + RdumTools.unicodeString(resuce) + 
				"\",\"group\"=\"" + RdumTools.unicodeString(group) +
				"\",\"groupId\"=\"" + groupId +
				"\",\"entry\"=\"" + (entry != null ? entry.getTime():"") +
				"\",\"quit\"=\"" + (quit != null ? quit.getTime():"") + 
				"\",\"item\"=\"" + item +
				"\",\"ip\"=\"" + ip +
				"\",\"remark\"=\""+RdumTools.unicodeString(remark)+"\"}";
	}
	
	/**
	 * 
	 * @param src
	 * @throws Exception 
	 */
	public RdumUser(String src) throws Exception {
		if(src == null || src.length() < 5)
			return;
		if(src.indexOf("{") == 0 && 
				src.lastIndexOf("}") == src.length()-1){
			Class c = this.getClass();
			src = src.substring(2, src.length()-2);
			String[] items = src.split("\",\"");
			for(String s : items){
				String[] kv = s.split("\"=\"");
				Field fd = c.getField(kv[0]);
				if(fd.getType() == Date.class){
					fd.set(this, (kv.length > 1 && kv[1] != null) ? new Date(Long.parseLong(kv[1])):null);
				}else{
					fd.set(this, kv.length > 1 ? RdumTools.gbkString(kv[1]): null);
				}
			}
		}
	}

	public RdumUser() {
	}
}
