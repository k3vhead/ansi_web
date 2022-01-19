<%@ tag description="" body-content="scriptless" %>
<% for ( com.ansi.scilla.common.payroll.common.EmployeeStatus status : com.ansi.scilla.common.payroll.common.EmployeeStatus.values() ) {%>
<option value="<%= status.display()%>"><%= status.display() %></option>
<% } %>