package com.ansi.scilla.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;

public class NotLoggedIn extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean condition() throws JspTagException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();
		boolean loggedIn = session.getAttribute(SessionData.KEY) == null;
		return loggedIn;
	}



}
