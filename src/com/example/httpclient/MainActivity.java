package com.example.httpclient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import roboguice.activity.RoboActivity;

import com.example.httpclient.ApplicationConfiguration.Defines;
import com.google.inject.Inject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends RoboActivity implements
		android.view.View.OnClickListener {
	@Inject
	protected ApplicationConfiguration configuration;
	Button getButton = null, postButton = null;
	TextView back = null;
	MyHttpClient hClient = new MyHttpClient();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getButton = (Button) findViewById(R.id.button1);
		postButton = (Button) findViewById(R.id.button2);
		back = (TextView) findViewById(R.id.textView1);
		getButton.setOnClickListener(this);
		postButton.setOnClickListener(this);
		String host = configuration.getConfig(Defines.CHANNEL_REMOTE_HOST);
		System.out.println(host);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Properties config = Config.loadConfig(this, "test.properties");
		String ip = (String) config.get("server.ip");
		String port = (String) config.get("server.port");
		if (v == getButton) {
			String resultString = hClient.doGet("http://" + ip + ":" + port
					+ "/HttpServer/Myserver");
			back.setText(resultString);

		} else if (v == postButton) {
			String resultString = hClient
					.doPost("http://192.168.2.102:8080/HttpServer/Myserver");
			back.setText(resultString);
		}

	}

}
