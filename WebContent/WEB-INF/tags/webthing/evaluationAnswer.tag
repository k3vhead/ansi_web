<%@ tag 
    description="" 
    body-content="empty" 
    
%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>


<%@ attribute name="type" required="true" rtexprvalue="true" %>
<%@ attribute name="choices" required="false" rtexprvalue="true" %>
<%@ attribute name="required" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="true" rtexprvalue="true" %>

<c:choose>
	<c:when test="${required=='1'}">
		<c:set var="reqclass" value="requiredAnswer" />
	</c:when>
	<c:otherwise>
		<c:set var="reqclass" value="" />
	</c:otherwise>
</c:choose>

<c:if test="${type=='DATE'}">
	<input type="text" name="q${name}" class="dateField evalq ${reqclass}" data-qnum="${name}" />
</c:if>

<c:if test="${type=='TEXT'}">
	<input type="text" name="q${name}"  data-qnum="${name}" class="evalq ${reqclass}" />
</c:if>

<c:if test="${type=='DROPDOWN'}">
	<select name="q${name}" data-qnum="${name}" class="evalq ${reqclass}">
		<option value=""></option>
		<c:forTokens var="option" items="${choices}" delims=",">
			<option value="<c:out value="${option}" />"><c:out value="${option}" /></option>
		</c:forTokens>
	</select>
</c:if>

<c:if test="${type=='RADIO_BUTTON'}">
	<c:forTokens var="option" items="${choices}" delims=",">
		<input type="radio" name="q${name}" data-qnum="${name}" class="evalq ${reqclass}" value="${option}" /> ${option}<br /> 
	</c:forTokens>
</c:if>

<c:if test="${type=='SHORT_ANSWER'}">
	<textarea rows="5" cols="30" name="q${name}"  data-qnum="${name}" class="evalq ${reqclass}"></textarea>
</c:if>


