package com.ansi.scilla.web.report.webReport;

import java.sql.Connection;

import com.ansi.scilla.report.reportBuilder.AbstractReport;
import com.ansi.scilla.report.reportBuilder.CompoundReport;
import com.ansi.scilla.report.sixMonthRollingVolume.SixMonthRollingVolumeReport;

/**
 * A wrapper for the SixMonthRollingVolume report, giving a full year of data
 * 
 * @author dclewis
 *
 */
public class SixMonthRollingVolumeWebReport extends CompoundReport {

	private static final long serialVersionUID = 1L;
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

	
}
