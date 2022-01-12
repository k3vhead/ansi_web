<%@ tag description="" body-content="scriptless" %>
<% for ( com.ansi.scilla.common.payroll.EmployeeStatus status : com.ansi.scilla.common.payroll.EmployeeStatus.values() ) {%>
<option value="<%= status.display()%>"><%= status.display() %></option>
<% } %>