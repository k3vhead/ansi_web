package com.ansi.scilla.web.job.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.actionForm.AbstractActionForm;

public class JobLookupForm extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	public static final String TYPE = "type";
	
	private String type;

	

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, this.type);
		if ( StringUtils.isBlank(type)) {
			type = JobLookupType.JOB.toString();
		} else {
			try {
				JobLookupType.valueOf(type);
			} catch ( NullPointerException e) {
				type = JobLookupType.JOB.toString();
			} catch ( IllegalArgumentException e) {
				type = JobLookupType.JOB.toString();
			}
		}
		return super.validate(mapping, request);
	}

	
	public enum JobLookupType {
		JOB,
		PAC,
		CONTACT
	}

	
	

}
