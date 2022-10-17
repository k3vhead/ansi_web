package com.ansi.scilla.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ansi.scilla.web.report.common.BatchReports;

public class BatchReportSelect extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		
		try {
			for ( BatchReports option : BatchReports.values() ) {
				if ( option.adminSubscription() ) {
					out.write("<option value=\"" + option.name() + "\">" + option.description() + "</option>");
				}
			}
		} catch ( IOException e ) {
			throw new JspException(e);
		}
		
		return EVAL_BODY_INCLUDE;
	}

	
}
