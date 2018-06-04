package com.ansi.scilla.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.UserPermission;

public class HasWrite extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean condition() throws JspTagException {
		boolean canWrite = false;
		HasPermission hasPermission = (HasPermission)super.getParent();
		
		if ( hasPermission == null ) {
			throw new JspTagException("must be nested in hasPermission tag");
		}
		
		String permissionRequired = hasPermission.getPermissionRequired();
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();

		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData != null ) {
			if ( sessionData.getUser().getSuperUser().equals(new Integer(1)) ) {
				canWrite = true;
			} else {
				String writePermission = permissionRequired + "_WRITE";
				for ( UserPermission userPermission : sessionData.getUserPermissionList() ) {
					if ( userPermission.getPermissionName().equalsIgnoreCase(writePermission)) {
						canWrite = true;
					}
//					if ( userPermission.getPermissionName().equalsIgnoreCase(permissionRequired)) {
//						if ( userPermission.getLevel().equals(new Integer(1))) {
//							canWrite = true;
//						}
//					}
				}
			}
		}

		return canWrite;
	}

}
