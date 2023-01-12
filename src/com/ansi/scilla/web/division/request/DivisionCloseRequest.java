package com.ansi.scilla.web.division.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class DivisionCloseRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "divisionId";
	
	private Integer divisionId;

	public DivisionCloseRequest() {
		super();
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public WebMessages validate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		if ( webMessages.isEmpty() ) {
			validateCanClose(conn, webMessages);
		}
		return webMessages;
	}

	private void validateCanClose(Connection conn, WebMessages webMessages) throws SQLException {
		String sql = "select division.act_close_date,\n" + 
				"isnull( \n" + 
				"	(select top(1) ansi_date from ansi_calendar where ansi_date > (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date asc ),\n" + 
				"	(select top(1) ansi_date from ansi_calendar where ansi_date < sysdatetime() and date_type='DIVISION_CLOSE' order by ansi_date desc )\n" + 
				") as next_close_date,\n" + 
				"(select top(1) ansi_date from ansi_calendar where ansi_date < (select act_close_date from division d where d.division_id=division.division_id) and date_type='DIVISION_CLOSE' order by ansi_date desc) as last_close_date\n" + 
				"from division\n" + 
				"where division.division_id=?";
		boolean canClose = false;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, this.divisionId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			Object actCloseDate = rs.getObject("act_close_date");
			java.sql.Date nextCloseDate = rs.getDate("next_close_date");
//			java.sql.Date lastCloseDate = rs.getDate("last_close_date");
			if ( actCloseDate == null ) {
				canClose = true;
			} else {
				Date now = new Date();
				if ( now.after(nextCloseDate)) {
					canClose = true;
				} else {
					canClose = false;
				}
			}
		}
		rs.close();
		
		if (canClose == false) {
			webMessages.addMessage(DIVISION_ID, "Cannot close Division at this time");
		}
	}
}
