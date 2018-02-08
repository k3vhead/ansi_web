<%@ tag description="" body-content="scriptless" %> 
<%@ attribute name="styleClass" required="false" rtexprvalue="true" %>
<%@ attribute name="styleId" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" %>
<%@ attribute name="text" required="false" rtexprvalue="true" %>
<%	
    String className = (String)jspContext.getAttribute("styleClass"); 
	String idName = (String)jspContext.getAttribute("styleId");
	String styleName = (String)jspContext.getAttribute("style");
	String text = (String)jspContext.getAttribute("text");
	String classString = className == null ? "" : "class=\"" + className + "\"";
	String idString = idName == null ? "" : "id=\"" + idName + "\"";
	String styleString = styleName == null ? "" : styleName;
	String[] textOption = new String[] {"Thinking...", "Hang on a minute...", "Patience...", "Working...", "Just a Moment...","I haven't had my coffee yet..."};
	String thinking = text == null ? textOption[java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND) % textOption.length] : text;
%>

<div <%=idString %> <%= classString %> style="text-align:center; <%= styleString %>">
	<%= thinking %><br />
	<i class="fa fa-spinner fa-pulse fa-fw fa-5x"></i>
</div>
