package com.naii.db;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;

import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEquipment;
import com.naii.db.dto.NaiiEvent;
import com.naii.db.dto.NaiiEventCache;
import com.naii.db.dto.NaiiHistory;
import com.naii.db.dto.NaiiUser;
import com.naii.tools.FileMs;
import com.naii.tools.NaiiLog;
import com.naii.tools.NaiiTools;
import com.naii.tools.Stream;

/**
 * 数据文件操作类
 * @author TouchWin
 */
public class NaiiDatabase {

	private NaiiConfig config;
	
	private List<NaiiUser> naiiUsers ;
	private List<NaiiEquipment> naiiEquipments ;
	
	public NaiiDatabase() {
		config = new NaiiConfig();
	}
	
	public void initializeDir() throws Exception {
		FileMs ms = config.getDataMs();
		
		String dir = config.USER_DIR +"\\db";
		
		createDir(ms, ms.getValueMap("directory"), dir);
	}
	
	private void createDir(FileMs ms, Map<String, String> map, String dir){
		if(map == null){
			return;
		}
		File f;
		for(Entry<String, String> entry : map.entrySet()){
			f = new File(dir+File.separator + entry.getKey());
			if(!f.isDirectory()){
				f.mkdirs();
			}
			NaiiLog.log("[createDir] "+f);
			createDir(ms, ms.getValueMap(entry.getKey()), f.getPath());
		}
	}
	
	public void initializeUser() throws Exception {
		
		clearRecycle();
		
		loadCompany(config.getCompanyCode());

		NaiiProperty.getProperty().parseCompany(config);
		
//		NaiiControl.getControl().addChangeListener(new NaiiChangeListener() {
//			
//			@Override
//			public void changeData(NaiiChangeEvent ce) {
//			}
//		});
		
//		try {
//			for(NaiiDto dto : naiiUsers){
//				saveObject(dto);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void loadCompany(String company) throws Exception{

		naiiUsers = loadCompanyObject(company, new NaiiUser());
		
//		sortDto(naiiUsers, "entry");

		NaiiLog.log("[loadCompany.users] "+naiiUsers.size());

		naiiEquipments = loadCompanyObject(company, new NaiiEquipment());
//		sortDto(naiiEquipments, "date");
		NaiiLog.log("[loadCompany.naiiEquipments] "+naiiEquipments.size());
	}
	
	private void clearRecycle(){
		NaiiTools.removeEmptyDir(new File(toKeysFile(NaiiConfig.KEY_RECYCLE)));
		NaiiTools.removeEmptyDir(new File(toKeysFile(NaiiConfig.KEY_DATA,NaiiConfig.KEY_EVENT)));
		NaiiTools.removeEmptyDir(new File(toKeysFile(NaiiConfig.KEY_DATA,NaiiConfig.KEY_HISTORY)));
	}
	
	public <T> List<T> loadCompanyObject( T c){
		return loadCompanyObject(null, c);
	}
	
