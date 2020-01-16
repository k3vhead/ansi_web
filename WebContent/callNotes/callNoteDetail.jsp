<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<div class="call-note-detail">
	<table>
		<tr>
			<td class="call-note-detail-text" rowspan="2">
				<span class="formLabel">Call Id:</span> <span class="callLogId"><c:out value="${ANSI_CALL_NOTE_DETAIL.callLogId}" /></span><br />
				<span class="formLabel">Xref:</span> <span class="xref"><c:out value="${ANSI_CALL_NOTE_DETAIL.xrefType}" /> <c:out value="${ANSI_CALL_NOTE_DETAIL.xrefId}" /></span><br />
				<span class="formLabel">Time:</span> <span class="startTime"><c:out value="${ANSI_CALL_NOTE_DETAIL.startTime}" /></span>
			</td>
			<td class="call-note-detail-text" style="width:62%;">
				<span class="formLabel">Summary:</span> <span class="summary"><c:out value="${ANSI_CALL_NOTE_DETAIL.summary}" /></span>
			</td>
		</tr>	
		<tr>
			<td class="call-note-detail-text" style="width:62%;">
				<span class="content"><c:out value="${ANSI_CALL_NOTE_DETAIL.content}" /></span>
			</td>
		</tr>
	</table>
	<table>
		<tr>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">Contact</span><br />
				<span class="formLabel">Name:</span> <span class="contactName"><c:out value="${ANSI_CALL_NOTE_DETAIL.contactName}" /></span><br />
				<span class="formLabel">Method:</span> <span class="contactType"><c:out value="${ANSI_CALL_NOTE_DETAIL.contactType}" /></span>
			</td>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">Address</span><br />
				<span class="addressName"><c:out value="${ANSI_CALL_NOTE_DETAIL.addressName}" /></span><br />
				<span class="address1"><c:out value="${ANSI_CALL_NOTE_DETAIL.address1}" /></span><br />
				<span class="address2container"><span class="address2"><c:out value="${ANSI_CALL_NOTE_DETAIL.address2}" /></span><br /></span>
				<span class="city"><c:out value="${ANSI_CALL_NOTE_DETAIL.city}" /></span>, <span class="state"><c:out value="${ANSI_CALL_NOTE_DETAIL.state}" /></span> <span class="zip"><c:out value="${ANSI_CALL_NOTE_DETAIL.zip}" /></span>
			</td>
			<td class="call-note-detail-text" style="width:33%;">
				<span class="formLabel">ANSI:</span><br />
				<span class="ansiContact"><c:out value="${ANSI_CALL_NOTE_DETAIL.ansiContact}" /></span><br />
				<webthing:phoneIcon /> <span class="ansiPhone"><c:out value="${ANSI_CALL_NOTE_DETAIL.ansiPhone}" /></span><br />
				<webthing:emailIcon /> <span class="ansiEmail"><c:out value="${ANSI_CALL_NOTE_DETAIL.ansiEmail}" /></span>
			</td>
		</tr>
	</table>
</div>

