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

	private final String ACTUAL_DL = "actualDL";
	private final String BC_TOTALS = "bcTotals";
	private final String EMPLOYEE_AUTOCOMPLETE = "employee";
	private final String EMPLOYEES = "employees";
	private final String INIT = "init";
	private final String KEEP_ALIVE = "keepAlive";
	private final String TICKET_CLAIM = "ticketClaim";
	private final String TICKETLIST = "ticketList";
	private final String TITLE = "title";
	private final String WEEKLY_TICKETLIST = "weeklyTicketList";
	
	
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
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "GET: " + destination);
		
		switch (destination) {
		case ACTUAL_DL:
			new BcrActualDLServlet().doGet(request, response);
			break;
		case BC_TOTALS:
			new BcrTotalsServlet().doGet(request, response);
			break;
		case EMPLOYEE_AUTOCOMPLETE:
			new BcrEmployeeAutoComplete().doGet(request, response);
			break;
		case EMPLOYEES:
			new BcrEmployeesServlet().doGet(request, response);
			break;
		case INIT:
			new BcrInitServlet().doGet(request, response);
			break;
		case KEEP_ALIVE:
			new BcrKeepAliveServlet().doGet(request, response);
			break;
		case TICKET_CLAIM:
			new BcrTicketClaimServlet().doGet(request, response);
			break;
		case TICKETLIST:
			new BcrTicketLookupServlet().doGet(request, response);
			break;	
		case TITLE:
			new BcrTitleServlet().doGet(request, response);
			break;
		case WEEKLY_TICKETLIST:
			new BcrWeeklyTicketLookupServlet().doGet(request, response);
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
		Integer index = destination.indexOf("/");   // in case we have something like bcr/ticket/12345
		destination = index > 0 ? destination.substring(0, index) : destination;
		
		logger.log(Level.DEBUG, "POST: " + destination);
		
		
		
		switch (destination) {
		case ACTUAL_DL:
			new BcrActualDLServlet().doPost(request, response);
			break;
		case INIT:
			new BcrInitServlet().doPost(request, response);
			break;
		case TICKET_CLAIM:
			new BcrTicketClaimServlet().doPost(request, response);
			break;
		case TITLE:
			new BcrTitleServlet().doPost(request, response);
			break;
		default:
			super.sendNotFound(response);
		}
	}


}
