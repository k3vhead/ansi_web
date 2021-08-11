package com.ansi.scilla.web.organization.action;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;


/**
 * Figures out what type of organization we're working with and passes that value along to the JSP
 * 
 * @author dclewis
 *
 */
public class OrganizationAction extends SessionPageDisplayAction {
	
	public static final String ORGANIZATION_KEY = "ANSI_ORGANIZATION_TYPE";
	public static final String ORGANIZATION_DISPLAY_KEY = "ANSI_ORGANIZATION_TYPE_DISPLAY";
	
	private final static HashMap<String, OrganizationType> titleMap;
	
	static {
		titleMap = new HashMap<String, OrganizationType>();
		titleMap.put("companies.html",OrganizationType.COMPANY);
		titleMap.put("regions.html", OrganizationType.REGION);
		titleMap.put("groups.html", OrganizationType.GROUP);
	}
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, "contextPath: " + request.getContextPath());
		logger.log(Level.DEBUG, "getPathInfo: " + request.getPathInfo());
		logger.log(Level.DEBUG, "getPathTranslated: " + request.getPathTranslated());
		logger.log(Level.DEBUG, "getRequestURI: " + request.getRequestURI());
		
		String[] uriPieces = request.getRequestURI().split("/");
		String key = uriPieces[uriPieces.length-1];
		logger.log(Level.DEBUG, "key: " + key);
		if ( titleMap.containsKey(key) ) {
			request.setAttribute(ORGANIZATION_KEY, titleMap.get(key).name());
			request.setAttribute(ORGANIZATION_DISPLAY_KEY, titleMap.get(key).display());
		} else {
			throw new Exception("Invalid organization type");
		}
		
		
		return super.execute(mapping, actionForm, request, response);
	}
}
