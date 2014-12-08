/* 
 * Description:
 * 		字符串处理的工具类。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-02-08   Created by Mingy
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理的工具类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class StringUtils {

	/**
	 * 判断字符串是否为空。
	 * 
	 * @param str
	 *            字符串
	 * @return true为空
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 判断字符串是否为空白。
	 * 
	 * @param str
	 *            字符串
	 * @return true为空白
	 */
	public static boolean isBlank(CharSequence str) {
		return isEmpty(str) || str.toString().trim().length() == 0;
	}

	/**
	 * 加密密码。
	 * 
	 * @param password
	 *            密码
	 * @return 密文
	 */
	public static String encodePassword(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("sha");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] bytes = md.digest(password.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]);
			sb.append(hex.length() > 1 ? hex.substring(hex.length() - 2)
					: ("0" + hex));
		}
		return sb.toString();
	}

	/**
	 * 精确的比较两个字符串。
	 * 
	 * @param s1
	 *            字符串一
	 * @param s2
	 *            字符串二
	 * @return true为相等
	 */
	public static boolean equals(String s1, String s2) {
		return equals(s1, s2, false);
	}

	/**
	 * 精确的比较两个字符串。
	 * 
	 * @param s1
	 *            字符串一
	 * @param s2
	 *            字符串二
	 * @param ignoreCase
	 *            true时忽略大小写
	 * @return true为相等
	 */
	public static boolean equals(String s1, String s2, boolean ignoreCase) {
		if (s1 == null && s2 == null)
			return true;
		if ((s1 == null && s2 != null) || (s1 != null && s2 == null))
			return false;
		return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
	}

	/**
	 * 非精确的比较两个字符串。<br>
	 * 字符串将忽略空格、0长度和null的区别。
	 * 
	 * @param s1
	 *            字符串一
	 * @param s2
	 *            字符串二
	 * @return true为相等
	 */
	public static boolean equalsAbout(String s1, String s2) {
		return equalsAbout(s1, s2, false);
	}

	/**
	 * 非精确的比较两个字符串。<br>
	 * 字符串将忽略空格、0长度和null的区别。
	 * 
	 * @param s1
	 *            字符串一
	 * @param s2
	 *            字符串二
	 * @param ignoreCase
	 *            true时忽略大小写
	 * @return true为相等
	 */
	public static boolean equalsAbout(String s1, String s2, boolean ignoreCase) {
		if (s1 != null)
			s1 = s1.trim();
		if (s2 != null)
			s2 = s2.trim();
		boolean b1 = isEmpty(s1);
		boolean b2 = isEmpty(s2);
		if (b1 && b2)
			return true;
		if (b1 != b2)
			return false;
		return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
	}

	/**
	 * 将指定的日期转换为19位长度的日期字串，日期格式为：yyyy-MM-dd HH:mm:ss。
	 * 
	 * @param date
	 *            日期对象
	 * @return 日期字串
	 */
	public static String formatDate19(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static boolean isNumber(String str) {
		if (!StringUtils.isBlank(str)) {
			Pattern p = Pattern.compile("^[0-9]+$");
			Matcher m = p.matcher(str);
			return m.matches();
		} else {
			return false;
		}

	}
}
