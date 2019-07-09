<%@ tag description="Generate submenu for top-level menu option" body-content="scriptless" %> 
<%@ attribute name="menuName" required="true" rtexprvalue="true" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<%
String menuName = (String)jspContext.getAttribute("menuName"); 
com.ansi.scilla.web.common.utils.Menu menu = com.ansi.scilla.web.common.utils.Menu.valueOf(menuName);
com.ansi.scilla.web.common.utils.MenuItem menuItem = new com.ansi.scilla.web.common.utils.MenuItem(menu);
String permission = menu.getPermissionRequired() == null ? null : menu.getPermissionRequired().name();
if ( permission == null ) {
%>
	<li>
		<webthing:menuItem menuName="<%= menuName %>">
			<jsp:doBody />
		</webthing:menuItem>
	</li>
<% } else { %>
	<webthing:hasPermission permissionRequired="<%= permission %>">
		<li>
		<webthing:menuItem menuName="<%= menuName %>">
			<jsp:doBody />
		</webthing:menuItem>
		</li>
	</webthing:hasPermission>
<% } %>