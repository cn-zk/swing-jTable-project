package com.naii.ctr;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.naii.db.NaiiConfig;
import com.naii.db.NaiiDatabase;
import com.naii.db.NaiiEventCheck;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEquipment;
import com.naii.db.dto.NaiiEvent;
import com.naii.db.dto.NaiiEventCache;
import com.naii.db.dto.NaiiHistory;
import com.naii.db.dto.NaiiJsonDto;
import com.naii.db.dto.NaiiUser;
import com.naii.tools.NaiiLog;
import com.naii.tools.NaiiTools;
import com.naii.ui.NaiiUI;
import com.naii.ui.view.NaiiLoadView;

public class NaiiControl {

	public static final String FILTER_3			="3";			// 3月内的人员
	public static final String FILTER_ENTRY		="entry";		// 在职
	public static final String FILTER_RESUCE	="resource";	// 资源池
	public static final String FILTER_QUIT		="quit";		// 离职
	public static final String FILTER_USE0		="use0";
	public static final String FILTER_USE1		="use1";
	public static final String FILTER_USE2		="use2";
	public static final String FILTER_EMP		="emp";
	public static final String FILTER_RECYCLE	="recycle";
	public static final String FILTER_GROUP 	="groupId";
	public static final String FILTER_EVENT		="event";
	
	private static NaiiControl ctrl;
	
	private NaiiUI naiiUI;
	private NaiiDatabase naiiDatabase;
	
	private List<Window> windows;
	
	private List<NaiiChangeListener> ls;
	
	private NaiiControl() {
		windows = new ArrayList<Window>(10);
		ls = new ArrayList<NaiiChangeListener>();
	}
	
	public static NaiiControl getControl(){
		if(ctrl == null){
			ctrl = new NaiiControl();
		}
		return ctrl;
	}
	
	public void testLoad() throws Exception{
		naiiDatabase = new NaiiDatabase();
		
		// validate directory.
		naiiDatabase.initializeDir();
		
		// load user data.
		naiiDatabase.initializeUser();
	}

