<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="namespace" required="true" rtexprvalue="true" %>
<%@ attribute name="cssId" required="true" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" %>
<%@ attribute name="page" required="true" rtexprvalue="true" %>


<%
    String namespace = (String)jspContext.getAttribute("namespace");
	String cssId = (String)jspContext.getAttribute("cssId");
	String cssClass = (String)jspContext.getAttribute("cssClass");
	String label = (String)jspContext.getAttribute("label");
	String page = (String)jspContext.getAttribute("page");
	
	String cssIdString = "id=\"" + cssId + "\"";			 
	String cssClassString = cssClass == null || cssClass.length()==0 ? "" : "class=\"" + cssClass + "\"";
%>
<script type="text/javascript">               
	
	
</script>     

<div <%= cssIdString %> <%= cssClassString %> >
	<form id="<%=namespace%>_address">
		<table>
			<tr>
				<td><b><%= label %></b></td>
				<td colspan="3"><input type="text" name="<%=namespace %>_name" style="width:315px" /></td>
			</tr>
			<tr>
				<td style="width:85px;">Address:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address1" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Address 2:</td>
				<td colspan="3"><input type="text" name="<%=namespace %>_address2" style="width:315px" /></td>
			</tr>
			<tr>
			<td colspan="4" style="padding:0; margin:0;">
				<table style="width:415px;border-collapse: collapse;padding:0; margin:0;">
				<tr>
					<td>City/State/Zip:</td>
					<td><input type="text" name="<%=namespace %>_city" style="width:90px;" /></td>
					<td><select name="<%=namespace %>_state" id="<%=namespace %>_state" style="width:85px !important;max-width:85px !important;"></select></td>
					<td><input type="text" name="<%=namespace %>_zip" style="width:47px !important" /></td>
				</tr>
				</table>
			</td>
			</tr>
			<tr>
				<td>County:</td>
				<td><input type="text" name="<%=namespace %>_county" id="<%=namespace %>_county" style="width:90%" /></td>
				<td colspan="2">
					<table style="width:180px">
						<tr>
							<td>Country:</td>
							<td align="right"><select name="<%=namespace %>_country" id="<%=namespace %>_country"></select></td>
						</tr>
					</table>
				
				
				</td>
				
			</tr>
			<tr>
			<% if ( page.equals("job") ) { %>
					<td>Job Contact:</td>
					<td style="width:140px;"><input type="text" name="<%=namespace %>_jobContactName" style="width:125px" placeholder="<name>"/></td>
					<td colspan="2"><input type="text" name="<%=namespace %>_jobContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
				<% } else if ( page.equals("bill") ){%>
					<td>Cont Contact:</td>
					<td style="width:140px;"><input type="text" name="<%=namespace %>_contractContactName" style="width:125px" placeholder="<name>"/></td>
					<td colspan="2"><input type="text" name="<%=namespace %>_contractContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
				<% } %>
			</tr>
			<tr>
			<% if ( page.equals("job") ) { %>
					<td>Site Contact:</td>
					<td style="width:140px;"><input type="text" name="<%=namespace %>_siteContactName" style="width:125px" placeholder="<name>"/></td>
					<td colspan="2"><input type="text" name="<%=namespace %>_siteContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
				<% } else if ( page.equals("bill") ){%>
					<td>Billing Contact:</td>
					<td style="width:140px;"><input type="text" name="<%=namespace %>_billingContactName" style="width:125px" placeholder="<name>"/></td>
					<td colspan="2"><input type="text" name="<%=namespace %>_billingContactInfo" style="width:170px" placeholder="<phone,mobile,email>"/></td>
				<% } %>
			</tr>
		</table>
		<input type="text" name="<%=namespace %>_id" style="display:none" />
		<input type="text" name="<%=namespace %>_Con1id" style="display:none" />
		<input type="text" name="<%=namespace %>_Con2id" style="display:none" />
	</form>
</div>
 