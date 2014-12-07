package com.example.httpclient;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import android.content.Context;

public class Config {
	public static Properties loadConfig(Context context, String file) {
		Properties properties = new Properties();
		try {
			InputStream in = context.getResources().getAssets().open(file);
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	public void saveConfig(Context context, String file, Properties properties) {
		try {
			FileOutputStream s = new FileOutputStream(file, false);
			properties.store(s, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
