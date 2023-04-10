package com.ansi.scilla.web.job.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractParsingServlet;
import com.ansi.scilla.web.common.servlet.AbstractServlet;


public class JobsServlet extends AbstractParsingServlet {
	private static final long serialVersionUID = 1L;

	public static final String REALM = "jobInfo";
	
	private final Logger logger = LogManager.getLogger(JobsServlet.class);

	

	private static final HashMap<String, Class<? extends AbstractServlet>> postMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> getMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> deleteMap = new HashMap<String, Class<? extends AbstractServlet>>();

	static {
//		postMap.put(ALIAS, LocaleAliasServlet.class);
		
		getMap.put(JobNoteLookupServlet.REALM, JobNoteLookupServlet.class);
		
//		deleteMap.put(ALIAS, LocaleAliasServlet.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doGet");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processGet(request, response, REALM, uri, (Class<? extends AbstractServlet>)null, getMap);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processPost(request, response, REALM, uri, (Class<? extends AbstractServlet>)null, postMap);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doDelete");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processDelete(request, response, REALM, uri, (Class<? extends AbstractServlet>)null, deleteMap);
	}

	

	


}
