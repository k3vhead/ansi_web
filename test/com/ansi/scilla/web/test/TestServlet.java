package com.ansi.scilla.web.test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.login.request.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.thewebthing.commons.lang.JsonException;

public abstract class TestServlet {

	public static final String PLATFORM_IS_LOCAL = "127.0.0.1";
	public static final String PLATFORM_IS_DEV = "dev.asa.ansi.local";
	public static final String PLATFORM_IS_UAT = "uat.asa.ansi.local";
	public static final String PLATFORM_IS_PROD = "prod.asa.ansi.local";
	
	
	protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
	protected final Logger logger = LogManager.getLogger(TestServlet.class);
	protected String userId=null;
	protected String password=null;
	protected String hostname = PLATFORM_IS_LOCAL;
	protected Integer hostport = 8080;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Integer getHostport() {
		return hostport;
	}
	public void setHostport(Integer hostport) {
		this.hostport = hostport;
	}
	public SimpleDateFormat getSdf() {
		return sdf;
	}
	public Logger getLogger() {
		return logger;
	}


	@SuppressWarnings("unused")
	protected Header doLogin() throws ClientProtocolException, IOException, URISyntaxException {
		this.logger.log(Level.DEBUG, "Logging In:" + this.userId);
		String pageContent = "Failed!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost(this.hostname);
		builder.setPort(this.hostport);
		builder.setPath("/ansi_web/login");
		HttpPost httpPost = new HttpPost(builder.build());
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserid(this.userId);
		loginRequest.setPassword(this.password);
		String loginString = AppUtils.object2json(loginRequest);
		StringEntity params = new StringEntity(loginString);
		httpPost.addHeader("content-type","application/json");
		httpPost.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		Header sessionCookie = response.getHeaders("Set-Cookie")[0];
		try {
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return sessionCookie;
	}
	
	
	
	protected void doLogoff(Header sessionCookie) throws ClientProtocolException, URISyntaxException, IOException  {
		String url = "/ansi_web/logoff.html";
		doGet(sessionCookie, url, null);
	}
	
	
	protected String doGet(Header sessionCookie, String url, HashMap<String, String> parmMap) throws URISyntaxException, ClientProtocolException, IOException {		
		this.logger.log(Level.DEBUG, "Get: " + url);
		String pageContent = "Failure!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost(this.hostname);
		builder.setPort(this.hostport);
		builder.setPath(url);
		if ( parmMap != null && ! parmMap.isEmpty() ) {
			for ( Map.Entry<String, String> entry : parmMap.entrySet() ) {
				builder.setParameter(entry.getKey(), entry.getValue());
			}
		}
		URI uri = builder.build();
		HttpGet httpGet = new HttpGet(uri);
		this.logger.log(Level.DEBUG, "Get: " + uri);
		httpGet.addHeader("Cookie", sessionCookie.getValue());
		CloseableHttpResponse response = httpClient.execute(httpGet);
		this.logger.log(Level.DEBUG, response.getStatusLine());
		try {
			HttpEntity entity = response.getEntity();
//			Long length = entity.getContentLength();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;		
	}


	protected String doPost(Header sessionCookie, String url, String parmString) throws ClientProtocolException, IOException, URISyntaxException {
		this.logger.log(Level.DEBUG, "Post: " + url);
		this.logger.log(Level.DEBUG, parmString);
		String pageContent = "Failed!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost(this.hostname);
		builder.setPort(this.hostport);
		builder.setPath(url);
		URI uri = builder.build();
		HttpPost httpPost = new HttpPost(uri);
		httpPost.addHeader("Cookie", sessionCookie.getValue());
		if ( ! StringUtils.isBlank(parmString)) {
			StringEntity params = new StringEntity(parmString);
			httpPost.setEntity(params);
		}
		httpPost.addHeader("content-type","application/json");
		CloseableHttpResponse response = httpClient.execute(httpPost);
		this.logger.log(Level.DEBUG, response.getStatusLine());
		try {
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
	
	
	
	
	protected String doDelete(Header sessionCookie, String url, HashMap<String, String> parmMap) throws URISyntaxException, ClientProtocolException, IOException {		
		this.logger.log(Level.DEBUG, "Delete: " + url);
		String pageContent = "Failure!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost(this.hostname);
		builder.setPort(this.hostport);
		builder.setPath(url);
		if ( parmMap != null && ! parmMap.isEmpty() ) {
			for ( Map.Entry<String, String> entry : parmMap.entrySet() ) {
				builder.setParameter(entry.getKey(), entry.getValue());
			}
		}
		URI uri = builder.build();
		HttpDelete httpDelete = new HttpDelete(uri);
		httpDelete.addHeader("Cookie", sessionCookie.getValue());
		CloseableHttpResponse response = httpClient.execute(httpDelete);
		this.logger.log(Level.DEBUG, response.getStatusLine());
		try {
			HttpEntity entity = response.getEntity();
//			Long length = entity.getContentLength();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		return pageContent;			
	}
	
	
	/**
	 * Turns a HTTP Get query string into a hashmap 
	 * @param parms
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected HashMap<String, String> makeParmMap(String parms) throws UnsupportedEncodingException {
		HashMap<String, String> parmMap = new HashMap<String, String>();
		String[] parmList = parms.split("&");
		for ( String parm : parmList ) {
			String[] kv = parm.split("=");
			String key = URLDecoder.decode(kv[0], "UTF-8");
			String value = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : null;
			parmMap.put(key, value);
		}
		return parmMap;
	}

	
	/**
	 * Turn hashmap into url parameters for an HTTP Get query string
	 * @param parmMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	protected String makeParmString(HashMap<String, String> parmMap) throws UnsupportedEncodingException {
		List<String> parmString = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : parmMap.entrySet()) {
			String value = URLEncoder.encode(entry.getValue(), "UTF-8");
			parmString.add(entry.getKey() + "=" + value);
		}
		
		return StringUtils.join(parmString, "&");
	}
	
	
	/**
	 * Turn hashmap into json, suitable for HTTP POST
	 * @param parmMap
	 * @return
	 * @throws JsonException 
	 * @throws JsonProcessingException 
	 * @throws UnsupportedEncodingException 
	 */
	protected String makeJson(HashMap<String, String> parmMap) throws JsonException, JsonProcessingException {		
		return parmMap == null ? null : AppUtils.object2json(parmMap);
	}
	
	
}
