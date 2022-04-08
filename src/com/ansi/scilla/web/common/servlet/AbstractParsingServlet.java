package com.ansi.scilla.web.common.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * This is a servlet designed to help with the parsing of the first part of a URL in order to get
 * to another servlet that will do the actual work.
 * 
 * For instance, if we want to handle URL's of the form:
 * 		"ansi_web/locale",
		"ansi_web/locale/",
		"ansi_web/locale/123",
		"ansi_web/locale/xxx",
		"ansi_web/locale/lookup",
		"ansi_web/locale/lookup/",
		"ansi_web/locale/lookup/123",
		"ansi_web/locale/lookup/xxx",
		"ansi_web/locale/alias",
		"ansi_web/locale/alias/",
		"ansi_web/locale/alias/123",
		"ansi_web/locale/alias/xxx",
		"ansi_web/locale/aliasLookup",
		"ansi_web/locale/aliasLookup/",
		"ansi_web/locale/aliasLookup/123",
		"ansi_web/locale/aliasLookup/xxx",
		"ansi_web/locale/xxx",
		"ansi_web/locale/xxx/123",
		"ansi_web/locale/xxx/xxx",
		
 *  This servlet will get WEB-INF/web.xml triggered by "locale" and "locale/*"
 *  In turn, it will figure out that "locale" "locale/" and "locale/123" go to the LocaleDetailServlet
 *  and "locale/lookup", "locale/lookup/123" go to LocaleLookupServlet, etc. etc.
 *  
 *  The key is to have a map from path to the servlet. The servlet will then be instantiated and the correct "doXXX" 
 *  method called. If no servlet is defined, then a 404 is returned.
 * @author dclewis
 *
 */
public abstract class AbstractParsingServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	protected void processGet(HttpServletRequest request, HttpServletResponse response, String realm, String myUri, Class<? extends AbstractServlet> defaultServlet, HashMap<String, Class<? extends AbstractServlet>> servletMap) throws ServletException, IOException {
		Class<? extends AbstractServlet> servlet = parseMyUri(realm, myUri, defaultServlet, servletMap);
		if ( servlet == null ) {
			super.sendNotFound(response);
		} else {
			try {
				Constructor<? extends AbstractServlet> constructor = servlet.getConstructor( (Class<?>[])null);
				AbstractServlet abstractServlet = constructor.newInstance( (Object[])null);
				abstractServlet.doGet(request, response);
			} catch (IOException e) {
				throw e;
			} catch ( Exception e ) {
				throw new ServletException(e);
			}
		}
	}

	protected void processPost(HttpServletRequest request, HttpServletResponse response, String realm, String myUri, Class<? extends AbstractServlet> defaultServlet, HashMap<String, Class<? extends AbstractServlet>> servletMap) throws ServletException, IOException {
		Class<? extends AbstractServlet> servlet = parseMyUri(realm, myUri, defaultServlet, servletMap);
		if ( servlet == null ) {
			super.sendNotFound(response);
		} else {
			try {
				Constructor<? extends AbstractServlet> constructor = servlet.getConstructor( (Class<?>[])null);
				AbstractServlet abstractServlet = constructor.newInstance( (Object[])null);
				abstractServlet.doPost(request, response);
			} catch (IOException e) {
				throw e;
			} catch ( Exception e ) {
				throw new ServletException(e);
			}	
		}
	}
	
	protected void processDelete(HttpServletRequest request, HttpServletResponse response, String realm, String myUri, Class<? extends AbstractServlet> defaultServlet, HashMap<String, Class<? extends AbstractServlet>> servletMap) throws ServletException, IOException {
		Class<? extends AbstractServlet> servlet = parseMyUri(realm, myUri, defaultServlet, servletMap);
		if ( servlet == null ) {
			super.sendNotFound(response);
		} else {
			try {
				Constructor<? extends AbstractServlet> constructor = servlet.getConstructor( (Class<?>[])null);
				AbstractServlet abstractServlet = constructor.newInstance( (Object[])null);
				abstractServlet.doDelete(request, response);
			} catch (IOException e) {
				throw e;
			} catch ( Exception e ) {
				throw new ServletException(e);
			}			
		}
	}
	
	
	private Class<? extends AbstractServlet> parseMyUri(String realm, String myUri, Class<? extends AbstractServlet> defaultServlet, HashMap<String, Class<? extends AbstractServlet>> servletMap) {
		Class<? extends AbstractServlet> servlet = null;
		
		int index = myUri.indexOf(realm);
		String x = myUri.substring(index+realm.length());
		if ( StringUtils.isBlank(x) || x.equals("/") ) {
			servlet = defaultServlet;
		} else {
			x = x.substring(1); // because everything starts with a slash
			String[] paths = x.split("/");
			if ( StringUtils.isNumeric(paths[0])) {
				servlet = defaultServlet;
			} else {
				if ( servletMap.containsKey(paths[0])) {
					servlet = servletMap.get(paths[0]);
				}
			}
		}
		
		return servlet;
		
	}

}
