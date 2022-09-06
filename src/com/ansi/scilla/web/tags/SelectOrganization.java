package com.ansi.scilla.web.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.division.common.CompanySelector;
import com.ansi.scilla.web.division.common.DivisionSelector;
import com.ansi.scilla.web.division.common.GroupSelector;
import com.ansi.scilla.web.division.common.RegionSelector;

public class SelectOrganization extends BodyTagSupport {

	private static final long serialVersionUID = 1L;	
	
	private String active;
	private String type;
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public int doStartTag() throws JspException {
//		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
//        HttpSession session = request.getSession();
//        SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

        boolean activeOnly = active.equalsIgnoreCase("yes") || active.equalsIgnoreCase("true");
        
//        BodyContent bc = getBodyContent();
		JspWriter out = pageContext.getOut();  //getPreviousOut();
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String options = makeOptionString(conn, activeOnly);
			out.write(options);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		return EVAL_BODY_INCLUDE;
//		return SKIP_BODY;
	}

	
	private String makeOptionString(Connection conn, boolean activeOnly) throws SQLException, JspException {
		String optionString = null;
		switch ( this.type.toUpperCase() ) {
		case "DIVISION":
			optionString = new DivisionSelector(conn).makeHtml(activeOnly);
			break;
		case "COMPANY":
			optionString = new CompanySelector(conn).makeHtml(activeOnly);
			break;
		case "REGION":
			optionString = new RegionSelector(conn).makeHtml(activeOnly);
			break;
		case "GROUP":
			optionString = new GroupSelector(conn).makeHtml(activeOnly);
			break;
		default:
			throw new JspException("Invalid type");
		}
		
		return optionString;
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
	
	
	
	
}
