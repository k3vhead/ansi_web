<%@ tag description="" body-content="scriptless" %> <%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = idName == null ? "" : "style=\"" + styleName + "\"";
%><span class="green fa fa-pencil tooltip <%= className %>" <%= idString %> <%= styleString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>