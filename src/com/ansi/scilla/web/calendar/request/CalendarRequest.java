package com.ansi.scilla.web.calendar.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class CalendarRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String DATE = "date";
	public static final String DATE_TYPE = "dateType";
	
	private Calendar date;
	private String dateType;
	
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	
	public WebMessages validateAdd(Connection conn) {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, DATE, this.date, true, (Date)null, (Date)null);
		RequestValidator.validateDateType(webMessages, DATE_TYPE, this.dateType, true);
		
		if ( webMessages.isEmpty() ) {
			try {
				// we do it this way because date and calendar objects have a time built in, and the date object we
				// get from the db may be inconsistent with the date object we get from the request object.
				// Weirdness ensues.
				PreparedStatement ps = conn.prepareStatement("select count(*) as record_count from ansi_calendar\n" + 
						"where year(ansi_date)=? and month(ansi_date)=? and day(ansi_date)=? and date_type=?");
				ps.setInt(1, this.date.get(Calendar.YEAR));
				ps.setInt(2, this.date.get(Calendar.MONTH)+1);   // because it's zero-based
				ps.setInt(3, this.date.get(Calendar.DAY_OF_MONTH));
				ps.setString(4, this.dateType);
				ResultSet rs = ps.executeQuery();
				if ( rs.next() ) {
					if ( rs.getInt("record_count") > 0 ) {
						webMessages.addMessage(DATE, "Duplicate Entry");
					}
				}
				rs.close();
			} catch ( Exception e) {
				throw new RuntimeException(e);
			}
		}
		return webMessages;
	}
	
	
	public WebMessages validateDelete() {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, DATE, this.date, true, (Date)null, (Date)null);
		RequestValidator.validateString(webMessages, DATE_TYPE, this.dateType, true);
		return webMessages;
	}
}
