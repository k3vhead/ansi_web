<%@ tag 
    description="" 
    body-content="scriptless" 
    import= "java.util.Enumeration,
    		java.util.List,
    		com.ansi.scilla.web.struts.SessionData,
    		com.ansi.scilla.web.common.Permission"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>


<%@ attribute name="permissionRequired" required="true" rtexprvalue="true" %>

<%
    String permissionRequired = (String)jspContext.getAttribute("permissionRequired");
	SessionData sessionData = (SessionData)session.getAttribute("org_fvdtc_sessiondata");
	if ( sessionData != null ) {
		List<Permission> permissions = sessionData.getPermissionList();
		if ( permissions != null ) {
			if ( permissions.contains( Permission.valueOf(permissionRequired))) {
%>
<jsp:doBody />
<%				
			}
		}
	}
%>

