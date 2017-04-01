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
        	$('#doPopulate').click(function () {
    			var $ticketNbr = $('#ticketNbr').val();
            	if ($ticketNbr != '') {
            		doPopulate($ticketNbr)
            	}
            });
        		
        		
        	function doPopulate($ticketNbr) {
        		
        		
		       	var jqxhr = $.ajax({
		       		type: 'GET',
		       		url: "ticket/" + $ticketNbr,
		       		//data: $ticketNbr,
		       		success: function($data) {
		       			populateTicketDetail($data.data);
		       			populateInvoiceDetail($data.data);
		       		},
		       		statusCode: {
	       				404: function($data) {
	        	    		$("#globalMsg").html("Bad Ticket Number").fadeIn(10);
	        	    	},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
		       			500: function($data) {
	            	    	$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
	            		},
		       		},
		       		dataType: 'json'
		       	});
        	}
        	
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
	
	
			$("#cancelUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				$('#addFormTicket').bPopup().close();
			});
	
	
			$("#goUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				$outbound = {};
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				 })
			});
	
			var jqxhr = $.ajax({
				type: 'POST',
				url: "ticket/" + $ticketNbr,
				data: JSON.stringify($outbound),
				success: function($data) {
				},
				statusCode: {
       				404: function($data) {
        	    		$("#globalMsg").html("Bad Ticket Number").fadeIn(10);
        	    	},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Log in and try again");
					},
	       			500: function($data) {
            	    	$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
            		},  
				},
				dataType: 'json'
			});
        
	
    });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Ticket Return</h1>
    	
    	<form id="form">
    		<div>
        		<input id="ticketNbr" name="ticketNbr" type="text"/>
    		</div>
		</form>
 		<input id="doPopulate" type="button" value="Search" />
    	
    	<div  id="selectPanel">
			<select id="panelSelector">
				<option value=""></option>
				<option value="completeTicket">Complete Ticket</option>
				<option value="skipTicket">Skip Ticket</option>
				<option value="voidTicket">Void Ticket</option>
				<option value="rejectTicket">Reject Ticket</option>
				<option value="requeueTicket">Re-Queue Ticket</option>
			</select>
		</div>
  	
		<div class="workPanel" id="completeTicket">
			<form action="#" method="post" id="addForm">
		    			<table>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Completion Date:</span></td>
		    					<td>
		    						<input type="text" name="completionDate"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">PPC:</span></td>
		    					<td>
		    						<input type="text" name="actPricePerCleaning"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">DL %:</span></td>
		    					<td>
		    						<input type="text" name="defaultActDlPct"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Direct Labor:</span></td>
		    					<td>
		    						<input type="text" name="actDlAmt"/>
		    					</td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Completion Notes:</span></td>
		    					<td>
		    						<input type="text" name="processNotes"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Customer Signature:</span></td>
		    					<td>
		    						<input type="checkbox" name="customerSignature" />
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Bill Sheet:</span></td>
		    					<td>
		    						<input type="checkbox" name="billSheet"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Manager Approval:</span></td>
		    					<td>
		    						<input type="checkbox" name="mgrApproval"/>
		    					</td>
		    				</tr>
		    				
		    				
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Complete" id="goUpdate" data-panel="complete" />
		    						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		    					</td>
		    				</tr>
		    			</table>
		    		</form>
		</div>

		<div class="workPanel" id="skipTicket">
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Skip Date:</span></td>
		    					<td>
		    						<input type="text" name="skipDate"/>
		    					</td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Skip Reason:</span></td>
		    					<td>
		    						<input type="text" name="description"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Skip" id="goUpdate"data-panel="skip" />
		    						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		    					</td>
		    				</tr>
		</div>
		<div class="workPanel" id="voidTicket">
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Void Date:</span></td>
		    					<td>
		    						<input type="text" name="divisionNbr"/>
		    					</td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Void Reason:</span></td>
		    					<td>
		    						<input type="text" name="description"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Void" id="goUpdate" data-panel="void"/>
		    						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		    					</td>
		    				</tr>
		</div>
  	
		<div class="workPanel" id="rejectTicket">
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Reject Date:</span></td>
		    					<td>
		    						<input type="text" name="divisionNbr"/>
		    					</td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Reject Reason:</span></td>
		    					<td>
		    						<input type="text" name="description"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Reject" id="goUpdate" data-panel="reject" />
		    						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		    					</td>
		    				</tr>
		</div>

		<div class="workPanel" id="requeueTicket">
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Process Date:</span></td>
		    					<td>
		    						<input type="text" name="divisionNbr"/>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Re-Queue" id="goUpdate" data-panel="requeue" />
		    						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		    					</td>
		    				</tr>
			
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
    	
    </tiles:put>

</tiles:insert>

