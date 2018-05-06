package com.ansi.scilla.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import com.ansi.scilla.web.common.struts.SessionData;

public class Password extends Text {

	private static final long serialVersionUID = 1L;
	protected final String ACTION_IS_EDIT = "edit";
	protected final String ACTION_IS_VIEW = "view";
	
	
	
	
	
	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();
        SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

//        BodyContent bc = getBodyContent();
		JspWriter out = pageContext.getOut();  //getPreviousOut();
		
		HasPermission hasPermission = (HasPermission)super.getParent();		
		if ( hasPermission == null ) {
			throw new JspTagException("must be nested in hasPermission tag");
		}		
		String permissionRequired = hasPermission.getPermissionRequired();
		boolean canEdit = canEdit(sessionData, permissionRequired);
		
		
		
		try {
			String tagString = makeTagString("password", canEdit);
//			System.out.println(bc.getString());
			out.write(tagString);
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new JspException(e);
		}
//		return EVAL_BODY_BUFFERED;
		return SKIP_BODY;
	}

	
	
	
}
