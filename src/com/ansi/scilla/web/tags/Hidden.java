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

public class Hidden extends AbstractInput {

	private static final long serialVersionUID = 1L;
	
	
	private String form;
	private String formaction;
	private String formmethod;
	private String formnovalidate;
	private String formtarget;
	private String max;
	private String maxlength;		
	private String min;
	private String minlength;
	private String pattern;
	private String required;
	private String size;
	private String value;
	
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
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMaxlength() {
		return maxlength;
	}
	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMinlength() {
		return minlength;
	}
	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
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
				String tagString = makeTagString("hidden", canEdit);
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
		pieces.add("/>");
		return StringUtils.join(pieces, " ");
	}
	
}
