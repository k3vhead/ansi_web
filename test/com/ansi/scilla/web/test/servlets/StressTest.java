package com.ansi.scilla.web.test.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


public class StressTest {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");

	private final String userId="geo@whitehouse.gov";
	private final String password="password1";
	private final Integer threadCount = 100;
	private final String hostname = "127.0.0.1";
	private final Integer hostport = 8080;

	private Logger logger;

	public static void main(String[] args) throws Exception {
		new StressTest().threadTest();
	}

	public StressTest() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}

	public void threadTest() throws ClientProtocolException, IOException, URISyntaxException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");

		Calendar start = Calendar.getInstance();
		System.out.println("main: " + sdf.format(start.getTimeInMillis()));
		Header sessionCookie = doLogin();
		
		List<Thread> threadList = new ArrayList<Thread>();
		for ( int i = 0; i < threadCount; i++ ) {
			StressRunnable h = new StressRunnable(i, this.logger, sessionCookie, this.hostname, this.hostport);
			Thread t = new Thread(h);
			threadList.add(t);
		}

		for ( Thread t : threadList ) {
			t.start();
		}
		while ( true ) {
			try {
				for ( Thread t : threadList ) {
					t.join();
				}
				break;
			} catch ( InterruptedException e) {
				System.err.println("Interrupted");
			}
		}
		Calendar end = Calendar.getInstance();
		System.out.println("main: " + sdf.format(end.getTimeInMillis()));
	}
	
	protected Header doLogin() throws ClientProtocolException, IOException, URISyntaxException {
		this.logger.log(Level.DEBUG, "Loggin In");
		Calendar start = Calendar.getInstance();
		String pageContent = "Failed!";
		CloseableHttpClient httpClient = HttpClients.createDefault();
//		String url = "http://127.0.0.1:8080/ansi_web/login";
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
		//		String loginString = "{\"userid\":\"dclewis@thewebthing.com\",\"password\":\"password1\"}";
		StringEntity params = new StringEntity(loginString);
		httpPost.addHeader("content-type","application/json");
		httpPost.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		Header cookie = response.getHeaders("Set-Cookie")[0];
		try {
			HttpEntity entity = response.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer, "UTF-8");
			pageContent = writer.toString();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
		Calendar end = Calendar.getInstance();

		logger.log(Level.INFO, "Login: " + sdf.format(start.getTimeInMillis()) + "\t" + sdf.format(end.getTimeInMillis()));
		return cookie;
	}

	public class StressRunnable implements Runnable {
		private final String invoiceLookupUrl = "/ansi_web/invoiceLookup";
		private final String invoiceLookupParm = "draw=1&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=true&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B11%5D%5Bdata%5D=function&columns%5B11%5D%5Bname%5D=&columns%5B11%5D%5Bsearchable%5D=true&columns%5B11%5D%5Borderable%5D=true&columns%5B11%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B11%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=&search%5Bregex%5D=false&divisionId=&ppcFilter=&_=1522235707209";
		private final String invoiceFilterUrl_u = "/ansi_web/invoiceLookup";
		private final String invoiceFilterParm_u = "draw=2&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=true&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B11%5D%5Bdata%5D=function&columns%5B11%5D%5Bname%5D=&columns%5B11%5D%5Bsearchable%5D=true&columns%5B11%5D%5Borderable%5D=true&columns%5B11%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B11%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=u&search%5Bregex%5D=false&divisionId=&ppcFilter=&_=1522235570829";
		private final String invoiceFilterUrl_union = "/ansi_web/invoiceLookup";
		private final String invoiceFilterParm_union = "draw=2&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=true&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B11%5D%5Bdata%5D=function&columns%5B11%5D%5Bname%5D=&columns%5B11%5D%5Bsearchable%5D=true&columns%5B11%5D%5Borderable%5D=true&columns%5B11%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B11%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc&start=0&length=10&search%5Bvalue%5D=u&search%5Bregex%5D=false&divisionId=&ppcFilter=&_=1522235570829";
		private final String crrUrl = "/ansi_web/report/CASH_RECEIPTS_REGISTER";
		private final String crrJson = "{\"startDate\":\"01/01/2018\",\"endDate\":\"01/31/2018\",\"reportDisplay\":{\"ul\":\"accordionList\",\"li\":\"accordionItem\",\"titleTag\":\"h4\",\"titleClass\":\"accHdr\"}}";
		private final String doUrl = "/ansi_web/report/DISPATCHED_OUTSTANDING_TICKET_REPORT";
		private final String doJson = "{\"divisionId\":\"102\",\"endDate\":\"01/31/2018\",\"reportDisplay\":{\"ul\":\"accordionList\",\"li\":\"accordionItem\",\"titleTag\":\"h4\",\"titleClass\":\"accHdr\"}}";



		private Integer threadId;
		private Header sessionCookie;
		private String hostname;
		private Integer hostport;
		private Logger logger;

		public StressRunnable(Integer threadId, Logger logger, Header sessionCookie, String hostname, Integer hostport) {
			super();
			this.threadId = threadId;
			this.logger = logger;
			this.sessionCookie = sessionCookie;
			this.hostname = hostname;
			this.hostport = hostport;
		}

		@Override
		public void run() {
			try {
				this.logger.log(Level.INFO, "Starting thread " + this.threadId);
				Calendar start = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");
				doInvoiceLookup();
				doInvoiceLookupU();
				doInvoiceLookupUnion();
				doCashReceipts();
				doDispatchedOutstanding();
				Calendar end = Calendar.getInstance();
				this.logger.log(Level.INFO, "Ending thread " + threadId + "\t" + sdf.format(start.getTimeInMillis()) + "\t" + sdf.format(end.getTimeInMillis()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		

		private HashMap<String, String> makeParmMap(String parms) throws UnsupportedEncodingException {
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


		


		private void doGet(String key, String url, String parmString) throws URISyntaxException, ClientProtocolException, IOException {
			Calendar start = Calendar.getInstance();
			HashMap<String, String> parmMap = makeParmMap(parmString);

			String pageContent = "Failure!";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http");
			builder.setHost(this.hostname);
			builder.setPort(this.hostport);
			builder.setPath(url);
			for ( Map.Entry<String, String> entry : parmMap.entrySet() ) {
				builder.setParameter(entry.getKey(), entry.getValue());
			}
			URI uri = builder.build();
			HttpGet httpGet = new HttpGet(uri);
			httpGet.addHeader("Cookie", sessionCookie.getValue());
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				Long length = entity.getContentLength();
				StringWriter writer = new StringWriter();
				IOUtils.copy(entity.getContent(), writer, "UTF-8");
				pageContent = writer.toString();
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
			Calendar end = Calendar.getInstance();
			long elapsed = end.getTimeInMillis() - start.getTimeInMillis();
			logger.log(Level.INFO, "Thread " + this.threadId + "\t" + key + "\t" + elapsed + "\t" + sdf.format(start.getTimeInMillis()) + "\t" + sdf.format(end.getTimeInMillis()));
		}


		protected void doPost(String key, String url, String parmString) throws ClientProtocolException, IOException, URISyntaxException {
			Calendar start = Calendar.getInstance();
			String pageContent = "Failed!";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http");
			builder.setHost(this.hostname);
			builder.setPort(this.hostport);
			builder.setPath(url);
			URI uri = builder.build();
			HttpPost httpPost = new HttpPost(uri);
			StringEntity params = new StringEntity(parmString);
			httpPost.addHeader("content-type","application/json");
			httpPost.setEntity(params);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				StringWriter writer = new StringWriter();
				IOUtils.copy(entity.getContent(), writer, "UTF-8");
				pageContent = writer.toString();
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
			Calendar end = Calendar.getInstance();
			long elapsed = end.getTimeInMillis() - start.getTimeInMillis();
			logger.log(Level.INFO, "Thread " + this.threadId + "\t" + key + "\t" + elapsed + "\t" + sdf.format(start.getTimeInMillis()) + "\t" + sdf.format(end.getTimeInMillis()));

		}


		protected void doInvoiceLookup() throws ClientProtocolException, IOException, URISyntaxException {
			doGet("Invoice Lookup", this.invoiceLookupUrl, this.invoiceLookupParm);
		}

		protected void doInvoiceLookupU() throws ClientProtocolException, IOException, URISyntaxException {
			doGet("Invoice Lookup (u)", this.invoiceFilterUrl_u, this.invoiceFilterParm_u);
		}

		protected void doInvoiceLookupUnion() throws ClientProtocolException, IOException, URISyntaxException {
			doGet("Invoice Lookup (union)", this.invoiceFilterUrl_union, this.invoiceFilterParm_union);
		}

		protected void doCashReceipts() throws ClientProtocolException, IOException, URISyntaxException {
			doPost("Cash Receipt", this.crrUrl, this.crrJson);
		}

		protected void doDispatchedOutstanding() throws ClientProtocolException, IOException, URISyntaxException {
			doPost("DO Rpt", this.doUrl, this.doJson);
		}






	}
}
