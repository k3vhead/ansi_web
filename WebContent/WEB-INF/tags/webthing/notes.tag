<%@ tag description="" body-content="scriptless" %><%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%@ attribute name="xrefType" required="true" rtexprvalue="true" %><%@ attribute name="xrefId" required="true" rtexprvalue="true" %><%@ attribute name="xrefName" required="false" rtexprvalue="true" %><%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String xrefName = (String)jspContext.getAttribute("xrefName");
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"" + styleName + "\"";
	String xrefType = (String)jspContext.getAttribute("xrefType");
	String xrefId = (String)jspContext.getAttribute("xrefId");
	String xrefNameString = xrefName == null ? "" : "data-xrefname=\"" + xrefName + "\"";
%><ansi:hasPermission permissionRequired="CALL_NOTE_READ"><span class="orange fas fa-music tooltip call-note-action-link <%= classString %>" data-xrefId="<%= xrefId %>" data-xrefType="<%= xrefType %>" <%= idString %> <%= styleString %> <%= xrefNameString %>ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span></ansi:hasPermission>