package com.ansi.scilla.web.bcr.common;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BcrTicketSpreadsheet {
	/**
	 * @author jwlewis
	 */
	public BcrTicketSpreadsheet() {
		//this.BcrTicketSpreadsheet(conn, year, weeks);
	}
	public BcrTicketSpreadsheet(Connection conn, Calendar date, List<Date> weeks) {
		String sql = BcrTicketSql.sqlFromClause;
		Integer year = date.YEAR;
	}
}
