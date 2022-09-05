package com.ansi.scilla.web.test.payroll;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionGroup;

public abstract class Loader {
	protected final DecimalFormat format1 = new DecimalFormat("###,###.00");
	protected final DecimalFormat dollarFormat = new DecimalFormat("$###,###.00");
	protected HashMap<Integer, Division> divMap = new HashMap<Integer, Division>();
	protected HashMap<Integer, DivisionGroup> groupMap = new HashMap<Integer, DivisionGroup>();

//	This one makes for duplicate keys because the state isn't handled
//	"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/67 WP WV 12-3-21.ods",

//	This one has no washers and so confuses the name parser. It will need to be handled in prod, but for loading test data we'll just skip it
//	"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Weekly Payroll 12-10-21 Div. 18.ods",


	protected final String[] fileNames = new String[] {
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/12-PAYROLL 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/15-PAYROLL 12-3-21[879722].ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/15-PAYROLL 12-10-21[879818].ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/23 SQUARES 12-3-2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/23 SQUARES 12-10-2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/67 Payroll 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/67 WP 12-3-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/71 Weekly Payroll  week 48.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/71 Weekly Payroll  week 49.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/89 WP 12-3-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/89 WP 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/2021 Payroll Wk 12-03-21[879698].ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll 77 12.03.2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll 77 12.10.2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll 78 12.03.2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll 78 12.10.2021.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll div 44 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll Squares 31 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll Squares 33 - 12-02-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Payroll Squares 33 - 12-10-21.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/PRS12-03-66.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/PRS-12-03-65.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/PRS-12-10-65.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/PRS-12-10-66.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Weekly Payroll 12-3-21 Div. 18.ods",
//			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Weekly Payroll 12-3-21 Div. 19.ods",
			"/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_worksheet/Weekly Payroll 12-10-21 Div. 19.ods",
		};
	
	protected void makeDivMap(Connection conn) throws Exception {
		List<Division> divisionList = Division.cast( new Division().selectAll(conn));
		for (Division div : divisionList ) {
			this.divMap.put(div.getDivisionNbr(), div);
		}
		
		List<DivisionGroup> groupList = DivisionGroup.cast( new DivisionGroup().selectAll(conn));
		for ( DivisionGroup group : groupList ) {
			if ( group.getGroupType().equalsIgnoreCase("COMPANY") ) {
				System.out.println(group.getGroupId() + "\t" + group.getCompanyCode());
				this.groupMap.put(group.getGroupId(), group);
			}
		}
		
	}
	
	protected BigDecimal makeBD(String value) throws ParseException {
		BigDecimal bd = null;
		try {
			bd = new BigDecimal(value);
		} catch ( NumberFormatException e ) {
			try {
				Number number = format1.parse(value);
				bd = new BigDecimal(number.doubleValue()).round(MathContext.DECIMAL32);
			} catch ( ParseException pe ) {
				Number number = dollarFormat.parse(value);
				bd = new BigDecimal(number.doubleValue()).round(MathContext.DECIMAL32);
			}
		}
		return bd;
	}
	
	
	protected class EmployeeName extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String first;
		public String last;
		public EmployeeName(String name) {
			super();
			String[] parsedName = StringUtils.split(name, ",");
			if ( parsedName.length == 1 ) {
				// name is first last
				String[] x= StringUtils.split(name, " ");
				this.first = StringUtils.strip(x[0]);
				this.last = StringUtils.strip(x[1]);
			} else if ( parsedName.length == 2 ) {
				// name is last, first
				this.first = StringUtils.strip(parsedName[1]);
				this.last = StringUtils.strip(parsedName[0]);
			} else {
				throw new RuntimeException("Unexpected Name format: " + name);
			}
		}
	}
}
