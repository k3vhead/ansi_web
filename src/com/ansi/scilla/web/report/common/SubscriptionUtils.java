package com.ansi.scilla.web.report.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.organization.Div;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.common.utils.QMarkTransformer;
import com.ansi.scilla.web.common.utils.UserPermission;

public class SubscriptionUtils {

	private static final Logger logger = LogManager.getLogger(SubscriptionUtils.class);
	
	
	
	/**
	 * Report subscriptions need to change when:
	 * <ul>
	 * <li>a user is moved from one permission group to another</li>
	 * <li>When the permissions in a permission group change</li>
	 * <li>When a user is removed from a division</li>
	 * </ul>
	 * This method will handle the first two cases by Removing the subscriptions the users are not 
	 * allowed to see, by deleting everything that is not in the the permission group's list of permissions.
	 * 
	 * @param conn
	 * @param userId
	 * @param permissionGroupId
	 * @throws Exception
	 */
	public static void cureReportSubscriptions(Connection conn, Integer permissionGroupId) throws Exception {
		// make list of permissions to keep, taking into account the hierarchy of permissions
		List<UserPermission> userPermissionList = UserPermission.getUserPermissions(conn, permissionGroupId);
		List<String> permissionsToKeep = IteratorUtils.toList(CollectionUtils.collect(userPermissionList, new PermissionStringTransformer()).iterator());		
		
		// make list of users in permission group. We only change subscriptions for these users.
		User key = new User();
		key.setPermissionGroupId(permissionGroupId);
		List<User> userList = User.cast(key.selectSome(conn));
		
		
		// make a list of reports that require one of the permissions in the we just made
		List<BatchReports> reportList = new ArrayList<BatchReports>();		
		for(String permission : permissionsToKeep) {
			Permission p = Permission.valueOf(permission);
			for(BatchReports br : BatchReports.values()) {
				if(br.permission().equals(p)) {
					reportList.add(br);
				}
			}
		}
		
		List<Object> subUserList = IteratorUtils.toList(CollectionUtils.collect(userList, new UserToId()).iterator());
		List<Object> subReportList = IteratorUtils.toList(CollectionUtils.collect(reportList, new ReportToName()).iterator());
		// handle new permission groups/groups with no assigned permissions
		if ( subUserList.size() > 0 && subReportList.size() > 0 ) {
			String sql = "delete from report_subscription where user_id in " + QMarkTransformer.makeQMarkWhereClause(subUserList) + " "
					+ "and report_id not in " + QMarkTransformer.makeQMarkWhereClause(subReportList) + "";
			logger.log(Level.DEBUG, sql);
			PreparedStatement ps = conn.prepareStatement(sql);

			int n = 1;
			for(int i = 0; i < userList.size(); i++) {
				ps.setInt(n, userList.get(i).getUserId());
				logger.log(Level.DEBUG, userList.get(i).getUserId());
				n++;
			}
			for(int i = 0; i < reportList.size(); i++) {
				ps.setString(n, reportList.get(i).name());
				logger.log(Level.DEBUG, reportList.get(i).name());
				n++;
			}

			
			ps.execute();
		}
	}


	/**
	 * Make a list of divisions that a user is assigned to
	 * @param conn
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
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
	
	private static class PermissionStringTransformer implements Transformer<UserPermission, String> {
		@Override
		public String transform(UserPermission userPermission) {
			return userPermission.getPermissionName();
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
	
	public static class UserToId implements Transformer<User, Integer> {
		@Override
		public Integer transform(User arg0) {			
			return arg0.getUserId();
		}		
	}
	
	
	public static class PermissionToName implements Transformer<Permission, String> {
		@Override
		public String transform(Permission arg0) {			
			return arg0.name();
		}
	}
	
	
	
	public static class ReportToName implements Transformer<BatchReports, String> {
		@Override
		public String transform(BatchReports arg0) {			
			return arg0.name();
		}
	}
}

