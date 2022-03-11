<%@ tag description="" body-content="scriptless" %> 
<% for (com.ansi.scilla.common.organization.OrganizationStatus status : com.ansi.scilla.common.organization.OrganizationStatus.values() ) { %>
<option value="<%= status.status() %>"><%= status.name() %></option>
<% } %>