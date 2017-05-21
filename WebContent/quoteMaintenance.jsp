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
        <script type="text/javascript" src="js/quotePrintHistory.js"></script>
        
        <%
			String quotePrintModal = "";
		%>
        
        <style type="text/css">

			#delData {
				margin-bottom: 15px;
				margin-top: 15px;
			 }
			  
			#quoteTable {border: solid 1px #000000;}
 
			 #printButton {
			 	cursor:pointer;
		 	}
			.quoteButton {
				background-color: #F0F0F0;
				color: #000000;
				min-height: 30px;
				width: 120px;
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
			 
			 .saveIcon{
			 float:right;
			 }
			 
			 .grey {
			 color: #696969;
			 }
			 
			 
			 .green {
			 color:green;
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
	
			.ui-accordion .ui-accordion-header {
				background: #c5c5c5 !important;
				border: #c5c5c5 !important;
			}
	
			#printHistoryDiv {
				display:none;
			}
			#viewPrintHistory {
				cursor:pointer;
			}
			#printQuoteDiv {
				display:none;
			}
        </style>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Quote Maintenance</h1>
    	<div id="loadingDiv"><i class='fa fa-refresh fa-spin'></i></div>
		<table id="quoteTable" valign="top" style="display:none;vertical-align:top;">
			<tr>
				<td colspan="2" valign="top" style="vertical-align:top;">
					<jsp:include page="quoteMaintenance/quoteTable_hdr.jsp" /><i id='QuoteSaveHead' class='fa fa-floppy-o saveIcon grey' aria-hidden='true'>
				</td>
			</tr>
			<tr>
				<td colspan='2' align="left">
					<jsp:include page="quoteMaintenance/quoteTable_contacts.jsp" />	
				</td>
			</tr>
			<tr>
				<td>
					<div id="loadingJobsDiv" style="display:none;"><i class='fa fa-refresh fa-spin'></i></div>
					<div id="accordian">
					</div>
				</td>
			</tr>
			<tr>
				<td>
    				<input type="button" id="addJobRow" class="prettyWideButton" value="New Job" />
				</td>
			</tr>
			<tr>
				<td align="right">
					<jsp:include page="quoteMaintenance/quoteTable_buttons.jsp" />
				</td>
			</tr>
		</table>  
		
		
	
		<webthing:jobActivateCancel page="QUOTE" namespace="activateModal" />
		
		<webthing:quotePrint modalName="printQuoteDiv" />
		
		<webthing:quotePrintHistory modalName="printHistoryDiv" />
		
        <script type="text/javascript">   
		      $( document ).ready(function() {
					QUOTEUTILS.pageInit('<c:out value="${ANSI_QUOTE_ID}" />');
					QUOTE_PRINT_HISTORY.init("#printHistoryDiv", "#viewPrintHistory");
		        });
        </script>        
    </tiles:put>

</tiles:insert>

