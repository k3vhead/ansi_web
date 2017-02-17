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

<% 
	// if we go through the servlet (from quote screen) parameters are set in the request attribute
	// if we go through a jsp include (from the job screen) parameters are set in a request parameter
	// figure out which one we've got, and use it
	String attrPanel = (String)request.getAttribute("panelname");
	String parmPanel = request.getParameter("panelname");	
	String panelname = attrPanel == null ? parmPanel : attrPanel;
	
	String attrPage = (String)request.getAttribute("page");
	String parmPage = request.getParameter("page");	
	String pageName = parmPage == null ? attrPage : parmPage;
	
	String jobSiteName = panelname + "_jobSite";
	String billToName = panelname + "_billTo";
	String jobPanelName = panelname + "_jobPanel";
	String jobProposal = panelname + "_jobProposal";
	String jobActivation = panelname + "_jobActivation";
	String jobDates = panelname + "_jobDates";
	String jobSchedule = panelname + "_jobSchedule";
	String jobInvoice = panelname + "_jobInvoice";
	String jobAudit = panelname + "_jobAudit";
%>

	<tr>
	<td>
		<table>
			<tr>
				<td class="jobTableCell">
					<webthing:addressPanel namespace="<%= jobSiteName %>" label="Job Site" cssId="jobSite" />
				</td>
				<td class="jobTableCell">
					<webthing:addressPanel namespace="<%= billToName %>" label="Bill To" cssId="billTo" />
				</td>
			</tr>
		</table>
		<table style="border:solid 1px #000000; margin-top:8px;">
			<tr>
				<td class="jobTableCell" colspan="2">
					JobPanel:					
					 <webthing:jobPanel namespace="<%=jobPanelName %>" cssId="jobPanel" page="<%= pageName %>" />
				</td>
			</tr>
			<tr>
				<td class="jobTableCell">
					JobProposal:
					<webthing:jobProposal namespace="<%= jobProposal %>" cssId="jobProposal" page="<%= pageName %>" />
				</td>
				<td class="jobTableCell">
					JobActivation:
					<webthing:jobActivation namespace="<%= jobActivation %>" cssId="jobActivation" page="<%= pageName %>" />
				</td>
			</tr>

			<tr>
				<td class="jobTableCell">
					JobDates:
					<webthing:jobDates namespace="<%= jobDates %>" cssId="jobDates" page="<%= pageName %>" />
					<br />
					Job Schedule:
					<webthing:jobSchedule namespace="<%= jobSchedule %>" cssId="jobSchedule" page="<%= pageName %>" />
				</td>
				<td class="jobTableCell">
					JOb Invoice:<br />			
					<webthing:jobInvoice namespace="<%= jobInvoice %>" cssId="jobInvoice" page="<%= pageName %>" />
				</td>
			</tr>
			<tr>
				<td class="jobTableCell">
					JobAudit:
					<webthing:jobAudit namespace="<%= jobAudit %>" cssId="jobAudit" page="<%= pageName %>" />
				</td>
				<td class="jobTableCell" style="text-align:center;">
					<input type="button" value="Cancel" id="jobCancelButton" />
					<input type="button" value="Save" id="jobSaveButton" />
					<input type="button" value="Save & Exit" id="jobExitButton" />
				</td>
			</tr>
		</table>    	
	</td>
	</tr>

		