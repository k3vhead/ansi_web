<%@ tag 
    description="" 
    body-content="empty"
    import="com.ansi.scilla.web.quote.servlet.QuotePrintServlet"
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="modalName" required="true" rtexprvalue="true" %>


<%
    String modalName = (String)jspContext.getAttribute("modalName");
%>

    	<div id="<%= modalName %>">
			<form action="#">
				<table>
					<tr>
						<td>Print Date: </td>
						<td><input type="text" class="dateField" id="printDate"/></td>
						<td><span class="err" id="printDateErr"></span></td>
					</tr>
					<tr>
						<td>Due Date: </td>
						<td><input type="text" class="dateField" id="dueDate"/></td>
						<td><span class="err" id="dueDateErr"></span></td>
					</tr>
					<%--
					<tr>
						<td colspan="2"><input type="button" value="Print Invoices" id="goPrint" /></td>
					</tr>
					 --%>
				</table>
			</form>
			<div style="width:100%; padding-top:20px; text-align:center;" id="hangOn">
				<i class="fa fa-spinner fa-pulse fa-3x fa-fw"></i><br />
				<br />
				This may take a while ....
			</div>
    	</div>

 