	public void load() throws Exception {

		// initialize object.
		naiiUI = new NaiiUI(new Runnable() {
			
			@Override
			public void run() {
				try {
					naiiUI.init();
					naiiDatabase = new NaiiDatabase();
					
					// validate directory.
					naiiDatabase.initializeDir();
					
					// load user data.
					naiiDatabase.initializeUser();
					
					naiiUI.hideLoad();
					
					// 重置所有人员数据
//					int i=0;
//					for(NaiiUser dto : naiiDatabase.getNaiiUsers()){
//						dto.phone = "13000000000";
//						dto.name = "NAME_"+(i++);
//						dto.remark = "无";
//						saveObject(dto);
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<NaiiDto> queryNaiiDto(NaiiDto dto, String filter) throws Exception{
		List<NaiiDto> list = null;
		List<?> temps = null;
		if(dto instanceof NaiiUser){
			temps = naiiDatabase.getNaiiUsers();
		}else if(dto instanceof NaiiEquipment){
			temps = naiiDatabase.getNaiiEquipments();
		}else{
			temps = naiiDatabase.loadCompanyObject(dto);
		}
		if(filter == null){
			list = (List<NaiiDto>) temps;
		}else{
			list = new ArrayList<NaiiDto>(temps.size());
			for(Object tdto : temps){
				if(filterNaiiDto((NaiiDto)tdto, filter)){
					list.add((NaiiDto)tdto);
				}
			}
		}
		
		String field = filterSotField(dto, filter);
		if(field != null){
			naiiDatabase.sortDto(list, field);
		}
		
		return list;
	}

	public List<NaiiDto> loadRecycleCompany(String date, NaiiDto dto) {
		try {
			return naiiDatabase.loadRecycleCompany(date, dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<NaiiDto>(0);
	}

	public File[] readRecycleList() {
		return naiiDatabase.readRecycleList();
	}
	
	public String getPath(NaiiDto dto){
		return naiiDatabase.getDtoDirectory(dto);
	}
	
	private String filterSotField(NaiiDto dto, String filter) {
		if(dto instanceof NaiiUser){
			if(NaiiControl.FILTER_ENTRY.equals(filter)){
				return "entry";
			}else if(NaiiControl.FILTER_QUIT.equals(filter)){
				return "quit";
			}else if(NaiiControl.FILTER_RESUCE.equals(filter)){
				return "resource";
			}else if(NaiiControl.FILTER_GROUP.equals(filter)){
				return "entry";
			}else{
				return "entry";
			}
		}else{
			return "create";
		}
	}

	public static boolean filterNaiiDto(NaiiDto dto, String filter) {
		if(dto instanceof NaiiUser){
			NaiiUser user = (NaiiUser) dto;
			if(NaiiControl.FILTER_ENTRY.equals(filter)){
				return user.quit == null && user.entry != null;
			}else if(NaiiControl.FILTER_QUIT.equals(filter)){
				return user.quit != null;
			}else if(NaiiControl.FILTER_RESUCE.equals(filter)){
				return user.resource != null && user.quit == null && user.entry == null;
			}else if(NaiiControl.FILTER_GROUP.equals(filter)){
				return "1".equals(user.group);
			}else if(FILTER_3.equals(filter)){
				return NaiiTools.containMonth(user.entry, 3) || NaiiTools.containMonth(user.quit, 3) || NaiiTools.containMonth(user.resource, 3) ;
			}
		}else if(dto instanceof NaiiEquipment){
			NaiiEquipment e = (NaiiEquipment) dto;
			if(NaiiControl.FILTER_USE0.equals(filter)){
				return e.useId != null && "0".equals(e.type);
			}else if(NaiiControl.FILTER_USE1.equals(filter)){
				return e.useId != null && "1".equals(e.type);
			}else if(NaiiControl.FILTER_USE2.equals(filter)){
				return e.useId != null && "2".equals(e.type);
			}else if(NaiiControl.FILTER_EMP.equals(filter)){
				return e.useId == null;
			}
		}
		return true;
	}
	
	public NaiiUI getNaiiUI() {
		return naiiUI;
	}

	public void addChangeListener(NaiiChangeListener naiiChangeListener) {
		ls.add(naiiChangeListener);
	}
	
	private void fireChangeListener(NaiiChangeEvent naiiChangeEvent){
		Iterator<NaiiChangeListener> iter = ls.iterator();
		while(iter.hasNext()){
			iter.next().changeData(naiiChangeEvent);
		}
		System.gc();
	}
	
	public int registWindow(Window window){
		windows.add(window);
		NaiiLog.log("[registWindow] "+windows.size());
		return windows.size()-1;
	}
	
	public void removeWindow(Integer index){
		if(index == null){
			windows.remove(windows.size()-1);
		}else{
			windows.remove((int)index);
		}
		NaiiLog.log("[removeWindow] "+windows.size());
	}
	
	public Window currentWondow(){
		NaiiLog.log("[currentWindow] "+(windows.size()-1));
		return windows.size() > 0 ? windows.get(windows.size()-1) : null;
	}
	
	public Vector<NaiiUser> queryGroupList() {
		Vector<NaiiUser> list = new Vector<NaiiUser>(naiiDatabase.getNaiiUsers().size());
		for(NaiiUser user : naiiDatabase.getNaiiUsers()){
			if("是".equals(user.group) || "1".equals(user.group)){
				list.add(user);
			}
		}
		return list;
	}

	public void saveObject(NaiiDto dto, int option) {
		try {
			naiiDatabase.saveObject(dto);
			NaiiLog.log("[saveObject] "+dto.toJSON());
			fireChangeListener(new NaiiChangeEvent(dto, option));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveObject(NaiiDto dto) {
		saveObject(dto, NaiiChangeEvent.OPTION_SAVE);
	}

	/**
	 * 移除对象
	 * @param ids
	 */
	public void removeObject(NaiiDto[] ids) {
		try {
			naiiDatabase.removeObject(ids);
			fireChangeListener(new NaiiChangeEvent(ids, NaiiChangeEvent.OPTION_REMOVE));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 撤回对象
	 * @param dtos
	 * @param date
	 */
	public void backObject(NaiiDto[] dtos, String date) {
		try {
			if(dtos.length < 1){
				return;
			}else if(dtos[0] instanceof NaiiHistory){
				naiiDatabase.revokeHistory(dtos);;
			}else{
				naiiDatabase.backObject(dtos, date);
			}
			
			fireChangeListener(new NaiiChangeEvent(dtos, NaiiChangeEvent.OPTION_BACK));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public NaiiJsonDto loadEventCache(String id, int month) {
		
		NaiiEventCache json = new NaiiEventCache();
		
		try {
			String[] CACHEs = NaiiProperty.CACHE_EVENT;
			for(int i=0;i<CACHEs.length;i++){
				NaiiEventCache ne = new NaiiEventCache();
				ne.id = NaiiConfig.KEY_EVENT + "_" + CACHEs[i];
				ne.dependId = id;
				ne = (NaiiEventCache) naiiDatabase.getDto(ne);
				
				for(String m : NaiiTools.getMonths(month)){
					json.push(CACHEs[i], ne.getNumber(m));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public NaiiLoadView getLoad() {
		return naiiUI != null ? naiiUI.getLoad() : null;
	}

	
	
	
	public Map<String, List<NaiiEvent>> loadEventViewData(String event) {
		return  loadEventViewData(event, null);
	}
	public Map<String, List<NaiiEvent>> loadEventViewData(final String event, String id) {
		return naiiDatabase.loadUserEventToMap(new NaiiEventCheck() {
			@Override
			public String checkKey(NaiiEvent e) {
				if("5".equals(event)){
					String str = e.remark;
					return str != null ? str.trim(): str;
				}else if("2".equals(event)){
					String str = e.remark;
					if(str != null){
						return str.split("出差")[0];
					}else{
						return null;
					}
				}
				return "";
			}
			
		}, event, id);
	}

	public Float[] loadUser3ViewData() {
		List<NaiiDto> dtos = null;
		try {
			dtos = NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_3);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Float[] fs = new Float[]{0f, 0f, 0f, 0f};
		for(NaiiDto u : dtos){
			NaiiUser user = (NaiiUser) u;
			if(user.quit != null){
				fs[1] ++;
			}else if(user.resource != null) {
				fs[2] ++;
			}else{
				fs[0] ++;
			}
			if(user.resource != null && user.entry!= null){
				fs[3] ++;
			}
		}
		
		return fs;
	}

	public Float[] loadEquipmentViewFloat() {
		//		"未用设备",
		//		"笔记本",
		//		"个人设备",
		//		"台式机",
		//		"设备总数"
		Float[] fs = new Float[]{0f,0f,0f,0f,0f};
		
		try {
			for(NaiiDto d : queryNaiiDto(new NaiiEquipment(), null)){
				NaiiEquipment dto = (NaiiEquipment) d;
				if(dto.useId == null){
					fs[0] ++;
				}else if("1".equals(dto.type)){
					fs[1] ++;
				}else if("2".equals(dto.type)){
					fs[2] ++;
				}else if("0".equals(dto.type)){
					fs[3] ++;
				}
				fs[4] ++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fs;
	}

	public Float[] loadUserViewFloat(String[] keys, String field) {
		List<NaiiDto> dtos = null;
		try {
			dtos = NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_ENTRY);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Float[] fs = new Float[keys.length];
		for(int i=0;i<fs.length;i++){
			fs[i] = 0f;
		}
		for(NaiiDto u : dtos){
			try {
				int i=0;
				for(i=0;i<keys.length;i++){
					if(keys[i].equals(NaiiUser.class.getField(field).get(u))){
						fs[i] ++;
						break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fs;
	}
}
