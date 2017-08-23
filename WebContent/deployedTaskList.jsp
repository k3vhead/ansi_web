<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<html>
	<head>
	<style type="text/css">
		.task_table {
			width:95%;
		}
		.task_date {
			vertical-alignt:top;
			width:100px;
			padding-right:10px;
		}
		.task_name {
			vertical-alignt:top;
			padding-right:10px;
		}
	</style>
	</head>
	<body>
		<table class="task_table">
			<c:forEach var="task" items="${DeployedTaskAction_task_list}">
				<tr>
					<td class="task_date"><fmt:formatDate value="${task.modifiedAt}" pattern="MM/dd/yyyy" /></td>
					<td class="task_name"><c:out value="${task.name}" /></td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>