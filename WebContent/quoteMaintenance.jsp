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


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Quote Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <script type="text/javascript" src="js/quoteMaintenance.js"></script>
        <script type="text/javascript" src="js/quotePrint.js"></script>
        <script type="text/javascript" src="js/addressUtils.js"></script>
        <%
			String quotePrintModal = "";
		%>
        
        <style type="text/css">

#delData {
	margin-bottom: 15px;
	margin-top: 15px;
 }
  
#quoteTable {border: solid 1px #000000;}
 
.quoteButton {
	background-color: #F0F0F0;
	color: #000000;
	min-height: 25px;
	width: 70px;
 }
 
.quoteSelect {
	max-width: 100px;
	min-width: 100px;
	width: 100px !important;
 }
 
.quoteSelect option {
	max-width: 100px;
	min-width: 100px;
	width: 100px !important;
 }
 
.labelSpan {
	display: inline-block;
	width: 90px !important;
 }
 
 .invoiceSpan {
 	width: 80px !important;
 }
 
.labelSpanSmall {
	display: inline-block;
	width: 60px !important;
 }
 
#jobDates {border: solid 1px #000000;}
 
#jobActivation2 {border: solid 1px #FF0000;}
 
td.jobTableCell {
	vertical-align: top;
	width: 50%;
 }
 
#jobProposal {border: solid 1px #000000;}
 
#jobActivation {
	border: solid 1px #000000;
	height: 100%;
 }
 
#jobActivation2 {border: solid 1px #FF0000;}
 
td.jobTableCell {
	vertical-align: top;
	width: 50%;
 }
 
#jobSite.select {
	max-width: 80px !important;
	width: 80px !important;
 }
 
#division-menu {max-height: 300px;}
 
#jobSite_state-menu {max-height: 300px;}
 
#billTo_state-menu {max-height: 300px;}
#jobProposal_jobFrequency-menu {max-height: 300px;}
#quoteTable {width: 100%;}

.prettyWideButton {
				height:30px;
				min-height:30px;
}

.error {
    color: red;  
}

#confirmDelete {
	background-color: #FFFFFF;
	color: #000000;
	display: none;
	padding: 15px;
	text-align: center;
	width: 300px;
 }
 
#displayTable {width: 90%;}
 
#addFormDiv {
	background-color: #FFFFFF;
	color: #000000;
	display: none;
	padding: 15px;
	width: 400px;
 }
 
#delData {
	margin-bottom: 15px;
	margin-top: 15px;
 }
 
#jobProposal {border: solid 1px #000000;}
 
#jobSchedule {
	border: solid 1px #000000;
	height: 100%;
 }
 
#billTo {border: solid 1px #000000;}
 
#jobSite {border: solid 1px #000000;}
 
#jobInvoice {border: solid 1px #000000;}
 
td.jobTableCell {
	vertical-align: top;
	width: 50%;
 }
 
