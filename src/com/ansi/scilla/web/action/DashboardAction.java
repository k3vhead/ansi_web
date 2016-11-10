package com.ansi.scilla.web.action;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ansi.scilla.web.struts.SessionData;

public class DashboardAction extends AbstractAction {

	public static final String FORWARD_IS_LOGIN = "login";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
//		Logger logger = AppUtils.getLogger();
//		Connection conn = null;
//		try {
//			conn = AppUtils.getDBCPConn();
//			conn.setAutoCommit(false);
//			logger.debug("Got a connection");
//		} catch ( Exception e) {
//			AppUtils.logException(e);
//			throw e;
//		} finally {
//			logger.debug("Closing conn");
//			if ( conn != null ) {
//				conn.close();
//			}
//		}
//		return mapping.findForward(FORWARD_IS_VALID);
		ActionForward forward = mapping.findForward(FORWARD_IS_LOGIN);
		HttpSession session = request.getSession();
		if ( session != null ) {
			SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
			if ( sessionData != null ) {
				forward = mapping.findForward(FORWARD_IS_VALID);
			}
		}
		return forward;
		
	}

}
