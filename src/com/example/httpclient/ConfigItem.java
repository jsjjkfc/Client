/* 
 * Description:
 * 		配置项。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-02-15   Created by Mingy
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

/**
 * 配置项。
 * @author Mingy
 * @since 1.0.0
 */
public class ConfigItem {

	/** 配置名 */
	private String name;

	/** 配置值 */
	private String value;
	
	/** 缺省值 */
	private String defaultValue;

	/** 值是否被修改 */
	private boolean changed;

	/**
	 * Get the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value.
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value.
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the defaultValue.
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Set the defaultValue.
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Get the changed.
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Set the changed.
	 * @param changed the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[key: " + name + ", value: " + value + "]"; 
	}
}
