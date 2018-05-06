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

public class Button extends AbstractInput {

	private static final long serialVersionUID = 1L;
	
	
	private String autocomplete;		
	private String autofocus;
	private String disabled;
	private String form;
	private String formaction;
	private String formenctype;
	private String formmethod;
	private String formnovalidate;
	private String formtarget;
	private String value;
	
	public String getAutocomplete() {
		return autocomplete;
	}
	public void setAutocomplete(String autocomplete) {
		this.autocomplete = autocomplete;
	}
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
	public String getFormaction() {
		return formaction;
	}
	public void setFormaction(String formaction) {
		this.formaction = formaction;
	}
	public String getFormenctype() {
		return formenctype;
	}
	public void setFormenctype(String formenctype) {
		this.formenctype = formenctype;
	}
	public String getFormmethod() {
		return formmethod;
	}
	public void setFormmethod(String formmethod) {
		this.formmethod = formmethod;
	}
	public String getFormnovalidate() {
		return formnovalidate;
	}
	public void setFormnovalidate(String formnovalidate) {
		this.formnovalidate = formnovalidate;
	}
	public String getFormtarget() {
		return formtarget;
	}
	public void setFormtarget(String formtarget) {
		this.formtarget = formtarget;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
		
		boolean displayButton = true;
		if ( (! StringUtils.isBlank(this.readonly)) && this.readonly.equalsIgnoreCase("true")) { displayButton = false; }
		if ( ! this.action.equalsIgnoreCase(ACTION_IS_EDIT)) { displayButton = false; }

		if ( canEdit && displayButton ) {
		
			try {
				String tagString = makeTagString("button", canEdit);
	//			System.out.println(bc.getString());
				out.write(tagString);
			} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new JspException(e);
			}
			
		}
//		return EVAL_BODY_BUFFERED;
		return SKIP_BODY;
	}

	
	protected String makeTagString(String inputType, boolean canEdit) throws IllegalArgumentException, IllegalAccessException {
		List<String> pieces = new ArrayList<String>();
		pieces.add("<input");
		pieces.add("type=\"" + inputType + "\"");
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
			System.out.println(field.getName());
			if ( ! Modifier.isFinal(field.getModifiers())) {
				System.out.println("Abstract 113: " + field.getName());
				String fieldName = field.getName().equalsIgnoreCase("styleClass") ? "class" : field.getName();
				Object value = field.get(this);
				if ( ! fieldName.equalsIgnoreCase("action")) {					
					if ( value != null ) {
						pieces.add(fieldName + "=\"" + String.valueOf(value) + "\"");
					}
				}
				
			}
		}
		pieces.add("/>");
		return StringUtils.join(pieces, " ");
	}
	
}
