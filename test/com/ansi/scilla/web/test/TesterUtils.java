package com.ansi.scilla.web.test;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.thewebthing.commons.lang.StringUtils;

public class TesterUtils {
	
	/**
	 * This is only here for backward compatability. It does nothing.
	 * @deprecated replace with LogManager.getLogger(logname);
	 */
	public static void makeLoggers() {
		
	}
	
	public static String postJson(String url, String jsonString ) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		String url = "http://192.168.100.199/addressNV/addressNV.wsgi/checkAddress";
		HttpPost httpPost = new HttpPost(url);
		String pageContent = null;
		System.out.println("Input\n" + jsonString);
		StringEntity params = new StringEntity(jsonString);
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		httpPost.setEntity(params);
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
	
	public static String doDelete(String url, String jsonString ) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		String url = "http://192.168.100.199/addressNV/addressNV.wsgi/checkAddress";
		HttpDelete httpDelete = new HttpDelete(url);
		String pageContent = null;
		System.out.println("Input\n" + jsonString);
		httpDelete.addHeader("content-type", "application/x-www-form-urlencoded");
		if ( ! StringUtils.isBlank(jsonString)) {
			StringEntity params = new StringEntity(jsonString);
//			httpDelete.setEntity(params);
		}
		CloseableHttpResponse response = httpclient.execute(httpDelete);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
	
	public static String getJson(String url) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		String pageContent = null;
		CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;
	}
}
