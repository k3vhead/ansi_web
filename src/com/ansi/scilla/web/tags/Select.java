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

public class Select extends AbstractInput {

	private static final long serialVersionUID = 1L;	
	
	
	private String autofocus;
	private String disabled;
	private String form;
	private String multiple;
	private String required;
	private String size;

	public String getAutofocus() {
		return autofocus;
	}
	public void setAutofocus(String autofocus) {
		this.autofocus = autofocus;
	}
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}


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
			String tagString = makeTagString("text", canEdit);
//			System.out.println(bc.getString());
			out.write(tagString);
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new JspException(e);
		}
		return EVAL_BODY_INCLUDE;
//		return SKIP_BODY;
	}

	
	@Override
	public int doAfterBody() throws JspException {
		//return EVAL_BODY_BUFFERED;
		return SKIP_BODY;
	}
	
	
	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut(); //= getPreviousOut();
		try {
			out.write("</select>");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
	
	
	protected String makeTagString(String inputType, boolean canEdit) throws IllegalArgumentException, IllegalAccessException {
		List<String> pieces = new ArrayList<String>();
		pieces.add("<select");
		List<Field> fieldList = new ArrayList<Field>();
		Field[] textList = Select.class.getDeclaredFields();
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
				if ( fieldName.equalsIgnoreCase("disabled")) {
					// if we want input to be readonly, make it readonly.
					// if we want input to be editable, make it readonly based on permission and action
					if ( String.valueOf(value).equalsIgnoreCase("true")) {
						pieces.add("disabled=\"true\"");
					} else {
						if ( this.action.equals(ACTION_IS_EDIT) && canEdit ) {
							// do nothing
						} else {
							pieces.add("disabled=\"true\"");
						}
					}
				} else if ( ! fieldName.equalsIgnoreCase("action")) {					
					if ( value != null ) {
						pieces.add(fieldName + "=\"" + String.valueOf(value) + "\"");
					}
				}
				
			}
		}
		pieces.add(">");
		return StringUtils.join(pieces, " ");
	}
	
}
