package cn.lanyue.cas.utils;

import java.io.File;

public class StringUtil {
	

	
    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    private static final char  CHAR_BLANK     = ' ';
    public final static String COLON          = ":";
    public final static String COMMA          = ",";
    public final static String EMPTY          = "";
    public final static String UNDER_LINE     = "_";
    public final static String ENDL           = "\n";
    public final static String SLASH          = "/";
    public final static String BLANK          = " ";
    public final static String DOT            = ".";

    public final static String FILE_SEPARATOR = File.separator;

	/**
	 * 检查字符串是否为空(空字符串不算空)
	 * 
	 * <pre>
	 * StringUtil.isEmpty(null) = true
	 * StringUtil.isEmpty("") = true
	 * StringUtil.isEmpty(" ") = false
	 * StringUtil.isEmpty("bob") = false
	 * StringUtil.isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 往左截断指定字符
	 * 
	 * <pre>
	 * StringUtil.substringBefore(null, *)      = null
	 * StringUtil.substringBefore("", *)        = ""
	 * StringUtil.substringBefore("abc", "a")   = ""
	 * StringUtil.substringBefore("abcba", "b") = "a"
	 * StringUtil.substringBefore("abc", "c")   = "ab"
	 * StringUtil.substringBefore("abc", "d")   = "abc"
	 * StringUtil.substringBefore("abc", "")    = ""
	 * StringUtil.substringBefore("abc", null)  = "abc"
	 * </pre>
	 * 
	 * @param str
	 * @param separator
	 * @return
	 */
	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.length() == 0) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * 往右截断指定字符
	 * 
	 * <pre>
	 * StringUtil.substringAfter(null, *)      = null
	 * StringUtil.substringAfter("", *)        = ""
	 * StringUtil.substringAfter(*, null)      = ""
	 * StringUtil.substringAfter("abc", "a")   = "bc"
	 * StringUtil.substringAfter("abcba", "b") = "cba"
	 * StringUtil.substringAfter("abc", "c")   = ""
	 * StringUtil.substringAfter("abc", "d")   = ""
	 * StringUtil.substringAfter("abc", "")    = "abc"
	 * </pre>
	 * 
	 * @param str
	 * @param separator
	 * @return
	 */
	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * 截取中间字符串
	 * 
	 * <pre>
	 * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
	 * StringUtils.substringBetween(null, *, *)          = null
	 * StringUtils.substringBetween(*, null, *)          = null
	 * StringUtils.substringBetween(*, *, null)          = null
	 * StringUtils.substringBetween("", "", "")          = ""
	 * StringUtils.substringBetween("", "", "]")         = null
	 * StringUtils.substringBetween("", "[", "]")        = null
	 * StringUtils.substringBetween("yabcz", "", "")     = ""
	 * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
	 * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
	 * </pre>
	 * 
	 * @param str
	 * @param open
	 * @param close
	 * @return
	 */
	public static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

}
