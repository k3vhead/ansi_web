package com.ansi.scilla.web.report.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.DivisionGroup;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.organization.Div;
import com.ansi.scilla.common.organization.Organization;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.report.common.BatchReports;
import com.ansi.scilla.web.report.common.SubscriptionUtils;

public class SubscriptionResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<Div> divisionList;
	private List<Report> reportList;
	private List<Group> companyList;
	private List<Group> regionList;
	private List<MySubscription> subscriptionList;
	private List<String> allDivReportList;
//	private List<Integer> allReportDivList;
	
	private Logger logger = LogManager.getLogger(SubscriptionResponse.class);

	
	public SubscriptionResponse(Connection conn, Integer userId) throws Exception {
		super();
		this.divisionList = new ArrayList<Div>();
		this.companyList = new ArrayList<Group>();
		this.regionList = new ArrayList<Group>();
		this.reportList = new ArrayList<Report>();
		this.subscriptionList = makeSubscriptionList(conn, userId);
		
		makeDivisionList(conn, userId);		
		makeGroupLists(conn, divisionList);
		makeAllDivReportList();
		
		for ( BatchReports report : BatchReports.values() ) {
			this.reportList.add(new Report(report));
		}
		Collections.sort(this.reportList);
	}

	private void makeDivisionList(Connection conn, Integer userId) throws SQLException {
		this.divisionList = SubscriptionUtils.makeDivisionList(conn, userId);	
		Collections.sort(divisionList);
	}

	
	private void makeGroupLists(Connection conn, List<Div> divisionList) throws SQLException, InvalidValueException {
		List<Integer> divIdList = (List<Integer>) CollectionUtils.collect(divisionList, new DivTransformer());
//		logger.log(Level.DEBUG, divIdList);
		HashMap<Integer, Organization> ansi = Organization.make(conn);
		for ( Organization org : ansi.values() ) {
			makeGroupLists(org, divIdList);
		}
		Collections.sort(this.companyList);
		Collections.sort(this.regionList);
	}
	
	private void makeGroupLists(Organization org, List<Integer> divIdList) throws InvalidValueException {
		List<Integer> orgDivIdList = (List<Integer>)CollectionUtils.collect(org.getAllDivs(), new DivTransformer() );
		if ( CollectionUtils.isSubCollection(orgDivIdList, divIdList) ) {
			// check to see if user belongs to all divs in an organization

			OrganizationType type = OrganizationType.valueOf(org.getType().toUpperCase());
			if ( type.equals(OrganizationType.REGION) ) {
				this.regionList.add(new Group(org));
			} else if ( type.equals(OrganizationType.COMPANY) ) {
				this.companyList.add(new Group(org));
			} else {
				throw new InvalidValueException("Unknown group type: " + type);
			}
		}

		if ( org.getChildren() != null ) {
			for ( Organization child : org.getChildren() ) {
				makeGroupLists(child, divIdList);
			}
		}
	}

	public List<MySubscription> makeSubscriptionList(Connection conn, Integer userId) throws SQLException {
		List<MySubscription> subscriptionList = new ArrayList<MySubscription>();
		PreparedStatement ps = conn.prepareStatement(MySubscription.sql);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			subscriptionList.add(new MySubscription(rs));
		}
		rs.close();
		return subscriptionList;
	}


	private void makeAllDivReportList() {
//		logger.log(Level.DEBUG, "MakeAllDivReportList: " + this.subscriptionList.size());
		this.allDivReportList = new ArrayList<String>();
		for ( MySubscription subscription : this.subscriptionList ) {
			Integer divCount = Math.toIntExact(IterableUtils.countMatches(this.subscriptionList, new ReportMatchPredicate(subscription.getReportId())));
//			logger.log(Level.DEBUG, subscription.getReportId() + "\t" + divCount);
			if ( divCount.equals(divisionList.size()) && ! this.allDivReportList.contains(subscription.getReportId())) {
				this.allDivReportList.add(subscription.getReportId());
			}
		}
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
	
	public List<MySubscription> getSubscriptionList() {
		return subscriptionList;
	}

	public void setSubscriptionList(List<MySubscription> subscriptionList) {
		this.subscriptionList = subscriptionList;
	}
	public List<String> getAllDivReportList() {
		return allDivReportList;
	}
	public void setAllDivReportList(List<String> allDivReportList) {
		this.allDivReportList = allDivReportList;
	}




	public class Report extends ApplicationObject implements Comparable<Report> {

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
		@Override
		public int compareTo(Report o) {
			return this.getCode().compareTo(o.getCode());
		}
		
		
	}
	


	public class Group extends ApplicationObject implements Comparable<Group> {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private String name;
		public Group(DivisionGroup group ) {
			this.id = group.getGroupId();
			this.name = group.getName();
		}
		public Group(Organization org) {
			this.id = org.getGroupId();
			this.name = org.getName();
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
		@Override
		public int compareTo(Group o) {
			return o.getName().compareTo(this.getName());
		}
		
	}
	
	
	public class MySubscription extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
		public static final String sql = "select subscription_id, report_id, division_id from report_subscription where user_id=?";
		
		private Integer subscriptionId;
		private String reportId;
		private Integer divisionId;
		private MySubscription() {
			super();
		}
		public MySubscription(ResultSet rs) throws SQLException {
			this();
			this.subscriptionId = rs.getInt("subscription_id");
			this.reportId = rs.getString("report_id");
			Object divisionId = rs.getObject("division_id");
			if ( divisionId != null ) {
				this.divisionId = (Integer)divisionId;
			}
		}
		public Integer getSubscriptionId() {
			return subscriptionId;
		}
		public void setSubscriptionId(Integer subscriptionId) {
			this.subscriptionId = subscriptionId;
		}
		public String getReportId() {
			return reportId;
		}
		public void setReportId(String reportId) {
			this.reportId = reportId;
		}
		public Integer getDivisionId() {
			return divisionId;
		}
		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}
		
	}
	
	
	public class DivTransformer implements Transformer<Div, Integer> {
		@Override
		public Integer transform(Div div) {
			return div.getDivisionId();
		}
		
	}
	
	
	public class ReportMatchPredicate implements Predicate<MySubscription> {
		private String reportId;
		
		public ReportMatchPredicate(String reportId) {
			super();
			this.reportId = reportId;
		}

		@Override
		public boolean evaluate(MySubscription subscription) {
			return subscription.getReportId().equals(reportId);
		}
	}
	
	
	
	public class DivMatchPredicate implements Predicate<MySubscription> {
		private Integer divisionId;
		
		public DivMatchPredicate(Integer divisionId) {
			super();
			this.divisionId = divisionId;
		}

		@Override
		public boolean evaluate(MySubscription subscription) {
			return subscription.getDivisionId().equals(divisionId);
		}
	}
}
