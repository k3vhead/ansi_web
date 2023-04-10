<%@ tag description="" body-content="scriptless" %> 
<% for (com.ansi.scilla.common.contact.ContactStatus status : com.ansi.scilla.common.contact.ContactStatus.values() ) { %>
<option value="<%= status.name() %>"><%= status.display() %></option>
<% } %>