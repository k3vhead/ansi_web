package com.ansi.scilla.web.payroll.common;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.payroll.validator.worksheet.ValidatedWorksheetEmployee;
import com.ansi.scilla.web.payroll.response.TimesheetEmployee;

/**
 * Converts a ValidatedWorksheetEmployee (from the ansi.common.payroll.validator) to a
 * TimesheetEmployee (suitable for use in a servlet response)
 * 
 * @author dclewis
 *
 */
public class EmployeeTransformer implements Transformer<ValidatedWorksheetEmployee, TimesheetEmployee> {
	@Override
	public TimesheetEmployee transform(ValidatedWorksheetEmployee arg0) {
		return new TimesheetEmployee(arg0);
	}

}
