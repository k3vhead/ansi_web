package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;


public class LocaleServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	public static final String REALM = "locale";
	
	public static final String ALIAS = "alias";
	public static final String ALIAS_LOOKUP = "aliasLookup";
	public static final String LOOKUP = "lookup";
	

	private final Logger logger = LogManager.getLogger(LocaleServlet.class);

	

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



	

	


}
