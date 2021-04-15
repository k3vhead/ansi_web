package com.ansi.scilla.web.tags;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.Permission;

public class HasPermission extends ConditionalTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String permissionRequired;
	private String maxLevel;

	public String getPermissionRequired() {
		return permissionRequired;
	}
	public void setPermissionRequired(String permissionRequired) {
		this.permissionRequired = permissionRequired;
	}
	public String getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(String maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	@Override
	protected boolean condition() throws JspTagException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();

        boolean hasPermission = false;
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData != null ) {
			if ( sessionData.getUser().getSuperUser().equals(Integer.valueOf(1)) ) {
				hasPermission = true;
			} else {
				hasPermission = hasPermission(sessionData) && hasLevel(sessionData);
			}
		}
		
		return hasPermission;
	}

	private boolean hasPermission(SessionData sessionData) {
		 return sessionData.hasPermission(permissionRequired);
	}
	
	private boolean hasLevel(SessionData sessionData) {
		boolean hasLevel = false;
		if ( needsLevelCheck() ) {
			boolean hasChild = false;
			Permission permission = Permission.valueOf(permissionRequired);
			for ( Permission p : permission.makeChildList() ) {
				if ( sessionData.hasPermission(p.name())) {
					hasChild = true;
				}
			}
			// if session contains child permission, then hasLevel is false; this is not the max level of permissions for this user
			hasLevel = hasChild ? false : true;
		} else {
			hasLevel = true;
		}

		return hasLevel;
	}
	
	private boolean needsLevelCheck() {
		List<String> isTrue = Arrays.asList(new String[] {"true","y","1","yes"});
		return this.maxLevel != null && isTrue.contains(this.maxLevel.toLowerCase());
	}
}
