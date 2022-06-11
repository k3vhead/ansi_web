package com.ansi.scilla.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ansi.scilla.common.jobticket.TicketType;

public class TicketTypeSelect extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		
		try {
			for ( TicketType ticketType : TicketType.values() ) {
				out.write("<option value=\"" + ticketType.name() + "\">" + ticketType.display() + "</option>");
			}
		} catch ( IOException e ) {
			throw new JspException(e);
		}
		
		return EVAL_BODY_INCLUDE;
	}

	
}
