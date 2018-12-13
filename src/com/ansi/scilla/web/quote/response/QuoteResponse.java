package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.UserPermission;

/**
 * Used to return a single quote to the client
 * 
 * 
 *
 */
public class QuoteResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private QuoteResponseItem quote;
	
	public QuoteResponse() {
		super();
	}
	
	public QuoteResponse(Connection conn, Integer jobId, List<UserPermission>permissionList) throws Exception {
		super();
		String sql = "select quote.* from job inner join quote on quote.quote_id=job.quote_id where job.job_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  jobId);
		ResultSet rs=ps.executeQuery();
		if ( rs.next() ) {
			ResultSetMetaData rsmd = rs.getMetaData();
			Quote quote = (Quote)new Quote().rs2Object(rsmd, rs);
			this.quote = new QuoteResponseItem(conn, quote, permissionList);
		}
	}
	
	public QuoteResponse(Connection conn, Quote quote, WebMessages webMessages, List<UserPermission>permissionList) throws Exception {
		super(webMessages);
		if ( quote != null ) {
			this.quote = new QuoteResponseItem(conn, quote, permissionList);
		}
	}

	public QuoteResponseItem getQuote() {
		return quote;
	}

	public void setQuote(QuoteResponseItem quote) {
		this.quote = quote;
	}
	


	

	
}
