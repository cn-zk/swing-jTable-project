package com.naii.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.swing.filechooser.FileSystemView;


/**
 * 
 * 	1.组件的透明混合色:
 * 	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
 *  
 *  2.鼠标的屏幕坐标:
 *  MouseInfo.getPointerInfo().getLocation();
 *  
 *  3.任务状态栏的大小:
 *  Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration());
 *  
 *  4.创建桌面图像 :
 *  new Robot().createScreenCapture(new Rectangle());
 * 
 *  5.long number to date:
 *  SimpleDateFormat sf = new SimpleDateFormat("HH(时):mm(分):ss(秒) ms(毫秒)");
 *	sf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
 *	System.out.println(sf.format(456614L));
 *
 *  6.边缘锯齿:
 *  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);
 *  
 *  7.Point:
 *  SwingUtilities.convertPointToScreen(p, this);
 *  SwingUtilities.convertPointFromScreen(p, this);
 *  
 *  8.Exception:
 *  throw new IllegalArgumentException("error") // the param's error exception.
 *  
 *  9.返回 c 的第一个 Window 祖先；如果 c 未包含在 Window 内，则返回 null。
 *  SwingUtilities.getWindowAncestor(c);
 *  
 *  10.ARBG
 *  new BufferedImage(1,1, BufferedImage.TYPE_4BYTE_ABGR);
 */
public class Assist {
	/**
	 * n 在args[] 中有相同的字符(arg不能有null)
	 *  
	 * @param n
	 * @param args
	 * @return (n = args[?]) true / false
	 */
	public static boolean equals(String arg , String...args){
		int i = 0;
		if(arg != null && args != null && args.length > 0)
			while(i < args.length)
				if(arg.equals(args[i++]))
					return true;
		return false;
	}
	
	/**
	 * n 在args[] 中有相同的字符(不区分大小写,arg不能有null)
	 *  
	 * @param n
	 * @param args
	 * @return (n = args[?]) true / false
	 */
	public static boolean equalsIgnoreCase(String arg , String...args){
		int i = 0;
		if(arg != null && args != null && args.length > 0)
			while(i < args.length)
				if(arg.equalsIgnoreCase(args[i++]))
					return true;
		return false;
	}
	
	/**
	 * 字符串数组包含空内容,如"",null,"  "等.
	 * @param val
	 * @return 有空返回true
	 */
	public static boolean isEmpty(String... val){
		if(val != null){
			int i = 0;
			while(i < val.length)
				if(isEmpty(val[i++])) 
					return true;
				return false;
		}
		return true;
	}
	
	/**
	 * 字符串等于空,如"",null,"  "等.
	 * @param str
	 * @return 是空返回true
	 */
	public static boolean isEmpty(String str){
		return str == null || str.trim().length() < 1;
	}
	
	
	public static boolean isNull(Object arg){
		return arg == null;
	}
	/**
	 * 是否包含为null的对象
	 * @param args
	 * @return []包含null：true/不包含null:false
	 */
	public static boolean isNull(Object... args){
		if(args != null){
			for(Object arg : args)
				if(isNull(arg))
					return true;
			return false;
		}
		return true;
	}
	/**
	 * 字符串切割
	 * @param arg		处理字符串
	 * @param splitchar 切割字符
	 * @return String[]
	 */
	public static String[] split(String arg, String splitchar){
		List<String> list = splitList(arg, splitchar);
		return list.toArray(new String[list.size()]);
	}
	
	public static List<String> splitList(String arg, String splitchar){
		List<String> list = new ArrayList<String>();
		if(arg != null){
			String source = new String(arg);
			int index = -1;
			while((index = source.indexOf(splitchar)) != -1){
				list.add(source.substring(0, index));
				source = source.substring(index +1);
			}
			list.add(source);
		}
		return list;
	}
	
	/**
	 * 打开文件
	 * @param bySoft	使用软件路径/Windows目录下的软件名称/null=explorer
	 * @param filePath	文件路径
	 * @param select	是否选择
	 * @return
	 * @throws IOException
	 */
	public static boolean openFile(String bySoft, String filePath, boolean select)
		throws IOException {
		if (filePath != null) {
			String exec = 
				bySoft == null ? 
				("cmd /c start explorer " +
					(select ? " /select," : "")
					+"\"" + filePath + "\"") : 
				(bySoft + " " + filePath);
			Runtime.getRuntime().exec(exec);
			return true;
		}
		return false;
	}
	
	/**
	 * 获得给定范围的随机数(随机数:n, min <= n >= max;)
	 * @param min	最小值
	 * @param max	最大值
	 * @return n	随机数
	 */
	public static int random(int min, int max){
		if(min > max)
			throw new IllegalArgumentException("min:"+min+" > max:"+max);
		max ++;
		return (int)((java.lang.Math.random() *  (max - min)) + min);
	}
	
	/**
	 * long时间格式化显示时间
	 * @param l
	 * @return
	 */
	public static String toTime(long l){
		SimpleDateFormat sf = new SimpleDateFormat("HH(时):mm(分):ss(秒) ms(毫秒)");
		sf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		return sf.format(l);
	}
	
	public static String replace(String val, String split, String... vals) {
		StringBuffer buf = new StringBuffer(val);
		if(vals != null && vals.length > 0){
			int index = -1;
			for(String str : vals){
				if((index = buf.indexOf(split)) != -1){
					buf = buf.replace(index, index + 1, str);
				}else{
					break;
				}
			}
		}
		return buf.toString();
	}

	/**
	 * 
	 * @param args
	 * @param arg
	 * @return
	 */
	public static boolean contains(List<String> args, String arg) {
		if(args == null || args.size() < 1 || arg == null ) ;else
		for(String a:args){
			if(a != null && a.equalsIgnoreCase(arg)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param args
	 * @param arg
	 * @return
	 */
	public static boolean contains(String[] args, String arg) {
		if(args == null || args.length < 1 || arg == null ) ;else
		for(String a:args){
			if(a != null && a.equalsIgnoreCase(arg)){
				return true;
			}
		}
		return false;
	}

	public static boolean enclosed(String str, String begin, String end){
		if(str != null){
			if(str.indexOf(begin) == 0 && str.lastIndexOf(end) == str.length() -1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获得桌面文件对象
	 * @return
	 */
	public static File getDesktopPath() {
		return FileSystemView.getFileSystemView() .getHomeDirectory();
	}

}
