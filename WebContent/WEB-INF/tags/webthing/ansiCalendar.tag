<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="monthsToDisplay" required="true" rtexprvalue="true" %>
<%@ attribute name="triggerSize" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" %>

<%
	String nameSpace = (String)jspContext.getAttribute("id");
	String monthsToDisplay = (String)jspContext.getAttribute("monthsToDisplay");
	String displaySize = (String)jspContext.getAttribute("displaySize");
	String label = (String)jspContext.getAttribute("label");
	
	String cssIdString = "id=\"" + nameSpace + "\"";
	Integer months = Integer.valueOf(monthsToDisplay);	
	String modalId = "id=\"" + nameSpace + "_dateTable\"";
%>
<div <%= cssIdString %>>
	<div <%=modalId %>>
		<div class="ansi-calendar-msg err"></div>
		<table class="dateTable">	
		</table>
	</div>
</div> 
<script type="text/javascript">
$(function() {
	ANSI_CALENDAR.init("<%= nameSpace %>", <%= months %>, "<%= label %>");
});
</script>

 