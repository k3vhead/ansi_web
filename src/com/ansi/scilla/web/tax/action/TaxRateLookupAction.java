package com.ansi.scilla.web.tax.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.web.common.action.SessionPageDisplayAction;
import com.ansi.scilla.web.common.actionForm.IdForm;
import com.ansi.scilla.web.common.utils.AppUtils;

public class TaxRateLookupAction extends SessionPageDisplayAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		IdForm form = (IdForm)actionForm;
		if ( form != null ) {
			if ( ! StringUtils.isBlank(form.getId())) {				
				request.setAttribute("ANSI_LOCALE_ID", form.getId());
				request.setAttribute("ANSI_LOCALE_DISPLAY", makeLocaleDisplay(form.getId()));
			}			
		}
		return super.execute(mapping, actionForm, request, response);
	}

	private String makeLocaleDisplay(String localeId) throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			Locale locale = new Locale();
			locale.setLocaleId(Integer.valueOf(localeId));
			locale.selectOne(conn);
			return locale.getName() + ", " + locale.getStateName();
		} finally {
			conn.close();
		}
		
	}

}
