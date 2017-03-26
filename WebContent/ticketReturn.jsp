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
        Ticket Return
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				text-align:center;
				padding:15px;
			}
			#displayTable {
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#jobProposal {
				border:solid 1px #000000;
			}
			#jobActivation {
				border:solid 1px #000000;
				height:100%;
			}
			#jobSchedule {
				border:solid 1px #000000;
				height:100%;
			}
			#billTo {
				border:solid 1px #000000;
			}
			#jobSite {
				border:solid 1px #000000;
			}
			#jobDates {
				border:solid 1px #000000;
			}
			#jobInvoice {
				border:solid 1px #000000;
			}
			td.jobTableCell {
				width:50%;
				vertical-align:top;
			}
			.formFieldDisplay {
				margin-left:30px;
			}
			#invoiceModal {
				display:none;
			}
			#displayTicketTable {
    			border-collapse: collapse;
				width:90%;
			}
			#displayInvoiceTable {
    			border-collapse: collapse;
				width:90%;
			}
			td, th {
    			border: 1px solid #dddddd;
    			text-align: left;
    			padding: 8px;
    		}
			.workPanel {
			width:95%;
			border:solid 1px #000000;
			display:none;
			}
			#selectPanel {
			width:95%;
			border:solid 1px #000000;
			}
        </style>
        
        <script type="text/javascript">
        $( document ).ready(function() {
			$("#panelSelector").change(function($event) {
				$(".workPanel").hide();
				$("#monitor").html("");
				var $selectedPanel = $('#panelSelector option:selected').val();					
        		if ($selectedPanel != '' ) {
        			$selectedId = "#" + $selectedPanel;
        			$($selectedId).fadeIn(1500);
        			$("#monitor").html("Displaying " + $selectedPanel);
        		} else {
        			$("#monitor").html("Hiding Everything");
        		}

			});
        
        
        var jqxhr = $.ajax({
			type: 'GET',
			url: 'ticket/677949',
			success: function($data) {
				populateTicketDetail($data.data);
				populateInvoiceDetail($data.data);
				populateJobData($data.data);
			},
			statusCode: {
				403: function($data) {
					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
				},
				500: function($data) {
     	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
     	    	} 
			},
			dataType: 'json'
		});
	
	function populateTicketDetail($data) {
		$("#ticketId").html($data.ticketDetail.ticketId);
		$("#actPricePerCleaning").html($data.ticketDetail.actPricePerCleaning);
		$("#totalVolPaid").html($data.ticketDetail.totalVolPaid);
		$("#actTax").html($data.ticketDetail.actTax);
		$("#totalTaxPaid").html($data.ticketDetail.totalTaxPaid);
		$("#ticketBalance").html($data.ticketDetail.balance);
		
	}
	function populateInvoiceDetail($data) {
		$("#invoiceId").html($data.invoiceDetail.invoiceId);
		$("#sumInvPpc").html($data.invoiceDetail.sumInvPpc);
		$("#sumInvPpcPaid").html($data.invoiceDetail.sumInvPpcPaid);
		$("#sumInvTax").html($data.invoiceDetail.sumInvTax);
		$("#sumInvTaxPaid").html($data.invoiceDetail.sumInvTaxPaid);
		$("#invoiceBalance").html($data.invoiceDetail.balance);
		
	}
        
        
	function populateJobData($data) {
			JOB_UTILS.pageInit('<c:out value="${ANSI_JOB_ID}" />');
			console.debug(JOB_DATA.invoiceStyleList);
			JOBINVOICE.init("invoiceModal", 
					JOB_DATA.invoiceStyleList, 
					JOB_DATA.invoiceGroupingList, 
					JOB_DATA.invoiceTermList, 
					JOB_DATA.jobDetail);
			
			$("#jobNbr").focus();
		}
      });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Ticket Return</h1>
    	
    	<div  id="selectPanel">
			<select id="panelSelector">
				<option value=""></option>
				<option value="newStatus">New Status Panel</option>
				<option value="completeTicket">Complete Ticket Panel</option>
				<option value="skipTicket">Skip Ticket Panel</option>
				<option value="voidTicket">Void Ticket Panel</option>
				<option value="rejectTicket">Reject Ticket Panel</option>
				<option value="requeueTicket">ReQueue Ticket Panel</option>
			</select>
			<span id="monitor"></span>
		</div>
		
		<div class="workPanel" id="newStatus">
			<h2>THis is panel 1</h2>
		</div>
  	
		<div class="workPanel" id="completeTicket">
			<h2>THis is panel 2</h2>
		</div>

		<div class="workPanel" id="skipTicket">
			<h2>THis is panel 3</h2>
		</div>
		<div class="workPanel" id="voidTicket">
			<h2>THis is panel 4</h2>
		</div>
  	
		<div class="workPanel" id="rejectTicket">
			<h2>THis is panel 5</h2>
		</div>

		<div class="workPanel" id="requeueTicket">
			<h2>THis is panel 6</h2>
		</div>
    	
    	
    	<table id="displayTicketTable">
    		<tr>
    			<th>Ticket</th>
    			<th>Ticket Amt</th>
    			<th>Ticket Paid</th>
    			<th>Ticket Tax</th>
    			<th>Tax Paid</th>
    			<th>Balance</th>
    		</tr>
    		<tr>
    			<td><span id="ticketId"></span></td>
    			<td><span id="actPricePerCleaning"></span></td>
    			<td><span id="totalVolPaid"></span></td>
    			<td><span id="actTax"></span></td>
    			<td><span id="totalTaxPaid"></span></td>
    			<td><span id="ticketBalance"></span></td>
    		</tr>
    	</table>
    	
    	<table id="displayInvoiceTable">
    		<tr>
    			<th>Inv #</th>
    			<th>Inv Amt</th>
    			<th>Inv Paid</th>
    			<th>Tax Amt</th>
    			<th>Tax Paid</th>
    			<th>Balance</th>
    		</tr>
    		<tr>
    			<td><span id="invoiceId"></span></td>
    			<td><span id="sumInvPpc"></span></td>
    			<td><span id="sumInvPpcPaid"></span></td>
    			<td><span id="sumInvTax"></span></td>
    			<td><span id="sumInvTaxPaid"></span></td>
    			<td><span id="invoiceBalance"></span></td>
    		</tr>
    	</table>
    	
    	
		<table style="border:solid 1px #000000; margin-top:8px;" id="jobPanelHolder">
			<tbody>
				<tr><td>&nbsp;</td></tr>
			</tbody>
		</table> 
    	
    </tiles:put>

</tiles:insert>

