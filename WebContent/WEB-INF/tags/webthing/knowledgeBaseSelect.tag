<%@ tag description="" body-content="scriptless" %>
<% for ( com.ansi.scilla.web.knowledgeBase.common.KnowledgeBaseTagName status : com.ansi.scilla.web.knowledgeBase.common.KnowledgeBaseTagName.values() ) {%>
<option value="<%= status.name() %>"><%= status.name() %></option>
<% } %>