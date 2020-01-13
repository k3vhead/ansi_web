<%@ tag description="" body-content="scriptless" %> <%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%@ attribute name="xrefId" required="true" rtexprvalue="true" %><%@ attribute name="xrefType" required="true" rtexprvalue="true" %><%@ attribute name="xrefName" required="false" rtexprvalue="true" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String xrefId = (String)jspContext.getAttribute("xrefId");
	String xrefType = (String)jspContext.getAttribute("xrefType");
	String xrefName = (String)jspContext.getAttribute("xrefName");
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"" + styleName + "\"";
	String xrefNameString = xrefName == null ? "" : "data-xrefname=\"" + xrefName + "\"";
%><span class="green fas fa-file-contract tooltip <%= classString %>" <%= idString %> <%= styleString %> ari-hidden="true" data-xrefid="<%= xrefId %>" data-xreftype="<%= xrefType %>" <%= xrefNameString %>><span class="tooltiptext"><jsp:doBody /></span></span>