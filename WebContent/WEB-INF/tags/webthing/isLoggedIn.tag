<%@ tag 
	description="display body of tag if user is logged" 
    body-content="scriptless"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<c:if test="${not empty org_fvdtc_sessiondata}">
<jsp:doBody />
</c:if>