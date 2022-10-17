package com.ansi.scilla.web.qotd.servlet;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.qotd.request.MotdRequest;
import com.ansi.scilla.web.qotd.response.MotdResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class MotdServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String KEY_QOTD = "com.ansi.scilla.web.servlets.QotdServlet.qotd";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();				
			processGet(conn, response);			
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			AppUtils.validateSession(request, Permission.SYSADMIN_WRITE);
			ApplicationProperties appProperties = new ApplicationProperties();
			appProperties.setPropertyId(KEY_QOTD);
			try {
				appProperties.delete(conn);
				conn.commit();
			} catch (RecordNotFoundException e) {
				// successfully did nothing
			}
			MotdResponse data = new MotdResponse();
			WebMessages messages = new WebMessages();
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
			data.setWebMessages(messages);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.SYSADMIN_WRITE);
			try{
				MotdRequest motdRequest = new MotdRequest();
				AppUtils.json2object(jsonString, motdRequest);
				SessionUser sessionUser = sessionData.getUser(); 
								
				WebMessages messages = motdRequest.validate();
				MotdResponse data = new MotdResponse();
				if ( messages.isEmpty()) {
					messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
					processUpsert(conn, motdRequest, sessionUser);
					data = new MotdResponse(motdRequest.getMessage(), "ANSI");
					data.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				MotdResponse data = new MotdResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				//send a Bad Ticket message back
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private void processGet(Connection conn, HttpServletResponse response) throws Exception {
		MotdResponse motdResponse = new MotdResponse();
		
		ApplicationProperties appProperties = new ApplicationProperties();
		appProperties.setPropertyId(KEY_QOTD);
		try {
			appProperties.selectOne(conn);
			String message = appProperties.getValueString();
			motdResponse = new MotdResponse(message, "ANSI");
		} catch ( RecordNotFoundException e) {
			String message = getQotd();
			motdResponse = new MotdResponse(message, "feedburner");
		}
		super.sendResponse(conn, response, ResponseCode.SUCCESS, motdResponse);
	}

	
	private void processUpsert(Connection conn, MotdRequest motdRequest, SessionUser sessionUser) throws Exception {
		ApplicationProperties appProperties = new ApplicationProperties();
		Date today = new Date();
		appProperties.setPropertyId(KEY_QOTD);
		try {
			appProperties.selectOne(conn);
			ApplicationProperties key = new ApplicationProperties();
			appProperties.setValueString(motdRequest.getMessage());
			appProperties.setUpdatedBy(sessionUser.getUserId());
			appProperties.setUpdatedDate(today);
			key.setPropertyId(KEY_QOTD);
			appProperties.update(conn, key);
		} catch ( RecordNotFoundException e) {
			appProperties.setPropertyId(KEY_QOTD);
			appProperties.setValueString(motdRequest.getMessage());
			appProperties.setUpdatedBy(sessionUser.getUserId());
			appProperties.setUpdatedDate(today);
			appProperties.setAddedBy(sessionUser.getUserId());
			appProperties.setAddedDate(today);
			appProperties.insertWithNoKey(conn);
		} catch ( Exception e ) {
			conn.rollback();
			throw e;
		}
		conn.commit();
	}

	
	@SuppressWarnings("unchecked")
	private String getQotd() throws IllegalArgumentException, FeedException, IOException {
		String qotd = "Have a Great Day!";
		final URL feedUrl= new URL("http://feeds.feedburner.com/QuotesDay?format=xml");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedUrl));
		List<SyndEntry> feedEntryList = feed.getEntries();
		SyndEntry feedEntry = feedEntryList.get(0);
		List<SyndContent> feedEntryContents = feedEntry.getContents();
		SyndContent content = feedEntryContents.get(0);
		Pattern funnyQuotePattern = Pattern.compile("(^.*<h2>Funny Quote of the Day</h2><p><em>)(.*)(<br />- <a href=http://www.quotes-day.com/by/love.*$)", Pattern.CASE_INSENSITIVE);
		Matcher funnyQuoteMatcher = funnyQuotePattern.matcher(content.getValue());
		if ( funnyQuoteMatcher.matches() ) {
			qotd = funnyQuoteMatcher.group(2);
		}

		return qotd;
	}

	
	
	
	

	
}
