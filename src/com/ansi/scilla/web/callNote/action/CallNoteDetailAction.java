package com.ansi.scilla.web.callNote.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.callNote.response.CallNoteDetailResponse;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.utils.AppUtils;

public class CallNoteDetailAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		IdForm form = (IdForm)actionForm;

		if ( form != null && ! StringUtils.isBlank(form.getId())) {
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				CallNoteDetailResponse data = new CallNoteDetailResponse(conn, Integer.valueOf(form.getId()));
				request.setAttribute("ANSI_CALL_NOTE_DETAIL", data);
			} finally {
				conn.close();
			}
			
		}
		return super.execute(mapping, actionForm, request, response);
	}

}
