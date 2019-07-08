<%@ tag description="Generate submenu for top-level menu option" body-content="scriptless" %> 
<%@ attribute name="menuName" required="true" rtexprvalue="true" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<%
    String menuName = (String)jspContext.getAttribute("menuName"); 
	com.ansi.scilla.web.common.utils.Menu menu = com.ansi.scilla.web.common.utils.Menu.valueOf(menuName);
	com.ansi.scilla.web.common.utils.MenuItem menuItem = new com.ansi.scilla.web.common.utils.MenuItem(menu);
%>
<a href="<%= menu.getLink() %>"><%= menu.getDisplayText() %></a>
<ul class="sub_menu" style="z-index:1000">
<% for (com.ansi.scilla.web.common.utils.MenuItem item : menuItem.getSubMenu() ) { %>
	<% if ( item.getMenu().getPermissionRequired() == null ) { %>
		<li><a href="<%= item.getMenu().getLink() %>"><%= item.getMenu().getDisplayText() %></a></li>
	<% } else { %>
		<ansi:hasPermission permissionRequired="<%= item.getMenu().getPermissionRequired().name() %>"><li><a href="<%= item.getMenu().getLink() %>"><%= item.getMenu().getDisplayText() %></a></li></ansi:hasPermission>
	<% } %>
<% } %>
<jsp:doBody />
</ul>
	

