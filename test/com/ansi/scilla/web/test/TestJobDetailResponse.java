package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.job.response.JobDetail;

public class TestJobDetailResponse {

	public static void main(String[] args) {
		try {
			new TestJobDetailResponse().go();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Date today = new Date();
			
//			JobDetailResponse jobDetailResponse = new JobDetailResponse(conn, 200193);
//			System.out.println(jobDetailResponse);
//			System.out.println(jobDetailResponse.toJson());
			
			Job job = new Job();
			job.setJobId(200193);
			job.selectOne(conn);
			User user = new User();
			user.setUserId(5);
			user.selectOne(conn);
			JobDetail detail = new JobDetail(job, user, user);
//			Date d = new Date();
//			d.setTime(job.getExpirationDate().getTime());
//			detail.setExpirationDate(d);
			System.out.println(detail.toJson());
			
			
			
			
			conn.rollback();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

}
