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

<%@ page import="com.ansi.scilla.common.jobticket.TicketStatus" %>

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
			#displaySummaryTable {
    			border-collapse: collapse;
				width:90%;
			}
			#selectPanel {
				width:100%;
				display:none;
			}
			#summaryTable{
				width:100%;
				display:none;
			}
			#ticketTable {
				width:100%;
				display:none;
			}
			#invoiceTable {
				width:100%;
				display:none;
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
        </style>
        
        <script type="text/javascript">
        $( document ).ready(function() {
        	
        	var $defaultTicketNbr='<c:out value="${ANSI_TICKET_ID}" />';
			var $ticketStatusList = ANSI_UTILS.getOptions("TICKET_STATUS");
			var $globalTicketId = 0;
			var $ticketStatusMap = {}
			$.each($ticketStatusList.ticketStatus, function($index, $value) {
			    $ticketStatusMap[$value.code]=$value.display;
			});
			
			
        	$("#doPopulate").click(function () {
    			var $ticketNbr = $('#ticketNbr').val();
    			$globalTicketId = $('#ticketNbr').val();
    			if ($ticketNbr != '') {            		
            		doPopulate($ticketNbr)
            	}
            });
        	
        	
        	
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });

        	$('#ticketNbr').focus(function($event) {
        		$('.workPanel').hide();
        		clearAddForm();
        		$(this).select();	
        	});
        	
			function populateTicketDetail($data) {
				$("#ticketId").html($data.ticketDetail.ticketId);
				$("#actPricePerCleaning").html($data.ticketDetail.actPricePerCleaning);
				$("#totalVolPaid").html($data.ticketDetail.totalVolPaid);
				$("#actTax").html($data.ticketDetail.actTax);
				$("#totalTaxPaid").html($data.ticketDetail.totalTaxPaid);
				$("#ticketBalance").html($data.ticketDetail.balance);

			}			
			
			function populatePanelSelect ($data) {
				$('option', "#panelSelector").remove();
				if ($data.ticketDetail.status=='N') {
					$("#panelSelector").hide();
					$("#REJECTED").fadeIn(1500);
				} else if ($data.ticketDetail.status=='C') {
					$("#panelSelector").hide();
				} else if ($data.ticketDetail.status=='I') {
					$("#panelSelector").hide();
				} else if ( $data.ticketDetail.nextAllowedStatusList.length == 0 ) {
					$("#panelSelector").hide();
				} else if ( $data.ticketDetail.nextAllowedStatusList.length == 1 ) {
					$panelId = "#" + $data.ticketDetail.nextAllowedStatusList[0];
					$("#panelSelector").hide();
					$($panelId).fadeIn(1500);
				} else {
					$("#panelSelector").append(new Option("", ""));
	                $.each($data.ticketDetail.nextAllowedStatusList, function($index, $status) {
						$("#panelSelector").append(new Option($status, $status));
	                });
	                $("#panelSelector").show();
				}
				
			}
			
			function populateInvoiceDetail ($data) {
				if ($data.invoiceDetail) {
					$("#invoiceId").html($data.invoiceDetail.invoiceId);					
					$("#sumInvPpc").html($data.invoiceDetail.sumInvPpc);
					$("#sumInvPpcPaid").html($data.invoiceDetail.sumInvPpcPaid);
					$("#sumInvTax").html($data.invoiceDetail.sumInvTax);
					$("#sumInvTaxPaid").html($data.invoiceDetail.sumInvTaxPaid);
					$("#invoiceBalance").html($data.invoiceDetail.balance);
	                $("#invoiceTable").show();
					$("#invoiceTable").fadeIn(4000);  
				}else{
					$("#invoiceTable").hide();				
				}				
			}
			
			function populateSummary($data) {
				$("#status").html($ticketStatusMap[$data.ticketDetail.status] + " (" + $data.ticketDetail.status + ")");
				$("#divisionDisplay").html($data.ticketDetail.divisionDisplay);
				$("#jobId").html($data.ticketDetail.jobId);				
			}
			
        	function doPopulate($ticketNbr) {    		
		       	var jqxhr = $.ajax({
		       		type: 'GET',
		       		url: "ticket/" + $ticketNbr,
		       		//data: $ticketNbr,
		       		success: function($data) {
						$.each($data.data.ticketList, function(index, value) {
							addRow(index, value);
						});
						$(".workPanel").hide();
		       			populateTicketDetail($data.data);	       			
		       			populateSummary($data.data);
		       			populatePanelSelect($data.data);
    					$("#summaryTable").fadeIn(4000);
    					$("#selectPanel").fadeIn(4000);
    					$("#ticketTable").fadeIn(4000);
		       			populateInvoiceDetail($data.data);	  					
					},
		       		statusCode: {
	       				404: function($data) {
	        	    		$("#globalMsg").html("Invalid Ticket Number").show().fadeOut(6000);
	        	    	},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again").show();
						},
		       			500: function($data) {
	            	    	$("#globalMsg").html("System Error: Contact Support").show();
	            		},
		       		},
		       		dataType: 'json'
		       	});
        	}
        	
        	function addRow(index, $ticket) {	
				var $rownum = index + 1;
       			rowTd = makeRow($ticket, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#displayTable').append(row);
			}
        	
        	
			$('#row_dim').hide(); 
    			$('#type').change(function(){
        			if($('#type').val() == 'parcel') {
           				 $('#row_dim').show(); 
        			} else {
            			$('#row_dim').hide(); 
        		} 
    		});
			
    			
			$("#panelSelector").change(function($event) {
				$(".workPanel").hide();
				var $selectedPanel = $('#panelSelector option:selected').val();		
        		if ($selectedPanel != '' ) {
        			$selectedId = "#" + $selectedPanel;
        			$($selectedId).fadeIn(1500);
        		}
			});
			
			
			$(".cancelUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				$(".clearMe").val("");
				$(".err").html("");
				clearAddForm();
			});
			
			
	
			$(".goUpdate").click( function($clickevent) {
				// the goButton -- $(this) -- tells us which panel is being submitted
				// loop through all input in that panel and put the values in $outbound
				var $panelName = $(this).data('panel');
				var $inputSelector = "#" + $panelName + " :input";				

				$outbound = {};
				$.each( $($inputSelector), function(index, value) {
					if ( value.name ) {
						var $fieldName = value.name;
						var $fieldValue = $(this).val();
						if ( $(this).attr("type") == "checkbox" ) {
							$fieldValue = $(this).prop("checked");
						}
						$outbound[$fieldName]=$fieldValue;
						if ( $fieldName == "defaultActDlPct" ) { //gag
							$outbound["actDlPct"] = $fieldValue;	//gag						
						} //gag
					}
				});


            	$url = "ticket/" + $globalTicketId;

				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					statusCode: {
						200: function($data) {
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								$("#globalMsg").html("Update Complete").show().fadeOut(6000);
								$("#ticketNbr").focus();
							} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								$('.err').html("");
								$.each($data.data.webMessages, function(key, messageList) {
									var identifier = "#" + $panelName + " ." + key + "Err";
									msgHtml = "<ul>";
									$.each(messageList, function(index, message) {
										msgHtml = msgHtml + "<li>" + message + "</li>";
									});
									msgHtml = msgHtml + "</ul>";
									$(identifier).html(msgHtml);
								});	
								$("#globalMsg").html($data.responseHeader.responseMessage).show().fadeOut(6000);
							} else {
								$("#globalMsg").html("Unexepected Response, Contact support (" + $data.responseHeader.responseMessage + ")").show();
							}
						},
	       				404: function($data) {
	        	    		$("#globalMsg").html("Invalid Ticket Number").show().fadeOut(6000);
	        	    	},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again").show();
						},
		       			500: function($data) {
	            	    	$("#globalMsg").html("System Error: Contact Support").show();
	            		},  
					},
					dataType: 'json'
				});
			});
			


            //$('#addForm').find("input").on('focus',function(e) {
            //	$required = $(this).data('required');
            //	if ( $required == true ) {
            //		markValid(this);
            //	}
           // });
            
           // $('#addForm').find("input").on('input',function(e) {
           // 	$required = $(this).data('required');
           // 	if ( $required == true ) {
           // 		markValid(this);
           // 	}
           // });
            
            function clearAddForm() {
				$.each( $('.addForm').find("input"), function(index, $inputField) {
					$('input[name=customerSignature]').prop('checked', false);
					$('input[name=billSheet]').prop('checked', false);
					$('input[name=mgrApproval]').prop('checked', false);
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						//markValid($inputField);
					}
				});
				//$('.err').html("");
            }
            
            //function markValid($inputField) {
            //	$fieldName = $($inputField).attr('name');
           // 	$fieldGetter = "input[name='" + $fieldName + "']";
            //	$fieldValue = $($fieldGetter).val();
            //	$valid = '#' + $($inputField).data('valid');
	        //    var re = /.+/;	            	 
            //	if ( re.test($fieldValue) ) {
            //		$($valid).removeClass("fa-ban");
            //		$($valid).removeClass("inputIsInvalid");
            //		$($valid).addClass("fa-check-square-o");
           // 		$($valid).addClass("inputIsValid");
            //	} else {
            //		$($valid).removeClass("fa-check-square-o");
            //		$($valid).removeClass("inputIsValid");
            //		$($valid).addClass("fa-ban");
            //		$($valid).addClass("inputIsInvalid");
            //	}
            //}
            
        	if ( $defaultTicketNbr != '' ) {
        		$("#ticketNbr").val($defaultTicketNbr);
        		$("#doPopulate").click();
        		$("#ticketNbr").focus();
        	}

    });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
		<h1>Ticket Return</h1>
    	
   		<div>
       		<span class="formLabel">Ticket:</span>
       		<input id="ticketNbr" name="ticketNbr" type="text" maxlength="10" />
       		<input id="doPopulate" type="button" value="Search" />
       	</div>
       	<div id="summaryTable">
	        <table id="displaySummaryTable">
	        	<tr>
	        		<th>Status</th><td><span id="status"></span></td>    		
	        		<th>Division</th><td><span id="divisionDisplay"></span></td>
	        		<th>Job ID</th><td><span id="jobId"></span></td>
	    		</tr>
			</table>
 		</div>
    	
    	<div id="selectPanel">
			<select id="panelSelector">
				<option value=""></option>
				<option value="completeTicket">Complete Ticket</option>
				<option value="skipTicket">Skip Ticket</option>
				<option value="voidTicket">Void Ticket</option>
				<option value="rejectTicket">Reject Ticket</option>
				<option value="requeueTicket">Re-Queue Ticket</option>
			</select>
		</div>
		
		
		<div class="workPanel" id="COMPLETED">
			<div id="addFormMsg" class="err"></div>
				<form action="#" method="post" class="addForm">
					<input type="hidden" name="newStatus" value="<%= TicketStatus.COMPLETED.code() %>" />
				    <table>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">Completion Date:</span></td>
				    		<td>
				    			<input type="text" class="dateField clearMe" name="processDate" data-required="true" data-valid="validProcessDate" />		    						
				    			<i id="validProcessDate" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err processDateErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">PPC:</span></td>
				    		<td>
				    			<input type="text" name="actPricePerCleaning" data-required="true" data-valid="validActPricePerCleaning" />
				    			<i id="validActPricePerCleaning" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err actPricePerCleaningErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">DL %:</span></td>
				    		<td>
				    			<input type="text" name="actDlPct" data-required="true" data-valid="validActDlPct" />
				    			<i id="validActActDlPct" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err actDlPctErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">Direct Labor:</span></td>
				    		<td>
				    			<input type="text" name="actDlAmt" data-required="true" data-valid="validActDlAmt" />
				    			<i id="validActDlAmt" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err actDlAmtErr"></span></td>
				    	</tr>
						<tr>
				    		<td><span class="formLabel">Completion Notes:</span></td>
				    		<td>
				    			<input type="text" name="processNotes"/>
				    		</td>
				    		<td><span class="err ProcessNotesErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="formLabel">Customer Signature:</span></td>
				    		<td>
				    			<input type="checkbox" name="customerSignature" />
				    		</td>
			    		</tr>
				    	<tr>
				    		<td><span class="formLabel">Bill Sheet:</span></td>
				    		<td>
				    			<input type="checkbox" name="billSheet"/>
				    		</td>
				    		<td><span class="err" id="billSheetErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="formLabel">Manager Approval:</span></td>
				    		<td>
				    			<input type="checkbox" name="mgrApproval"  />
				    		</td>
				    	</tr>
			    		<tr>
				    		<td colspan="2" style="text-align:center;">
				    			<input type="button" class="prettyButton goUpdate" value="Complete" data-panel="COMPLETED" />
				    			<input type="button" class="prettyButton cancelUpdate" value="Clear" />
				    		</td>
				    	</tr>
				    </table>
				</form>
			</div>
		
			<div class="workPanel" id="SKIPPED">
				<div id="addFormMsg" class="err"></div>
				<form action="#" method="post" class="addForm">
				<input type="hidden" name="newStatus" value="<%= TicketStatus.SKIPPED.code() %>" />
				<table>
			    	<tr>
			    		<td><span class="required">*</span><span class="formLabel">Skip Date:</span></td>
	  					<td>
	  						<input type="text" class="dateField clearMe" name="processDate" data-required="true" data-valid="validProcessDate" />
	  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
	  					</td>
	  					<td><span class="err processDateErr"></span></td>
	  				</tr>
					<tr>
		  					<td><span class="required">*</span><span class="formLabel">Skip Reason:</span></td>
		  					<td>
		  						<input type="text" class="textField clearMe" name="processNotes"/>
		  					</td>
		  					<td><span class="err processNotesErr"></span></td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton goUpdate" value="Skip" data-panel="SKIPPED" />
		  						<input type="button" class="prettyButton cancelUpdate" value="Clear" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
			
			
			<div class="workPanel" id="VOIDED">
				<div id="addFormMsg" class="err"></div>
				<form action="#" method="post" class="addForm">
				<input type="hidden" name="newStatus" value="<%= TicketStatus.VOIDED.code() %>" />
				<table>
	  				<tr>
	  					<td><span class="required">*</span><span class="formLabel">Void Date:</span></td>
	  					<td>
	  						<input type="text" class="dateField clearMe" name="processDate" data-required="true" data-valid="validProcessDate" />
	  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
	  					</td>
	  					<td><span class="err processDateErr"></span></td>
	  				</tr>
					<tr>
		  					<td><span class="required">*</span><span class="formLabel">Void Reason:</span></td>
		  					<td>
		  						<input type="text" class="textField clearMe" name="processNotes"/>
		  						<i id="validProcessNotes" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err processNotesErr"></span></td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton goUpdate" value="Void" data-panel="VOIDED"/>
		  						<input type="button" class="prettyButton cancelUpdate" value="Clear" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
		 	
			<div class="workPanel" id="REJECTED">
				<div id="addFormMsg" class="err"></div>
				<form action="#" method="post" class="addForm">
				<input type="hidden" name="newStatus" value="<%= TicketStatus.REJECTED.code() %>" />
				<table>
		  				<tr>
		  					<td><span class="required">*</span><span class="formLabel">Reject Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField clearMe" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err processDateErr"></span></td>
		  				</tr>
					<tr>
		  					<td><span class="required">*</span><span class="formLabel">Reject Reason:</span></td>
		  					<td><input type="text" class="textField clearMe" name="processNotes"/></td>
		  					<td><span class="err processNotesErr"></span></td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton goUpdate" value="Reject" data-panel="REJECTED" />
		  						<input type="button" class="prettyButton cancelUpdate" value="Clear" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
			
			
		
			<div class="workPanel" id="NOT_DISPATCHED">
				<div id="addFormMsg" class="err"></div>
				<form action="#" method="post" class="addForm">
				<input type="hidden" name="newStatus" value="<%= TicketStatus.NOT_DISPATCHED.code() %>" />
				<table>
		  				<tr>
		  					<td><span class="required">*</span><span class="formLabel">Process Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField clearMe" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err processDateErr"></span></td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton goUpdate" value="Re-Queue" data-panel="NOT_DISPATCHED" />
		  						<input type="button" class="prettyButton cancelUpdate" value="Clear" />
		  					</td>
		  				</tr>
		  			</table>			
			</div>
			
			<div class="workPanel" id="INVOICED"> </div>
			<div class="workPanel" id="PAID"> </div>
		   	
		<div id="ticketTable">
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
		   	
		</div>
		   	
		<div id = "invoiceTable">		   	
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
		</div>
    	
    </tiles:put>

</tiles:insert>

