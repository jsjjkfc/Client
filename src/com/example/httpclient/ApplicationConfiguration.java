/* 
 * Description:
 * 		应用的配置。
 * 
 * History：
 * ========================================
 * Date         Memo
 * 2012-02-07   Created by Mingy
 * ========================================
 * 
 * Copyright 2010 , 迪爱斯通信设备有限公司保留。
 */
package com.example.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import roboguice.util.Ln;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 应用的配置。
 * 
 * @author Mingy
 * @since 1.0.0
 */
@Singleton
public class ApplicationConfiguration {

	/**
	 * 配置项的定义。<br>
	 * 该值应当与配置文件中的名称相对应。
	 * 
	 * @author Mingy
	 * @since 1.0.0
	 */
	public static interface Defines {
		/** 线程池容量 */
		String THREAD_POOL_CAPACITY = "thread.pool.capacity";

		/** 应用存储的根路径 */
		String STORAGE_ROOT = "storage.root";

		/** 存储是否加密 */
		String STORAGE_SECURE = "storage.secure";

		/** 存储的秘钥 */
		String STORAGE_KEY = "storage.key";

		/** DB存储的文件路径 */
		String STORAGE_DB_PATH = "storage.db.path";

		/** DB存储的版本号 */
		String STORAGE_DB_VERSION = "storage.db.version";

		/** VFS存储的文件路径 */
		String STORAGE_VFS_PATH = "storage.vfs.path";

		/** 本地通道端口 */
		String UDP_LOCAL_PORT = "udp.local.port";

		/** 远端通道主机 */
		String CHANNEL_REMOTE_HOST = "channel.remote.host";

		/** 远端通道端口 */
		String CHANNEL_REMOTE_PORT = "channel.remote.port";

		/** 视频服务器主机 */
		String CHANNEL_VIDEO_HOST = "channel.video.host";

		/** 视频服务器端口 */
		String CHANNEL_VIDEO_PORT = "channel.video.port";

		/** 视频服务器用户名 */
		String CHANNEL_VIDEO_NAME = "channel.video.name";

		/** 视频服务器密码 */
		String CHANNEL_VIDEO_PASSWORD = "channel.video.password";

		/** 视频服务器EPID */
		String CHANNEL_VIDEO_EPID = "channel.video.epid";

		/** 定位的最小时间间隔 */
		String LOCATION_MIN_INTERVAL = "location.min.interval";

		/** 位置变化的最小距离间隔 */
		String LOCATION_MIN_DISTANCE = "location.min.distance";

		/** 通道的ping周期 */
		String CHANNEL_PING_INTERVAL = "channel.ping.interval";

		/** 消息发送失败后重试等待的时间 */
		String CHANNEL_MSG_RESEND_WAIT = "channel.msg.resend.wait";

		/** 发送后等待消息回复的超时 */
		String CHANNEL_MSG_REPLY_TIMEOUT = "channel.msg.reply.timeout";

		/** 信道连接超时 */
		String CHANNEL_CONNECT_TIMEOUT = "channel.connect.timeout";

		/** 信道重连时间间隔 */
		String CHANNEL_RECONNECT_INTERVAL = "channel.reconnect.interval";
		/** 车辆状态 */
		String VEHICLE_STATUS = "vehicle.status";

		/** 备勤状态位置上报的时间间隔 */
		String DUTY_LOCATION_INTERVAL = "location.report.interval.duty";
		/** 巡逻状态位置上报的时间间隔 */
		String PATROL_LOCATION_INTERVAL = "location.report.interval.patrol";
		/** 出警状态位置上报的时间间隔 */
		String POLICE_LOCATION_INTERVAL = "location.report.interval.police";
		/** 到场状态位置上报的时间间隔 */
		String ARRIVAL_LOCATION_INTERVAL = "location.report.interval.arrival";

		/** 使用的地图 */
		String GIS_CONFIG = "gis.config";
		/** 事件信息、通知信息、求助信息、违规信息缓存的时间段 */
		String INCIDENT_CACHE_INTERVAL = "incident.cache.interval";
		String NOTIFICATION_CACHE_INTERVAL = "notification.cache.interval";
		String SOS_CACHE_INTERVAL = "sos.cache.interval";
		String BREAK_RULE_CACHE_INTERVAL = "break.rule.cache.interval";
		// 默认地图中心经纬度
		String DEFAULT_LONGITUDE = "default.longitude";
		String DEFAULT_LATITUDE = "default.latitude";

