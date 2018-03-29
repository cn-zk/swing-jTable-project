package com.rdum.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * StreamBag
 * @date	12/01/01
 */
public class Stream {

	// 读写速度控制M以内
	public static int MAX_LENGTH = 1024 * 1024;
	
	// 默认的读写格
	public static String DEFAULT_FORMAT = "UTF-8";
	
	public static String readerString(File file) throws IOException{
		return readerString(file, DEFAULT_FORMAT);
	}
	/**
	 * 读文
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readerString(File file, String format) throws IOException{
		return readerString(file, format, false);
	}
	/**
	 * 读文
	 * @param file		文件
	 * @param lineWarp	是否过滤换行
	 * @return
	 * @throws IOException
	 */
	public static String readerString(File file, String format, boolean lineWarp) throws IOException{
		StringBuffer content = new StringBuffer((int)file.length());
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), format));
		try {
			if(lineWarp){
				while(read.ready()){
					content.append(read.readLine());
				}
			}else{
				char[] c = new char[1024];
				int end = -1;
				while((end = read.read(c)) != -1){
					content.append(new String(c, 0, end));
				}
			}
		} finally {
			if (read != null)
				read.close();
		}
		return content.toString(); 
	}
	public static String readerString(InputStream input) throws IOException{
		return readerString(input, DEFAULT_FORMAT);
	}
	public static String readerString(InputStream input, String format) throws IOException{
		return readerString(input, format, false);
	}
	/**
	 * 字符
	 * @param input  
	 * @param format 字符
	 * @param lineWarp 过滤换行
	 * @return String
	 * @throws IOException
	 */
	public static String readerString(InputStream input, String format,
			boolean lineWarp) throws IOException{
		StringBuffer content = new StringBuffer();
		BufferedReader read = new BufferedReader(new InputStreamReader(input, format));
		try {
			if(lineWarp){
				while(read.ready()){
					content.append(read.readLine());
				}
			}else{
				char[] c = new char[1024];
				int end = -1;
				while((end = read.read(c)) != -1){
					content.append(new String(c, 0, end));
				}
			}
		} finally {
			if (read != null)
				read.close();
		}
		return content.toString(); 
	}
	
	/**
	 * 读取数据存放到Array
	 * @param in
	 * @param format
	 * @return List<String>
	 * @throws IOException
	 */
	public static List<String> readerArray(InputStream in) throws IOException {
		return readerArray(in, "utf-8");
	}
	public static List<String> readerArray(InputStream in, String format) throws IOException {
		return readerArray(in, format, null);
	}
	public static List<String> readerArray(InputStream in, String format, StringFilter filter) throws IOException {
		List<String> arr = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, format));
			if(filter != null){
				String line;
				while (br.ready()){
					line = br.readLine();
					if(filter.filter(line))
						arr.add(line);
				}
			}else
				while (br.ready())
					arr.add(br.readLine());
		} finally {
			if (in != null)
				in.close();
		}
		return arr;
	}
	
	public interface StringFilter{
		public boolean filter(String line);
	}
	
	/**
	 * Byte copy (自动根据文件大小调整拷贝速度,创建字节数组不超M)
	 * 
	 * @param path
	 * @param toPath
	 * @return
	 * @throws IOException
	 */
	public static boolean byteCopy(String path, String toPath)
			throws IOException {
		return byteCopy(new File(path), new File(toPath));
	}
	
	/**
	 * 字节拷贝(效率
	 * @param file
	 * @param toFile
	 * @return
	 * @throws IOException
	 */
	public static boolean byteCopy(File file, File toFile)
	throws IOException {
		boolean flag = false;
		if (file.exists()) {
			toFile.getParentFile().mkdirs();
			toFile.createNewFile();
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(toFile);
			try {
				// 文件大小小于1M
				if(file.length() < MAX_LENGTH){
					int len = new Long(file.length()).intValue();
					byte[] arr = new byte[len];
					in.read(arr, 0, len);
					out.write(arr);
					out.flush();
				}else{
					int len = -1;
					byte[] arr = new byte[MAX_LENGTH];
					while((len = in.read(arr)) != -1){
						// 每次写入1M的数
						out.write(arr, 0, len);
						out.flush();
					}
				}
				flag = true;
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
		}
		return flag;
	}
	
	/**
	 * 字节流读文件(自动根据文件大小调整拷贝速度,创建字节数组不超M)
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String inputString(File file) throws IOException {
		StringBuffer buf = new StringBuffer();
		if (file != null && file.exists()) {
			InputStream in = new FileInputStream(file);
			try {
				byte b[] = new byte[smartLength(file)];
				int end = 0;
				while ((end = in.read(b)) != -1){
					buf.append(new String(b, 0, end));
				}
			} finally {
				if (in != null)
				in.close();
			}
		}
		return buf.toString();
	}

	// The smart auto set file max length.
	private static int smartLength(File file) {
		return file.length() < MAX_LENGTH ? (int) file.length() : MAX_LENGTH;
	}
	/**
	 * Byte字节数组1024,不建议读取大文件)
	 * @param in	inputStream
	 * @return read content
	 * @throws Exception
	 */
	public static String inputString(InputStream in) throws IOException {
		StringBuffer buf = new StringBuffer();
		try {
			byte b[] = new byte[1024];
			int end = 0;
			while ((end = in.read(b)) != -1){
				buf.append(new String(b, 0, end));
			}
		} finally {
			if (in != null)
			in.close();
		}
		return buf.toString();
	}
	
	/**
	 * The write string
	 * @param str	write string
	 * @param save	save file
	 * @throws IOException
	 */
	public static void write(String str, File save) throws IOException{
		OutputStream out = new FileOutputStream(save);
		out.write(str.getBytes());
		out.flush();
		out.close();
	}
}

