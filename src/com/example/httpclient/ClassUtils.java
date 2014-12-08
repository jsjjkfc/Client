/* 
 * Description:
 * 		对象类的工具类。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-03-08   Created by Mingy
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

import java.lang.reflect.Field;

/**
 * 对象类的工具类。
 * @author Mingy
 * @since 1.0.0
 */
public abstract class ClassUtils {

	/**
	 * 安全的取得类成员变量的值。
	 * @param field 成员变量
	 * @param obj 对象实例
	 * @return 值，如果取得失败则返回null
	 */
	public static Object getField(Field field, Object obj) {
		boolean b = field.isAccessible();
		try {
			if (!b)
				field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException e) {
			return null;
		} finally {
			if (!b)
				field.setAccessible(false);
		}
	}

	/**
	 * 安全的为类成员变量赋值。
	 * @param field 成员变量
	 * @param obj 对象实例
	 * @param val 值
	 */
	public static void setField(Field field, Object obj, Object val) {
		boolean b = field.isAccessible();
		try {
			if (!b)
				field.setAccessible(true);
			field.set(obj, val);
		} catch (IllegalAccessException e) {
			// ignore
		} finally {
			if (!b)
				field.setAccessible(false);
		}
	}
}
