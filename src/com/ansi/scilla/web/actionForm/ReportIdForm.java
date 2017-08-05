package com.ansi.scilla.web.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ansi.scilla.web.common.ReportType;
import com.thewebthing.commons.lang.StringUtils;

public class ReportIdForm extends IdForm {

	private static final long serialVersionUID = 1L;
	
	

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if ( ! isValidReport(getId())) {
			errors.add(ID, new ActionMessage("err.invalid.reportId"));
		}
		
		
		return errors;
	}
	
	
	private boolean isValidReport(String id) {
		boolean isValid = false;
		if ( StringUtils.isBlank(id) ) {
			isValid = false;
		} else {
			try {
				ReportType reportType = ReportType.valueOf(id);
				isValid = reportType != null;
			} catch (IllegalArgumentException e) {
				isValid = false;
			}
		}
		return isValid; 
	}


}
