package com.ansi.scilla.web.knowledgeBase.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.KnowledgeBase;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class KnowledgeBaseViewServlet extends KnowledgeBaseDetailServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "view";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request);
			
			String kbKey = makeKey(request);		
			logger.log(Level.DEBUG, "KB Key: " + kbKey);
			
			String languageStuff = request.getHeader("Accept-Language");
			logger.log(Level.DEBUG, languageStuff);
			List<Locale.LanguageRange> languageList = LanguageRange.parse(languageStuff);
			String kbTitle = null;
			String kbContent = null;
			for ( Locale.LanguageRange range : languageList ) {
				KnowledgeBase kb = new KnowledgeBase();
				kb.setKbKey(kbKey);
				kb.setKbLanguage(range.getRange());
				try {
					logger.log(Level.DEBUG, "Trying kb: " + range.getRange());
					kb.selectOne(conn);
					kbTitle = kb.getKbTitle();
					kbContent = kb.getKbContent();
					logger.log(Level.DEBUG, "success " + range.getRange());
				} catch ( RecordNotFoundException e ) {
					logger.log(Level.DEBUG, "Failed kb: " + range.getRange());
					// we don't care -- just try the next one
				}
			}
			
			if ( StringUtils.isBlank(kbTitle) ) {
				KnowledgeBase kb = new KnowledgeBase();
				kb.setKbKey(kbKey);
				kb.setKbLanguage("en");
				try {
					logger.log(Level.DEBUG, "resorting to en");
					kb.selectOne(conn);
					kbTitle = kb.getKbTitle();
					kbContent = kb.getKbContent();
					logger.log(Level.DEBUG, "success en");
					super.sendResponse(response, ResponseCode.SUCCESS, makeHtml(kbTitle, kbContent));
				} catch ( RecordNotFoundException e ) {
					logger.log(Level.DEBUG, "failed en");
					super.sendNotFound(response);
				}
			} else {
				super.sendResponse(response, ResponseCode.SUCCESS, makeHtml(kbTitle, kbContent));
			}

		} catch ( TimeoutException e) {
			super.sendNotAllowed(response);
		} catch ( Exception e) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}

	}

	private String makeHtml(String kbTitle, String kbContent) {
		StringBuffer buffer = new StringBuffer();
	
		buffer.append("<div class=\"kbTitle\">" + kbTitle + "</div>");
		buffer.append("<div class=\"kbContent\">" + kbContent + "</div>");
		logger.log(Level.DEBUG, buffer.toString());
		return buffer.toString();
	}

	private String makeKey(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int index = uri.indexOf(REALM) + REALM.length();
		String[] path = StringUtils.split(uri.substring(index+1),"/");
		return path[path.length-1];
	}

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	
}
