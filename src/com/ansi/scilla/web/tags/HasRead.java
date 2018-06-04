package com.ansi.scilla.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.UserPermission;

/**
 * Returns true (body of tag is displayed) if user has "READ" permission, but no "WRITE"
 * 
 * @author dclewis
 *
 */
public class HasRead extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean condition() throws JspTagException {
//		boolean readOnly = false;
		HasPermission hasPermission = (HasPermission)super.getParent();
		
		if ( hasPermission == null ) {
			throw new JspTagException("must be nested in hasPermission tag");
		}
		
		String permissionRequired = hasPermission.getPermissionRequired();
		String readPermission = permissionRequired + "_READ";
		String writePermission = permissionRequired + "_WRITE";
		boolean foundRead = false;
		boolean foundWrite = false;

		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();

		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData != null ) {
			for ( UserPermission userPermission : sessionData.getUserPermissionList() ) {
				if ( userPermission.getPermissionName().equalsIgnoreCase(readPermission) ) {
					foundRead = true;
				}
				if ( userPermission.getPermissionName().equalsIgnoreCase(writePermission) ) {
					foundWrite = true;
				}
//				if ( userPermission.getPermissionName().equalsIgnoreCase(permissionRequired)) {
//					if ( userPermission.getLevel().equals(new Integer(0))) {
//						readOnly = true;
//					}
//				}
			}
		}

		return foundRead == true && foundWrite == false;
	}

}
