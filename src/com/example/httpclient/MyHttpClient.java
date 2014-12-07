package com.example.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;

public class MyHttpClient {
	public String doGet(String uriString){
		String result="";
		String readline=null;
		HttpGet get=new HttpGet(uriString);
		HttpClient client=new DefaultHttpClient();
		try {
			HttpResponse response=client.execute(get);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity=response.getEntity();
				InputStream is=entity.getContent();
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				result=br.readLine();
				/*while((readline =br.readLine())!=null){
					result=result+readline;
				}*/
				is.close();
				br.close();
				
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	public String doPost(String url){
		String resultString=null;
		HttpPost hPost=new HttpPost(url);
		List<NameValuePair> values=new ArrayList<NameValuePair>();
		NameValuePair nvp1=new BasicNameValuePair("name", "姓名");
		values.add(nvp1);
		HttpEntity entity;
		try {
			entity=new UrlEncodedFormEntity(values,"UTF-8");
			hPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClient hc=new DefaultHttpClient();
		HttpResponse response;
		try {
			response=hc.execute(hPost);
			HttpEntity entity1=response.getEntity();
			InputStream is=entity1.getContent();
			BufferedReader bReader=new BufferedReader(new InputStreamReader(is));
			resultString=bReader.readLine();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return resultString;
	}

}
