package com.ansi.scilla.web.test.specialOverride;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.specialOverride.common.ParameterType;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class TestSQLParse {

	
	public void go() throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		Connection conn = null;
		MyUser user = new MyUser();
		MyRequest request = new MyRequest();
		
//		SpecialOverrideType type = SpecialOverrideType.UNCOMPLETE_TICKET;
//		request.put("ticket_id","811790");
		SpecialOverrideType type = SpecialOverrideType.UPDATE_PAYMENT_AMOUNT;
		request.put("payment_id", "49907");
		request.put("payment_date", "2020-01-10");
		request.put("new_payment_date", "2020-01-09");
		
		int i = 1;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			//		String sql = type.getUpdateSql().replaceAll(" where ", ", updated_by=?, updated_date=SYSDATETIME() where ");
			String sql = type.getUpdateSql();
			int idx = sql.lastIndexOf("where");
			String part1 = sql.substring(0, idx);
			String part2 = sql.substring(idx);
			String fixed = part1 + ", updated_by=?, updated_date=SYSDATETIME() " + part2;
			logger.log(Level.DEBUG, fixed);


			// figure out how many parameters we need to skip before setting the bind variable
			// for the update user
			Pattern sqlPattern = Pattern.compile("^(update .*)( where )(.*)$", Pattern.CASE_INSENSITIVE);
			Pattern wherePattern = Pattern.compile("(.*=\\?)?", Pattern.CASE_INSENSITIVE);
			Matcher sqlMatcher = sqlPattern.matcher(fixed);

			if ( ! sqlMatcher.matches() ) {
				throw new RuntimeException("Something's wrong with the sql: " + fixed);
			}
			String whereClause = sqlMatcher.group(sqlMatcher.groupCount());
			logger.log(Level.DEBUG, "Where clause: " + whereClause);

//			Matcher whereMatcher = wherePattern.matcher(whereClause);
//			int whereParmCount = 0;
//			while ( whereMatcher.find()) {
//				whereParmCount++;
//			}
			int whereParmCount = 0;
			int whereIdx = whereClause.indexOf("=?");
			while ( whereIdx > 0 ) {
				whereParmCount++;
				System.out.println(whereIdx + "\t" + whereParmCount);
				whereIdx = whereClause.indexOf("=?", whereIdx+1);
			}
			
			
			int updateParmCount = type.getUpdateParms().length - whereParmCount;

			logger.log(Level.DEBUG, "Updates: " + updateParmCount + "\tWheres:" + whereParmCount);


			PreparedStatement ps = conn.prepareStatement(fixed);
			if ( updateParmCount == 0 ) {
				logger.log(Level.DEBUG, i + " : userId : " + user.getUserId());
				ps.setInt(i, user.getUserId());
				i++;
			}
			for(ParameterType p : type.getUpdateParms()) {
				logger.log(Level.DEBUG, i + " : " + p.getFieldName() + " : " + request.getParameter(p.getFieldName()));
				p.setPsParm(ps, request.getParameter(p.getFieldName()), i);
				if ( i == updateParmCount ) {
					i++;
					logger.log(Level.DEBUG, i + " : userId : " + user.getUserId());
					ps.setInt(i, user.getUserId());
				}
				i++;
			}

			ps.executeUpdate();
			//		conn.commit();
			conn.rollback();
		} catch ( Exception e ) {
			if ( conn != null ) {
				conn.rollback();
			}
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestSQLParse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyRequest extends HashMap<String, String> {

		private static final long serialVersionUID = 1L;

		public String getParameter(String parameterName) {
			return super.get(parameterName);
		}
	}
	
	public class MyUser {
		public Integer getUserId() { return 5; }
	}
}
