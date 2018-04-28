<%@ tag description="" body-content="scriptless" %> <%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"" + styleName + "\"";
%><span class="orange fas fa-pencil-alt tooltip <%= classString %>" <%= idString %> <%= styleString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>