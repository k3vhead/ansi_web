package com.ansi.scilla.web.knowledgeBase.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractParsingServlet;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.knowledgeBase.common.KnowledgeBaseTagName;


public class KnowledgeBaseServlet extends AbstractParsingServlet {
	private static final long serialVersionUID = 1L;

	public static final String REALM = "knowledgeBase";
	
	
	

	private final Logger logger = LogManager.getLogger(KnowledgeBaseServlet.class);

	private static final HashMap<String, Class<? extends AbstractServlet>> postMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> getMap = new HashMap<String, Class<? extends AbstractServlet>>();
	private static final HashMap<String, Class<? extends AbstractServlet>> deleteMap = new HashMap<String, Class<? extends AbstractServlet>>();

	static {
//		postMap.put(KnowledgeBaseDetailServlet.REALM, KnowledgeBaseDetailServlet.class);
		
		getMap.put(KnowledgeBaseLookupServlet.REALM, KnowledgeBaseLookupServlet.class);
		getMap.put(LanguageAutocompleteServlet.REALM, LanguageAutocompleteServlet.class);
		getMap.put(KnowledgeBaseViewServlet.REALM, KnowledgeBaseViewServlet.class);
		
//		deleteMap.put(ALIAS, LocaleAliasServlet.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "KB doGet");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "KB URI: " + uri);
		int index = uri.indexOf(REALM) + REALM.length();
		String[] path = StringUtils.split(uri.substring(index+1),"/");
		// if the first thing in the path is a knowledge base tag name, go to the detail servlet
		// else, go to the servlet as defined in the routing hashmap
		try {
			KnowledgeBaseTagName.valueOf(path[0]);
			new KnowledgeBaseDetailServlet().doGet(request, response);
		} catch ( IllegalArgumentException e) {
			super.processGet(request, response, REALM, uri, KnowledgeBaseDetailServlet.class, getMap);
		}
		
	}
	
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "KB doPost");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "KB URI: " + uri);
		super.processPost(request, response, REALM, uri, KnowledgeBaseDetailServlet.class, postMap);
	}

	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "KB doDelete");
		String uri = request.getRequestURI();
		logger.log(Level.DEBUG, "KB URI: " + uri);
		int index = uri.indexOf(REALM) + REALM.length();
		String[] path = StringUtils.split(uri.substring(index+1),"/");
		// if the first thing in the path is a knowledge base tag name, go to the detail servlet
		// else, go to the servlet as defined in the routing hashmap
		try {
			KnowledgeBaseTagName.valueOf(path[0]);
			new KnowledgeBaseDetailServlet().doDelete(request, response);
		} catch ( IllegalArgumentException e) {
			super.processDelete(request, response, REALM, uri, KnowledgeBaseDetailServlet.class, deleteMap);
		}
		
	}
	
	
	
}
