<%@ tag 
    description="" 
    body-content="empty" 
    
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
    		Quote Number: <span class="quoteNumber"></span><br />
    		Quote Date: <input type="text" class="dateField" name="<%= com.ansi.scilla.web.servlets.quote.QuotePrintServlet.QUOTE_DATE %>" /><br />
    	</form>
    </div>

 