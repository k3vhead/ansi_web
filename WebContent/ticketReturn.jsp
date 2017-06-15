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
    	<script type="text/javascript" src="js/addressUtils.js"></script>
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
			#jobId {
				cursor:pointer;
				text-decoration:underline;
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
    		#displaySummaryTable th {
    			white-space:nowrap;
    		}
			.workPanel {
				width:95%;
				border:solid 1px #000000;
				display:none;
			}
			.ansi-address-label {
				font-weight:bold;
			}
			.bottomRow {
				border-bottom:solid 1px #000000;
			}
			.editApprovals {
				cursor:pointer;
			}
			#editApprovalsModal {
				display:none;
			}
        </style>
        
        <script type="text/javascript">
        $( document ).ready(function() {
        	
        	GLOBAL_DATA = {};
        	var $defaultTicketNbr='<c:out value="${ANSI_TICKET_ID}" />';
			var $ticketStatusList = ANSI_UTILS.getOptions("TICKET_STATUS");
			var $globalTicketId = 0;
			var $ticketStatusMap = {}
			$.each($ticketStatusList.ticketStatus, function($index, $value) {
			    $ticketStatusMap[$value.code]=$value.display;
			});
			
			
			
			$("#editApprovalsModal").dialog({
				title:'Edit Approvals',
				autoOpen: false,
				height: 300,
				width: 400,
				modal: true,
				buttons: [
					{
						id: "cancelModal",
						click: function() {
							$("#editApprovalsModal").dialog( "close" );
						}
					},{
						id: "saveModal",
						click: function($event) {
							saveApprovals();
						}
					}
				],
				close: function() {
					$("#editApprovalsModal").dialog( "close" );
				}
			});
    		$('#saveModal').button('option', 'label', 'Save');
    		$('#cancelModal').button('option', 'label', 'Cancel');

			
			
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
        		$(this).select();	
        	});
        	
        	$("input[type=text]").focus(function() {
        		$(this).select();
        	});
        	
        	
        	
        	
            var $ticketComplete = $( "#ticketNbr" ).autocomplete({
				source: "ticketTypeAhead",
                minLength: 3,
                appendTo: "#someTicket",
                select: function( event, ui ) {
                	var $ticketNbr = ui.item.id;
                	$globalTicketId = $ticketNbr
					$("#ticketNbr").val($ticketNbr);
            		doPopulate($ticketNbr);
                },
                response: function(event, ui) {
                    if (ui.content.length === 0) {
                    	$("#globalMsg").html("No Matching Ticket");
                    	clearTicketData()
                    } else {
                    	$("#globalMsg").html("");
                    }
                }
          	}).data('ui-autocomplete');
			$ticketComplete._renderMenu = function( ul, items ) {
				var that = this;
				$.each( items, function( index, item ) {
					that._renderItemData( ul, item );
				});
				if ( items.length == 1 ) {
					var $ticketNbr = items[0].id;
					$globalTicketId = $ticketNbr
					$("#ticketNbr").val($ticketNbr);
					$("#ticketNbr").autocomplete("close");
            		doPopulate($ticketNbr);
				}
			}

        	
        	
        	
			$(".editApprovals").click(function($event) {
				$("#editApprovalsModal input[name='modalCustomerSignature']").prop("checked", false);
				$("#editApprovalsModal input[name='modalManagerApproval']").prop("checked", false);
				$("#editApprovalsModal input[name='modalBillSheet']").prop("checked", false);
				
				if ( GLOBAL_DATA['globalTicket'].customerSignature == true ) {
					$("#modalCustomerSignature").show();
					$("#editApprovalsModal input[name='modalCustomerSignature']").hide();
				} else {
					$("#modalCustomerSignature").hide();
					$("#editApprovalsModal input[name='modalCustomerSignature']").show();					
				}
				if ( GLOBAL_DATA['globalTicket'].billSheet == true ) {
					$("#modalBillSheet").show();
					$("#editApprovalsModal input[name='modalBillSheet']").hide();
				} else {
					$("#modalBillSheet").hide();
					$("#editApprovalsModal input[name='modalBillSheet']").show();
				}
				if ( GLOBAL_DATA['globalTicket'].mgrApproval == true ) {
					$("#modalManagerApproval").show();
					$("#editApprovalsModal input[name='modalManagerApproval']").hide();
				} else {
					$("#modalManagerApproval").hide();
					$("#editApprovalsModal input[name='modalManagerApproval']").show();
				}
				
				$("#editApprovalsModal").dialog("open");
			});
        	
        	
        	
			function populateTicketDetail($data) {
       			GLOBAL_DATA['globalTicket'] = $data.ticketDetail;
				$("#ticketId").html($data.ticketDetail.ticketId);
				$("#actPricePerCleaning").html($data.ticketDetail.actPricePerCleaning);
				$("#totalVolPaid").html($data.ticketDetail.totalVolPaid);
				$("#actTax").html($data.ticketDetail.actTax);
				$("#totalTaxPaid").html($data.ticketDetail.totalTaxPaid);
				$("#ticketBalance").html($data.ticketDetail.balance);
				
				$("#completedRow").hide();
				if ( $data.ticketDetail.status=='N') {
					$("#processNotesRow").hide();
				} else {
					$("#processNotesRow").show();
					$("#processNotesRow td").addClass("bottomRow");
					if ( $data.ticketDetail.status == 'R' ) {
						$processLabel = "Reject Date:";
					} else if ( $data.ticketDetail.status == 'D' ) {
						$processLabel = "Dispatch Date:";
					} else if ( $data.ticketDetail.status == 'V' ) {
						$processLabel = "Void Date:";
					} else if ( $data.ticketDetail.status == 'S' ) {
						$processLabel = "Skip Date:";
					} else if ( $data.ticketDetail.status == 'C' ) {
						$processLabel = "Complete Date:";
						$("#processNotesRow td").removeClass("bottomRow");
						$("#completedRow").show();
						if ( $data.ticketDetail.customerSignature == true ) {
							markValid($("#customerSignature"));
						} else {
							markInvalid($("#customerSignature"));
						}
						if ( $data.ticketDetail.billSheet == true ) {
							markValid($("#billSheet"));
						} else {
							markInvalid($("#billSheet"));
						}
						if ( $data.ticketDetail.mgrApproval == true ) {
							markValid($("#managerApproval"));
						} else {
							markInvalid($("#managerApproval"));
						}
						
						if ($data.ticketDetail.customerSignature == true && $data.ticketDetail.billSheet == true && $data.ticketDetail.mgrApproval == true) {
							$(".editApprovals").hide();
						} else {
							$(".editApprovals").show();
						}
						
					} else if ( $data.ticketDetail.status == 'I' ) {
						$processLabel = "Invoice Date:";
					} else if ( $data.ticketDetail.status == 'P' ) {
						$processLabel = "Paid Date:";
					} else {
						$processLabel = "Process Date (" + $data.ticketDetail.status + ")";
					}
					
					$("#processDateLabel").html($processLabel);
					$("#processDate").html($data.ticketDetail.processDate);
					$("#processNotes").html($data.ticketDetail.processNotes);
				}
			}			
			
			function populatePanelSelect ($data) {
				$('option', "#panelSelector").remove();
				if ($data.ticketDetail.status=='N') {
					$("#panelSelector").hide();
					$("#REJECTED").fadeIn(1500);
//				} else if ($data.ticketDetail.status=='C') {
//					$("#panelSelector").hide();
				} else if ($data.ticketDetail.status=='I') {
					$("#panelSelector").hide();
				} else if ( $data.ticketDetail.nextAllowedStatusList.length == 0 ) {
					$("#panelSelector").hide();
//				} else if ( $data.ticketDetail.nextAllowedStatusList.length == 1 ) {
//					$panelId = "#" + $data.ticketDetail.nextAllowedStatusList[0];
//					$("#panelSelector").hide();
//					$($panelId).fadeIn(1500);
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
				//$("#jobId").attr("data-jobid",$data.ticketDetail.jobId);
				$( "#jobId" ).on( "click", function($clickevent) {
					 location.href="jobMaintenance.html?id=" + $data.ticketDetail.jobId;
				});
				$("#serviceDescription").html($data.ticketDetail.serviceDescription);
				$("#jobFrequency").html($data.ticketDetail.jobFrequency);
				$("#invoiceStyle").html($data.ticketDetail.invoiceStyle);
			}

			
			function populateDefaultValues($data) {
				$actPricePerCleaning = $data.ticketDetail.actPricePerCleaning.substring(1).replace(/,/,"");
       			if ( $data.actDlPct == null ) {
       				$actDlPct = $data.ticketDetail.defaultDirectLaborPct;
       			} else {i
       				$actDlPct = $data.ticketDetail.actDlPct;
       			}
       			if ($data.ticketDetail.actDlAmt == null ) {
       				if ( $actPricePerCleaning == null ) {
       					$actDlAmt = null;
       				} else {
       					$actDlAmt = $actPricePerCleaning.substring(1).replace(/,/,"");
       				}	
       			} else {
       				$actDlAmt = $data.ticketDetail.actDlAmt.substring(1).replace(/,/,"");
       			}
   				
   				$("#COMPLETED input[name=actPricePerCleaning]").val($actPricePerCleaning);
   				$("#COMPLETED input[name=actDlPct]").val($actDlPct);
   				$("#COMPLETED span[class=actDlPct]").html(($actDlPct * 100).toFixed(3));		       				
   				$("#COMPLETED input[name=actDlAmt]").val($actDlAmt);
			}

			
			function markValid($item) {
	    		$item.removeClass("fa");
	    		$item.removeClass("fa-ban");
				$item.removeClass("inputIsInvalid");

				$item.addClass("fa");
	    		$item.addClass("fa-check-square-o");
				$item.addClass("inputIsValid");
			}
			
			function markInvalid($item) {
				$item.removeClass("fa");
	    		$item.removeClass("fa-check-square-o");
				$item.removeClass("inputIsValid");

				$item.addClass("fa");
	    		$item.addClass("fa-ban");
				$item.addClass("inputIsInvalid");
			}
			
			$("#COMPLETED input[name=actPricePerCleaning]").change(function($event) {
				var $actPricePerCleaning = $("#COMPLETED input[name=actPricePerCleaning]").val();
				var $actDlPct = $("#COMPLETED input[name=actDlPct]").val();
				if ( isNaN($actPricePerCleaning) ) {
					markInvalid($("#validActPricePerCleaning"));
				} else {
					markValid($("#validActPricePerCleaning"));
					var $actDlAmt = ($actPricePerCleaning * $actDlPct);
					$("#COMPLETED input[name=actDlAmt]").val($actDlAmt.toFixed(2));					
				}
			});
			
			
			$("#COMPLETED input[name=actDlAmt]").change(function($event) {
				var $actPricePerCleaning = $("#COMPLETED input[name=actPricePerCleaning]").val();
				var $actDlAmt = $("#COMPLETED input[name=actDlAmt]").val();
				if ( isNaN($actDlAmt) ) {
					markInvalid($("#validActDlAmt"));
				} else {
					markValid($("#validActDlAmt"));
					var $actDlPct = ($actDlAmt / $actPricePerCleaning);
					$("#COMPLETED input[name=actDlPct]").val($actDlPct);					
					$("#COMPLETED span[class=actDlPct]").html(($actDlPct*100).toFixed(3));
				}
			});
			
			
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
		       			populateDefaultValues($data.data);
						ADDRESS_UTILS.populateAddress("#jobSiteAddress", $data.data.ticketDetail.jobSiteAddress);
						ADDRESS_UTILS.populateAddress("#billToAddress", $data.data.ticketDetail.billToAddress);
		       			
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
				        		$("#doPopulate").click();
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
									$(identifier).html(msgHtml).show().fadeOut(6000);
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
			


            
            function clearAddForm() {
				$.each( $('.addForm').find("input"), function(index, $inputField) {
					$('input[name=customerSignature]').prop('checked', false);
					$('input[name=billSheet]').prop('checked', false);
					$('input[name=mgrApproval]').prop('checked', false);
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
					}
				});
            }
            

			function saveApprovals() {
				var $outbound = {}
				$outbound['customerSignature'] = $("#editApprovalsModal input[name='modalCustomerSignature']").prop("checked");
				$outbound['managerApproval'] = $("#editApprovalsModal input[name='modalManagerApproval']").prop("checked");
				$outbound['billSheet'] = $("#editApprovalsModal input[name='modalBillSheet']").prop("checked");

				
            	$url = "ticketApprovalOverride/" + $globalTicketId;

				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					statusCode: {
						200: function($data) {
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								$("#editApprovalsModal").dialog("close");
								$("#globalMsg").html("Update Complete").show().fadeOut(6000);
								$("#doPopulate").click();
				        		$("#ticketNbr").focus();
							} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								$("#approvalErr").html($data.data.webMessages.GLOBAL_MESSAGE[0]).show().fadeOut(6000);
							} else {
								$("#editApprovalsModal").dialog("close");
								$("#globalMsg").html("Unexepected Response, Contact support (" + $data.responseHeader.responseMessage + ")").show();
							}
						},
	       				404: function($data) {
							$("#editApprovalsModal").dialog("close");
	        	    		$("#globalMsg").html("Invalid Ticket Number").show().fadeOut(6000);
	        	    	},
						403: function($data) {
							$("#editApprovalsModal").dialog("close");
							$("#globalMsg").html("Session Timeout. Log in and try again").show();
						},
						405: function($data) {
							$("#editApprovalsModal").dialog("close");
							$("#globalMsg").html("You're not allowed to perform this function").show();
						},
		       			500: function($data) {
							$("#editApprovalsModal").dialog("close");
	            	    	$("#globalMsg").html("System Error: Contact Support").show();
	            		},  
					},
					dataType: 'json'
				});
				
				
				
			}

            
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
	        		<th>Payment Terms:</th><td><span id="invoiceStyle"></span></td>
	        		<th>Frequency</th><td><span id="jobFrequency"></span></td>
        		</tr>
        		<tr>
	        		<th colspan="2">Service Description:</th><td colspan="7"><span id="serviceDescription"></span></td>
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
				    			<span class="actDlPct"></span>
				    			<input type="hidden" name="actDlPct" data-required="true" data-valid="validActDlPct" />
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
				    		<td><span class="err customerSignatureErr"></span></td>
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
				    		<td><span class="err mgrApprovalErr"></span></td>
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
		   			<td style="border-bottom:solid 1px #000000;"><span id="ticketId"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span id="actPricePerCleaning"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span id="totalVolPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span id="actTax"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span id="totalTaxPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000;"><span id="ticketBalance"></span></td>
		   		</tr>
		   		<tr id="processNotesRow">
		   			<td colspan="2"><span class="formLabel" id="processDateLabel"></span> <span id="processDate"></span></td>
		   			<td colspan="4"><span class="formLabel">Process Notes:</span> <span id="processNotes"></span></td>
		   		</tr>
		   		<tr id="completedRow">
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Customer Signature: </span> <i id="customerSignature" class="fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Bill Sheet: </span> <i id="billSheet" class="fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2">
		   				<span class="formLabel">Manager Approval: </span> <i id="managerApproval" class="fa" aria-hidden="true"></i>
		   				<div style="float:right;">
		   					<span class="green fa fa-pencil tooltip editApprovals" ari-hidden="true"><span class="tooltiptext">Edit</span></span>
		   				</div>
		   			</td>
		   		</tr>
		   		
		   		<tr>
		   			<td colspan="3" style="border-top:solid 1px #000000; border-right:solid 1px #000000;">
		   				<webthing:addressDisplayPanel cssId="jobSiteAddress" label="Job Site" />
		   			</td>
		   			<td colspan="3" style="border-top:solid 1px #)00000; border-left:solid 1px #FF0000;">
						<webthing:addressDisplayPanel cssId="billToAddress" label="Bill To" />
		   			
		   			</td>
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
    	
    	
    	
    	<div id="editApprovalsModal">
    		<div class="err" id="approvalErr"></div>
    		<table>
    			<tr>
    				<td><span class="formLabel">Customer Signature:</span></td>
    				<td>
    					<i id="modalCustomerSignature" class="fa fa-check-square-o" aria-hidden="true"></i>
    					<input type="checkbox" name="modalCustomerSignature" />
    				</td>
    			</tr>
    			<tr>
    				<td><span class="formLabel">Bill Sheet:</span></td>
    				<td>
    					<i id="modalBillSheet" class="fa fa-check-square-o" aria-hidden="true"></i>
    					<input type="checkbox" name="modalBillSheet" />
    				</td>
    			</tr>
    			<tr>
    				<td><span class="formLabel">Manager Approval:</span></td>
    				<td>
    					<i id="modalManagerApproval" class="fa fa-check-square-o" aria-hidden="true"></i>
    					<input type="checkbox" name="modalManagerApproval" />
    				</td>
    			</tr>
    		</table>
    	</div>
    </tiles:put>

</tiles:insert>


