package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse.EmployeeClaim;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestEmployeeResponse extends AbstractBcrTest {

	private final Integer userId = 5;
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			
			BudgetControlEmployeesResponse response = new BudgetControlEmployeesResponse(conn, userId, divisionList, div_12il02, 2020, workWeekOctober2020);

//			System.out.println(response);
//			String json = AppUtils.object2json(response);
//			System.out.println(json);

			System.out.println("Claim Year: " + response.getClaimYear());
			for ( String x : response.getClaimWeeks() ) {
				System.out.println("Claim Week: " + x);
			}
			System.out.println("********************************");
			for ( EmployeeClaim x : response.getEmployees() ) {
				System.out.println("Employee: " + x.getEmployee());
				Collection<Double> claimedDl = x.getWeeklyClaimedDL().values();
				System.out.println("ClaimedDL:\t" + StringUtils.join(claimedDl, "+") + "=" + x.getTotalClaimedDL());
				Collection<Double> claimedVol = x.getWeeklyClaimedVolume().values();
				System.out.println("ClaimedVol:\t" + StringUtils.join(claimedVol, "+") + "=" + x.getTotalClaimedVolume());
				System.out.println("********************************");
			}
			
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			new TestEmployeeResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
