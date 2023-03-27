package com.ansi.scilla.web.test.job;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.job.response.JobDetailResponse;

public class TestJobDetailResponse {

	public static void main(String[] args) {
		try {
			new TestJobDetailResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void go() throws Exception {
		Connection conn = null;
		try {
//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			String dbURL = "jdbc:sqlserver://192.168.10.252:1433;databaseName=ansi_sched;integratedSecurity=false;";
//			String dbID = "ansi_sched.webapp";
//			String dbPass = "@dataflex1997";
//			conn =  DriverManager.getConnection(dbURL, dbID, dbPass);
			
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Date today = new Date();
			
			List<UserPermission> permissionList = Arrays.asList(new UserPermission[] {
				new UserPermission("QUOTE_READ",1)
			});
			JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, 211470, permissionList);
			System.out.println(jobDetailResponse);
			System.out.println(jobDetailResponse.toJson());
			
//			Job job = new Job();
//			job.setJobId(211473);
//			job.selectOne(conn);
//			User user = new User();
//			user.setUserId(5);
//			user.selectOne(conn);
//			JobDetail detail = new JobDetail(job, user, user);
//			Date d = new Date();
//			d.setTime(job.getExpirationDate().getTime());
//			detail.setExpirationDate(d);
//			System.out.println(detail.toJson());
			
			
			
			
			conn.rollback();
		} catch ( Exception e) {
			if ( conn != null ) {
				conn.rollback();
			}
			throw e;
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}

}
