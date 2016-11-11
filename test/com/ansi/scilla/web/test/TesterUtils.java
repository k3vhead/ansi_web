package com.ansi.scilla.web.test;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.PropertyNames;

public class TesterUtils {
	public TesterUtils() {
		makeLoggers();
	}
	
	public static void makeLoggers() {
		for ( String loggerName : new String[] {"com.thewebthing","org.apache"} ) {
			makeLogger(loggerName, Level.INFO);
		}
		String ansiLoggerName = AppUtils.getProperty(PropertyNames.LOG_NAME);
		makeLogger(ansiLoggerName, Level.DEBUG);
	}

	private static void makeLogger(String loggerName, Level level) {
		Logger logger = Logger.getLogger(loggerName);
		PatternLayout layout = new PatternLayout(AppUtils.getProperty(PropertyNames.LOG_PATTERN));
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		consoleAppender.activateOptions();
		logger.setLevel(level);
		
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
