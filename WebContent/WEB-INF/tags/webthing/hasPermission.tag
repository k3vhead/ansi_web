<%@ tag 
    description="" 
    body-content="scriptless" 
    import= "com.ansi.scilla.web.struts.SessionData,
    		com.ansi.scilla.web.common.utils.UserPermission"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>


<%@ attribute name="permissionRequired" required="true" rtexprvalue="true" %>

<%
    String permissionRequired = (String)jspContext.getAttribute("permissionRequired");
	SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
	if ( sessionData != null ) {
		if ( sessionData.hasPermission(permissionRequired) ) {
%>
<jsp:doBody />
<%				
		}
	}
%>