.formFieldDisplay {margin-left: 30px;}
 
 .addressTable {display:none;}
  .ui-autocomplete-category {
    font-weight: bold;
    padding: .2em .4em;
    margin: .8em 0 .2em;
    line-height: 1.5;
  }
  .ui-state-disabled{
	   opacity: 0.7 !important;
	}
	input[type="text"][disabled] {
	   color: black;
	}
	input[type=checkbox] {width:20px; height:20px;}

        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
    	<div id="loadingDiv"><i class='fa fa-refresh fa-spin'></i></div>
		<table id="quoteTable" style="display:none;">
			<tr>
				<td colspan="2">
					<table >
						<tr>
							<td><input type="button" name="modifyQuoteButton" value="Modify" class="quoteButton"/></td>
							<td><span class="labelSpanSmall" id="managerLabel">Manager:</span>
								<select name="manager" id="manager" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td><span class="labelSpan"  id="divisionLabel">Division:</span>
								<select name="division" id="division" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td align="center"><span  id="quoteLabel">Quote</span>span></td>
							<td align="center"><span  id="revisionLabel">Revision</span></td>
							<td rowspan="2" align="right" style="padding-right:10px;"><input type="button" name="printButton" value="Print" class="quoteButton"/></td>
						</tr>
						<tr>
							<td><input type="button" name="copyQuoteButton" value="Copy" class="quoteButton"/></td>
							<td><span class="labelSpanSmall" id="leadTypeLabel">Lead Type:</span>
								<select name="leadType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td><span class="labelSpan" id="accountTypeLabel">Account Type:</span>
								<select name="accountType" id="accountType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td>Q:&nbsp;&nbsp;<input type="text" name="quoteNumber"  style="width:80px" value="<c:out value="${ANSI_QUOTE_ID}" />"/></td>
							<td>R:&nbsp;&nbsp;<input type="text" name="revision"  style="width:40px"/></td>
						</tr>
						<tr>
							<td><input type="button" name="newQuoteButton" value="New" class="quoteButton"/></td>
							<td>Signed By:&nbsp;&nbsp;<input type="text" name="signedBy"  style="width:170px" disabled="disabled"/></td>
							
							<td><span class="labelSpan">Proposed Date:</span>
								<input type="text" name="proposalDate"  style="width:95px" disabled="disabled"/>
							</td>
							<td colspan="2">Print Date:&nbsp;&nbsp;<input type="text" name="printDate"  style="width:90px"/></td>
							<td>Print Count:&nbsp;&nbsp;<input type="text" name="printCount"  style="width:90px" disabled="disabled"/></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td align="right"  style="padding-right:10px;"><a href="#" name="viewPrintHistory">View Print History</a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan='2' align="left">
					<table>
					<tr>
						<td align="left" style="width:480px;">
							<span style="display: inline-block;width:51px;"><b>Job Site</b></span><input type="text" name="jobSite_name" style="width:425px" />
						</td>
						<td align="left" style="width:480px;">
							<span style="display: inline-block;width:51px;"><b>Bill To</b></span><input type="text" name="billTo_name" style="width:425px" />
						</td>
					</tr>
					<tr>
						<td align="left" style="width:480px;">
							<!--<webthing:addressPanel label="Job Site" namespace="jobSite" cssId="jobSite" page="job"/>-->
							<webthing:addressDisplayPanel cssId="jobSite" />
						</td>
						<td align="left" style="width:480px;">
							<!--<webthing:addressPanel label="Bill To"  namespace="billTo" cssId="billTo"  page="bill"/>-->
							<webthing:addressDisplayPanel cssId="billTo" />
						</td>
					</tr>
					<tr>
						<td align="left" style="width:480px;">
							<table style="width:100%;">
								<tr>
									<td><span id="jobSiteC1">Job Contact:</span></td>
									<td style="width:140px;"><input type="text" name="jobSite_jobContactName" style="width:125px" placeholder="<name>"/></td>
									<td colspan="2"><span name="jobSite_jobContactInfo" style="display: inline-block;width:170px;"></span></td>
								</tr>
								<tr>
									<td><span id="jobSiteC2">Site Contact:</span></td>
									<td style="width:140px;"><input type="text" name="jobSite_siteContactName" style="width:125px" placeholder="<name>"/></td>
									<td colspan="2"><span name="jobSite_siteContactInfo" style="display: inline-block;width:170px;"></span></td>
								</tr>
							</table>
						</td>
						<td align="left" style="width:480px;">
							<table style="width:100%;border:1px solid #FFFFFF">
								<tr>
									<td><span id="billToC1">Cont Contact:</span></td>
									<td style="width:140px;"><input type="text" name="billTo_contractContactName" style="width:125px" placeholder="<name>"/></td>
									<td colspan="2"><span name="billTo_contractContactInfo" style="display: inline-block;width:170px;"></span></td>
								</tr>
								<tr>
									<td><span id="billToC2">Billing Contact:</span></td>
									<td style="width:140px;"><input type="text" name="billTo_billingContactName" style="width:125px" placeholder="<name>"/></td>
									<td colspan="2"><span name="billTo_billingContactInfo" style="display: inline-block;width:170px;"></span></td>
								</tr>
							</table>
						</td>
					</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
				<div id="loadingJobsDiv" style="display:none;"><i class='fa fa-refresh fa-spin'></i></div>
					<table style="border:solid 1px #000000; margin-top:8px;" id="jobPanelHolder">
						<tbody>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td>
    				<input type="button" id="addJobRow" class="prettyWideButton" value="New Job" />
				</td>
			</tr>
			<tr>
				<td align="right">
					<table style="border:0;border-collapse:collapse;"  align="right">
						<tr>
							<td><input type="button" class="prettyWideButton" value="Cancel" id="quoteCancelButton" /></td>
							<td><input type="button" class="prettyWideButton" value="Save" id="quoteSaveButton" /></td>
							<td><input type="button" class="prettyWideButton" value="Save & Exit" id="quoteExitButton" /></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>  
		
		
	
		<webthing:jobActivateCancel page="QUOTE" namespace="activateModal" />
		
		<webthing:quotePrint modalName="printQuoteDiv" />
		
        <script type="text/javascript">   
		      $( document ).ready(function() {
					QUOTEUTILS.pageInit('<c:out value="${ANSI_QUOTE_ID}" />');
		        });
        </script>        
    </tiles:put>

</tiles:insert>

