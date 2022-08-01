package com.ansi.scilla.web.test.payroll;

import java.io.File;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportParser;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportRecord;
import com.ansi.scilla.common.utils.AppUtils;

@Deprecated
/**
 * This is a first effort -- probably doesn't work properly because of prod code changes that haven't propagated to 
 * this module. Update if needed.
 * @author dclewis
 *
 */
public class LoadPayrollEmployees extends Loader {

	
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	private final DecimalFormat df = new DecimalFormat("$##,###.00");
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			makeDivMap(conn);			
			Date now = Calendar.getInstance(new AnsiTime()).getTime();
			
			File file = new File("/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20211211_payroll/payroll_employee/PAYCOM Import Template-NEW-GEN (1).csv");
			EmployeeImportParser parser = new EmployeeImportParser(conn, file);

			for ( EmployeeImportRecord rec : parser.getEmployeeRecords() ) {
				try {
					PayrollEmployee emp = new PayrollEmployee();
					String status = EmployeeStatus.lookup(rec.getStatus()).name();
					Division div = divMap.get(Integer.valueOf(rec.getDivisionId()));
					
					emp.setAddedBy(5);
					emp.setAddedDate(now);				
					emp.setCompanyCode(rec.getCompanyCode());
					emp.setDeptDescription(rec.getDepartmentDescription());
//					emp.setDivision(rec.getDivisionId());
					emp.setDivisionId(div.getDivisionId());
					emp.setEmployeeCode(Integer.valueOf(rec.getEmployeeCode()));
					emp.setEmployeeFirstName(rec.getFirstName());
					emp.setEmployeeLastName(rec.getLastName());
					emp.setEmployeeTerminationDate( StringUtils.isBlank(rec.getTerminationDate()) ? null : sdf.parse(rec.getTerminationDate()));
					emp.setEmployeeStatus(status);
//					emp.setProcessDate(sdf.parse(rec.getProcessDate()));
					emp.setUnionCode(StringUtils.isBlank(rec.getUnionCode()) ? null : rec.getUnionCode() );
					emp.setUnionMember( StringUtils.isBlank(rec.getUnionMember()) ? 0 : 1);
					emp.setUnionRate( StringUtils.isBlank(rec.getUnionRate()) ? null : makeBD(rec.getUnionRate()));
					emp.setUpdatedDate(now);
					emp.setUpdatedBy(5);
					System.out.println(emp);
					emp.insertWithNoKey(conn);
				} catch ( Exception e) {
					System.err.println("Err on line: " + rec.getEmployeeCode() + "\t" + rec.getLastName());
					throw e;
				}
			}
			
			
			conn.commit();
		}
		catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	

	public static void main(String[] args) {
		try {
			new LoadPayrollEmployees().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
}
