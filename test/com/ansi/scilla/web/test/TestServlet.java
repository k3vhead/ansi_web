package com.ansi.scilla.web.test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
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

public abstract class TestServlet {

	protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
	protected final Logger logger = LogManager.getLogger(TestServlet.class);
	protected String userId=null;
	protected String password=null;
	protected String hostname = "127.0.0.1";
	protected Integer hostport = 8080;
	protected boolean LogDebugMsgs;
	
		
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

	public Boolean getLogDebugMsgs() {
		return this.LogDebugMsgs;
	}
	public void setLogDebugMsgs(Boolean LogMsgs) {
		this.LogDebugMsgs = LogMsgs;
	}

	protected Header doLogin() throws ClientProtocolException, IOException, URISyntaxException {
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "Logging In:" + this.userId);
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
	
	
	protected String doGet(Header sessionCookie, String url, HashMap<String, String> parmMap) throws URISyntaxException, ClientProtocolException, IOException {		
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, url);
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
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "uri = " + uri);
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "Creating HttpGet Object");
		HttpGet httpGet = new HttpGet(uri);
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "Addiing sessionCookie to HttpGet Object");
		httpGet.addHeader("Cookie", sessionCookie.getValue());
		if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "httpClient.execute");
		CloseableHttpResponse response = httpClient.execute(httpGet);
		try {
			if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "in the try");
			HttpEntity entity = response.getEntity();
			if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, response.getStatusLine());
//			Long length = entity.getContentLength();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
			if(this.LogDebugMsgs) this.logger.log(Level.DEBUG, "Consumed.." + pageContent);
		} finally {
			response.close();
		}
		return pageContent;		
	}

//	protected String doPost(Header sessionCookie, String url, String parmString) throws ClientProtocolException, IOException, URISyntaxException {
	protected String doPost(Header sessionCookie, String url, HashMap<String, String> params) throws ClientProtocolException, IOException, URISyntaxException {

		if(this.LogDebugMsgs) 
			this.logger.log(Level.DEBUG, "Post: " + url);
		
		String pageContent = "Failed!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http");
		builder.setHost(this.hostname);
		builder.setPort(this.hostport);
		builder.setPath(url);
		
		for(Entry<String, String> entry : params.entrySet())
		{
			builder.addParameter(entry.getKey(), entry.getValue());
		}
		
		URI uri = builder.build();

		System.out.println("uri used is : " + uri);
		
		HttpPost httpPost = new HttpPost(uri);

		//StringEntity params = new StringEntity(parmString);		
		//System.out.println("parmString used is : " + parmString);		
		//httpPost.addHeader("content-type","application/json");
		//httpPost.setEntity(params);
		
		System.out.println("httpPost used is : " + httpPost.getURI());

		CloseableHttpResponse response = httpClient.execute(httpPost);
		try {
			System.out.println("inside the try..");
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			writer.close();
			EntityUtils.consume(entity);
			System.out.println("after consumption..");
		} finally {
			response.close();
		}
		return pageContent;
	}

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

}
