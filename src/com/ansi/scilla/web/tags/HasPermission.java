package com.ansi.scilla.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.struts.SessionData;

public class HasPermission extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String permissionRequired;

	public String getPermissionRequired() {
		return permissionRequired;
	}
	public void setPermissionRequired(String permissionRequired) {
		this.permissionRequired = permissionRequired;
	}



	@Override
	protected boolean condition() throws JspTagException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();

        boolean hasPermission = false;
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData != null ) {
			if ( sessionData.getUser().getSuperUser().equals(new Integer(1)) || sessionData.hasPermission(permissionRequired) ) {
				hasPermission = true;
			}
		}
		
		return hasPermission;
	}

}
