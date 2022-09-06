package com.ansi.scilla.web.organization.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;


public class OrganizationServlet extends AbstractServlet {
	public static final String REALM = "organization";

	private static final long serialVersionUID = 1L;
	private final Logger logger = LogManager.getLogger(OrganizationServlet.class);

	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, "Organization doGet");
		try {
			AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			
			String uri = request.getRequestURI();
			String trigger = REALM + "/";
			String uriDetail = uri.substring(uri.indexOf(trigger)+trigger.length());
			// destination[0] should be one of the OrganizationType values (except for division)
			String[] destination = uriDetail.split("/");
	
			OrganizationType type = OrganizationType.valueOf(destination[0].toUpperCase());
			if ( destination.length == 1 ) {
				new OrganizationLookupServlet(type).doGet(request, response);
			} else {
				new OrganizationDetailServlet(type).doGet(request, response);
			}			
		} catch (IllegalArgumentException e) {
			// if the destination is not a valid organization type
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);	
		}
		
	}



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			AppUtils.validateSession(request, Permission.SYSADMIN_READ);
			
			String uri = request.getRequestURI();
			String trigger = REALM + "/";
			String uriDetail = uri.substring(uri.indexOf(trigger)+trigger.length());
			logger.log(Level.DEBUG, uriDetail);
			// destination[0] should be one of the OrganizationType values (except for division)
			String[] destination = uriDetail.split("/");
	
			OrganizationType type = OrganizationType.valueOf(destination[0].toUpperCase());
//			if ( destination.length == 1 ) {
//				new OrganizationLookupServlet(type).doPost(request, response);
//			} else {
				new OrganizationDetailServlet(type).doPost(request, response);
//			}			
		} catch (IllegalArgumentException e) {
			// if the destination is not a valid organization type
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);	
		}
	}

	


}
