package com.ansi.scilla.web.job.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.job.actionForm.JobLookupForm;

public class JobLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		JobLookupForm form = (JobLookupForm)actionForm;
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, actionForm);

		if ( form != null && ! StringUtils.isBlank(form.getType())) {
			logger.log(Level.DEBUG, "Setting attr");
			request.setAttribute("ANSI_JOB_LOOKUP_TYPE", form.getType());
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
