<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="monthsToDisplay" required="true" rtexprvalue="true" %>

<%
	String nameSpace = (String)jspContext.getAttribute("id");
	String monthsToDisplay = (String)jspContext.getAttribute("monthsToDisplay");
	
	String cssIdString = "id=\"" + nameSpace + "\"";
	Integer months = Integer.valueOf(monthsToDisplay);	
%>
<script type="text/javascript">
$(function() {
	ANSI_CALENDAR.init("<%= nameSpace %>", <%= months %>);
});
</script>
<div <%= cssIdString %>>
	<table class="dateTable">
	</table>
</div> 

 