	private <T> List<T> loadCompanyObject( String company, T c){
		List<T> list = new ArrayList<T>();
		
		File c_files = new File(getDtoDirectory((NaiiDto)c, company));
		if(c_files.isDirectory()){
			try {
				loadDtoByFile(c_files, list, c.getClass());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
//			c_files.mkdirs();
//			NaiiLog.log("[loadCompanyObject.mkdirs] "+c_files);
		}
		return list;
	}
	
	public Map<String, List<NaiiEvent>> loadUserEventToMap(NaiiEventCheck check, String event){
		return loadUserEventToMap(check, event, null);
	}
	public Map<String, List<NaiiEvent>> loadUserEventToMap(NaiiEventCheck check, String event, String id){
		Map<String, List<NaiiEvent>> itemMap = new HashMap<String, List<NaiiEvent>>();
		
		if(id == null){
			Iterator<NaiiUser> iter = naiiUsers.iterator();
			while(iter.hasNext()){
				List<NaiiEvent> es = loadCompanyObject(new NaiiEvent(iter.next().id, true));
				for(NaiiEvent e : es){
					if(e.event.equals(event)){
						String str = check.checkKey(e);
						if(str != null){
							List<NaiiEvent> l = itemMap.get(str);
							if(l == null){
								l = new ArrayList<NaiiEvent>();
								l.add(e);
								itemMap.put(str, l);
							}else{
								l.add(e);
							}
						}
					}
				}
			}
		}else{
			List<NaiiEvent> es = loadCompanyObject(new NaiiEvent(id, true));
			for(NaiiEvent e : es){
				if(e.event.equals(event)){
					String str = check.checkKey(e);
					if(str != null){
						List<NaiiEvent> l = itemMap.get(str);
						if(l == null){
							l = new ArrayList<NaiiEvent>();
							l.add(e);
							itemMap.put(str, l);
						}else{
							l.add(e);
						}
					}
				}
			}
		}
		
		return itemMap;
	}
	
	/**
	 * 保存
	 * @param dto
	 * @throws Exception
	 */
	public void saveObject(NaiiDto dto) throws Exception {
		if(dto.id == null){
			dto.id = NaiiTools.getId();
		}
		
		File dfile = new File(getDtoDirectory(dto) +File.separator + dto.id);
		if(!dfile.isFile()){
			if(dto instanceof NaiiUser){
				naiiUsers.add((NaiiUser) dto);
			}else if(dto instanceof NaiiEquipment){
				naiiEquipments.add((NaiiEquipment) dto);
			}
		} 
			
		if(!dfile.getParentFile().isDirectory()){
			dfile.getParentFile().mkdirs();
		}
		
		if(dto instanceof NaiiEvent){
			NaiiEvent ne = (NaiiEvent) dto;
			NaiiEvent ol = new NaiiEvent();
			ol.id = ne.id;
			ol.dependId = ne.dependId;
			
			ol = (NaiiEvent) getDto(ol);
			
			if(ol != null){
				updateNaiiEventCache(ol, false);
			}
			
			updateNaiiEventCache(ne, true);
		}
		
		Stream.write(dto.toJSON(), dfile);
	}
	
	/**
	 * 更新依赖缓存
	 * @param ne
	 * @param add
	 * @throws Exception
	 */
	private void updateNaiiEventCache(NaiiEvent ne, boolean add) throws Exception{
		NaiiEventCache ec = (NaiiEventCache) getDto(new NaiiEventCache(ne));
		String key = new SimpleDateFormat("yyyy-MM").format(ne.date);
		float val = ne.value != null ? ne.value : 0;
		if(add){
			ec.push(key, val);
		}else{
			ec.remove(key, val);
		}
		saveObject(ec);
		NaiiLog.log("[updateNaiiEventCache] "+add +" " + ec.toJSON());
	}


	/**
	 * 排序
	 * @param datas
	 * @param field
	 * @throws Exception
	 */
	public void sortDto(List<?> datas, final String field) throws Exception{
		sortDto(datas, field, true);
	}
	public void sortDto(List<?> datas, String field, boolean asc) throws Exception{
		try {
			Collections.sort(datas, new NaiiComparator(field, asc));
		} catch (Exception e) {
			NaiiLog.error("[sort] "+e.getMessage());
			throw e;
		}
	}

	/**
	 * 载入回收站资源
	 * @param file
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public List<NaiiDto> loadRecycleCompany(String date, NaiiDto dto) throws Exception {
		List<NaiiDto> list = new ArrayList<NaiiDto>();
		
		File recucleDir = new File(getDtoDirectory(dto, true));
		if(recucleDir.isDirectory()){
			loadDtoByFile(recucleDir, list, dto.getClass());
			NaiiLog.log("[loadRecycleCompany] "+list.size());
		}
		return list;
	}
	
	/**
	 * 载入通过递归目录文件载入dto
	 * @param dir
	 * @param list
	 * @param clas
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadDtoByFile(File dir, List list, Class clas) throws Exception{
		for(File f : dir.listFiles()){
			if(f.isDirectory()){
				loadDtoByFile(f, list, clas);
			}else if(!f.isHidden()){
				try {
					String json = Stream.readerString(f);
					list.add(clas
							.getConstructor(String.class)
							.newInstance(json));
				} catch (Exception e) {
					System.err.println(f.getPath() +"  " + e.getMessage());
				}
			}
		}
	}

	/**
	 * 
	 * @param dtos
	 * @throws Exception
	 */
	public void removeObject(NaiiDto[] dtos) throws Exception {
		removeObject(dtos, true);
	}
	/**
	 * 
	 * @param dtos
	 * @param recycle		是否放入回收站
	 * @throws Exception
	 */
	public void removeObject(NaiiDto[] dtos, boolean recycle) throws Exception {
		for(NaiiDto dto: dtos){
			if(dto == null)
				continue;
			String c_path = getDtoDirectory(dto);
			String t_path = getDtoDirectory(dto, true);
			File dfile = new File(c_path +File.separator + dto.id);
			File tfile = new File(t_path +File.separator);
			if(dfile.isFile()){
				boolean r ;
				if(recycle){
					dto.remove = new Date();
					saveObject(dto);
					tfile.mkdirs();
					r = dfile.renameTo(new File(tfile.getPath()+File.separator + dto.id));
				}else{
					r = dfile.delete();
				}
				NaiiLog.log("[removeObject] "+r);
				if(r){
					if(dto instanceof NaiiUser){
						naiiUsers.remove(dto);
					}else if(dto instanceof NaiiEquipment){
						naiiEquipments.remove(dto);
					}else if(dto instanceof NaiiEvent){
						updateNaiiEventCache((NaiiEvent) dto, false);
					}
				}
			}
		}
	}
	
	public void revokeHistory(NaiiDto[] dtos) throws Exception{
		for(NaiiDto dto : dtos){
			NaiiHistory h = (NaiiHistory) dto;
			
			NaiiDto[] rem = null;
			if(NaiiConfig.KEY_EVENT.equals(h.key)){
				JSONArray arr = new JSONArray(h.value);
				rem = new NaiiEvent[arr.length()];
				int i=0;
				for(Object val: arr){
					rem[i++] = getDto(new NaiiEvent((String)val));
				}
			}
			
			removeObject(rem, false);
		}
		removeObject(dtos, false);
	}
	
	public void backObject(NaiiDto[] dtos, String date) throws Exception{
		for(NaiiDto dto: dtos){
			String c_path = getDtoDirectory(dto);
			String t_path = getDtoDirectory(dto, true, date);
			File dfile = new File(c_path);
			File tfile = new File(t_path +File.separator + dto.id);
			if(tfile.isFile()){
				if(!dfile.isDirectory()){
					dfile.mkdirs();
				}
				boolean r = tfile.renameTo(new File(dfile.getPath()+File.separator + dto.id));
				if(r){
					NaiiLog.log("[backObject] "+r);
					if(dto instanceof NaiiUser){
						naiiUsers.add((NaiiUser) dto);
					}else if(dto instanceof NaiiEquipment){
						naiiEquipments.add((NaiiEquipment) dto);
					}else if(dto instanceof NaiiEvent){
						
					}
					dto.remove = null;
					saveObject(dto);
				}else{
					NaiiLog.error("[backObject] "+r);
				}
			}
		}
	}
	
	public NaiiDto getDto(NaiiDto dto) throws Exception{
		File dir = new File(getDtoDirectory(dto));
		if(!dir.isDirectory()){
			dir.mkdirs();
		}
		File f = new File(dir.getPath()+File.separator + dto.id);
		if(f.isFile()){
			return dto.getClass()
					.getConstructor(String.class)
					.newInstance(Stream.readerString(f));
		}else if(dto instanceof NaiiEventCache){
			return dto;
		}
		
		return null;
	}
	
	private String toKeysFile(String... keys){
		StringBuffer dir = new StringBuffer(config.USER_DIR +"\\db");
		for(String key : keys){
			if(key != null)
			dir.append(File.separator).append(key);
		}
		return dir.toString();
	}
	
	public String getDtoDirectory(NaiiDto dto){
		return getDtoDirectory(dto, false);
	}
	public String getDtoDirectory(NaiiDto dto, boolean isRecycle){
		return getDtoDirectory(dto, isRecycle, null, null);
	}
	public String getDtoDirectory(NaiiDto dto, String company){
		return getDtoDirectory(dto, false, null, company);
	}
	public String getDtoDirectory(NaiiDto dto, boolean isRecycle, String date){
		return getDtoDirectory(dto, isRecycle, date, null);
	}
	public String getDtoDirectory(NaiiDto dto, boolean isRecycle, String date, String company){
		
		if(company == null){
			company = NaiiProperty.getProperty().getCompanyCode();
		}
		
		String ROOT = isRecycle ? 
				NaiiConfig.KEY_RECYCLE+ File.separator + (
						date == null ?
						new SimpleDateFormat("yyyy").format(new Date()) :
						date ): 
					NaiiConfig.KEY_DATA;
		if(dto instanceof NaiiUser){
			return toKeysFile(
					ROOT, 
					NaiiConfig.KEY_USER,
					company);
		}else if(dto instanceof NaiiEquipment){
			return toKeysFile(
					ROOT, 
					NaiiConfig.KEY_EQUIPMENT,
					company);
		}else if(dto instanceof NaiiEvent){
			NaiiEvent ev = (NaiiEvent) dto;
			return toKeysFile(
					ROOT, 
					NaiiConfig.KEY_EVENT,
					company,
					ev.dependId);
		}else if(dto instanceof NaiiEventCache){
			return toKeysFile(
					ROOT, 
					NaiiConfig.KEY_CACHE,
					company,
					((NaiiEventCache) dto).dependId);
		}else if(dto instanceof NaiiHistory){
			NaiiHistory h = (NaiiHistory) dto;
			if(h.date == null){
				return toKeysFile(
						ROOT, 
						NaiiConfig.KEY_HISTORY,
						company);
			}else{
				return toKeysFile(
						ROOT, 
						NaiiConfig.KEY_HISTORY,
						company,
						new SimpleDateFormat("yyyy").format(h.date),
						h.option,
						h.key);
			}
		}
		
		return null;
	}

	public File[] readRecycleList(){
		return new File(toKeysFile(NaiiConfig.KEY_RECYCLE)).listFiles();
	}
	
	public List<NaiiUser> getNaiiUsers() {
		return naiiUsers;
	}

	public List<NaiiEquipment> getNaiiEquipments() {
		return naiiEquipments;
	}
}

class NaiiComparator implements Comparator<Object> {
	
	private boolean asc;
	private String field;
	
	public NaiiComparator(String field, boolean asc) {
		this.field = field;
		this.asc = asc;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		long 
		a = toNumber(o1),
		b = toNumber(o2);
		
		if(asc){
			if(a < b){
				return 1;
			}else if(a > b){
				return -1;
			}
		}else{
			if(a > b){
				return 1;
			}else if(a < b){
				return -1;
			}
		}
		return 0;
	}
	
	private long toNumber(Object o){
		long l = 0;
		try {
			Field t = o.getClass().getField(field);
			if(t.getType() == Date.class){
				Date d = (Date) t.get(o);
				l = d.getTime();
			}
		} catch (Exception e) {
			l = -1;
//			NaiiLog.error("[toNumber] "+e.getMessage());
		}
		return l;
	}
}
