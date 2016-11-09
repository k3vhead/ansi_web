package com.ansi.scilla.web.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.common.AppUtils;

public class DashboardAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Logger logger = AppUtils.getLogger();
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			logger.debug("Got a connection");
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw e;
		} finally {
			logger.debug("Closing conn");
			if ( conn != null ) {
				conn.close();
			}
		}
		return mapping.findForward(FORWARD_IS_VALID);
	}

}
