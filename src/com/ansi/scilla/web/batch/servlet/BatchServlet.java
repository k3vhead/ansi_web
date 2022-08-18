package com.ansi.scilla.web.batch.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.servlet.AbstractServlet;


public class BatchServlet extends AbstractServlet {
	public static final String REALM = "batch";

	public static final String LOG = "log";

	
	
	
	private static final long serialVersionUID = 1L;
	private final Logger logger = LogManager.getLogger(BatchServlet.class);

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
		case LOG:
			new BatchLogServlet().doGet(request, response);
			break;		
		default:
			super.sendNotFound(response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}


}
