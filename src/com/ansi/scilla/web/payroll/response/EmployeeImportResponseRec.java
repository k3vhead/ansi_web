package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;
import com.ansi.scilla.common.utils.AnsiComparable;

	
public	class EmployeeImportResponseRec extends EmployeeImportRecord implements AnsiComparable {
	
	private static final long serialVersionUID = 1L;
		private Boolean recordMatches;
		
		public EmployeeImportResponseRec(EmployeeImportRecord record) throws IllegalAccessException, InvocationTargetException {
			super();
			BeanUtils.copyProperties(this, record);
			this.recordMatches = null;
		}

		public Boolean getRecordMatches() {
			return recordMatches;
		}

		public void setRecordMatches(Boolean recordMatches) {
			this.recordMatches = recordMatches;
		}

		@Override
		public String[] makeFieldNameList(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
