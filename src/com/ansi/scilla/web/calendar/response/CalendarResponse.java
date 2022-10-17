package com.ansi.scilla.web.calendar.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.calendar.CalendarDateType;
import com.ansi.scilla.common.db.AnsiCalendar;
import com.ansi.scilla.web.common.response.MessageResponse;

public class CalendarResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer selectedYear;
	private List<Integer> years;
	private HashMap<String, List<CalendarResponseItem>> dates;
	private String updated;
	
	public CalendarResponse() {
		super();
	}
	
	public CalendarResponse(Connection conn, Integer selectedYear) throws Exception {
		this();
		this.selectedYear = selectedYear;
		makeYearList(conn);
		makeDateList(conn, selectedYear);
	}
	
	public Integer getSelectedYear() {
		return selectedYear;
	}
	public void setSelectedYear(Integer selectedYear) {
		this.selectedYear = selectedYear;
	}
	public List<Integer> getYears() {
		return years;
	}
	public void setYears(List<Integer> years) {
		this.years = years;
	}
	public HashMap<String, List<CalendarResponseItem>> getDates() {
		return dates;
	}
	public void setDates(HashMap<String, List<CalendarResponseItem>> dates) {
		this.dates = dates;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}

	private void makeDateList(Connection conn, Integer year) throws SQLException {
		this.dates = new HashMap<String, List<CalendarResponseItem>>();
		String sql = "select ansi_date, date_type from ansi_calendar where year(ansi_date)=? order by ansi_date asc";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, year);
		ResultSet rs = ps.executeQuery();
		List<CalendarResponseItem> dateList = new ArrayList<CalendarResponseItem>();
		while ( rs.next() ) {
			dateList.add(new CalendarResponseItem(rs));
		}
		rs.close();

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
		String[] monthList = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		for ( String month : monthList ) {
			List<CalendarResponseItem> monthItemList = new ArrayList<CalendarResponseItem>();
			for (CalendarResponseItem item : dateList ) {
				if ( sdf.format(item.getDate().getTime()).equals(month)) {
					monthItemList.add(item);
				}
			}
			this.dates.put(month, monthItemList);
		}
		
	}

	private void makeYearList(Connection conn) throws SQLException {
		this.years = new ArrayList<Integer>();
		String sql = "select distinct year(ansi_date) as ansi_year from ansi_calendar order by ansi_year";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while ( rs.next() ) {
			this.years.add(rs.getInt("ansi_year"));
		}
		rs.close();
	}



	public class CalendarResponseItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Calendar date;
		private String dateString;
		private String dateType;
		private String dateTypeDescription;
		
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		
		public CalendarResponseItem(ResultSet rs) throws SQLException {
			this.date = DateUtils.toCalendar(rs.getDate(AnsiCalendar.ANSI_DATE));
			this.dateString = dateFormatter.format(rs.getDate(AnsiCalendar.ANSI_DATE));
			this.dateType = rs.getString(AnsiCalendar.DATE_TYPE);
			try {
				this.dateTypeDescription = CalendarDateType.valueOf(this.dateType).description();
			} catch ( IllegalArgumentException e) {
				this.dateTypeDescription = this.dateType + " (Invalid value)";
			}
		}

		public String getDateType() {
			return dateType;
		}
		public Calendar getDate() {
			return date;
		}
		public void setDate(Calendar date) {
			this.date = date;
		}
		public String getDateString() {
			return dateString;
		}

		public void setDateString(String dateString) {
			this.dateString = dateString;
		}

		public void setDateType(String dateType) {
			this.dateType = dateType;
		}
		public String getDateTypeDescription() {
			return dateTypeDescription;
		}
		public void setDateTypeDescription(String dateTypeDescription) {
			this.dateTypeDescription = dateTypeDescription;
		}
	}
}
