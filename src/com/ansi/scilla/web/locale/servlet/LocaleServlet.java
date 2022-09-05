package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.servlet.AbstractParsingServlet;


public class LocaleServlet extends AbstractParsingServlet {
	private static final long serialVersionUID = 1L;

	public static final String REALM = "locale";
	
	public static final String ALIAS = "alias";
	public static final String ALIAS_LOOKUP = "aliasLookup";
	public static final String LOOKUP = "lookup";
	

	private final Logger logger = LogManager.getLogger(LocaleServlet.class);

	private static final HashMap<String, Class<? extends AbstractServlet>> postMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> getMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> deleteMap = new HashMap<String, Class<? extends AbstractServlet>>();

	static {
		postMap.put(ALIAS, LocaleAliasServlet.class);
		
		getMap.put(ALIAS, LocaleAliasServlet.class);
		getMap.put(ALIAS_LOOKUP, LocaleAliasLookupServlet.class);
		getMap.put(LOOKUP, LocaleLookupServlet.class);
		
		deleteMap.put(ALIAS, LocaleAliasServlet.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doGet");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processGet(request, response, REALM, uri, LocaleDetailServlet.class, getMap);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processPost(request, response, REALM, uri, LocaleDetailServlet.class, postMap);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doDelete");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		super.processDelete(request, response, REALM, uri, LocaleDetailServlet.class, deleteMap);
	}
	
	
	/*
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doGet");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "GET: " + destination);
		
		if ( StringUtils.isNumeric(destination)) {
			new LocaleDetailServlet().doGet(request, response);
		} else {
			switch (destination) {
			case ALIAS_LOOKUP:
				new LocaleAliasLookupServlet().doGet(request, response);
				break;
			case ALIAS:
				new LocaleAliasServlet().doGet(request,response);
				break;
			case LOOKUP:
				new LocaleLookupServlet().doGet(request, response);
				break;
			default:
				super.sendNotFound(response);
			}
		}
		
		
	}



	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doDelete");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "DELETE: " + destination);
		
		if ( StringUtils.isNumeric(destination)) {
			new LocaleDetailServlet().doDelete(request, response);
		} else {
			switch (destination) {
			case ALIAS:
				new LocaleAliasServlet().doDelete(request,response);
				break;

			default:
				super.sendNotFound(response);
			};
		}
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Locale doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "LocaleURI: " + uri);
		
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "POST: " + destination);
		
		if ( StringUtils.isNumeric(destination)) {
			new LocaleDetailServlet().doPost(request, response);
		} else {
			switch (destination) {
			case ALIAS:
				new LocaleAliasServlet().doPost(request,response);
				break;
			default:
				super.sendNotFound(response);
			}
		}
	}

	*/

	

	


}
