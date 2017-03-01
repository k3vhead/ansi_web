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
        Quote
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <script type="text/javascript" src="js/quoteMaintenance.js"></script>
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
 

        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
		<table id="quoteTable" style="display:none;">
			<tr>
				<td colspan="2">
					<table >
						<tr>
							<td><input type="button" name="modifyButton" value="Modify" class="quoteButton"/></td>
							<td><span class="labelSpanSmall">Manager:</span>
								<input type="text" name="manager"  style="width:150px" disabled="disabled"/>
							</td>
							<td><span class="labelSpan">Division:</span>
								<select name="division" id="division" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td align="center">Quote</td>
							<td align="center">Revision</td>
							<td rowspan="2" align="right" style="padding-right:10px;"><input type="button" name="printButton" value="Print" class="quoteButton"/></td>
						</tr>
						<tr>
							<td><input type="button" name="copyButton" value="Copy" class="quoteButton"/></td>
							<td><span class="labelSpanSmall">Lead Type:</span>
								<select name="leadType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td><span class="labelSpan">Account Type:</span>
								<select name="accountType" id="accountType" class="quoteSelect">
									<option value=""></option>
								</select>
							</td>
							<td>Q:&nbsp;&nbsp;<input type="text" name="quoteNumber"  style="width:80px" value="<c:out value="${ANSI_QUOTE_ID}" />"/></td>
							<td>R:&nbsp;&nbsp;<input type="text" name="revision"  style="width:40px"/></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							
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
						<td align="center" style="width:480px;">
							<webthing:addressPanel label="Job Site" namespace="jobSite" cssId="jobSite" />
						</td>
						<td align="center" style="width:480px;">
							<webthing:addressPanel label="Bill To"  namespace="billTo" cssId="billTo" />
						</td>
					</table>
				</td>
			</tr>
			<tr>
				<td>		
					<table style="border:solid 1px #000000; margin-top:8px;" id="jobPanelHolder">
						<tbody>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td>
    				<input type="button" id="addJobRow" class="prettyWideButton" value="New" />
				</td>
			</tr>
			<tr>
				<td align="right">
					<table style="border:0;border-collapse:collapse;"  align="right">
						<tr>
							<td><input type="button" class="prettyWideButton" value="Cancel" id="jobCancelButton" /></td>
							<td><input type="button" class="prettyWideButton" value="Save" id="jobSaveButton" /></td>
							<td><input type="button" class="prettyWideButton" value="Save & Exit" id="jobExitButton" /></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>  
		
		
	
		<span id="modalSpan"></span>
		
		
        <script type="text/javascript">   
		      $( document ).ready(function() {
					QUOTEUTILS.pageInit('<c:out value="${ANSI_QUOTE_ID}" />');
		        });
        </script>        
    </tiles:put>

</tiles:insert>

