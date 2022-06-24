package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;
import com.ansi.scilla.common.utils.compare.AnsiComparable;
import com.ansi.scilla.common.utils.compare.AnsiComparison;
import com.ansi.scilla.common.utils.compare.BooleanIshComparison;
import com.ansi.scilla.common.utils.compare.IntComparison;
import com.ansi.scilla.common.utils.compare.String2DateComparison;
import com.ansi.scilla.common.utils.compare.String2IntComparison;
import com.ansi.scilla.common.utils.compare.String2NumberComparison;
import com.ansi.scilla.common.utils.compare.StringComparison;

	
public	class EmployeeImportResponseRec extends EmployeeImportRecord implements AnsiComparable {
	private final Logger logger = LogManager.getLogger(EmployeeImportResponseRec.class);
	private static final long serialVersionUID = 1L;
	private String notes;
	private Boolean recordMatches;

	public EmployeeImportResponseRec() {
		super();
	}

	public EmployeeImportResponseRec(EmployeeImportRecord record) throws IllegalAccessException, InvocationTargetException {
		this();
		//			BeanUtils.copyProperties(this, record);
		this.employeeCode = record.getEmployeeCode();
		this.companyCode = record.getCompanyCode();
		this.divisionNbr = record.getDivisionNbr();

		this.firstName = record.getFirstName();
		this.lastName = record.getLastName();
		this.departmentDescription = record.getDepartmentDescription();
		this.status = record.getStatus();
		this.terminationDate = record.getTerminationDate();
		this.unionMember = record.getUnionMember();
		this.unionCode = record.getUnionCode();
		this.unionRate = record.getUnionRate();
		//			this.processDate = record.getProcessDate();
		this.recordStatus = record.getRecordStatus();
		this.fieldList = record.getFieldList();
		this.rowId = record.getRowId();
		this.divisionId = record.getDivisionId();
		this.div = record.getDiv();
		this.recordMatches = null;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getRecordMatches() {
		return recordMatches;
	}

	public void setRecordMatches(Boolean recordMatches) {
		this.recordMatches = recordMatches;
	}

	@Override
	public AnsiComparison[] makeFieldNameList(String className) throws Exception {
		if (className.equals( PayrollEmployee.class.getName() )) {
			return new AnsiComparison[] {
				new AnsiComparison("firstName", "employeeFirstName", new StringComparison(false)),	
				new AnsiComparison("lastName", "employeeLastName", new StringComparison(false)),
				new AnsiComparison("status", "employeeStatus", new StringComparison(false)),
				new AnsiComparison("unionMember", "unionMember", new BooleanIshComparison()),


				new AnsiComparison("employeeCode","employeeCode", new String2IntComparison()),
				new AnsiComparison("companyCode","companyCode", new StringComparison(false)),
				new AnsiComparison("divisionNbr","division", new StringComparison(false)),
				new AnsiComparison("departmentDescription","deptDescription", new StringComparison(false)),
				new AnsiComparison("unionCode","unionCode", new StringComparison(false)),
				new AnsiComparison("divisionId","divisionId", new IntComparison()),
				new AnsiComparison("notes","notes", new StringComparison(false)),
				new AnsiComparison("terminationDate","employeeTerminationDate", new String2DateComparison(EmployeeImportRecord.EMPLOYEE_RECORD_DATE_FORMAT)),
				new AnsiComparison("unionRate","unionRate", new String2NumberComparison()),

				//new AnsiComparison("recordStatus","", new StringComparison(false)),
				//new AnsiComparison("fieldList","", new StringComparison(false)),
				//new AnsiComparison("rowId","", new StringComparison(false)),
				//new AnsiComparison("div","", new StringComparison(false)),
				
			};
		} else {
			throw new InvalidValueException("Don't know how to compare to " + className);
		}
	}




	


}
	
