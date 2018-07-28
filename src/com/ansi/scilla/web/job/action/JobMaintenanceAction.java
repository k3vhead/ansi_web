package com.ansi.scilla.web.job.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.utils.AppUtils;

public class JobMaintenanceAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		IdForm form = (IdForm)actionForm;

		Connection conn = null;
		if ( form != null && ! StringUtils.isBlank(form.getId())) {
			try {
				conn = AppUtils.getDBCPConn();
				Job job = new Job();
				Integer jobid = Integer.valueOf(form.getId());
				job.setJobId(jobid);
				job.selectOne(conn);

				request.setAttribute("ANSI_QUOTE_ID", job.getQuoteId());
				request.setAttribute("ANSI_JOB_ID", form.getId());
			} finally {
				conn.close();
			}

		}
		return super.execute(mapping, actionForm, request, response);
	}

}
