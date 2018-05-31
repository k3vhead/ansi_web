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

public class Text extends AbstractInput {

	private static final long serialVersionUID = 1L;	
	
	
	private String autocomplete;		
	private String autofocus;
	private String capture;
	private String checked;
	private String disabled;
	private String form;
	private String formaction;
	private String formmethod;
	private String formnovalidate;
	private String formtarget;
	private String height;
	private String inputmode;
	private String list;
	private String max;
	private String maxlength;		
	private String min;
	private String minlength;
	private String multiple;
	private String pattern;
	private String placeholder;
	private String required;
	private String selectionDirection;
	private String selectionEnd;
	private String selectionStart;		
	private String size;
	private String spellcheck;
	private String src;
	private String step;
	private String tabindex;
	private String value;
	private String width;

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
	public String getCapture() {
		return capture;
	}
	public void setCapture(String capture) {
		this.capture = capture;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
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
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getInputmode() {
		return inputmode;
	}
	public void setInputmode(String inputmode) {
		this.inputmode = inputmode;
	}
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
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
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public String getSelectionDirection() {
		return selectionDirection;
	}
	public void setSelectionDirection(String selectionDirection) {
		this.selectionDirection = selectionDirection;
	}
	public String getSelectionEnd() {
		return selectionEnd;
	}
	public void setSelectionEnd(String selectionEnd) {
		this.selectionEnd = selectionEnd;
	}
	public String getSelectionStart() {
		return selectionStart;
	}
	public void setSelectionStart(String selectionStart) {
		this.selectionStart = selectionStart;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getSpellcheck() {
		return spellcheck;
	}
	public void setSpellcheck(String spellcheck) {
		this.spellcheck = spellcheck;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getTabindex() {
		return tabindex;
	}
	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
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
//		return EVAL_BODY_BUFFERED;
		return SKIP_BODY;
	}

	
	protected String makeTagString(String inputType, boolean canEdit) throws IllegalArgumentException, IllegalAccessException {
		List<String> pieces = new ArrayList<String>();
		pieces.add("<input");
		pieces.add("type=\"" + inputType + "\"");
		List<Field> fieldList = new ArrayList<Field>();
		Field[] textList = Text.class.getDeclaredFields();
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
		pieces.add("/>");
		return StringUtils.join(pieces, " ");
	}
	
}
