package com.rdum.tools.convert;

import java.io.UnsupportedEncodingException;

import com.rdum.tools.RdumTools;

/**
 * 字符转换
 */
public class ConvertChar {

	/**
	 * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * @param src byte[] data
	 * @return hex string
	 */
	public static String toHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder(src.length);
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[](16 进制 转 byte[])
	 * @param hexString the hex string
	 * @return byte[]
	 */
	public static byte[] HexStringToBytes(String hexString) {
		if (RdumTools.isEmpty(hexString)) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	// JAVA UNICODE 与 字节字符串 互相转换
	/**
	 * 将字符串转换成 UNICODE 码
	 */
	public static String toUnicode(String strText) {
		if(strText == null){
			return null;
		}
		char c;
		String strRet = "";
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = c;
			if (intAsc > 128) {
				strHex = Integer.toHexString(intAsc);
				strRet += "\\u" + strHex;
			} else {
				strRet = strRet + c;
			}
		}
		return strRet;
	}

	/**
	 * 将 UNICODE 码 转换成字符串
	 * @param strText
	 * @return
	 */
	public static String UnicodeDecode(String uniconde) {
		if(uniconde == null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		char c;
		while (i < uniconde.length()) {
			c = uniconde.charAt(i);
			if (c == '\\' && (i + 1) != uniconde.length()
					&& uniconde.charAt(i + 1) == 'u') {
				sb.append((char) Integer.parseInt(uniconde.substring(i + 2,
						i + 6), 16));
				i += 6;
			} else {
				sb.append(c);
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * ISO-8859-1 to GBK
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String ISO2GBK(String str)
			throws UnsupportedEncodingException {
		return new String(str.getBytes("ISO-8859-1"), "GBK");
	}

	/**
	 * Convert char to byte
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
