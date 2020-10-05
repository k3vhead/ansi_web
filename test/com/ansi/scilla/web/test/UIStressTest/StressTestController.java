package com.ansi.scilla.web.test.UIStressTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.test.TestServlet;


public class StressTestController {
	
	protected final Logger logger = LogManager.getLogger("ansi_stress_log");
	protected final Logger stopLogger = LogManager.getLogger("ansi_stress_stop_log");
	
	protected final String hostname = TestServlet.PLATFORM_IS_DEV;
	
	/**
	 * Create stresslogins - these will create an httpsession (log a user in) and then call the Stressers
	 * Each Stresser will take a login session (essentially the cookie) and run multiple simultaneous requests
	 * The controller will ensure multiple simultaneous logins
	 * 
	 * @throws Exception
	 */
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Statement s = conn.createStatement();
			startStressers(s);
		} finally {
			conn.close();
		}

	}
		
	private void startStressers(Statement s) throws Exception {	
		logger.log(Level.DEBUG, "Making Data");
		stopLogger.log(Level.DEBUG, "Making Data");
		
		List<String> userIdList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "ansi_user", "email", 17), new StringTransformer()));
		List<String> addressList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "address", "address1", 10), new StringTransformer())); 
		List<String> jobIdList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "job", "job_id", 10), new StringTransformer()));
		List<String> ticketIdList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "ticket", "ticket_id", 10), new StringTransformer()));
		List<String> serviceList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "job", "service_description", 10), new StringTransformer()));
		List<String> invoiceList = IterableUtils.toList(CollectionUtils.collect(makeDataList(s, "invoice", "invoice_id", 10), new StringTransformer()));
		
		StressLogin login = new StressLogin();
		List<Header> loginList = makeLoginList(login, userIdList, this.hostname);
		
		logger.log(Level.DEBUG, "Making Sessions");
		List<Stresser> stresserList = new ArrayList<Stresser>();
		stresserList.addAll(makeQuoteTableSession(loginList, addressList));
		stresserList.addAll(makeTicketTypeAhead(loginList, ticketIdList));
		stresserList.addAll(makeJobLookup(loginList, jobIdList));
		stresserList.addAll(makeJobLookup(loginList, serviceList));
		stresserList.addAll(makeInvoiceLookup(loginList, invoiceList));
		stresserList.addAll(makeInvoiceLookup(loginList, addressList));
		
		logger.log(Level.DEBUG, "Starting Controller");
		stopLogger.log(Level.DEBUG, "Starting Controller");
		
		Calendar startTime = Calendar.getInstance();
	
		List<Thread> threadList = new ArrayList<Thread>();
		for ( Stresser stresser : stresserList ) {
			threadList.add(new Thread(stresser));
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
		
		
		for ( Header sessionCookie : loginList ) {
			login.doLogoff("?", sessionCookie);
		}

		Calendar endTime = Calendar.getInstance();
		
		logger.log(Level.DEBUG, "Ending Controller");
		stopLogger.log(Level.DEBUG, "Ending Controller");
		
		Long start = startTime.getTimeInMillis();
		Long end = endTime.getTimeInMillis();
		Long elapsed = end - start;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.S");
		logger.log(Level.DEBUG, "Elapsed: " + sdf.format(startTime.getTime()) + "-" + sdf.format(endTime.getTime()) + "\t" + elapsed);
		stopLogger.log(Level.DEBUG, "Elapsed: " + sdf.format(startTime.getTime()) + "-" + sdf.format(endTime.getTime()) + "\t" + elapsed);
	}
	
	
	
	

	private Collection<? extends Stresser> makeInvoiceLookup(List<Header> loginList, List<String> dataList) {
		List<Stresser> stresserList = new ArrayList<Stresser>();
		
		for ( Header login : loginList ) {
			List<StresserParam> paramList = new ArrayList<StresserParam>();
			paramList.add(new StresserParam("/ansi_web/invoiceLookup.html", "", false));
			paramList.add(new StresserParam("/ansi_web/invoiceLookup", "", false));
			for ( String dataString : dataList ) {
//				String dataString = String.valueOf(data);
				for ( int i = 4; i <= dataString.length() && i <=10 ; i++ ) {
//					System.out.println(dataString.substring(0, i));
					paramList.add(new StresserParam("/ansi_web/invoiceLookup", "draw=3&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=true&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B11%5D%5Bdata%5D=function&columns%5B11%5D%5Bname%5D=&columns%5B11%5D%5Bsearchable%5D=true&columns%5B11%5D%5Borderable%5D=true&columns%5B11%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B11%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=desc&start=0&length=10&search%5Bvalue%5D="+dataString.substring(0, i)+"&search%5Bregex%5D=false&divisionId=&ppcFilter=&_=1598529624537", false));
				}
			}
			stresserList.add(new Stresser("1", login, this.hostname, paramList));
		}

		return stresserList;
	}

	private Collection<? extends Stresser> makeJobLookup(List<Header> loginList, List<String> dataList) {
		List<Stresser> stresserList = new ArrayList<Stresser>();
		
		for ( Header login : loginList ) {
			List<StresserParam> paramList = new ArrayList<StresserParam>();
			paramList.add(new StresserParam("/ansi_web/jobLookup.html", "", false));
			paramList.add(new StresserParam("/ansi_web/jobTable", "", false));
			for ( String dataString : dataList ) {
//				String dataString = String.valueOf(data);
				for ( int i = 4; i <= dataString.length() && i <=10 ; i++ ) {
//					System.out.println(dataString.substring(0, i));
					paramList.add(new StresserParam("/ansi_web/jobTable", "draw=4&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=true&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B11%5D%5Bdata%5D=function&columns%5B11%5D%5Bname%5D=&columns%5B11%5D%5Bsearchable%5D=true&columns%5B11%5D%5Borderable%5D=true&columns%5B11%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B11%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B12%5D%5Bdata%5D=function&columns%5B12%5D%5Bname%5D=&columns%5B12%5D%5Bsearchable%5D=true&columns%5B12%5D%5Borderable%5D=true&columns%5B12%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B12%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B13%5D%5Bdata%5D=function&columns%5B13%5D%5Bname%5D=&columns%5B13%5D%5Bsearchable%5D=true&columns%5B13%5D%5Borderable%5D=true&columns%5B13%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B13%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B14%5D%5Bdata%5D=function&columns%5B14%5D%5Bname%5D=&columns%5B14%5D%5Bsearchable%5D=true&columns%5B14%5D%5Borderable%5D=true&columns%5B14%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B14%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B15%5D%5Bdata%5D=function&columns%5B15%5D%5Bname%5D=&columns%5B15%5D%5Bsearchable%5D=true&columns%5B15%5D%5Borderable%5D=true&columns%5B15%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B15%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B16%5D%5Bdata%5D=function&columns%5B16%5D%5Bname%5D=&columns%5B16%5D%5Bsearchable%5D=true&columns%5B16%5D%5Borderable%5D=true&columns%5B16%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B16%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B17%5D%5Bdata%5D=function&columns%5B17%5D%5Bname%5D=&columns%5B17%5D%5Bsearchable%5D=true&columns%5B17%5D%5Borderable%5D=true&columns%5B17%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B17%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B18%5D%5Bdata%5D=function&columns%5B18%5D%5Bname%5D=&columns%5B18%5D%5Bsearchable%5D=true&columns%5B18%5D%5Borderable%5D=true&columns%5B18%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B18%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B19%5D%5Bdata%5D=function&columns%5B19%5D%5Bname%5D=&columns%5B19%5D%5Bsearchable%5D=true&columns%5B19%5D%5Borderable%5D=true&columns%5B19%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B19%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B20%5D%5Bdata%5D=function&columns%5B20%5D%5Bname%5D=&columns%5B20%5D%5Bsearchable%5D=true&columns%5B20%5D%5Borderable%5D=true&columns%5B20%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B20%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B21%5D%5Bdata%5D=function&columns%5B21%5D%5Bname%5D=&columns%5B21%5D%5Bsearchable%5D=true&columns%5B21%5D%5Borderable%5D=false&columns%5B21%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B21%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=desc&start=0&length=10&search%5Bvalue%5D="+ dataString.substring(0, i) + "&search%5Bregex%5D=false&_=1598527255543", false));					
				}
			}
			stresserList.add(new Stresser("1", login, this.hostname, paramList));
		}

		return stresserList;
	}

	private List<Stresser> makeTicketTypeAhead(List<Header> loginList, List<String> dataList) {
		List<Stresser> stresserList = new ArrayList<Stresser>();
		for ( Header login : loginList ) {
			List<StresserParam> paramList = new ArrayList<StresserParam>();
			paramList.add(new StresserParam("/ansi_web/ticketReturn.html", "", false));
			for ( String dataString : dataList ) {
				for ( int i = 3; i <= dataString.length() && i <=10 ; i++ ) {
//					System.out.println(dataString.substring(0, i));
					paramList.add(new StresserParam("/ansi_web/ticketTypeAhead", "term="+dataString.substring(0, i), false));
				}
				paramList.add(new StresserParam("/ansi_web/ticket/"+dataString, "", false));
			}
			stresserList.add(new Stresser("1", login, this.hostname, paramList));
		}
		return stresserList;		
	}
	
	
	

	private List<Stresser> makeQuoteTableSession(List<Header> loginList, List<String> addressList) {
		List<Stresser> stresserList = new ArrayList<Stresser>();
		
		for ( Header login : loginList ) {
			List<StresserParam> paramList = new ArrayList<StresserParam>();
			paramList.add(new StresserParam("/ansi_web/quoteTable", "", false));
			for ( String address : addressList ) {
				for ( int i = 4; i <= address.length() && i <=10 ; i++ ) {
//					System.out.println(address.substring(0, i));
					paramList.add(new StresserParam("/ansi_web/quoteTable", "draw=2&columns%5B0%5D%5Bdata%5D=function&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=function&columns%5B1%5D%5Bname%5D=&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=function&columns%5B2%5D%5Bname%5D=&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=function&columns%5B3%5D%5Bname%5D=&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=function&columns%5B4%5D%5Bname%5D=&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=function&columns%5B5%5D%5Bname%5D=&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=function&columns%5B6%5D%5Bname%5D=&columns%5B6%5D%5Bsearchable%5D=true&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=function&columns%5B7%5D%5Bname%5D=&columns%5B7%5D%5Bsearchable%5D=true&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B8%5D%5Bdata%5D=function&columns%5B8%5D%5Bname%5D=&columns%5B8%5D%5Bsearchable%5D=true&columns%5B8%5D%5Borderable%5D=true&columns%5B8%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B8%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B9%5D%5Bdata%5D=function&columns%5B9%5D%5Bname%5D=&columns%5B9%5D%5Bsearchable%5D=true&columns%5B9%5D%5Borderable%5D=true&columns%5B9%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B9%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B10%5D%5Bdata%5D=function&columns%5B10%5D%5Bname%5D=&columns%5B10%5D%5Bsearchable%5D=true&columns%5B10%5D%5Borderable%5D=false&columns%5B10%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B10%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=desc&start=0&length=10&search%5Bvalue%5D="+address.substring(0, i)+"&search%5Bregex%5D=false&_=1598270872389", false));
				}
			}
			stresserList.add(new Stresser("1", login, this.hostname, paramList));
		}

		return stresserList;
	}

	
	
	private List<Header> makeLoginList(StressLogin login, List<String> userIdList, String hostname) throws ClientProtocolException, IOException, URISyntaxException {
		List<Header> loginList = new ArrayList<Header>();
		for ( String email : userIdList ) {
			loginList.add(login.doLogin(email,"password1", hostname));
		}
		return loginList;
	}

	private List<Object> makeDataList(Statement s, String table, String fieldName, Integer limit) throws Exception {
		List<Object> dataList = new ArrayList<Object>();
		ResultSet rs = s.executeQuery("select * from " + table + " order by " + fieldName + " offset 0 rows fetch next " + limit + " rows only");
		while ( rs.next() ) {
			dataList.add(rs.getObject(fieldName));
		}
		rs.close();
		return dataList;
	}



	public static void main(String[] args) {
		try {
			new StressTestController().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public class StressLogin extends TestServlet {
		public StressLogin() {
			super();
		}
		
		public Header doLogin(String userId, String password, String hostname) throws ClientProtocolException, IOException, URISyntaxException {
			super.setUserId(userId);
			super.setPassword(password);
			super.setHostname(hostname);
			return super.doLogin();
		}
		
		
		public void doLogoff(String loginId, Header sessionCookie) throws ClientProtocolException, URISyntaxException, IOException {
			logger.log(Level.DEBUG, "Logging off: " + loginId);
			super.doLogoff(sessionCookie);			
		}
	}
	
	
	public class StringTransformer implements Transformer<Object, String> {

		@Override
		public String transform(Object arg0) {
			return String.valueOf(arg0);
		}
		
	}
	
//	public class IntegerTransformer implements Transformer<Object, Integer> {
//
//		@Override
//		public Integer transform(Object arg0) {
//			return (Integer)arg0;
//		}
//		
//	}
	
}
