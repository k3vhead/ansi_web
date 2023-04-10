<%@ tag description="" body-content="scriptless" %>
<%@ attribute name="cssId" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" %>
<%@ attribute name="key" required="true" rtexprvalue="true" %>
<%
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	String style = (String)jspContext.getAttribute("style");
	String key = (String)jspContext.getAttribute("key");
	String size = (String)jspContext.getAttribute("size");
	
	String cssIdString = cssId == null || cssId.length() == 0 ? "" : "id=\"" + cssId + "\"";	
	String cssClassString = cssClass == null || cssClass.length() == 0 ? "" : cssClass;	
	String styleString = style == null || style.length() == 0 ? "" : "style=\"" + style + "\"";
	String sizeString = size == null || size.length() == 0 ? "fa-2x" : "fa-" + size + "x";
%>
<a href="#" data-key="<%= key %>" class="kbtag red fas <%= sizeString %> fa-medkit tooltip <%= cssClassString %>" <%= cssIdString %> <%= styleString %> ><span class="tooltiptext"><jsp:doBody /></span></a>
