<%@ tag 
    description="" 
    body-content="empty"
    import="com.ansi.scilla.web.servlets.quote.QuotePrintServlet"
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="modalName" required="true" rtexprvalue="true" %>


<%
    String modalName = (String)jspContext.getAttribute("modalName");
%>

    <div id="<%= modalName %>">
    	<form method="post" action="quotePrint" target="_new">
    		<input type="hidden" name="quoteId" />
    		<span class="formLabel">Quote Number:</span> <span class="quoteNumber"></span><br />
    		<span class="formLabel">Quote Date:</span> <input type="text" class="dateField" name="<%= QuotePrintServlet.QUOTE_DATE %>" /><br />
    		<div style="margin-top:8px;">
    			<span class="formLabel">Print Type:</span><br />
	    		<input type="radio" name="<%= QuotePrintServlet.PRINT_TYPE %>" value="<%= QuotePrintServlet.PRINT_TYPE_IS_PREVIEW %>" checked="checked"/> Preview<br />
	    		<input type="radio" name="<%= QuotePrintServlet.PRINT_TYPE %>" value="<%= QuotePrintServlet.PRINT_TYPE_IS_PROPOSE %>" /> Propose<br />
	    	</div>
    		 
    	</form>
    </div>

 