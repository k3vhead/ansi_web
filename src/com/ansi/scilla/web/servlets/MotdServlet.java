package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.qotd.response.MotdResponse;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class MotdServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();				
			String message = doGetWork(conn);
//			String message = getQotd();
			MotdResponse motdResponse = new MotdResponse(message);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, motdResponse);
		} catch (Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public String doGetWork(Connection conn) throws Exception {
		String message = null;
		ApplicationProperties appProperties = new ApplicationProperties();
		appProperties.setPropertyId("com.ansi.scilla.web.servlets.QotdServlet.qotd");
		try {
			appProperties.selectOne(conn);
			message = appProperties.getValueString();
		} catch ( RecordNotFoundException e) {
			message = getQotd();
		}
		return message;
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
