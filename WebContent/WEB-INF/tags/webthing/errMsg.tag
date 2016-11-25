<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>

<%@ attribute name="key" required="true" rtexprvalue="true" %>

<%
    String key = (String)jspContext.getAttribute("key");
%>
<logic:messagesPresent property="<%=key%>">
    <span class="err">
        <html:messages property="<%=key%>" id="msg">
            <bean:write name="msg" filter="false"/><br/>
        </html:messages>
    </span>
</logic:messagesPresent>
