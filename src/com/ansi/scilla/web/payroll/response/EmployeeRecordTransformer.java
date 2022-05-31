package com.ansi.scilla.web.payroll.response;

import java.util.HashMap;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;

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
//		Logger logger = LogManager.getLogger(EmployeeRecordTransformer.class);
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
				rec.setRecordMatches( rec.ansiEquals(employee) );
			} else {
				rec.setRecordMatches( false );
			}
			return rec;
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
