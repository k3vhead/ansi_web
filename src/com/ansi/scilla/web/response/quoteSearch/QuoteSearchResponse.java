package com.ansi.scilla.web.response.quoteSearch;

import java.io.Serializable;

import com.ansi.scilla.web.response.MessageResponse;

/**
 * Used to return a single code to the client
 * 
 * @author dclewis
 *
 */
public class QuoteSearchResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private QuoteSearchRecord quoteSearch;

	public QuoteSearchResponse() {
		super();
	}

	public QuoteSearchResponse(QuoteSearchRecord quoteSearch) {
		super();
		this.quoteSearch = quoteSearch;
	}

/*	public QuoteSearchResponse(Connection conn, QuoteSearch quoteSearch) throws IllegalAccessException, InvocationTargetException, SQLException {
		this();
		this.quoteSearch = new QuoteSearchRecord();
		PropertyUtils.copyProperties(this.quoteSearch, quoteSearch);
		Integer billToName = this.makeUserCount(conn, quoteSearch.getQuoteId());
		this.quoteSearch.setBillToName(billToName);
	}
*/
/*	private Integer makeUserCount(Connection conn, Integer divisionId) throws SQLException {
		String sql = "select count(*) as user_count from division_user where division_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ResultSet rs = ps.executeQuery();
		Integer userCount = 0;
		if(rs.next()){
			userCount=rs.getInt("user_count");
		}
		rs.close();
		return userCount;
	}
*/
	public QuoteSearchRecord getQuoteSearch() {
		return quoteSearch;
	}
	
	
}
