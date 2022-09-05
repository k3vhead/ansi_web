package com.ansi.scilla.web.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ansi.scilla.common.ApplicationObject;

public abstract class OptionTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private String format;
	private String name;
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	abstract protected List<Option> makeOptionList() throws Exception;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			List<Option> optionList = makeOptionList();
			
			FormatOption formatOption = FormatOption.valueOf(getFormat().toUpperCase());
			
			switch ( formatOption ) {
			case CHECKBOX:
				makeCheckbox(out, optionList);
				break;
			case RADIO:
				makeRadio(out, optionList);
				break;
			case SELECT:
				makeSelect(out, optionList);
				break;
			default:
				throw new JspException("Invalid Format option");		
			}
			
	//		return EVAL_BODY_INCLUDE;
			return SKIP_BODY;
		} catch ( Exception e ) {
			throw new JspException(e);
		}
	}
	
	
	
	
	private void makeCheckbox(JspWriter out, List<Option> optionList) throws IOException {
		for ( Option option : optionList ) {
			String optionString = "<input type=\"checkbox\" name=\"" + this.name + "\" value=\""+option.value +"\" ><label for=\""+option.value+"\">"+option.display +"</label><br />\n";
			out.write(optionString);
		};
	}

	private void makeRadio(JspWriter out, List<Option> optionList) throws IOException {
		for ( Option option : optionList ) {
			String optionString = "<input type=\"radio\" name=\"" + this.name + "\" value=\""+option.value +"\" ><label for=\""+option.value+"\">"+option.display +"</label><br />\n";
			out.write(optionString);
		};
	}

	private void makeSelect(JspWriter out, List<Option> optionList) throws IOException {
		for ( Option option : optionList ) {
			String optionString = "<option value=\""+option.value +"\">"+option.display +"</option>";
			out.write(optionString);
		};
	}




	protected class Option extends ApplicationObject implements Comparable<Option> {
		private static final long serialVersionUID = 1L;
		public String value;
		public String display;
		
		
		public Option(String value, String display) {
			super();
			this.value = value;
			this.display = display;
		}


		@Override
		public int compareTo(Option o) {
			return display.compareTo(o.display);
		}
	}
	
	protected enum FormatOption {
		SELECT,
		RADIO,
		CHECKBOX,
		;
	}
}
