package com.rdum.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rdum.io.FileMs;
import com.rdum.io.Stream;
import com.rdum.tools.RdumTools;

public class RdumDb {

	// user directory
	private static String user = File.separator + "user";
	
	private static String back = File.separator + "back";
	
	private static String recycle = File.separator + "recycle";

	private FileMs ms;
	
	private List<RdumUser> users;
	
	String db_path ;
	
	public RdumDb() throws Exception {
		
		String rdum_ini = System.getProperty("user.dir")+File.separator+"rdum.ini";
		ms = new FileMs();
		ms.load(new File(rdum_ini));
		
		Map<String, String> sys = ms.getValueMap("sys");
		db_path = sys.get("db.dir");
		db_path = RdumTools.convertString(db_path);
	
		loadUser(db_path);
	}
	
	private void loadUser(String path) throws Exception{
		
		users = new ArrayList<RdumUser>();
		
		File dir = new File(path+user);
		if(!dir.isDirectory()){
			dir.mkdirs();
		}
//		for(int i=0;i<50;i++)
		for(File f : dir.listFiles()){
			if(f.isFile() && !f.isHidden()){
				RdumUser u = new RdumUser(Stream.inputString(f));
				users.add(u);
			}
		}
		
		sortUser(users, true);
	}
	
	private void sortUser(List<RdumUser> users, final boolean in){
		Collections.sort(users, new Comparator<RdumUser>() {
			@Override
			public int compare(RdumUser o1, RdumUser o2) {
				long a, b;
				if(in){
					a= o1.entry != null ? o1.entry.getTime() : 0;
					b = o2.entry != null ? o2.entry.getTime() : 0;
				}else{
					a= o1.quit != null ? o1.quit.getTime() : 0;
					b = o2.quit != null ? o2.quit.getTime() : 0;
				}
				return a < b ? 1 : -1;
			}
		});
	}

	public int getMsInt(String key) {
		Integer n = 0;
		try{
			n = Integer.parseInt(String.valueOf(ms.getValueMap("sys").get(key)));
		}catch(NumberFormatException e){}
		return n == null ? 0 : n;
	}

	public String[] getMsArray(String key) {
		String[] ss = new String[0];
		Object val = ms.getValueMap("sys").get(key);
		if(val != null){
			ss = String.valueOf(val).split(",");
		}
		return ss;
	}

	public List<RdumUser> getUsers() {
		return users;
	}

	public void save(RdumUser formUser) throws Exception {
		File f = new File(db_path+user+File.separator+formUser.id);
		Stream.write(formUser.toString(), f);
		
	}
	public void save(RdumUser formUser, int index) throws Exception {
		File f = new File(db_path+user+File.separator+formUser.id);
		Stream.write(formUser.toString(), f);
		if(index == -1){
			users.add(formUser);
		}else{
			users.set(index, formUser);
		}
	}

	public void recycle(RdumUser selectUser) {
		
		// current day;
		SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");
		// recycle dir
		File f = new File(db_path+recycle +File.separator+fm.format(new Date()));
		if(!f.isDirectory())
			f.mkdirs();
		
		File selectFile = new File(db_path + user+File.separator + selectUser.id);
		selectFile.renameTo(new File(f.getPath() + File.separator + selectUser.id));
		
		int index = -1;
		Iterator<RdumUser> iter = users.iterator();
		while(iter.hasNext()){
			++index;
			if(iter.next().id.equals(selectUser.id)){
				break;
			}
		}
		users.remove(index);
	}

	public Object[] getRecycleList() {
		
		File[] fs = new File(db_path+recycle).listFiles();
		Object[] os = new Object[fs.length];
		int i=0;
		for(File f:fs){
			os[i++] = f.getName();
		}
		return os;
	}
	
	public List<RdumUser> loadRecycleUser(String day) throws Exception{
		List<RdumUser> users = new ArrayList<RdumUser>();
		
		File dir = new File(db_path+recycle+File.separator+day);
		if(dir.isDirectory())
		for(File f : dir.listFiles()){
			if(f.isFile()){
				users.add(new RdumUser(Stream.inputString(f)));
			}
		}
		return users;
	}

	public Map<String, List<RdumUser>> getTabsList() {
		Map<String, List<RdumUser>> map = new HashMap<String, List<RdumUser>>(3);
		
		Map<String, List<RdumUser>> map1 = new HashMap<String, List<RdumUser>>(3);
		List<RdumUser> list1 = new ArrayList<RdumUser>();	// 
		List<RdumUser> list2 = new ArrayList<RdumUser>();	// source
		List<RdumUser> list3 = new ArrayList<RdumUser>();	// quit
		
		Iterator<RdumUser> iter = users.iterator();
		RdumUser u;
		while(iter.hasNext()){
			 u = iter.next();
			 
			 if(u.quit != null){
				 list3.add(u);
			 }else if("是".equals(u.resuce)){
				 list2.add(u);
			 }else {
				 list1 = map1.get(u.groupId);
				 if(list1 == null){
					 list1 = new ArrayList<RdumUser>();
					 map1.put(u.groupId, list1);
				 }
				 list1.add(u);
			 }
		}
		
		sortUser(list2, true);
		sortUser(list3, false);
		
		list1 = new ArrayList<RdumUser>();
		for(String groupId : map1.keySet()){
			sortUser(map1.get(groupId), true);
			for(RdumUser ru : map1.get(groupId)){
				list1.add(ru);
			}
		}
		
		map.put("group", list1);
		map.put("source", list2);
		map.put("quit", list3);
		
		return map;
	}

}
