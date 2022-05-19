package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.PayrollEmployee;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;
import com.ansi.scilla.common.utils.compare.AnsiComparable;
import com.ansi.scilla.common.utils.compare.AnsiComparator;
import com.ansi.scilla.common.utils.compare.AnsiComparison;
import com.ansi.scilla.common.utils.compare.StringComparison;

	
public	class EmployeeImportResponseRec extends EmployeeImportRecord implements AnsiComparable {
	private final Logger logger = LogManager.getLogger(EmployeeImportResponseRec.class);
	private static final long serialVersionUID = 1L;
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
			this.processDate = record.getProcessDate();
			this.recordStatus = record.getRecordStatus();
			this.fieldList = record.getFieldList();
			this.rowId = record.getRowId();
			this.divisionId = record.getDivisionId();
			this.div = record.getDiv();
			logger.log(Level.DEBUG, record.getEmployeeCode() + "\t" + record.getRowId() + "\t" + this.getRowId());
			this.recordMatches = null;
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


						/*
						private String companyCode;
						private String divisionNbr;

						private String departmentDescription;
						private String ;
						private String terminationDate;
						private String ;
						private String unionCode;
						private String unionRate;
						private String processDate;
						private String recordStatus;
						private List<String> fieldList = new ArrayList<String>();
						private String rowId;
						// We are figuring out the div and divisionId
						private Integer divisionId;
						private String div;









						private String companyCode;
						private String deptDescription;
						private String division;
						private Integer divisionId;
						private Integer employeeCode;
						private Date employeeTerminationDate;
						private String notes;
						private String unionCode;
						private BigDecimal unionRate;
						private Date processDate;
						 */
				};
			} else {
				throw new InvalidValueException("Don't know how to compare to " + className);
			}
		}
		
		
		
		
		public class BooleanIshComparison implements AnsiComparator {

			@Override
			public boolean fieldsAreEqual(Object obj1, Object obj2) throws Exception {
				Boolean value1 = null;
				if ( obj1 instanceof String ) {
					value1 = string2boolean( (String)obj1 );
				} else if ( obj1 instanceof Integer ) {
					value1 = int2boolean( (Integer)obj1 );
				} else if ( obj1 instanceof Boolean) {
					value1 = (Boolean)obj1;
				} else {
					throw new InvalidValueException("Unexpected Type: " + obj1.getClass().getName());
				}
				
				Boolean value2 = null;
				if ( obj2 instanceof String ) {
					value2 = string2boolean( (String)obj2 );
				} else if ( obj2 instanceof Integer ) {
					value2 = int2boolean( (Integer)obj2 );
				} else if ( obj1 instanceof Boolean) {
					value2 = (Boolean)obj2;
				} else {
					throw new InvalidValueException("Unexpected Type: " + obj2.getClass().getName());
				}
				
				return value1.equals(value2);
			}
			
			private boolean string2boolean(String value) {
				boolean b = false;
				if ( ! StringUtils.isBlank(value) ) {
					b = value.equals("1") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
				}
				return b;
			}
			private boolean int2boolean(Integer value) {
				boolean b = false;
				if ( value != null ) {
					b = value.intValue() == 1;
				}
				return b;
			}
		}
		
		
	}
	
