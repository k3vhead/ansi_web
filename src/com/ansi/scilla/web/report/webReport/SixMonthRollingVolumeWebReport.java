package com.ansi.scilla.web.report.webReport;

import java.sql.Connection;
import java.util.Calendar;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.report.reportBuilder.reportType.AbstractReport;
import com.ansi.scilla.report.reportBuilder.reportType.CompoundReport;
import com.ansi.scilla.report.reportBuilder.reportBy.ReportByDivMonthYear;
import com.ansi.scilla.report.reportBuilder.reportBy.ReportByDivision;
import com.ansi.scilla.report.sixMonthRollingVolume.SixMonthRollingVolumeReport;

/**
 * A wrapper for the SixMonthRollingVolume report, giving a full year of data
 * 
 * @author dclewis
 *
 */
public class SixMonthRollingVolumeWebReport extends CompoundReport implements ReportByDivMonthYear, ReportByDivision {

	private static final long serialVersionUID = 1L;
	public static final String FILENAME = "SMRV";
	public static final String REPORT_TITLE = "6-Month Rolling Volume";

	
	protected SixMonthRollingVolumeWebReport(Connection conn, Integer divisionId, Integer month1, Integer year1, Integer month2, Integer year2) throws Exception {
		super(new AbstractReport[] {
			SixMonthRollingVolumeReport.buildReport(conn, divisionId, month1, year1),
			SixMonthRollingVolumeReport.buildReport(conn, divisionId, month2, year2)
		});
	}
	

	public static SixMonthRollingVolumeWebReport buildReport(Connection conn, Integer divisionId, Integer month, Integer year) throws Exception {
		Integer month2 = month < 7 ? month + 6 : month+6-12;
		Integer year2 = month < 7 ? year : year + 1;
		return new SixMonthRollingVolumeWebReport(conn, divisionId, month, year, month2, year2);
	}


	
	public String makeFileName(String arg0, Calendar arg1, Division arg2, Calendar arg3, Calendar arg4) {
		throw new RuntimeException("This is only here to make the compiler happy");
	}


	@Override
	public String makeFileName(Calendar runDate, Division division, Calendar startDate, Calendar endDate) {
		return makeFileName(FILENAME, runDate, division, startDate, endDate);
	}

	
}
