package com.naii.db.dto;

import java.util.Date;

import com.naii.db.annotation.NaiiRetention;

public class NaiiEquipment extends NaiiDto{
	
	@NaiiRetention(name="设备名称")
	public String name;	// 设备名称
	@NaiiRetention(name="设备类型", type="combobox")
	public String type;	// 设备类型
	@NaiiRetention(name="购买用户", type="user")
	public String userId;	// 添加用户
	@NaiiRetention(name="使用者", type="user")
	public String useId;		// 使用id
	@NaiiRetention(name="所属部门")
	public String dept;	// 所属部门
	@NaiiRetention(name="设备价格", type="number")
	public Float money;	// 设备价格
	@NaiiRetention(name="处理器")
	public String cpu;		// cpu
	@NaiiRetention(name="内存")
	public String memory;	// 内存
	@NaiiRetention(name="硬盘")
	public String disk;	// 磁盘
	@NaiiRetention(name="机身S/N")
	public String sn;
	@NaiiRetention(name="无线mac")
	public String mac;
	@NaiiRetention(name="有线mac")
	public String wiredmac;
	@NaiiRetention(name="显卡")
	public String graphics;// 显卡
	@NaiiRetention(name="购买时间", type="date")
	public Date	date;		// 离职
	@NaiiRetention(name="创建时间", type="date")
	public Date	create;		// 离职
	@NaiiRetention(name="其他", type="textarea")
	public String remark;	// 其他
	
	
	public NaiiEquipment(String src) {
		super(src);
	}
	
	public NaiiEquipment() {
		// TODO Auto-generated constructor stub
	}
}
