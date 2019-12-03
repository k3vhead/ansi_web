<%@ tag description="" body-content="scriptless" %><%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%@ attribute name="xrefType" required="true" rtexprvalue="true" %><%@ attribute name="xrefId" required="true" rtexprvalue="true" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"" + styleName + "\"";
	String xrefType = (String)jspContext.getAttribute("xrefType");
	String xrefId = (String)jspContext.getAttribute("xrefId");
%><span class="orange fas fa-music tooltip call-note-action-link <%= classString %>" data-xrefId="<%= xrefId %>" data-xrefType="<%= xrefType %>" <%= idString %> <%= styleString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>