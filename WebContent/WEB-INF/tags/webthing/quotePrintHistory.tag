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

	<%--
	private Date printDate;
	private Date quoteDate;
	
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private Integer quoteId;
	private Integer userId;
 --%>
    <div id="<%= modalName %>">
		<table class="printHistoryTable" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:650px;width:650px;">
	        <colgroup>
	        	<col style="width:25%;" />
	        	<col style="width:25%;" />
	        	<col style="width:50%;" />
	   		</colgroup>        
	        <thead>
	            <tr>
	                <th>Print Date</th>
	    			<th>Quote Date</th>
	    			<th>Printed By</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>Print Date</th>
	    			<th>Quote Date</th>
	    			<th>Printed By</th>
	            </tr>
	        </tfoot>
	    </table>
    </div>

 