		String VIDEO_DEGREE = "video.degree";
	}

	/** 单例 */
	private static ApplicationConfiguration instance;

	/** 应用 */
	private Application application;

	/** 当前版本号 */
	private String version;

	/** 所有配置项 */
	private Map<String, ConfigItem> configItems;

	/** 执行服务 */
	private ExecutorService executorService;

	/** HTTP客户端连接池 */
	private ClientConnectionManager connectionManager;

	/** Cookie存储 */
	private CookieStore cookieStore;

	/** 全局的属性 */
	private Map<String, Object> attributes;

	/** IMEI */
	private String deviceId;

	/** IMSI */
	private String subscriberId;

	/** 网络类型：0为移动网络、1为WIFI、-1为无网络 */
	private int networkType = -1;

	/** 生产序列号的种子 */
	private String serialSeed;

	private List<Activity> activityList = new LinkedList<Activity>();

	/**
	 * 返回全局配置的实例。
	 * 
	 * @return 全局配置。
	 */
	public static ApplicationConfiguration getInstance() {
		return instance;
	}

	/**
	 * 构造器。
	 */
	@Inject
	public ApplicationConfiguration(Application application) {
		instance = this;
		this.application = application;
		this.attributes = new HashMap<String, Object>();
		loadPreferenceKeys();
		loadConfigs();
		loadDeviceInfo();
		readNetworkType();
	}

	/**
	 * 转换字符串资源ID为首选项的键。
	 * 
	 * @param resId
	 *            字符串资源ID
	 * @return 首选项的键
	 */
	private static String KEY(int resId) {
		return String.valueOf(resId);
	}

	/**
	 * 将KEY(id)的定义加载成实际的字符串。
	 * 
	 * @see {@link #KEY(int)}
	 */
	private void loadPreferenceKeys() {
		for (Field field : Defines.class.getFields()) {
			Object key = ClassUtils.getField(field, null);
			if (key instanceof String) {
				String value;
				try {
					int id = Integer.parseInt((String) key);
					value = application.getString(id);
				} catch (Exception e) {
					continue;
				}
				ClassUtils.setField(field, null, value);
				Ln.v(field.getName() + ": " + value);
			}
		}
	}

	/**
	 * 取得配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值，配置不存在返回null
	 */
	public String getConfig(String key) {
		ConfigItem item = configItems.get(key);
		return item != null ? item.getValue() : null;
	}

	/**
	 * 取得配置项。
	 * 
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public String getConfig(String key, String defaultValue) {
		String value = getConfig(key);
		return StringUtils.isBlank(value) ? value : defaultValue;
	}

	/**
	 * 取得布尔型配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值，配置不存在返回false
	 */
	public boolean getConfigBoolean(String key) {
		String value = getConfig(key);
		return Boolean.parseBoolean(value);
	}

