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
    	THis is the history modal
    </div>

 