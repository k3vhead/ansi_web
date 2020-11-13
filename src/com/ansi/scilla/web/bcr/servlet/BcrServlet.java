package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;


public class BcrServlet extends AbstractServlet {
	private final String REALM = "bcr";
	private final String TITLE = "title";
	private final String DIRECT_LABOR = "directLabor";
	private final String TOTALS = "totals";
	private final String EMPLOYEES = "employees";
	private final String TICKETLIST = "ticketList";
	private final String INIT = "init";
	
	
	private static final long serialVersionUID = 1L;
	private final Logger logger = LogManager.getLogger(BcrServlet.class);

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		
		switch (destination) {
		case INIT:
			new BcrInitServlet().doGet(request, response);
			break;
		case TITLE:
			new BcrTitleServlet().doGet(request, response);
			break;
		case TICKETLIST:
			new BcrTicketLookupServlet().doGet(request, response);
			break;
		default:
			super.sendNotFound(response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String trigger = REALM + "/";
		String destination = uri.substring(uri.indexOf(trigger)+trigger.length());
		
		switch (destination) {
		case INIT:
			new BcrInitServlet().doPost(request, response);
			break;
		case TITLE:
			new BcrTitleServlet().doPost(request, response);
			break;
		case TICKETLIST:
			super.sendNotFound(response);
			break;
		default:
			super.sendNotFound(response);
		}
	}


}
