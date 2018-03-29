package com.rdum.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Microsoft default config file.
 * @author tomorrow
 */
@SuppressWarnings("serial")
public class FileMs implements Serializable{

	private String format = "utf-8";
	private Map<String, Map<String, String>> ini ;
	private boolean filterEmpty = false;
	
	public FileMs() {
		ini = new HashMap<String, Map<String,String>>();
	}
	
	public void load(File file) throws IOException{
		if(file == null) return;
		load(new FileInputStream(file));
	}
	
	public void load(InputStream in) throws IOException{
		if(in == null) return;
		
		BufferedReader read = new BufferedReader(new InputStreamReader(in, format));
		try {
			String line = "";
			Map<String, String> itemMap = null;
			String name = null;;
			String items[] = null;
			while(read.ready()){
				line = read.readLine().trim();
				if(itemMap == null ? line.length() < 2 : line.length() < 1) continue;
				
				if(itemMap == null && line.indexOf("[") == 1){
					line = line.substring(1);
				}
				
				if(line.indexOf("[") == 0 && line.lastIndexOf("]") == line.length()-1){
					name = line.substring(1, line.length()-1);
					ini.put(name , itemMap = new HashMap<String, String>());
				}else{
					if(itemMap == null)
						ini.put("" , itemMap = new HashMap<String, String>());
					
					if(line.indexOf("=") != -1){
						items = line.split("=");
						if(items != null && items.length > 0){
							itemMap.put(items[0].trim(), items.length < 2 ? "" : items[1].trim());
						}
					}else{
						itemMap.put(line.trim(), "");
					}
				}
			}
		} finally {
			if (read != null)
				read.close();
			if (in != null)
				in.close();
		}
	}
	
	
	public void save(File file) throws IOException {
		save(new FileOutputStream(file));
	}
	public void save(OutputStream out) throws IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, format));
		try{
			Map<String, String> m ;
			
			for(String key : ini.keySet()){
				m = ini.get(key);
				
				if(filterEmpty && m.values().size() < 1){
					continue;
				}
				
				writer.write("["+key+"]\r\n");
				for(String k : m.keySet()){
					writer.write(k+"="+m.get(k)+"\r\n");
				}
			}
		}finally{
			if(writer != null)
				writer.close();
			if(out != null)
				out.close();
		}
	}
	
	public void setFilterEmpty(boolean filterEmpty) {
		this.filterEmpty = filterEmpty;
	}
	public boolean isFilterEmpty() {
		return filterEmpty;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Map<String, Map<String, String>> getIniMap() {
		return ini;
	}
	public void setIniMap(Map<String, Map<String, String>> ini) {
		this.ini = ini;
	}
	public Map<String, String> getValueMap(String key){
		return ini.get(key);
	}
}
