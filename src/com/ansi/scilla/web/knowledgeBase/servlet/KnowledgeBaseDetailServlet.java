package com.ansi.scilla.web.knowledgeBase.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.KnowledgeBase;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.knowledgeBase.request.KnowledgeBaseDetailRequest;
import com.ansi.scilla.web.knowledgeBase.response.KnowledgeBaseDetailResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class KnowledgeBaseDetailServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			AppUtils.validateSession(request);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String uri = request.getRequestURI();
				int index = uri.indexOf(KnowledgeBaseServlet.REALM) + KnowledgeBaseServlet.REALM.length();
				String[] path = StringUtils.split(uri.substring(index+1),"/");
				
				KnowledgeBaseDetailResponse data = new KnowledgeBaseDetailResponse(conn, path[0], path[1]);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data); 
				
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			throw new ServletException(e);
		}
		
		
		
	}

	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring1:"+jsonString);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.KNOWLEDGE_WRITE);
			KnowledgeBaseDetailResponse data = new KnowledgeBaseDetailResponse();
			WebMessages webMessages = new WebMessages();
			
			
			try{
				KnowledgeBaseDetailRequest kbRequest = new KnowledgeBaseDetailRequest();
				AppUtils.json2object(jsonString, kbRequest);
								
				logger.log(Level.DEBUG, kbRequest);
				webMessages = kbRequest.validate(conn);
				if(webMessages.isEmpty() == true) {
					doUpdate(conn, kbRequest, sessionData, response);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
						
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
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



	

	

	protected void doUpdate(Connection conn, KnowledgeBaseDetailRequest kbRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		Calendar rightNow = Calendar.getInstance(new AnsiTime());
		
		KnowledgeBase kb = new KnowledgeBase();
		kb.setKbKey(kbRequest.getKbTagName());
		kb.setKbLanguage(kbRequest.getLanguageCode());
		try {
			kb.selectOne(conn);
			kb.setKbTitle(kbRequest.getKbTitle());
			kb.setKbContent(kbRequest.getKbContent());
			kb.setUpdatedBy(sessionData.getUser().getUserId());
			kb.setUpdatedDate(rightNow.getTime());
			kb.setKbStatus(kbRequest.getKbStatus());
			KnowledgeBase key = new KnowledgeBase();
			key.setKbKey(kbRequest.getKbTagName());
			key.setKbLanguage(kbRequest.getLanguageCode());			
			kb.update(conn, key);
		} catch ( RecordNotFoundException e) {
			kb.setKbKey(kbRequest.getKbTagName());
			kb.setKbLanguage(kbRequest.getLanguageCode());
			kb.setKbTitle(kbRequest.getKbTitle());
			kb.setKbContent(kbRequest.getKbContent());
			kb.setKbStatus(kbRequest.getKbStatus());
			kb.setUpdatedBy(sessionData.getUser().getUserId());
			kb.setUpdatedDate(rightNow.getTime());
			kb.setAddedBy(sessionData.getUser().getUserId());
			kb.setAddedDate(rightNow.getTime());
			kb.insertWithNoKey(conn);
		}
		conn.commit();		
		KnowledgeBaseDetailResponse data = new KnowledgeBaseDetailResponse();
		data.setKbContent(kb.getKbContent());
		data.setKbTagName(kb.getKbKey());
		data.setKbTitle(kb.getKbTitle());
		data.setLanguageCode(kb.getKbLanguage());
		
		super.sendResponse(conn, response, ResponseCode.SUCCESS, null);
	}


	
	
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
//			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.KNOWLEDGE_WRITE);
			Connection conn = null;
			KnowledgeBaseDetailResponse kbResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				String uri = request.getRequestURI();
				int index = uri.indexOf(KnowledgeBaseServlet.REALM) + KnowledgeBaseServlet.REALM.length();
				String[] path = StringUtils.split(uri.substring(index+1),"/");
				
				try {
					KnowledgeBase kb = new KnowledgeBase();
					kb.setKbKey(path[0]);
					kb.setKbLanguage(path[1]);
					kb.delete(conn);
					conn.commit();
				} catch ( RecordNotFoundException e ) {
					// we don't care -- deleting something that isn't there
				}
				
				kbResponse = new KnowledgeBaseDetailResponse();
				super.sendResponse(conn, response, ResponseCode.SUCCESS, kbResponse); 
				
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
//		} catch (ResourceNotFoundException e1) {
//			AppUtils.logException(e1);
//			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}		
		
	}
	
}
