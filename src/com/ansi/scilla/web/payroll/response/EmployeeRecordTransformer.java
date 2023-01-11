package com.ansi.scilla.web.payroll.response;

import java.util.HashMap;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.payroll.parser.employee.EmployeeImportRecord;

public class EmployeeRecordTransformer implements Transformer<EmployeeImportRecord, EmployeeImportResponseRec> {

	HashMap<Integer, PayrollEmployee> employeeMap;
	private HashMap<Integer, Division> divMap;
	private HashMap<String, EmployeeStatus> employeeStatusMap;
	
	public EmployeeRecordTransformer(HashMap<Integer, PayrollEmployee> employeeMap,
			HashMap<Integer, Division> divMap, HashMap<String, EmployeeStatus> employeeStatusMap) {
		super();
		this.employeeMap = employeeMap;
		this.divMap = divMap;
		this.employeeStatusMap = employeeStatusMap;
	}



	@Override
	public EmployeeImportResponseRec transform(EmployeeImportRecord arg0) {
		Logger logger = LogManager.getLogger(EmployeeRecordTransformer.class);
		try {
			EmployeeImportResponseRec rec = new EmployeeImportResponseRec(arg0);
			if ( StringUtils.isNumeric(arg0.getDivisionNbr())) {
				if ( divMap.containsKey(Integer.valueOf(arg0.getDivisionNbr()))) {
					Division division= divMap.get(Integer.valueOf(arg0.getDivisionNbr()));
					rec.setDivisionId(division.getDivisionId());
					rec.setDiv(division.getDivisionNbr() + "-" + division.getDivisionCode());
					
				}
				
			}
			
			if ( employeeStatusMap.containsKey(arg0.getStatus())) {
				EmployeeStatus employeeStatus= employeeStatusMap.get(arg0.getStatus());
				rec.setStatus(employeeStatus.name());
				
			}
			
			
			
			Integer employeeCode = Integer.valueOf( arg0.getEmployeeCode() );
			if ( employeeMap.containsKey(employeeCode) ) {
				PayrollEmployee employee = employeeMap.get(employeeCode);
				rec.setFieldList( rec.makeFieldList(employee) );
				rec.setNewEmployee(false);
				rec.setRecordMatches( rec.getFieldList() == null || rec.getFieldList().size() == 0 );
			} else {
				rec.setRecordMatches( false );
				rec.setNewEmployee(true);
			}
			return rec;
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
