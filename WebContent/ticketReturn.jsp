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
			#dataTables {
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
			var $ticketStatusList = ANSI_UTILS.getOptions("TICKET_STATUS");
			var $ticketStatusMap = {}
			$.each($ticketStatusList.ticketStatus, function($index, $value) {
			    $ticketStatusMap[$value.code]=$value.display;
			});
                
        	$('#doPopulate').click(function () {
    			var $ticketNbr = $('#ticketNbr').val();
            	if ($ticketNbr != '') {
            		doPopulate($ticketNbr)
            	}
            });
        	
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
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
				$("#panelSelector").append(new Option("", ""));
                $.each($data.ticketDetail.nextAllowedStatusList, function($index, $status) {
					$("#panelSelector").append(new Option($status, $status));
                });
				
			}
			
			function populateInvoiceDetail($data) {
				$("#invoiceId").html($data.invoiceDetail.invoiceId);
				$("#sumInvPpc").html($data.invoiceDetail.sumInvPpc);
				$("#sumInvPpcPaid").html($data.invoiceDetail.sumInvPpcPaid);
				$("#sumInvTax").html($data.invoiceDetail.sumInvTax);
				$("#sumInvTaxPaid").html($data.invoiceDetail.sumInvTaxPaid);
				$("#invoiceBalance").html($data.invoiceDetail.balance);
				
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
						doFunctionBinding();
		       			populateTicketDetail($data.data);
		       			populateInvoiceDetail($data.data);
		       			populateSummary($data.data);
		       			populatePanelSelect($data.data);
    					$("#summaryTable").fadeIn(4000);
    					$("#selectPanel").fadeIn(4000);
    					$("#dataTables").fadeIn(4000);
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
        	
        	function addRow(index, $ticket) {	
				var $rownum = index + 1;
       			rowTd = makeRow($ticket, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#displayTable').append(row);
			}
        	
        	
        	//$nextAllowedStatusList = ticket.nextAllowedStatusList();
			//$("#status").append(new Option("",""));
			//$.each($nextAllowedStatusList, function(index, val) {
			//	$("#status").append(new Option(val.status));
			//});

        	
			$('#row_dim').hide(); 
    			$('#type').change(function(){
        			if($('#type').val() == 'parcel') {
           				 $('#row_dim').show(); 
        			} else {
            			$('#row_dim').hide(); 
        		} 
    		});
			
    			
    			//if ( this.value == '1')
    			//      //.....................^.......
    			//      {
    			 //       $("#business").show();
    			 //     }
    			 //     else
    			  //    {
    			  //      $("#business").hide();
    			  //    }
    			  //  });
			
        	
			$("#panelSelector").change(function($event) {
				$(".workPanel").hide();
				var $selectedPanel = $('#panelSelector option:selected').val();		
        		if ($selectedPanel != '' ) {
        			$selectedId = "#" + $selectedPanel;
        			$($selectedId).fadeIn(1500);
        			$("#monitor").html("Displaying " + $selectedPanel);
        		} else {
        			$("#P").hide();
        			$("#C").hide();
        			$("#I").hide();
        			$("#V").hide();
        			$("#monitor").html("Hiding Everything");
        		}
        		//var $ticketStatus = $('#ticketStatus option:selected').val;
        		//if($ticketStatus != '' ){
        		//	$
        		//}

			});
			

			
			function doFunctionBinding() {
				$('.updAction').bind("click", function($clickevent) {
					doUpdate($clickevent);
				});
				$('.dataRow').bind("mouseover", function() {
					$(this).css('background-color','#CCCCCC');
				});
				$('.dataRow').bind("mouseout", function() {
					$(this).css('background-color','transparent');
				});
			}			
			
			function doUpdate($clickevent) {
				var $action = $event.currentTarget.attributes['data-panel'].value;
				$outbound['action'] = $action;
				$clickevent.preventDefault();
				$('#addForm').data('rownum',$rownum);
				clearAddForm();
				
				var $rowId = eval($rownum) + 1;
            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
            	var $row = $($rowFinder)  
            	var tdList = $row.children("td");
            	var $processDate = $row.children("td")[0].textContent;
            	var $actPricePerCleaning = $row.children("td")[1].textContent;
            	var $defaultActDlPct = $row.children("td")[2].textContent;
            	var $actDlAmt = $row.children("td")[3].textContent;
            	var $processNotes = $row.children("td")[4].textContent;
            	var $customerSignature = $row.children("td")[5].textContent;
            	var $billSheet = $row.children("td")[6].textContent;
            	var $mgrApproval = $row.children("td")[7].textContent;

            	$("#addForm input[name='processDate']").val($processDate);
            	$("#addForm input[name='actPricePerCleaning']").val($actPricePerCleaning);
            	$("#addForm input[name='defaultActDlPct']").val($defaultActDlPct);
            	$("#addForm input[name='actDlAmt']").val($actDlAmt);
            	$("#addForm input[name='processNotes']").val($processNotes);
            	$("#addForm input[name='customerSignature']").val($customerSignature);
            	$("#addForm input[name='billSheet']").val($billSheet);
            	$("#addForm input[name='mgrApproval']").val($mgrApproval);
				
            	
				$.each( $('#addForm :input'), function(index, value) {
					markValid(value);
				});

             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			}
	
	
			$("#cancelUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
			});
			
			
	
			$("#goUpdate").click( function($clickevent) {
				$outbound = {};
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				})

				$outbound['processDate'] = $("#addForm select[name='processDate'] option:selected").val();
				$outbound['actPricePerCleaning'] = $("#addForm select[name='actPricePerCleaning'] option:selected").val();
				$outbound['defaultActDlPct'] = $("#addForm select[name='defaultActDlPct'] option:selected").val();
				$outbound['actDlAmt'] = $("#addForm select[name='actDlAmt'] option:selected").val();			
				$outbound['processNotes'] = $("#addForm select[name='processNotes'] option:selected").val();
				$outbound['customerSignature'] = $("#addForm select[name='customerSignature'] option:selected").val();
				$outbound['billSheet'] = $("#addForm select[name='billSheet'] option:selected").val();			
				$outbound['mgrApproval'] = $("#addForm select[name='mgrApproval'] option:selected").val();

				if ( $('#addForm').data('rownum') == null ) {
					$url = "ticket/" + $ticketNbr ;
				} else {
					$rownum = $('#addForm').data('rownum')
					var $tableData = [];
	                $("#displayTable").find('tr').each(function (rowIndex, r) {
	                    var cols = [];
	                    $(this).find('th,td').each(function (colIndex, c) {
	                        cols.push(c.textContent);
	                    });
	                    $tableData.push(cols);
	                });
	
	            	$url = "ticket/" + $ticketNbr;
				}	    		    
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								if ( $url == "'ticket/' + $ticketNbr" ) {
									var count = $('#displayTable tr').length - 1;
									addRow(count, $data.data.ticket);
								} else {
					            	var $rownum = $('#addForm').data('rownum');
					                var $rowId = eval($rownum) + 1;
					            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
					            	var $rowTd = makeRow($data.data.ticket, $rownum);
					            	$($rowFinder).html($rowTd);
								}
								doFunctionBinding();
								clearAddForm();
								$('#addFormDiv').bPopup().close();
								$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
							} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								$('.err').html("");
								 $.each($data.data.webMessages, function(key, messageList) {
									var identifier = "#" + key + "Err";
									msgHtml = "<ul>";
									$.each(messageList, function(index, message) {
										msgHtml = msgHtml + "<li>" + message + "</li>";
									});
									msgHtml = msgHtml + "</ul>";
									$(identifier).html(msgHtml);
								});	
								$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
							} else {
								
							}
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
			


            $('#addForm').find("input").on('focus',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		markValid(this);
            	}
            });
            
            $('#addForm').find("input").on('input',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		markValid(this);
            	}
            });
            
            function clearAddForm() {
				$.each( $('#addForm').find("input"), function(index, $inputField) {
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						markValid($inputField);
					}
				});
				$('.err').html("");
				$('#addForm').data('rownum',null);
            }
            
            function markValid($inputField) {
            	$fieldName = $($inputField).attr('name');
            	$fieldGetter = "input[name='" + $fieldName + "']";
            	$fieldValue = $($fieldGetter).val();
            	$valid = '#' + $($inputField).data('valid');
	            var re = /.+/;	            	 
            	if ( re.test($fieldValue) ) {
            		$($valid).removeClass("fa-ban");
            		$($valid).removeClass("inputIsInvalid");
            		$($valid).addClass("fa-check-square-o");
            		$($valid).addClass("inputIsValid");
            	} else {
            		$($valid).removeClass("fa-check-square-o");
            		$($valid).removeClass("inputIsValid");
            		$($valid).addClass("fa-ban");
            		$($valid).addClass("inputIsInvalid");
            	}
            }
			
			
			
    });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Ticket Return</h1>
    	
   		<div>
       		<span class="formLabel">Ticket:</span>
       		<input id="ticketNbr" name="ticketNbr" type="text"/>
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
				<form action="#" method="post" id="addForm">
				    <table>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">Completion Date:</span></td>
				    		<td>
				    			<input type="text" class="dateField" name="processDate" data-required="true" data-valid="validProcessDate" />		    						
				    			<i id="validProcessDate" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err" id="processDateErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">PPC:</span></td>
				    		<td>
				    			<input type="text" name="actPricePerCleaning" data-required="true" data-valid="validActPricePerCleaning" />
				    			<i id="validActPricePerCleaning" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err" id="actPricePerCleaningErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">DL %:</span></td>
				    		<td>
				    			<input type="text" name="defaultActDlPct" data-required="true" data-valid="validDefaultActDlPct" />
				    			<i id="validDefaultActDlPct" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err" id="defaultActDlPctErr"></span></td>
				    	</tr>
				    	<tr>
				    		<td><span class="required">*</span><span class="formLabel">Direct Labor:</span></td>
				    		<td>
				    			<input type="text" name="actDlAmt" data-required="true" data-valid="validActDlAmt" />
				    			<i id="validActDlAmt" class="fa" aria-hidden="true"></i>
				    		</td>
				    		<td><span class="err" id="actDlAmtErr"></span></td>
				    	</tr>
						<tr>
				    		<td><span class="formLabel">Completion Notes:</span></td>
				    		<td>
				    			<input type="text" name="processNotes"/>
				    		</td>
				    		<td><span class="err" id="ProcessNotesErr"></span></td>
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
				    			<input type="button" class="prettyButton" value="Complete" id="goUpdate" data-panel="completeTicket" />
				    			<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
				    		</td>
				    	</tr>
				    </table>
				</form>
			</div>
		
			<div class="workPanel" id="SKIPPED">
				<div id="addFormMsg" class="err"></div>
				<table>
			    	<tr>
			    		<td><span class="required">*</span><span class="formLabel">Skip Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err" id="processDateErr"></span></td>
		  				</tr>
					<tr>
		  					<td><span class="formLabel">Skip Reason:</span></td>
		  					<td>
		  						<input type="text" name="processNotes"/>
		  					</td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton" value="Skip" id="goUpdate" data-panel="skipTicket" />
		  						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
			
			
			<div class="workPanel" id="VOIDED">
				<div id="addFormMsg" class="err"></div>
				<table>
		  				<tr>
		  					<td><span class="required">*</span><span class="formLabel">Void Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err" id="processDateErr"></span></td>
		  				</tr>
					<tr>
		  					<td><span class="formLabel">Void Reason:</span></td>
		  					<td>
		  						<input type="text" name="processNotes"/>
		  						<i id="validProcessNotes" class="fa" aria-hidden="true"></i>
		  					</td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton" value="Void" id="goUpdate" data-panel="voidTicket"/>
		  						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
		 	
			<div class="workPanel" id="REJECTED">
				<div id="addFormMsg" class="err"></div>
				<table>
		  				<tr>
		  					<td><span class="required">*</span><span class="formLabel">Reject Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err" id="processDateErr"></span></td>
		  				</tr>
					<tr>
		  					<td><span class="formLabel">Reject Reason:</span></td>
		  					<td>
		  						<input type="text" name="processNotes"/>
		  					</td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton" value="Reject" id="goUpdate" data-panel="rejectTicket" />
		  						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		  					</td>
		  				</tr>
		  			</table>
			</div>
			
			
		
			<div class="workPanel" id="NOT_DISPATCHED">
				<div id="addFormMsg" class="err"></div>
				<table>
		  				<tr>
		  					<td><span class="required">*</span><span class="formLabel">Process Date:</span></td>
		  					<td>
		  						<input type="text" class="dateField" name="processDate" data-required="true" data-valid="validProcessDate" />
		  						<i id="validProcessDate" class="fa" aria-hidden="true"></i>
		  					</td>
		  					<td><span class="err" id="processDateErr"></span></td>
		  				</tr>
		  				<tr>
		  					<td colspan="2" style="text-align:center;">
		  						<input type="button" class="prettyButton" value="Re-Queue" id="goUpdate" data-panel="requeueTicket" />
		  						<input type="button" class="prettyButton" value="Clear" id="cancelUpdate" />
		  					</td>
		  				</tr>
		  			</table>			
			</div>
			
			<div class="workPanel" id="INVOICED"> </div>
			<div class="workPanel" id="PAID"> </div>
		   	
		<div id="dataTables">
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
		</div>
    	
    </tiles:put>

</tiles:insert>

