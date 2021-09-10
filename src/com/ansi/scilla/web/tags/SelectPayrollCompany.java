package com.ansi.scilla.web.tags;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.division.common.CompanySelector;
import com.ansi.scilla.web.division.common.DivisionGroupSelectorItem;

public class SelectPayrollCompany extends BodyTagSupport {

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
			List<DivisionGroupSelectorItem> companyList = CompanySelector.getInstance(conn).getGroupList();
			List<String> options = new ArrayList<String>();
			for ( DivisionGroupSelectorItem item : companyList ) {
				if ( ! StringUtils.isBlank(item.getCompanyCode())) {
					if ( activeOnly == false || item.getActive() == true ) {
						options.add(item.makeCompanyHtml());
					}
				}
			}
			out.write(StringUtils.join(options, "\n"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		} finally {
			AppUtils.closeQuiet(conn);
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
	
	
	
	
}
