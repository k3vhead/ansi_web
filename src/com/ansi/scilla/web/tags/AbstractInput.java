package com.ansi.scilla.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.UserPermission;

public abstract class AbstractInput extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	protected final String ACTION_IS_EDIT = "edit";
	protected final String ACTION_IS_VIEW = "view";
	
	
	protected String action;
	protected String name;
	protected String style;
	protected String styleClass;
	protected String id;
	protected String readonly;

	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReadonly() {
		return readonly;
	}
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	@Override
	public abstract int doStartTag() throws JspException;
	
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
	
	
	
	
	protected boolean canEdit(SessionData sessionData, String permissionRequired) throws JspTagException {
		boolean canWrite = false;
		if ( sessionData != null ) {
			if ( sessionData.getUser().getSuperUser().equals(Integer.valueOf(1)) ) {
				canWrite = true;
			} else {
				for ( UserPermission userPermission : sessionData.getUserPermissionList() ) {
					if ( userPermission.getPermissionName().equalsIgnoreCase(permissionRequired)) {
						if ( userPermission.getLevel().equals(Integer.valueOf(1))) {
							canWrite = true;
						}
					}
				}
			}
		}
	
		return canWrite;
	}
	
	
}
