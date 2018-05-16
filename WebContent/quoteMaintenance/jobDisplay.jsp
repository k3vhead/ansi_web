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
String jobId = request.getParameter("jobid");
String idString = "id=\"job" + jobId + "\"";
String dataString = "data-jobid=\"" + jobId + "\"";
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Job Display</title>
</head>
	<body>
		<table style="width:100%;" <%= idString %>>
			<colgroup>
	        	<col style="width:50%;" />
	    		<col style="width:50%;" />
	    	</colgroup>
			<tr>
				<td style="vertical-align:top;">
					Job Proposal
				</td>
				<td style="vertical-align:top;">
					Job Activation
				</td>
			</tr>
			<tr>
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobProposalDisplayPanel />			    					
				</td>
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobActivationDisplayPanel />
				</td>
			</tr>
			<tr>
				<td>Job Dates</td>
				<td>Job Invoice</td>
			</tr>
			<tr class="job-data-row">
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobDatesDisplayPanel />			    					
				</td>
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobInvoiceDisplayPanel />
				</td>
			</tr>
			<tr>
				<td>Job Schedule</td>
				<td>Job Audit</td>
			</tr>
			<tr>
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobScheduleDisplayPanel />
				</td>
				<td style="vertical-align:top; border:solid 1px #404040;">
					<quote:jobAuditDisplayPanel />
				</td>
			</tr>
		</table>
	</body>
</html>