	/**
	 * 取得整型配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public int getConfigInt(String key) throws NullPointerException {
		String value = getConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Integer.parseInt(value);
	}

	/**
	 * 取得整型配置项。
	 * 
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public int getConfigInt(String key, int defaultValue) {
		try {
			return getConfigInt(key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * 取得长整型配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public long getConfigLong(String key) throws NullPointerException {
		String value = getConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Long.parseLong(value);
	}

	/**
	 * 取得长整型配置项。
	 * 
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public long getConfigLong(String key, long defaultValue) {
		try {
			return getConfigLong(key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * 取得单精度浮点型配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public float getConfigFloat(String key) throws NullPointerException {
		String value = getConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Float.parseFloat(value);
	}

	/**
	 * 取得单精度浮点型配置项。
	 * 
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public float getConfigFloat(String key, float defaultValue) {
		try {
			return getConfigFloat(key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * 取得双精度浮点型配置项。
	 * 
	 * @param key
	 *            键名
	 * @return 值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public double getConfigDouble(String key) throws NullPointerException {
		String value = getConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Double.parseDouble(value);
	}

	/**
	 * 取得双精度浮点型配置项。
	 * 
	 * @param key
	 *            键名
	 * @param defaultValue
	 *            默认值
	 * @return 值
	 */
	public double getConfigDouble(String key, double defaultValue) {
		try {
			return getConfigDouble(key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * 取得配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值，配置不存在返回null
	 */
	public String getDefaultConfig(String key) {
		ConfigItem item = configItems.get(key);
		return item != null ? item.getDefaultValue() : null;
	}

	/**
	 * 取得布尔型配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值，配置不存在返回false
	 */
	public boolean getDefaultConfigBoolean(String key) {
		String value = getDefaultConfig(key);
		return Boolean.parseBoolean(value);
	}

	/**
	 * 取得整型配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public int getDefaultConfigInt(String key) throws NullPointerException {
		String value = getDefaultConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Integer.parseInt(value);
	}

	/**
	 * 取得长整型配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public long getDefaultConfigLong(String key) throws NullPointerException {
		String value = getDefaultConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Long.parseLong(value);
	}

	/**
	 * 取得单精度浮点型配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public float getDefaultConfigFloat(String key) throws NullPointerException {
		String value = getDefaultConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Float.parseFloat(value);
	}

	/**
	 * 取得双精度浮点型配置项的默认值。
	 * 
	 * @param key
	 *            键名
	 * @return 默认值
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public double getDefaultConfigDouble(String key)
			throws NullPointerException {
		String value = getDefaultConfig(key);
		if (StringUtils.isBlank(value))
			throw new NullPointerException(key + " not set");
		return Double.parseDouble(value);
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, String value) {
		String old = getConfig(key);
		if (!StringUtils.equalsAbout(value, old)) {
			ConfigItem item = configItems.get(key);
			if (item == null) {
				item = new ConfigItem();
				item.setName(key);
				configItems.put(key, item);
			}
			item.setValue(value);
			item.setChanged(true);
		}
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, boolean value) {
		setConfig(key, String.valueOf(value));
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, int value) {
		setConfig(key, String.valueOf(value));
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, long value) {
		setConfig(key, String.valueOf(value));
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, float value) {
		setConfig(key, String.valueOf(value));
	}

	/**
	 * 设置配置项。
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            值
	 */
	public void setConfig(String key, double value) {
		setConfig(key, String.valueOf(value));
	}

	/**
	 * 取消配置项。
	 * 
	 * @param key
	 *            键名
	 */
	public void unsetConfig(String key) {
		ConfigItem item = configItems.get(key);
		if (item != null
				&& !StringUtils.equalsAbout(item.getValue(),
						item.getDefaultValue())) {
			item.setValue(item.getDefaultValue());
			item.setChanged(true);
		}
	}

	/**
	 * 载入默认配置项。
	 */
	private void loadDefaultConfigs() {
		InputStream is = null;
		try {
			is = application.getAssets().open("application.properties");
			Properties properties = new Properties();
			properties.load(is);
			Enumeration<?> names = properties.propertyNames();
			String name;
			while (names.hasMoreElements()) {
				name = (String) names.nextElement();
				ConfigItem item = configItems.get(name);
				if (item == null) {
					item = new ConfigItem();
					item.setName(name);
					configItems.put(name, item);
				}
				item.setDefaultValue(properties.getProperty(name));
				item.setValue(item.getDefaultValue());
			}
		} catch (IOException e) {
			throw new ApplicationException("unable to load config", e);
		} finally {
			IOUtils.close(is);
		}
	}

	/**
	 * 载入保存的配置项。
	 */
	private void loadStoredConfigs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(application);
		for (Entry<String, ?> entry : prefs.getAll().entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			ConfigItem item = configItems.get(name);
			if (item == null) {
				item = new ConfigItem();
				item.setName(name);
				configItems.put(name, item);
			}
			item.setValue(value != null ? value.toString() : "");
			Ln.v("loaded: " + item);
		}
	}

	/**
	 * 加载所有配置项。
	 */
	public synchronized void loadConfigs() {
		if (configItems == null)
			configItems = new HashMap<String, ConfigItem>();
		else
			configItems.clear();
		loadDefaultConfigs();
		loadStoredConfigs();
	}

	/**
	 * 保存所有改变的配置项。
	 * 
	 * @return true表示保存成功，如配置项未改变也会返回true
	 */
	public synchronized boolean saveConfigs() {
		List<ConfigItem> items = new ArrayList<ConfigItem>();
		for (ConfigItem item : configItems.values()) {
			if (item.isChanged())
				items.add(item);
		}
		if (items.isEmpty())
			return true;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(application);
		Editor e = prefs.edit();
		for (ConfigItem item : items) {
			if (!StringUtils.equalsAbout(item.getValue(),
					item.getDefaultValue())) {
				e.putString(item.getName(), item.getValue());
				Ln.d("save " + item.getName() + ": " + item.getValue());
			} else {
				e.remove(item.getName());
				Ln.d("remove " + item.getName());
			}
			item.setChanged(false);
		}
		return e.commit();
	}

	/**
	 * 恢复所有配置项到默认值。
	 * 
	 * @param removeAllStored
	 *            true表示会移除所有保存的配置项，否则仅移除有默认值的配置项
	 * @return true表示恢复成功
	 */
	public synchronized boolean restoreConfigs(boolean removeAllStored) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(application);
		Map<String, ?> map = prefs.getAll();
		if (map != null && !map.isEmpty()) {
			Editor e = prefs.edit();
			for (String name : map.keySet()) {
				if (removeAllStored
						|| (configItems.containsKey(name) && configItems.get(
								name).getDefaultValue() != null)) {
					e.remove(name);
					Ln.d("remove " + name);
				}
			}
			if (!e.commit())
				return false;
		}
		for (Iterator<ConfigItem> it = configItems.values().iterator(); it
				.hasNext();) {
			ConfigItem item = it.next();
			if (item.getDefaultValue() != null) {
				item.setValue(item.getDefaultValue());
				item.setChanged(false);
			} else if (removeAllStored) {
				it.remove();
			}
		}
		return true;
	}

	/**
	 * 保存指定的配置项。
	 * 
	 * @param key
	 *            键名
	 * @return true表示保存成功，如配置项未改变也会返回true
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public synchronized boolean saveConfig(String key)
			throws NullPointerException {
		ConfigItem item = configItems.get(key);
		if (item == null)
			throw new NullPointerException(key + " not set");
		if (!item.isChanged())
			return true;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(application);
		Editor e = prefs.edit();
		if (!StringUtils.equalsAbout(item.getValue(), item.getDefaultValue())) {
			e.putString(item.getName(), item.getValue());
			Ln.d("save " + item.getName() + ": " + item.getValue());
		} else {
			e.remove(item.getName());
			Ln.d("remove " + item.getName());
		}
		item.setChanged(false);
		return e.commit();
	}

	/**
	 * 恢复指定配置项到默认值。
	 * 
	 * @param key
	 *            键名
	 * @return true表示恢复成功
	 * @exception NullPointerException
	 *                配置不存在时抛出
	 */
	public boolean restoreConfig(String key) throws NullPointerException {
		ConfigItem item = configItems.get(key);
		if (item == null)
			throw new NullPointerException(key + " not set");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(application);
		if (prefs.contains(key)) {
			Editor e = prefs.edit();
			e.remove(key);
			Ln.d("remove " + key);
			if (!e.commit())
				return false;
		}
		if (item.getDefaultValue() != null) {
			item.setValue(item.getDefaultValue());
			item.setChanged(false);
		} else {
			configItems.remove(key);
		}
		return true;
	}

	/**
	 * 取得当前应用。
	 * 
	 * @return 应用
	 */
	public Application getApplication() {
		return (Application) application;
	}

	/**
	 * 取得当前应用的版本。
	 * 
	 * @return 版本
	 */
	public String getVersion() {
		if (version == null) {
			try {
				version = application.getPackageManager().getPackageInfo(
						application.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				throw new ApplicationException(e);
			}
		}
		return version;
	}

	/**
	 * 取得执行服务。
	 * 
	 * @return 执行服务
	 */
	public ExecutorService getExecutorService() {
		if (executorService == null) {
			synchronized (ExecutorService.class) {
				if (executorService == null) {
					int size = 0;
					try {
						size = getConfigInt(Defines.THREAD_POOL_CAPACITY);
					} catch (Exception e) {
						// ignore
					}
					executorService = size > 0 ? Executors
							.newFixedThreadPool(size) : Executors
							.newCachedThreadPool();
				}
			}
		}
		return executorService;
	}

	/**
	 * 创建HTTP客户端。
	 * 
	 * @return HTTP客户端
	 */
	public HttpClient createHttpClient() {
		return createHttpClient(null);
	}

	/**
	 * 创建HTTP客户端。
	 * 
	 * @param params
	 *            创建参数
	 * @return HTTP客户端
	 */
	public HttpClient createHttpClient(HttpParams params) {
		DefaultHttpClient client = new DefaultHttpClient(connectionManager,
				params);
		if (cookieStore != null)
			client.setCookieStore(cookieStore);
		else
			cookieStore = client.getCookieStore();
		return client;

	}

	/**
	 * 返回Cookie值。
	 * 
	 * @return Cookie值
	 */
	public String getCookie() {
		StringBuilder sb = new StringBuilder();
		for (Cookie cookie : cookieStore.getCookies()) {
			if (!cookie.isExpired(new Date())) {
				sb.append(sb.length() > 0 ? "; " : "").append(cookie.getName())
						.append("=").append(cookie.getValue());
			}
		}
		return sb.toString();
	}

	/**
	 * 取得属性值。
	 * 
	 * @param name
	 *            属性名
	 * @return 属性值
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * 设置属性值。
	 * 
	 * @param name
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 旧的属性值
	 */
	public Object setAttribute(String name, Object value) {
		return attributes.put(name, value);
	}

	/**
	 * 移除指定的属性。
	 * 
	 * @param name
	 *            属性名
	 * @return 移除的属性值
	 */
	public Object removeAttribute(String name) {
		return attributes.remove(name);
	}

	/**
	 * 根据类型取得对象。
	 * 
	 * @param clazz
	 *            类型
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(Class<T> clazz) {
		return (T) getAttribute(clazz.getName());
	}

	/**
	 * 按类型设置对象。
	 * 
	 * @param clazz
	 *            类型
	 * @param object
	 *            对象
	 * @return 旧的对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(Class<T> clazz, T object) {
		return (T) setAttribute(clazz.getName(), object);
	}

	/**
	 * 根据对象的类型进行设置，对象不能为空。
	 * 
	 * @param object
	 *            对象
	 * @return 旧的对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(T object) {
		return (T) setAttribute(object.getClass().getName(), object);
	}

	/**
	 * 移除类型。
	 * 
	 * @param clazz
	 *            类型
	 * @return 移除的对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(Class<T> clazz) {
		return (T) removeAttribute(clazz.getName());
	}

	/**
	 * 加载设备信息。
	 */
	public void loadDeviceInfo() {
		TelephonyManager manager = (TelephonyManager) application
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = manager.getDeviceId();
		if (StringUtils.isBlank(deviceId)) {
			deviceId = "0123456789abcde";
		}
		subscriberId = manager.getSubscriberId();
		serialSeed = deviceId
				+ new SimpleDateFormat("HHmmss").format(new Date());
	}

	/**
	 * 读取网络类型。
	 */
	public void readNetworkType() {
		ConnectivityManager manager = (ConnectivityManager) application
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = manager.getActiveNetworkInfo();
		if (netInfo != null) {
			if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE
					|| netInfo.getType() == ConnectivityManager.TYPE_WIFI)
				networkType = netInfo.getType();
			else
				networkType = -1;
		} else {
			networkType = -1;
		}
	}

	/**
	 * 取得设备的IMEI。
	 * 
	 * @return IMEI
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * 取得设备的IMSI。
	 * 
	 * @return IMSI
	 */
	public String getSubscriberId() {
		return subscriberId;
	}

	/**
	 * 取得设备的网络类型。
	 * 
	 * @return 0为移动网络、1为WIFI、-1为无网络
	 */
	public int getNetworkType() {
		return networkType;
	}

	/**
	 * 取得生产序列号的种子。
	 * 
	 * @return 生产序列号的种子
	 */
	public String getSerialSeed() {
		return serialSeed;
	}

	/**
	 * 添加Activity
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 结束所有Activity,退出程序
	 */
	public void exit() {
		try {
			for (Activity activity : activityList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}
