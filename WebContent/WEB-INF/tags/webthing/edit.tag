<%@ tag 
    description="" 
    body-content="scriptless"
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>


<%@ attribute name="styleClass" required="false" rtexprvalue="true" %>
<%@ attribute name="styleId" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" %>

<%
    String className = (String)jspContext.getAttribute("styleClass");
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = idName == null ? "" : "style=\"" + styleName + "\"";
%>
<span class="green fa fa-pencil tooltip <%= className %>" <%= idString %> <%= styleString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>



