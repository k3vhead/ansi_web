package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.ansi.scilla.web.common.AppUtils;
import com.thewebthing.commons.lang.StringUtils;

public class Heartbeat extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_MATCH_PHRASE = "We are up";
	
	private final String html = "~UPDOWNMESSAGE~\n"
			+ "\n"
			+ "Connection:\n"
			+ "URL:~URL~\n"
			+ "Initial Size:~INITIAL~\n"
			+ "Max Life:~LIFE~\n"
			+ "Min Idle:~MINIDLE~\n"
			+ "Max Idle:~MAXIDLE~\n"
			+ "Num Idle:~NUMIDLE~\n"
			+ "Max Total:~MAXTOTAL~\n"
			+ "Num Active:~ACTIVE~\n"
			+ "Test on Borrow:~TEST~\n";



	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doWork(request, response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doWork(request, response);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}
	}

	private void doWork(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		
		String matchPhrase = request.getQueryString();
		if ( StringUtils.isBlank(matchPhrase)) {
			matchPhrase = DEFAULT_MATCH_PHRASE;
		} else {
			matchPhrase = URLDecoder.decode(matchPhrase, "UTF-8");
		}
		
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			BasicDataSource ds = (BasicDataSource)ctx.lookup("java:comp/env/jdbc/ansi");

			Boolean firstConnIsGood = true;
			Boolean secondConnIsGood = null;

			ds.setLogAbandoned(true);

			// Get Connection and Statement
			conn =  ds.getConnection();
			
			if ( conn == null ) {
				firstConnIsGood = false;
				conn = ds.getConnection();
				secondConnIsGood = conn != null;
			}

			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select SYSDATETIME ()");
			boolean dbSelectIsGood = rs.next();
			
			DecimalFormat formatGiggage = new DecimalFormat( "###,###,###,##0");
			DecimalFormat formatPct = new DecimalFormat("##0.0000");
			
			Runtime runtime = Runtime.getRuntime();
			long percentFree = runtime.freeMemory() / runtime.maxMemory();
			
			String output = "";
			
			if ( (firstConnIsGood || secondConnIsGood) && dbSelectIsGood ) {
				output = html.replace("~UPDOWNMESSAGE~", matchPhrase);
			} else {
				output = html.replace("~UPDOWNMESSAGE~","Something failed");
			}
			output = output.replace("~URL~", ds.getUrl());
			output = output.replace("~INITIAL~", String.valueOf(ds.getInitialSize()));
			output = output.replace("~LIFE~", String.valueOf(ds.getMaxConnLifetimeMillis()));
			output = output.replace("~MINIDLE~", String.valueOf(ds.getMinIdle()));
			output = output.replace("~MAXIDLE~", String.valueOf(ds.getMaxIdle()));
			output = output.replace("~NUMIDLE~", String.valueOf(ds.getNumIdle()));
			output = output.replace("~MAXTOTAL~", String.valueOf(ds.getMaxTotal()));
			output = output.replace("~ACTIVE~", String.valueOf(ds.getNumActive()));
			output = output.replace("~TEST~", String.valueOf(ds.getTestOnBorrow()));
			output = output + "\nFirst Connection: " + firstConnIsGood;
			if ( secondConnIsGood == null ) {
				output = output + "\nSecond Connection: Not tested";
			} else {
				output = output + "\nSecond Connection: " + secondConnIsGood;
			}				
			output = output + "\n\n";
			output = output + "Max Memory: " + formatGiggage.format(runtime.maxMemory()) + "\n";
			output = output + "Total Memory: " + formatGiggage.format(runtime.totalMemory()) + "\n";
			output = output + "Free Memory: " + formatGiggage.format(runtime.freeMemory()) + " (" + formatPct.format(percentFree) + "%)\n";
			output = output + "\n\n";
			output = output + String.valueOf(new Date());
			
			response.setStatus(HttpServletResponse.SC_OK);
				

			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(output);
			writer.flush();
			writer.close();


		} finally {
			conn.close();
		}
	}


}
