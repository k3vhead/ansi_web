package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.web.bcr.response.BudgetControlEmployeesResponse;
import com.ansi.scilla.web.bcr.response.BudgetControlTotalsResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketSpreadsheet extends AbstractBCRSpreadsheet {
	
	private static final long serialVersionUID = 1L;

	public BcrTicketSpreadsheet(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer claimYear, String workWeeks) 
			throws Exception {
		super();		
		List<BCRRow> data = makeData(conn, divisionList, divisionId, claimYear, workWeeks);
		String[] weekList = workWeeks.split(",");
		
		this.workbook = new XSSFWorkbook();
		makeStyles();
		initWorkbook();
		BCRRowPredicate filter = new BCRRowPredicate();				
		List<WorkWeek> workCalendar = makeWorkCalendar(claimYear, weekList);
		int tabNumber = 0;
		
		BudgetControlTotalsResponse bctr = new BudgetControlTotalsResponse(conn, userId, divisionList, divisionId, claimYear, workWeeks);
		makeActualDLTotalsTab(tabNumber, workCalendar, bctr);
		tabNumber++;
		
		makeBudgetControlTotalsTab(tabNumber, workCalendar, bctr);
		tabNumber++;
		
		BudgetControlEmployeesResponse employeeResponse = new BudgetControlEmployeesResponse(conn, userId, divisionList, divisionId, claimYear, workWeeks);
		makeBudgetControlEmployeesTab(tabNumber, claimYear, workCalendar, employeeResponse);
		tabNumber++;
		
		conn.close();

		makeTicketTab(data, tabNumber, "All Tickets");
		tabNumber++;
		for ( int i = 0; i < weekList.length; i++ ) {
			String tabName = claimYear + "-" + weekList[i];
			filter.setTabName(tabName);
			List<BCRRow> weeklyData = IterableUtils.toList(IterableUtils.filteredIterable(data, filter));
			makeTicketTab(weeklyData, tabNumber, tabName);
			tabNumber++;
		}
		
		
		makeUnclaimedEquipmentTab(data, tabNumber);
	}
	
	
	


	


	
	
	

	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
