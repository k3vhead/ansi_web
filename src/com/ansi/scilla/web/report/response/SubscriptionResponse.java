package com.ansi.scilla.web.report.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionGroup;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.report.common.BatchReports;

public class SubscriptionResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<Div> divisionList;
	private List<Report> reportList;
	private List<Group> companyList;
	private List<Group> regionList;
	private Logger logger = LogManager.getLogger(SubscriptionResponse.class);
	
	
	public SubscriptionResponse(Connection conn, Integer userId) throws Exception {
		super();
		this.divisionList = new ArrayList<Div>();
		this.companyList = new ArrayList<Group>();
		this.regionList = new ArrayList<Group>();
		this.reportList = new ArrayList<Report>();
		
		this.divisionList = makeDivisionList(conn, userId);
		
		List<DivisionGroup> groupList = DivisionGroup.cast(new DivisionGroup().selectAll(conn));
		for ( DivisionGroup group : groupList ) {
			if ( group.getGroupType().equals(DivisionGroup.GroupType.COMPANY.name())) {
				this.companyList.add(new Group(group));
			} else if ( group.getGroupType().equals(DivisionGroup.GroupType.REGION.name())) {
				this.regionList.add(new Group(group));
			} else {
				throw new InvalidValueException("Invalid division group type: " + group.getGroupType());
			}
		}
		
		for ( BatchReports report : BatchReports.values() ) {
			this.reportList.add(new Report(report));
		}
	}

	private List<Div> makeDivisionList(Connection conn, Integer userId) throws SQLException {
		List<Div> divisionList = new ArrayList<Div>();
		String fieldList = StringUtils.join(new String[] {
				"division." + Division.DIVISION_ID, 
				"division." + Division.DIVISION_NBR,
				"division." + Division.DIVISION_CODE, 
				"division." + Division.DESCRIPTION}, ",");
		String sql = "select " + fieldList + "\n" + 
				"from division_user\n" + 
				"inner join division on division.division_id=division_user.division_id\n" + 
				"where user_id=?\n" + 
				"order by concat(division_nbr,'-',division_code)";
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			divisionList.add(new Div(rs));
		}
		rs.close();
		return divisionList;
	}

	
	public List<Div> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<Div> divisionList) {
		this.divisionList = divisionList;
	}

	public List<Report> getReportList() {
		return reportList;
	}

	public void setReportList(List<Report> reportList) {
		this.reportList = reportList;
	}

	public List<Group> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Group> companyList) {
		this.companyList = companyList;
	}

	public List<Group> getRegionList() {
		return regionList;
	}

	public void setRegionList(List<Group> regionList) {
		this.regionList = regionList;
	}

	public class Report extends ApplicationObject {

		private static final long serialVersionUID = 1L;
		private String reportId;
		private String code;
		private String description;
		private Boolean executiveReport;
		private Boolean summaryReport;
		private Boolean divisionReport;
		public Report(BatchReports report) {
			super();
			this.reportId = report.name();
			this.code = report.abbreviation();
			this.description = report.description();
			this.executiveReport = report.isExecutiveReport();
			this.summaryReport = report.isSummaryReport();
			this.divisionReport = report.isDivisionReport();
		}
		public String getReportId() {
			return reportId;
		}
		public void setReportId(String reportId) {
			this.reportId = reportId;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Boolean getExecutiveReport() {
			return executiveReport;
		}
		public void setExecutiveReport(Boolean executiveReport) {
			this.executiveReport = executiveReport;
		}
		public Boolean getSummaryReport() {
			return summaryReport;
		}
		public void setSummaryReport(Boolean summaryReport) {
			this.summaryReport = summaryReport;
		}
		public Boolean getDivisionReport() {
			return divisionReport;
		}
		public void setDivisionReport(Boolean divisionReport) {
			this.divisionReport = divisionReport;
		}
		
		
	}
	
	public class Div extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer divisionId;
		private String div;
		private String description;
		
		public Div(ResultSet rs) throws SQLException {
			super();
			this.divisionId = rs.getInt(Division.DIVISION_ID);
			this.div = String.valueOf(rs.getInt(Division.DIVISION_NBR)) + "-" + rs.getString(Division.DIVISION_CODE);
			this.description = rs.getString(Division.DESCRIPTION);
		}

		public Integer getDivisionId() {
			return divisionId;
		}

		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}

		public String getDiv() {
			return div;
		}

		public void setDiv(String div) {
			this.div = div;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
	}

	public class Group extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private String name;
		public Group(DivisionGroup group ) {
			this.id = group.getGroupId();
			this.name = group.getName();
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
}
