package com.ansi.scilla.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;

public class IsSuperUser extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected boolean condition() throws JspTagException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();

        boolean hasPermission = false;
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData != null ) {
			hasPermission =  sessionData.getUser().getSuperUser().equals(Integer.valueOf(1));				
		}
		
		return hasPermission;
	}

	
}
