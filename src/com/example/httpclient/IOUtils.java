/* 
 * Description:
 * 		I/O操作的工具类。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-02-10   Created by Mingy
 * 2013-09-13	Modified by Bezaleel
 * 1、增加通用IO流关闭方法close(Closable...cs)
 * 2、增加sdcard状态判断方法
 * 3、增加文件创建方法
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

import roboguice.util.Ln;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * I/O操作的工具类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class IOUtils {

	/** 默认的缓存大小 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * 关闭HTTP连接。
	 * 
	 * @param conn
	 *            HTTP连接
	 */
	public static void close(HttpURLConnection conn) {
		try {
			if (conn != null)
				conn.disconnect();
		} catch (Exception e) {
			Ln.e(e);
		}
	}

	/**
	 * 关闭输入流。
	 * 
	 * @param is
	 *            输入流
	 */
	public static void close(InputStream is) {
		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			Ln.e(e);
		}
	}

	/**
	 * 关闭输出流。
	 * 
	 * @param os
	 *            输出流
	 */
	public static void close(OutputStream os) {
		try {
			if (os != null)
				os.close();
		} catch (IOException e) {
			Ln.e(e);
		}
	}

	/**
	 * 通用IO流关闭方法 <br>
	 * <b>注意使用该方法时由于参数压栈为LIFO</b><br>
	 * 所以IO流多层装饰的对象关闭时，需将外层装饰者流放到内层流后面<br>
	 * 输入流放在输出流后面
	 * 
	 * @author Bezaleel
	 * @since 1.1.0
	 * @param cs
	 *            IO流数组
	 * @throws IOException
	 *             IO异常
	 */
	public static void close(Closeable... cs) {
		if (cs != null && cs.length > 0) {
			for (Closeable c : cs) {
				if (c != null) {
					try {
						c.close();
					} catch (IOException e) {
						Ln.w("io Stream close exception info : ", e);
					}
				}
			}
		}
	}

	/**
	 * 复制输入流到输出。
	 * 
	 * @param reader
	 *            输入流
	 * @param writer
	 *            输出流
	 * @return 复制的大小
	 * @throws IOException
	 */
	public static int copy(Reader reader, Writer writer) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		int count = 0;
		int n = 0;
		while (-1 != (n = reader.read(buffer))) {
			writer.write(buffer, 0, n);
			count += n;
		}
		writer.flush();
		return count;
	}

	/**
	 * 将输入流转换成字符串
	 * 
	 * @param is
	 *            输入流
	 * @return 字符串
	 * @throws IOException
	 */
	public static String toString(InputStream is) throws IOException {
		StringWriter writer = new StringWriter();
		copy(new InputStreamReader(is), writer);
		return writer.toString();
	}

	/**
	 * 将输入流转换成字符串
	 * 
	 * @param is
	 *            输入流
	 * @param charset
	 *            编码格式
	 * @return 字符串
	 * @throws IOException
	 */
	public static String toString(InputStream is, String charset)
			throws IOException {
		if (charset != null) {
			StringWriter writer = new StringWriter();
			copy(new InputStreamReader(is, charset), writer);
			return writer.toString();
		} else {
			return toString(is);
		}
	}

	/**
	 * 克隆实现了{@link Parcelable}接口的对象。
	 * 
	 * @param parcelable
	 *            {@link Parcelable}
	 * @return 克隆的对象
	 */
	public static <T extends Parcelable> T clone(T parcelable) {
		return clone(parcelable, 0);
	}

	/**
	 * 克隆实现了{@link Parcelable}接口的对象。
	 * 
	 * @param parcelable
	 *            {@link Parcelable}
	 * @param flags
	 *            标志位
	 * @return 克隆的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Parcelable> T clone(T parcelable, int flags) {
		Parcel p = Parcel.obtain();
		p.writeParcelable(parcelable, flags);
		p.setDataPosition(0);
		return (T) p.readParcelable(parcelable.getClass().getClassLoader());
	}

	/**
	 * 判断SD卡是否可用。
	 * 
	 * @author Bezaleel
	 * @since 1.1.0
	 * @return true 可用
	 */
	public static boolean isExternalStorageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 判断SD卡是否只读。
	 * 
	 * @author Bezaleel
	 * @since 1.1.0
	 * @return true 只读
	 */
	public static boolean isExternalStorageReadOnly() {
		return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 智能创建文件
	 * 
	 * @author Bezaleel
	 * @since 1.1.0
	 * @param file
	 *            文件对象
	 * @return true 创建成功
	 * @throws IOException
	 *             异常
	 */
	public static boolean creatFileIfNotExists(File file) throws IOException {
		if (file.exists())
			return true;
		boolean parentExists = file.getParentFile().exists();
		if (file.isDirectory()) {
			file.mkdirs();
		} else if (file.isFile() && parentExists) {
			file.createNewFile();
		} else {
			file.getParentFile().mkdirs();
			return file.createNewFile();
		}
		return true;
	}

	/**
	 * 字节转换成object对象
	 */
	public static Object byteToObject(byte[] bytes) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream sIn = new ObjectInputStream(in);
		return sIn.readObject();
	}

	/**
	 * object对象转换成字节
	 */
	public static byte[] objectToByte(Object obj) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream sOut = new ObjectOutputStream(out);
		sOut.writeObject(obj);
		sOut.flush();
		byte[] bytes = out.toByteArray();
		return bytes;

	}

	/** 序列化对象 */
	public static void serializeObject(String path, Object object) {
		FileOutputStream fs = null;
		ObjectOutputStream os;
		try {
			if (path == null)
				return;
			fs = new FileOutputStream(path);
			os = new ObjectOutputStream(fs);
			os.writeObject(object);
			os.flush();
			fs.close();
			os.close();
		} catch (Exception e) {

			Log.e("序列化信息", e.getMessage());
		}
	}

	/**
	 * 反序列化信息
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> Object deserializeObject(String path, T object)
			throws Exception {
		FileInputStream fs;
		ObjectInputStream ois;
		T c = null;
		if (path == null)
			return null;
		fs = new FileInputStream(path);
		ois = new ObjectInputStream(fs);
		c = (T) ois.readObject();
		fs.close();
		ois.close();

		return c;

	}

	/**
	 * @param serializeFileName
	 *            对象序列化后的文件名
	 * @param username
	 *            用户标识
	 * @param object
	 *            需要序列化的对象
	 */
	public static void saveSerializeObject(String serializeFileName,
			String username, Object object) {
		String path = ApplicationConfiguration.getInstance().getConfig(
				"SDCARD_PATH");
		String filePath = path + "/mit/temp/" + username + "/";
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		serializeObject(filePath + serializeFileName, object);
	}

	/**
	 * @param serializeFileName
	 *            对象序列化后的文件名
	 * @param username
	 *            用户标识
	 * @param object
	 *            强制转换对象的类型
	 * @throws Exception
	 */
	public static <T> Object getSerializeObject(String serializeFileName,
			String username, T object) throws Exception {
		String path = ApplicationConfiguration.getInstance().getConfig(
				"SDCARD_PATH");
		String filePath = path + "/mit/temp/" + username + "/"
				+ serializeFileName;
		return deserializeObject(filePath, object);
	}

	public static String getEvidenceFilePath(String userCode) {
		String path = ApplicationConfiguration.getInstance().getConfig(
				"SDCARD_PATH");
		String filePath = path + "/mit/temp/" + userCode + "evidence/";
		return filePath;
	}

	public static boolean isEvidenceFileExist(String userCode, String fileName) {
		boolean flag = new File(getEvidenceFilePath(userCode) + fileName)
				.exists();
		return flag;
	}

}
