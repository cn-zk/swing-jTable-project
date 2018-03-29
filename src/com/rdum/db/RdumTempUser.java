package com.rdum.db;

public class RdumTempUser{
	
	RdumUser user;
	
	public RdumTempUser(RdumUser u) {
		user = u;
	}
	
	public RdumUser getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return user != null ? user.name : "无";
	}
}