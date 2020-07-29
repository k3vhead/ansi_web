package com.ansi.scilla.web.report.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.organization.Div;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.common.utils.UserPermission;

public class SubscriptionUtils {

	private static final Logger logger = LogManager.getLogger(SubscriptionUtils.class);
	
	
	public static List<Div> makeDivisionList(Connection conn, Integer userId) throws SQLException {
		List<Div> divisionList = new ArrayList<Div>();
		String sql = "select division.*\n" + 
				"from division_user\n" + 
				"inner join division on division.division_id=division_user.division_id\n" + 
				"where user_id=?\n" + 
				"order by concat(division_nbr,'-',division_code)";
//		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			divisionList.add(new Div(rs));
		}
		rs.close();	
		
		return divisionList;
	}

	
	/**
	 * Returns a list of reports I'm allowed to see, based on my permissions
	 * 
	 * @param permissions
	 * @return
	 */
	public static List<BatchReports> myReports(List<UserPermission> userPermissions) {
		List<BatchReports> reportList = new ArrayList<BatchReports>();
		// convert userPermissions to permissions so we can match
		List<Permission> permissionList = IterableUtils.toList(IterableUtils.transformedIterable(userPermissions, new PermissionTransformer()));
		// make a List of reports so the iterableutils will work
		List<BatchReports> allReports = Arrays.asList(BatchReports.values());
		for ( Permission permission : permissionList ) {
			// get a list of reports that match this permission
			Iterable<BatchReports> reports = IterableUtils.filteredIterable(allReports, new PermissionPredicate(permission));
			// add that list to the general list
			reportList.addAll(IterableUtils.toList(reports));
		}
		// remove any duplicates.
		return IterableUtils.toList(IterableUtils.uniqueIterable(reportList));
	}
	
	/**
	 * Determines which reports in the given list are &quote;All-Ansi&quot; reports.  If you want the entire
	 * list of All-Ansi, pass in <code>Arrays.asList(BatchReports.values())</code>
	 * @param batchReports
	 * @return
	 */
	public static List<BatchReports> makeAllAnsiReportList(List<BatchReports> batchReports) {
		return IterableUtils.toList(IterableUtils.filteredIterable( batchReports, new ReportTypePredicate(MatchType.allAnsiReport)));
	}
	
	/**
	 * Determines which reports in the given list are &quote;Trend&quot; reports.  If you want the entire
	 * list of All-Ansi, pass in <code>Arrays.asList(BatchReports.values())</code>
	 * @param batchReports
	 * @return
	 */
	public static List<BatchReports> makeTrendReportList(List<BatchReports> batchReports) {
		return IterableUtils.toList(IterableUtils.filteredIterable( batchReports, new ReportTypePredicate(MatchType.trendReport)));
	}
	
	/**
	 * Determines which reports in the given list are &quote;Summary&quot; reports.  If you want the entire
	 * list of All-Ansi, pass in <code>Arrays.asList(BatchReports.values())</code>
	 * @param batchReports
	 * @return
	 */
	public static List<BatchReports> makeSummaryReportList(List<BatchReports> batchReports) {
		return IterableUtils.toList(IterableUtils.filteredIterable( batchReports, new ReportTypePredicate(MatchType.summaryReport)));
	}
	
	/**
	 * Determines which reports in the given list are &quote;Division&quot; reports.  If you want the entire
	 * list of All-Ansi, pass in <code>Arrays.asList(BatchReports.values())</code>
	 * @param batchReports
	 * @return
	 */
	public static List<BatchReports> makeDivisionReportList(List<BatchReports> batchReports) {
		return IterableUtils.toList(IterableUtils.filteredIterable( batchReports, new ReportTypePredicate(MatchType.divisionReport)));
	}
	
	/**
	 * Determines which reports in the given list are &quote;Utility&quot; reports.  If you want the entire
	 * list of All-Ansi, pass in <code>Arrays.asList(BatchReports.values())</code>
	 * @param batchReports
	 * @return
	 */
	public static List<BatchReports> makeUtilityReportList(List<BatchReports> batchReports) {
		return IterableUtils.toList(IterableUtils.filteredIterable( batchReports, new ReportTypePredicate(MatchType.utilityReport)));
	}
	
	
	
	private static class ReportTypePredicate implements Predicate<BatchReports> {
		private MatchType matchType;
		
		public ReportTypePredicate(MatchType matchType) {
			super();
			this.matchType = matchType;
		}

		@Override
		public boolean evaluate(BatchReports batchReport) {			
			return (matchType.equals(MatchType.allAnsiReport) && batchReport.isAllAnsiReport()) || 
					(matchType.equals(MatchType.summaryReport) && batchReport.isSummaryReport()) || 
					(matchType.equals(MatchType.divisionReport) && batchReport.isDivisionReport()) || 
					(matchType.equals(MatchType.trendReport) && batchReport.isTrendReport()) || 
					(matchType.equals(MatchType.utilityReport) && batchReport.isUtilityReport()); 
		}
		
	}
	
	private enum MatchType {
		allAnsiReport,
		summaryReport,
		divisionReport,
		trendReport,
		utilityReport,
		;
	}
	
	
	private static class PermissionTransformer implements Transformer<UserPermission, Permission> {
		@Override
		public Permission transform(UserPermission userPermission) {
			return Permission.valueOf(userPermission.getPermissionName());
		}		
	}
	
	private static class PermissionPredicate implements Predicate<BatchReports> {
		private Permission permission;		
		public PermissionPredicate(Permission permission) {
			super();
			this.permission = permission;
		}

		@Override
		public boolean evaluate(BatchReports batchReports) {
			return batchReports.permission().equals(this.permission);
		}
		
	}
}

