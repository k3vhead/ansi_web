package com.ansi.scilla.web.payroll.response;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.payroll.parser.EmployeeImportRecord;

	
public	class EmployeeImportResponseRec extends EmployeeImportRecord{
	
	private static final long serialVersionUID = 1L;
		private Boolean recordMatches;
		
		public EmployeeImportResponseRec(EmployeeImportRecord record) {
			super();
			try {
				BeanUtils.copyProperties(this, record);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.recordMatches = null;
		}

		public Boolean getRecordMatches() {
			return recordMatches;
		}

		public void setRecordMatches(Boolean recordMatches) {
			this.recordMatches = recordMatches;
		}
		
	}
	
