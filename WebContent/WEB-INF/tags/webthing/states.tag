<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>

<%@ attribute name="defaultValue" required="false" rtexprvalue="true" %>

<%
	String defaultValue = (String)jspContext.getAttribute("defaultValue");
	String states="AL,AK,AZ,AR,CA,CO,DE,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,MO,NE,NV,NH,NJ,NM,NY,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WA,WV,WI,WY";
	 for ( String state : states.split(",") ) { 
		 String selectedString = defaultValue != null && defaultValue.equalsIgnoreCase(state) ? "selected=\"selected\"" : "";
%>
	<option value="<%= state %>" <%=selectedString %>><%= state %></option>
<% } %>
