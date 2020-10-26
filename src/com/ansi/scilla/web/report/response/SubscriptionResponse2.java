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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.DivisionGroup;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.organization.Div;
import com.ansi.scilla.common.organization.Organization;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.report.common.BatchReports;
import com.ansi.scilla.web.report.common.SubscriptionUtils;


public class SubscriptionResponse2 extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<Report> allAnsiReports = new ArrayList<Report>();
	private List<Report> summaryReports = new ArrayList<Report>();
	private List<Report> divisionReports = new ArrayList<Report>();
	private List<Report> trendReports = new ArrayList<Report>();
	private List<Report> utilityReports = new ArrayList<Report>();
	
	private List<Div> divisionList = new ArrayList<Div>();
	private List<Group> companyList = new ArrayList<Group>();
	private List<Group> regionList = new ArrayList<Group>();
	private List<Group> groupList = new ArrayList<Group>();
	
	private List<MySubscription> subscriptionList;

	
	private Logger logger = LogManager.getLogger(SubscriptionResponse2.class);

	
	public SubscriptionResponse2(Connection conn, Integer userId, List<UserPermission> userPermissionList) throws Exception {
		super();
		ReportTransformer reportTransformer = new ReportTransformer();
		this.divisionList = makeDivisionList(conn, userId);	
		makeGroups(conn, divisionList);
		
		List<BatchReports> allowedReports = SubscriptionUtils.myReports(userPermissionList); // reports I can see based on my permissions
		
		this.allAnsiReports = makeAllAnsiReports(conn, reportTransformer, allowedReports, userId);
		this.trendReports = makeTrendReports(allowedReports, reportTransformer);
		this.utilityReports = makeUtilityReports(allowedReports, reportTransformer);
		if ( this.divisionList.size() > 0 ) {
			this.divisionReports = makeDivisionReports(allowedReports, reportTransformer);
		}
		if ( this.companyList.size() > 0 || this.regionList.size() > 0 || this.groupList.size() > 0 ) {
			this.summaryReports = makeSummaryReports(allowedReports, reportTransformer);
		}
		this.subscriptionList = makeSubscriptionList(conn, userId);
		
		
		
		Collections.sort(allAnsiReports);
		Collections.sort(summaryReports);
		Collections.sort(divisionReports);
		Collections.sort(trendReports);
		Collections.sort(utilityReports);
		
		Collections.sort(divisionList);
		Collections.sort(companyList);
		Collections.sort(regionList);
		Collections.sort(groupList);
		
	}

	public List<Report> getAllAnsiReports() {
		return allAnsiReports;
	}
	public void setAllAnsiReports(List<Report> allAnsiReports) {
		this.allAnsiReports = allAnsiReports;
	}
	public List<Report> getSummaryReports() {
		return summaryReports;
	}
	public void setSummaryReports(List<Report> summaryReports) {
		this.summaryReports = summaryReports;
	}
	public List<Report> getDivisionReports() {
		return divisionReports;
	}
	public void setDivisionReports(List<Report> divisionReports) {
		this.divisionReports = divisionReports;
	}
	public List<Report> getTrendReports() {
		return trendReports;
	}
	public void setTrendReports(List<Report> trendReports) {
		this.trendReports = trendReports;
	}
	public List<Report> getUtilityReports() {
		return utilityReports;
	}
	public void setUtilityReports(List<Report> utilityReports) {
		this.utilityReports = utilityReports;
	}
	public List<Div> getDivisionList() {
		return divisionList;
	}
	public void setDivisionList(List<Div> divisionList) {
		this.divisionList = divisionList;
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
	public List<Group> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}

	public List<MySubscription> getSubscriptionList() {
		return subscriptionList;
	}
	public void setSubscriptionList(List<MySubscription> subscriptionList) {
		this.subscriptionList = subscriptionList;
	}
	
	
	private List<Report> makeAllAnsiReports(Connection conn, ReportTransformer reportTransformer, List<BatchReports> allowedReports, Integer userId) throws SQLException {
		
		PreparedStatement ps = conn.prepareStatement("SELECT division.division_id, concat(division_nbr,'-',division_code) as div, description, \n" + 
				"	case \n" + 
				"	when division_user.division_id is null then 0\n" + 
				"	else 1 \n" + 
				"	end as my_division\n" + 
				"FROM division\n" + 
				"left outer join division_user on division_user.division_id=division.division_id and division_user.user_id=?\n" + 
				"order by concat(division_nbr,'-',division_code)");
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		boolean allAnsi = true;
		
		while ( rs.next() ) {
			Integer myDiv = rs.getInt("my_division");
			if ( myDiv.intValue() == 0 ) {
				allAnsi = false;
			}
		}
		rs.close();
		return allAnsi ? IterableUtils.toList(IterableUtils.transformedIterable(SubscriptionUtils.makeAllAnsiReportList(allowedReports), reportTransformer)) : new ArrayList<Report>();
		
	}

	private List<Div> makeDivisionList(Connection conn, Integer userId) throws SQLException {		
		List<Div> divisionList = SubscriptionUtils.makeDivisionList(conn, userId);	
		Collections.sort(divisionList);
		return divisionList;
	}

	
	private void makeGroups(Connection conn, List<Div> divisionList) throws SQLException, InvalidValueException {
		List<Integer> divIdList = (List<Integer>) CollectionUtils.collect(divisionList, new DivTransformer());
//		logger.log(Level.DEBUG, divIdList);
		HashMap<Integer, Organization> ansi = Organization.make(conn);
		for ( Organization org : ansi.values() ) {
			makeGroupLists(org, divIdList);
		}
		Collections.sort(this.companyList);
		Collections.sort(this.regionList);
		Collections.sort(this.groupList);
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

	private List<Report> makeDivisionReports(List<BatchReports> allowedReports, ReportTransformer reportTransformer) {
		return IterableUtils.toList(IterableUtils.transformedIterable(SubscriptionUtils.makeDivisionReportList(allowedReports), reportTransformer));
	}
	
	private List<Report> makeTrendReports(List<BatchReports> allowedReports, ReportTransformer reportTransformer) {
		return IterableUtils.toList(IterableUtils.transformedIterable(SubscriptionUtils.makeTrendReportList(allowedReports), reportTransformer));
	}
	
	private List<Report> makeUtilityReports(List<BatchReports> allowedReports, ReportTransformer reportTransformer) {
		return IterableUtils.toList(IterableUtils.transformedIterable(SubscriptionUtils.makeUtilityReportList(allowedReports), reportTransformer));
	}
	
	private List<Report> makeSummaryReports(List<BatchReports> allowedReports, ReportTransformer reportTransformer) {
		return IterableUtils.toList(IterableUtils.transformedIterable(SubscriptionUtils.makeSummaryReportList(allowedReports), reportTransformer));
	}

	private List<MySubscription> makeSubscriptionList(Connection conn, Integer userId) throws SQLException {
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


	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("allAnsi:\n");
		for ( Report report : allAnsiReports ) {
			buffer.append("\t" + report.getCode() + "\n");
			
		}
		buffer.append("summary:\n");
		for ( Report report : summaryReports ) {
			buffer.append("\t" + report.getCode() + "\n");
			
		}
		buffer.append("division:\n");
		for ( Report report : divisionReports ) {
			buffer.append("\t" + report.getCode() + "\n");
			
		}
		buffer.append("trends:\n");
		for ( Report report : trendReports ) {
			buffer.append("\t" + report.getCode() + "\n");
			
		}
		buffer.append("utility:\n");
		for ( Report report : utilityReports ) {
			buffer.append("\t" + report.getCode() + "\n");
			
		}
		buffer.append("division:\n");
		for ( Div div : divisionList ) {
			buffer.append("\t" + div.getDivisionCode() + "\n");
		}
		buffer.append("company:\n");
		for ( Group group : companyList ) {
			buffer.append("\t" + group.getName() + "\n");
		}
		buffer.append("region:\n");
		for ( Group group : regionList ) {
			buffer.append("\t" + group.getName() + "\n");
		}
		buffer.append("group:\n");
		for ( Group group : groupList ) {
			buffer.append("\t" + group.getName() + "\n");
		}

		return buffer.toString();
	}








	public class Report extends ApplicationObject implements Comparable<Report> {

		private static final long serialVersionUID = 1L;
		private String reportId;
		private String code;
		private String description;
		public Report(BatchReports report) {
			super();
			this.reportId = report.name();
			this.code = report.abbreviation();
			this.description = report.description();
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
		
		public static final String sql = "select subscription_id, report_id, division_id, group_id from report_subscription where user_id=?";
		
		private Integer subscriptionId;
		private String reportId;
		private Integer divisionId;
		private Integer groupId;
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
			Object groupId = rs.getObject("group_id");
			if ( groupId != null ) {
				this.groupId = (Integer)groupId;
			}
		}
		public Integer getSubscriptionId() {
			return subscriptionId;
		}
		public String getReportId() {
			return reportId;
		}
		public Integer getDivisionId() {
			return divisionId;
		}
		public Integer getGroupId() {
			return groupId;
		}
	}
	
	
	public class DivTransformer implements Transformer<Div, Integer> {
		@Override
		public Integer transform(Div div) {
			return div.getDivisionId();
		}
		
	}
	
	public class ReportTransformer implements Transformer<BatchReports, Report> {
		@Override
		public Report transform(BatchReports batchReport) {
			return new Report(batchReport);
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
