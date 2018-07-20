<%@ page contentType="text/html"%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing"%>
<%@ taglib tagdir="/WEB-INF/tags/quote" prefix="quote"%>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>

<html>
	<head>
	</head>
	<body>
		<table style="width:95%" class="jobScheduleEditPanel">	
			<colgroup>
				<col style="width:20%;" />
				<col style="width:20%;" />
				<col style="width:30%;" />
				<col style="width:10%;" />
				<col style="width:10%;" />
			</colgroup>		    					
			<tr>
				<td><span class="formLabel">Last Run:</span></td>
				<td><span class="job-schedule-last-run">99/99/99</span></td>
				<td><span class="formLabel">Repeated Annually:</span></td>
				<td>
					<input type="checkbox" name="repeatedAnnually" data-apiname="repeatScheduleAnnually" value="true" />
				</td>
				<td rowspan="2" style="text-align:center;">
					<a href="#" class="job-schedule-ticket-list-link"><i style="cursor:pointer;" class="fa fa-list-alt fa-2x tooltip job-schedule-ticket-list" aria-hidden="true"><span class="tooltiptext">Ticket List</span></i></a>
				</td>
			</tr>
			<tr>
				<td><span class="formLabel">Next Due:</span></td>
				<td><span class="job-schedule-next-due">99/99/99</span></td>
				<td><span class="formLabel">Created Thru:</span></td>
				<td><span class="job-schedule-created-thru">99/99/99</span></td>
			</tr>
		</table>		
	</body>
</html>