/* 
 * Description:
 * 		应用程序异常。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-02-11   Created by Mingy
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

/**
 * 应用程序异常。 
 * @author Mingy
 * @since 1.0.0
 */
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -1362421536037239152L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
