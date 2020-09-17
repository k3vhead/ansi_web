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
		<table style="width:100%" class="jobProposalEditPanel">
			<colgroup>
				<col style="width:25%" />
				<col style="width:25%" />
				<col style="width:25%" />
				<col style="width:25%" />
			</colgroup>
			<tr>
				<td><span class="formLabel">Job #:</span></td>
				<td><input type="text" name="job-proposal-job-nbr" data-apiname="jobNbr" style="width:50px;" /></td>
				<td><span class="formLabel">Price&nbsp;Per&nbsp;Cleaning:</span></td>
				<td><input type="text"  name="job-proposal-ppc" data-apiname="pricePerCleaning" style="width:75px;" /></td>
			</tr>
			<tr>
				<td><span class="formLabel">&nbsp;</span></td>
				<td>&nbsp;</td>
				<td><span class="formLabel">Frequency:</span></td>
				<td><select name="job-proposal-freq" data-apiname="jobFrequency"></select></td>
			</tr>
			<tr>
				<td colspan="4">
					<span class="formLabel">Service Description:</span><br />
					<textarea name="job-proposal-desc"  data-apiname="serviceDescription" rows="5" cols="40"></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="4">
				<span class="formLabel">Tags:</span><br />
				<span class="job-proposal-jobtag"  data-apiname="jobtag"></span>
				</td>
			</tr>
		</table>
	</body>
</html>
