<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<% 
String action="view";
String jobId="null";
%>
<c:choose>
	<c:when test="${param.action eq 'view'}">
		<% action="view"; %>
	</c:when>
	<c:when test="${param.action eq 'edit'}">
		<% action="edit"; %>
	</c:when>
	<c:otherwise>
		<% action="view"; %>
	</c:otherwise>
</c:choose>
<ansi:hasPermission permissionRequired="QUOTE">
<table style="width:100%" class="jobScheduleDisplayPanel">	
	<colgroup>
		<col style="width:15%;" />
		<col style="width:20%;" />
		<col style="width:25%;" />
		<col style="width:20%;" />
		<col style="width:20%;" />
	</colgroup>		    					
	<tr>
		<td><span class="formLabel">Last Run:</span></td>
		<td><span class="job-schedule-last-run"></span></td>
		<td><span class="formLabel">Repeated Annually:</span></td>
		<td><ansi:checkbox name="repeatedAnnually" action="<%= action %>" value="true" /></td>
		<td rowspan="2" style="text-align:center;">
			<a class="job-schedule-ticket-list" style="text-decoration:none; color:#404040;"><webthing:ticketList jobId="<%= jobId %>" styleClass="job-schedule-ticket-list">Ticket List</webthing:ticketList></a>
			<%-- <i style="cursor:pointer;" class="fa fa-list-alt fa-2x tooltip" aria-hidden="true" ><span class="tooltiptext">Ticket List</span></i> --%>
		</td>
	</tr>
	<tr>
		<td><span class="formLabel">Next Due:</span></td>
		<td><span class="job-schedule-next-due"></span></td>
		<td><span class="formLabel">Created Thru:</span></td>
		<td><span class="job-schedule-created-thru"></span></td>
	</tr>
</table>
</ansi:hasPermission>