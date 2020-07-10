package com.ansi.scilla.web.report.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.organization.Div;

public class SubscriptionUtils {

	private static final Logger logger = LogManager.getLogger(SubscriptionUtils.class);
	
	
	public static List<Div> makeDivisionList(Connection conn, Integer userId) throws SQLException {
		List<Div> divisionList = new ArrayList<Div>();
		String sql = "select division.*\n" + 
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

	
	
	
}
