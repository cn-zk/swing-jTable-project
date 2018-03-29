package com.naii.tools.convert;
/**
 * <pre>
 * description:
 * copyright: copyright (c) 2004
 * </pre>
 * 取出汉字字符串的拼音首字母
 */
public class ConvertPinyin {

	// 字母z使用了两个标签，这里有２７个值
	// i, u, v都不做声母, 跟随前面的字母
	private final static char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈',
			'击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌',
			'挖', '昔', '压', '匝', '座' };

	private final static char[] alphatable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',

	'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };

	private final static int[] table = new int[27];

	// 初始化
	static{
		for (int i = 0; i < 27; ++i) {
			table[i] = gbvalue(chartable[i]);
		}
	}

	// 主函数,输入字符,得到他的声母,
	// 英文字母返回对应的大写字母
	// 其他非简体汉字返回 '0'

	public static char char2alpha(char ch) {
		if (ch >= 'a' && ch <= 'z')
			return ch;

		int gb = gbvalue(ch);
		if (gb < table[0])
			// return '0';
			return ch;

		int i;
		for (i = 0; i < 26; ++i) {
			if (match(i, gb))
				break;
		}

		return i >= 26 ? '0' : alphatable[i]; 
	}

	/**
	 * 是否存在中文
	 * @param str
	 * @return
	 */
	public static boolean existChines(String str){
		for(char ch : str.toCharArray()){
			if(isChines(ch)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 这个字符串中是否存在中文
	 * @param ch
	 * @return
	 */
	public static boolean isChines(char ch){
		return ch != char2alpha(ch);
	}

	/**
	 * 字符串中的中文转换成拼音的首字母
	 * @param sourcestr
	 * @return
	 */
	public static String toAlphaLetter(String sourcestr) {
		String result = "";
		int strlength = sourcestr.length();
		int i;
		try {
			for (i = 0; i < strlength; i++) {
				result += char2alpha(sourcestr.charAt(i));
			}
		} catch (Exception e) {
			result = "";
		}
		return result;
	}

	/**
	 * 比较
	 * @param i
	 * @param gb
	 * @return
	 */
	private static boolean match(int i, int gb) {
		if (gb < table[i])
			return false;

		int j = i + 1;

		// 字母z使用了两个标签
		while (j < 26 && (table[j] == table[i]))
			++j;

		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];

	}
	
	/**
	 * <p>
	 * 过程名称：intercept(截取文字的长度)
	 * </p>
	 * <p>
	 * 创建时间：2005-8-16,17:01:27
	 * </p>
	 * 
	 * @param fromstr	需要截取的文字
	 * @param maxlen	截取多长
	 * @param showmore	是否显示更多的三点
	 * @return 截取后的文字
	 */
	public static String intercept(String fromstr, int maxlen, boolean showmore) {
		String tostr = "";

		if (maxlen <= 0)
			return fromstr;
		if (fromstr == null)
			return "";
		if (maxlen >= chineselen(fromstr))
			return fromstr;
		if (showmore)
			if (maxlen > 5)
				maxlen = maxlen - 3;
		int k = 0;
		try {
			for (int i = 0; i < maxlen;) {
				char str = fromstr.charAt(k);
				tostr = tostr + String.valueOf(str);
				if (gbvalue(str) > 0)
					i = i + 2;
				else
					i++;
				k++;

			}
		} catch (Exception e) {
		}
		if (showmore)
			if (maxlen > 4)
				tostr = tostr + "...";
		return tostr;
	}
	public static String intercept(String fromstr, int maxlen) {
		return intercept(fromstr, maxlen, true);
	}

	/**
	 * 包涵中文字符的总长度
	 * @param fromstr
	 * @return
	 * 
	 */
	public static int chineselen(String fromstr) {
		if (fromstr == null)
			return 0;
		int fromlen = fromstr.length();
		int chineselen = 0;
		for (int i = 0; i < fromlen; i++) {
			if (gbvalue(fromstr.charAt(i)) > 0) {
				chineselen = chineselen + 2;
			} else {
				chineselen++;
			}
		}
		return chineselen;
	}

	/**
	 * <pre>
	 * 过程名称：gbvalue(返回gbk的编码)
	 * 创建时间：2005-8-16,16:22:52
	 * </pre>
	 * 
	 * @param ch
	 * @return
	 */
	public static int gbvalue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("gbk");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}
	}
}