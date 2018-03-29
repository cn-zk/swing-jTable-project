package com.naii.db;

import java.io.File;

import com.naii.tools.FileMs;
import com.naii.tools.NaiiLog;

public class NaiiConfig {

	public final String USER_DIR;
	
	public final static String KEY_DATA				="data";
	public final static String KEY_RECYCLE			="recycle";
	public final static String KEY_BACK				="back";
	public final static String KEY_EQUIPMENT		="equipment";
	public final static String KEY_USER				="user";
	
	public final static String KEY_EVENT			="event";
	public final static String KEY_CACHE			="cache";		// 缓存
	public final static String KEY_HISTORY			="history";
	public final static String KEY_REMIND			="remind";
	
	private final static String COMFIG_KEY_SYS		="sys";
	private final static String COMFIG_KEY_SEL_COMP	="sel_comp";
	private final static String COMFIG_KEY_COMPANY	="company";
	
	private final static String MS_DATA				="data.ini";
	private final static String MS_CONFIG			="config.ini";

	public final static String VALUE_NAME			="name";
	
	private FileMs dataMs;
	private FileMs configMs;
	
	public NaiiConfig() {
		USER_DIR = System.getProperty("user.dir");
	}
	
	public FileMs getDataMs() throws Exception {
		if(dataMs == null){
			dataMs = new FileMs();
			dataMs.load(new File(USER_DIR + File.separator + MS_DATA));
			NaiiLog.log("[getDataMs] "+USER_DIR + File.separator + MS_DATA);
		}
		return dataMs;
	}


	public FileMs getConfigMs() throws Exception {
		if(configMs == null){
			configMs = new FileMs();
			configMs.load(new File(USER_DIR + File.separator + MS_CONFIG));
			NaiiLog.log("[getConfigMs] "+USER_DIR + File.separator + MS_CONFIG);
		}
		return configMs;
	}

	/**
	 * 获得当前公司
	 * @return
	 * @throws Exception 
	 */
	public String getCompanyCode() throws Exception{
		String companyCode = getConfigMs().getValueMap(COMFIG_KEY_SYS).get(COMFIG_KEY_SEL_COMP);
		if(companyCode == null){
			if(getCompanyCodes() != null){
				companyCode = getCompanyCodes()[0];
			}
		}
		return companyCode;
	}
	
	/**
	 * 获得当前公司名称
	 * @return
	 * @throws Exception 
	 */
	public String getCompanyName(String companyCode) throws Exception{
		return getConfigMs().getValueMap(companyCode).get("name");
	}
	
	/**
	 * 保存选中公司
	 * @param company_code
	 * @throws Exception
	 */
	public void setCurrentCompany(String company_code) throws Exception{
		getConfigMs().getValueMap(COMFIG_KEY_SYS).put(COMFIG_KEY_SEL_COMP, company_code);
		getConfigMs().save(new File(USER_DIR + File.separator + MS_CONFIG));
	}

	/**
	 * 获得所有公司数组
	 * @return String[]
	 * @throws Exception 
	 */
	public String[] getCompanyCodes() throws Exception {
		String _company = getConfigMs().getValueMap(COMFIG_KEY_SYS).get(COMFIG_KEY_COMPANY);
		if(_company != null){
			return _company.split(",");
		}
		return null;
	}

}
