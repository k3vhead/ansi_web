package com.ansi.scilla.web.tags;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.struts.SessionData;

public class Required extends AbstractInput {

	private static final long serialVersionUID = 1L;
	
	
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
		
		boolean displayButton = true;
		if ( (! StringUtils.isBlank(this.readonly)) && this.readonly.equalsIgnoreCase("true")) { displayButton = false; }
		if ( ! this.action.equalsIgnoreCase(ACTION_IS_EDIT)) { displayButton = false; }

		if ( canEdit && displayButton ) {
		
			try {
				String tagString = makeTagString(canEdit);
	//			System.out.println(bc.getString());
				out.write(tagString);
			} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
				throw new JspException(e);
			}
			
		}
//		return EVAL_BODY_BUFFERED;
		return SKIP_BODY;
	}

	
	protected String makeTagString(boolean canEdit) throws IllegalArgumentException, IllegalAccessException {
		//"<span class=\"required\">*</span>";
		List<String> pieces = new ArrayList<String>();
		super.setStyleClass( StringUtils.isBlank(super.getStyleClass()) ? "required" : "required " + super.getStyleClass());
		pieces.add("<span");
		List<Field> fieldList = new ArrayList<Field>();
		Field[] textList = this.getClass().getDeclaredFields();
		Field[] abstractList = AbstractInput.class.getDeclaredFields();
		for ( Field field : textList ) {
			fieldList.add(field);
		}
		for ( Field field : abstractList ) {
			fieldList.add(field);
		}

		for ( Field field : fieldList ) {
			if ( ! Modifier.isFinal(field.getModifiers())) {
				String fieldName = field.getName().equalsIgnoreCase("styleClass") ? "class" : field.getName();
				Object value = field.get(this);
				if ( ! fieldName.equalsIgnoreCase("action")) {					
					if ( value != null ) {
						pieces.add(fieldName + "=\"" + String.valueOf(value) + "\"");
					}
				}
				
			}
		}
		pieces.add(">");
		return StringUtils.join(pieces, " ") + "*</span>";
	}
	
}
