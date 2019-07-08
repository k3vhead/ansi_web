package com.ansi.scilla.web.user.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;

public class DashboardFavoriteQuery extends DashboardFavoriteBaseQuery {

	private static final long serialVersionUID = 1L;

//	private final String selectSql = "select favorite_option.option_id, favorite_option.name as option_name, favorite_option.html_link, "
//			+ "(select count(*) from user_favorite where user_favorite.user_id=? and user_favorite.option_id=favorite_option.option_id) as option_selected\n";
//	private final String fromSql =
//			"from favorite_option\n" + 
//			"left outer join user_favorite on user_favorite.option_id=favorite_option.option_id\n";
	private final String whereSql = 
			"where favorite_option.permission_required in $PERMISSION_FILTER$ and favorite_option.option_type=?\n"; 
//	private final String orderSql =
//			"group by favorite_option.option_id, favorite_option.name, favorite_option.html_link\n" +
//			"order by favorite_option.name asc";
	
//	private Integer userId;
//	private List<String> permissionList;
//	private String optionType;
	
	public DashboardFavoriteQuery(Integer userId, List<String> permissionList, String optionType) {
		super(userId, permissionList, optionType);
		this.userId = userId;
		this.permissionList = permissionList;
		this.optionType = optionType;
	}

//	public Integer getUserId() {
//		return userId;
//	}
//
//	public void setUserId(Integer userId) {
//		this.userId = userId;
//	}
//
//	public List<String> getPermissionList() {
//		return permissionList;
//	}
//
//	public void setPermissionList(List<String> permissionList) {
//		this.permissionList = permissionList;
//	}
//
//	public String getOptionType() {
//		return optionType;
//	}
//
//	public void setOptionType(String optionType) {
//		this.optionType = optionType;
//	}
	
	@Override
	protected String makeWhereClause() {
		String boundVars = AppUtils.makeBindVariables(this.permissionList);
		String whereClause = this.whereSql.replaceAll("\\$PERMISSION_FILTER\\$", boundVars);
		return whereClause;
	}
	
//	protected String makeSql() {
//		String sql = selectSql + " " + fromSql + " " + makeWhereClause() + " " + orderSql;
//		return sql;
//	}
//	
//	protected String makeCountSql() {
//		String sql = "select count(*) as record_count" + " " + fromSql + " " + makeWhereClause();
//		return sql;
//	}
	
	@Override
	public ResultSet select(Connection conn) throws Exception {
		String sql = makeSql();
		System.out.println(sql);
		PreparedStatement statement = conn.prepareStatement(sql);
		int n = 1;
		statement.setInt(n, this.userId);
		n++;
		for ( String permission : permissionList ) {
			statement.setString(n, permission);
//			System.out.println(n + " " + permission);
			n++;
		}
		statement.setString(n, this.optionType);
		ResultSet rs = statement.executeQuery();
		return rs;
	}

	@Override
	public Integer selectCount(Connection conn) throws Exception {
		//In this case, there is no filtering, so count and countall are the same
		return selectCountAll(conn);
	}

	@Override
	public Integer selectCountAll(Connection conn) throws Exception {
		String sql = makeCountSql();
		System.out.println(sql);
		PreparedStatement statement = conn.prepareStatement(sql);
		Integer n = 1;
		for ( String permission : permissionList ) {
			statement.setString(n, permission);
			n++;
		}
		statement.setString(n, this.optionType);
		ResultSet rs = statement.executeQuery();
		Integer count = 0;
		if ( rs.next() ) {
			count = rs.getInt("record_count");
		}
		return count;
	}
	

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<String> permissionList = Arrays.asList(new String[] {"ADDRESS_READ","TICKET_READ","CONTACT_READ","QUOTE_READ","INVOICE_READ","CONTACT_WRITE"});
			DashboardFavoriteQuery fav = new DashboardFavoriteQuery(5, permissionList, "lookup");
			ResultSet rs = fav.select(conn);
			System.out.println("********");
			while ( rs.next() ) {
				System.out.println(rs.getString("option_name"));
			}
			rs.close();
			System.out.println("********");
			System.out.println(fav.selectCount(conn));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
}
