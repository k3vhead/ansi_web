<%@ tag description="" body-content="scriptless" %> <%@ attribute name="styleClass" required="false" rtexprvalue="true" %><%@ attribute name="styleId" required="false" rtexprvalue="true" %><%@ attribute name="style" required="false" rtexprvalue="true" %><%@ attribute name="tabindex" required="false" rtexprvalue="true" %><%
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String tabIndex = (String)jspContext.getAttribute("tabindex");
	String classString = className == null ? "" : className;
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : "style=\"" + styleName + "\"";
	String tabString = tabIndex == null ? "" : "tabindex=\"" + tabIndex + "\"";
%><span class="red fas fa-exclamation-triangle tooltip <%= classString %>" <%= idString %> <%= styleString %> <%=tabString %> ari-hidden="true"><span class="tooltiptext"><jsp:doBody /></span></span>