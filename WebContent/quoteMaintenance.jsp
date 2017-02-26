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
        Quote Panel Demo
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
 
 

        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
		<table id="quoteTable">
			<tr>
				<td colspan="2">
					<table >
						<tr>
							<td><input type="button" name="modifyButton" value="Modify" class="quoteButton"/></td>
							<td><span class="labelSpanSmall">Manager:</span>
								<input type="text" name="manager"  style="width:95px"/>
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
		</table>  
		
		<input type="button" id="addJobRow" value="Add a Job" />		
		<table style="border:solid 1px #000000; margin-top:8px;" id="jobPanelHolder">
			<tbody>
			</tbody>
		</table>  
				
  	
		
        <script type="text/javascript">   
		      $( document ).ready(function() {

					$("select[name='division']").selectmenu({ width : '100px'});
					$("select[name='leadType']").selectmenu({ width : '100px'});
					$("select[name='accountType']").selectmenu({ width : '100px'});
					
				
					$optionData = ANSI_UTILS.getOptions('JOB_FREQUENCY,JOB_STATUS,INVOICE_TERM,INVOICE_GROUPING,INVOICE_STYLE,COUNTRY');
					var $jobFrequencyList = $optionData.jobFrequency;
					var $jobStatusList = $optionData.jobStatus;
					var $invoiceTermList = $optionData.invoiceTerm;
					var $invoiceGroupingList = $optionData.invoiceGrouping;
					var $invoiceStyleList = $optionData.invoiceStyle;
					var $countryList = $optionData.country;
					//var $accountTypeList = $optionData.accountType;
					
					var $accountTypeList = ANSI_UTILS.getCodes("quote","account_type");
					var $leadTypeList = ANSI_UTILS.getCodes("quote","lead_type");
					
					
					$divisionList = ANSI_UTILS.getDivisionList();
					$buildingTypeList = ANSI_UTILS.makeBuildingTypeList();
					
					console.log($divisionList);
					
					var $jobDetail = null;			
					var $quoteDetail = null;
					var $lastRun = null;
					var $nextDue = null;
					var $lastCreated = null;
					$jobSiteDetail = null;
					$billToDetail = null;
					/*
					if ( '<c:out value="${ANSI_JOB_ID}" />' != '' ) {
						$jobData = JOBUTILS.getJobDetail('<c:out value="${ANSI_JOB_ID}" />');				
						$jobDetail = $jobData.job;
						$quoteDetail = $jobData.quote;
						$lastRun = $jobData.lastRun;
						$nextDue = $jobData.nextDue;
						$lastCreated = $jobData.lastCreated;
					}
					*/

					
					JOBPANEL.init("jobPanel", $divisionList, $jobDetail);
					JOBPROPOSAL.init("jobProposal", $jobFrequencyList, $jobDetail);
					JOBACTIVATION.init("jobActivation", $buildingTypeList, $jobDetail);
					JOBDATES.init("jobDates", $quoteDetail, $jobDetail);
					JOBSCHEDULE.init("jobSchedule", $lastRun, $nextDue, $lastCreated)
					//JOBINVOICE.init("jobInvoice", $invoiceStyleList, $invoiceGroupingList, $invoiceTermList, $jobDetail);
					JOBAUDIT.init("jobAudit", $jobDetail);
					ADDRESSPANEL.init("jobSite", $countryList, $jobSiteDetail);
					ADDRESSPANEL.init("billTo", $countryList, $billToDetail);
					
					
					var $select = $("select[name='accountType']");
					$select.selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
					
					$('option', $select).remove();
					$.each($accountTypeList.codeList, function($index, $accountType) {
						$select.append(new Option($accountType.displayValue));
					});

					$select.selectmenu();
					
					$select = $("select[name='leadType']");
					$select.selectmenu({ width : '120px', maxHeight: '400 !important', style: 'dropdown'});
					
					$('option', $select).remove();
					$.each($leadTypeList.codeList, function($index, $leadType) {
						$select.append(new Option($leadType.displayValue));
					});

					$select.selectmenu();
					
					$select = $("select[name='division']");
					$select.selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
					
					$('option', $select).remove();
					$.each($divisionList, function($index, $division) {
						$select.append(new Option($division.divisionCode));
					});

					$select.selectmenu();
					
					if ( '<c:out value="${ANSI_QUOTE_ID}" />' != '' ) {
						$quoteData = QUOTEUTILS.getQuoteDetail('<c:out value="${ANSI_QUOTE_ID}" />');
						$quoteData = $quoteData.quoteList[0];
						console.log($quoteData);
						if($quoteData.billToAddressId != ''){
							var $billToData = ADDRESSPANEL.getAddress($quoteData.billToAddressId);
							//console.log($billToData[0]);
							ADDRESSPANEL.setAddress("billTo",$billToData[0]);
						}
						if($quoteData.jobSiteAddressId != ''){
							var $jobSiteData = ADDRESSPANEL.getAddress($quoteData.jobSiteAddressId);
							//console.log($jobSiteData[0]);
							ADDRESSPANEL.setAddress("jobSite",$jobSiteData[0]);
						}
						if($quoteData.divisionId){
							$divisionData = getDivision($quoteData.divisionId);
							$divisionData = $divisionData.divisionList[0];
							if($divisionData.divisionCode != null){
								$("select[name='division']").val($divisionData.divisionCode)
								;
								$("select[name='division").selectmenu("refresh");
							}
							console.log($divisionData);
						}
						//$('input[name=quoteNumber]').val('<c:out value="${ANSI_QUOTE_ID}" />');
					/*	$jobDetail = $jobData.job;
						$quoteDetail = $jobData.quote;
						$lastRun = $jobData.lastRun;
						$nextDue = $jobData.nextDue;
						$lastCreated = $jobData.lastCreated;
						*/
					}
					
					
					$("#jobNbr").focus();
					var $currentRow = 0;
					$("#addJobRow").click(function(){
						var $namespace = "jobpanel" + $currentRow.toString();
						$currentRow++;
						var jqxhr1 = $.ajax({
							type: 'GET',
							url: 'quotePanel.html',
							data: {"panelname":$namespace,"page":"QUOTE"},
							success: function($data) {
								//console.log($data);
								
								$('#jobPanelHolder > tbody:last-child').append($data);
								$('#addressTable').remove();
								JOBPANEL.init($namespace+"_jobPanel", $divisionList, "activateModal", $jobDetail);
								JOBPROPOSAL.init($namespace+"_jobProposal", $jobFrequencyList, $jobDetail);
								JOBACTIVATION.init($namespace+"_jobActivation", $buildingTypeList, $jobDetail);
								JOBDATES.init($namespace+"jobDates", $quoteDetail, $jobDetail);
								JOBSCHEDULE.init($namespace+"_jobSchedule", $jobDetail, $lastRun, $nextDue, $lastCreated)
								JOBINVOICE.init($namespace+"_jobInvoice", $invoiceStyleList, $invoiceGroupingList, $invoiceTermList, $jobDetail);
								JOBAUDIT.init($namespace+"_jobAudit", $jobDetail);
								
								bindAndFormat();
								
								
							},
							statusCode: {
								403: function($data) {
									$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
								} 
							},
							dataType: 'html'
						});
							
					});
					
					
				function bindAndFormat(){
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
				}
				
				function getDivision($divisionId) {
					var $returnValue = null;
					if ( $divisionId != null ) {
						var $url = "division/" + $divisionId
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							data: {},
							statusCode: {
								200: function($data) {
									$returnValue = $data.data;
								},					
								403: function($data) {
									$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
								},
								404: function($data) {
									$returnValue = {};
								},
								500: function($data) {
									
								}
							},
							dataType: 'json',
							async:false
						});
					}
					return $returnValue;

				}
				bindAndFormat();
		        });
        </script>        
    </tiles:put>

</tiles:insert>

