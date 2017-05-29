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
	String namespace = (String)request.getAttribute("namespace");
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
	String jobSave = panelname + "_jobSave";
	String jobCancel = panelname + "_jobCancel";
%>
	
	<tr>
	<td>
	<% if ( !pageName.equals("QUOTE") ) { %>
	 	<table class="addressTable">
			<tr>
					<td align="left" style="width:480px;border:1px solid #000000; white-space:nowrap;">
							<span style="display: inline-block;width:51px;"><b>Bill&nbsp;To:</b></span> <input type="text" name="billTo_name" style="width:425px" />
							<i id="billToAddressIdErr" aria-hidden="true"></i>	
							</td>
							<td align="left" style="width:480px;border:1px solid #000000; white-space:nowrap;">
								<span style="display: inline-block;width:51px;"><b>Job&nbsp;Site:</b></span> <input type="text" name="jobSite_name" style="width:425px" />
								<i id="jobSiteAddressIdErr" aria-hidden="true"></i>
							</td>
						</tr>
						<tr>
							<td align="left" style="width:480px;">
								<!--<webthing:addressPanel label="Bill To"  namespace="billTo" cssId="billTo"  page="bill"/>-->
								<webthing:addressDisplayPanel cssId="billTo" />
							</td>
							<td align="left" style="width:480px;">
							<!--<webthing:addressPanel label="Job Site" namespace="jobSite" cssId="jobSite" page="job"/>-->
								<webthing:addressDisplayPanel cssId="jobSite" />
							</td>
						</tr>
						<tr>
							<td align="left" style="width:480px;">
								<table style="width:100%;border:1px solid #000000">
									<tr>
										<td><span id="billToC1">Cont Contact:</span></td>
										<td style="width:140px;"><input type="text" name="billTo_contractContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="billTo_contractContactInfo" style="display: inline-block;width:170px;"></span><i id="contractContactIdErr" aria-hidden="true"></i></td>
									</tr>
									<tr>
										<td><span id="billToC2">Billing Contact:</span></td>
										<td style="width:140px;"><input type="text" name="billTo_billingContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="billTo_billingContactInfo" style="display: inline-block;width:170px;"></span><i id="billingContactIdErr" aria-hidden="true"></i></td>
									</tr>
								</table>
							</td>
							<td align="left" style="width:480px;">
								<table style="width:100%;border:1px solid #000000">
									<tr>
										<td><span id="jobSiteC1">Job Contact:</span></td>
										<td style="width:140px;"><input type="text" name="jobSite_jobContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="jobSite_jobContactInfo" style="display: inline-block;width:170px;"></span><i id="jobContactIdErr" aria-hidden="true"></i></td>
									</tr>
									<tr>
										<td><span id="jobSiteC2">Site Contact:</span></td>
										<td style="width:140px;"><input type="text" name="jobSite_siteContactName" style="width:125px" placeholder="<name>"/></td>
										<td colspan="2"><span name="jobSite_siteContactInfo" style="display: inline-block;width:170px;"></span><i id="siteContactErr" aria-hidden="true"></i></td>
									</tr>
								</table>
							</td>
						</tr>
		</table> 
		<% }  %>
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
					<webthing:jobSchedule namespace="<%= jobSchedule %>" cssId="jobSchedule" jobId="" page="<%= pageName %>" />
				</td>
				<td class="jobTableCell">
					Job Invoice:<br />			
					<webthing:jobInvoice namespace="<%= jobInvoice %>" cssId="jobInvoice" page="<%= pageName %>" />
				</td>
			</tr>
			<tr>
				<td class="jobTableCell">
					JobAudit:
					<webthing:jobAudit namespace="<%= jobAudit %>" cssId="jobAudit" page="<%= pageName %>" />
				</td>
				
				<% if ( pageName.equals("QUOTE") ) { %>
				<td class="jobTableCell" style="text-align:center;">
				<!-- <input type="button" name="<%= jobCancel %>" value="Cancel" rownum="<%= panelname %>" id="jobCancelButton" /> -->
				<!-- <input type="button" class="jobSave" name="<%= jobSave %>" value="Save" rownum="<%= panelname %>" id="jobSaveButton" /> -->
				</td>
				<% }  %>
				
			</tr>
		</table>    	
	</td>
	</tr>
	
		