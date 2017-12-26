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
        Ticket Override
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
			#editInvoiceIdModal {
				display:none;
			}
			#editActualPPCModal {
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
			#editApprovalsModal {
				display:none;
			}
			#editStartDateModal {
				display:none;
			}
			#editProcessDateModal {
				display:none;
			}			
			#newInvoiceDateRow {
				display:none;
			}
			.action-link {
				cursor:pointer;
			}
			#status {
				white-space:nowrap;
			}
			.joblink {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        $( document ).ready(function() {
        	
        	GLOBAL_DATA = {};
        	;TICKET_OVERRIDE = {
        		init:function() {       
        			GLOBAL_DATA.globalTicketId = 0;
        			TICKET_OVERRIDE.makeStatusMap();
        			TICKET_OVERRIDE.initDialog();
        			TICKET_OVERRIDE.initFields();
        			TICKET_OVERRIDE.initButtons();
        			TICKET_OVERRIDE.initTypeAhead();
        			$(".editApprovals").click(function($event) {
        				TICKET_OVERRIDE.doEditApprovals($event);
        			});
        			$(".editStartDate").click(function($event) {
        				TICKET_OVERRIDE.doEditStartDate($event);
        			});
        			$(".editProcessDate").click(function($event) {
        				TICKET_OVERRIDE.doEditProcessDate($event);
        			});
        			$(".editInvoiceId").click(function($event) {
        				TICKET_OVERRIDE.doEditInvoiceId($event);
        			});
        			$(".editInvoiceDate").click(function($event) {
        				TICKET_OVERRIDE.doEditInvoiceDate($event);
        			});
        			$(".editActualPPC").click(function($event) {
        				TICKET_OVERRIDE.doEditActualPPC($event);
        			});
        			$("#generateInvoice").checkboxradio();
					$("#generateInvoice").click(function($event) {
						var doNewInvoice = $("#generateInvoice").is(":checked");
						if (doNewInvoice == true) {
							$("#invoiceNbr").attr("disabled","disabled");
							$("#newInvoiceDateRow").show();
						} else {
							$("#invoiceNbr").removeAttr("disabled");
							$("#newInvoiceDateRow").hide();
						}
					});
//        			$("#COMPLETED input[name=actPricePerCleaning]").change(function($event) {
//        				TICKET_OVERRIDE.doPpcChange($event);
//        			});
//        			$("#COMPLETED input[name=actDlAmt]").change(function($event) {
//        				TICKET_OVERRIDE.doActDlAmtChg($event);
//        			});
//           			$('#type').change(function(){
//           				TICKET_OVERRIDE.doTypeChg();
//           			});
//        			$(".cancelUpdate").click( function($clickevent) {
//        				TICKET_OVERRIDE.doCancelUpdate($clickevent);
//        			});
//        			$(".goUpdate").click( function($clickevent) {
//        				var $goButton=$(this);
//        				TICKET_OVERRIDE.goUpdate($goButton);
//        			});
        			
        			
        			
        			// handle "?id=nnnnn" in the URL: 
                	var $defaultTicketNbr='<c:out value="${ANSI_TICKET_ID}" />';
                	if ( $defaultTicketNbr != '' ) {
                		$("#ticketNbr").val($defaultTicketNbr);
                		$("#doPopulate").click();
                		$("#ticketNbr").focus();
                	}

        		},
        		
        		
        		makeStatusMap:function() {
        			var $ticketStatusList = ANSI_UTILS.getOptions("TICKET_STATUS");
        			GLOBAL_DATA.ticketStatusMap = {};   // map codes to display text
        			GLOBAL_DATA.ticketSeqMap = {};		// figure out which codes can go before/after
        			$.each($ticketStatusList.ticketStatus, function($index, $value) {
        				GLOBAL_DATA.ticketStatusMap[$value.code]=$value.display;
        				GLOBAL_DATA.ticketSeqMap[$value.code]=[$value.name, $value.allPreviousValues];
        			});
        		},
        		
        		
       			initDialog:function() {
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
        							TICKET_OVERRIDE.saveApprovals();
        						}
        					}
        				],
        				close: function() {
        					$("#editApprovalsModal").dialog( "close" );
        				}
        			});
            		$('#saveModal').button('option', 'label', 'Save');
            		$('#cancelModal').button('option', 'label', 'Cancel');
            		
            		
            		
            		
            		
        			$("#editStartDateModal").dialog({
        				title:'Edit Start Date',
        				autoOpen: false,
        				height: 300,
        				width: 400,
        				modal: true,
        				buttons: [
        					{
        						id: "cancelStartDateModal",
        						click: function() {
        							$("#editStartDateModal").dialog( "close" );
        						}
        					},{
        						id: "saveStartDateModal",
        						click: function($event) {
        							TICKET_OVERRIDE.saveNewStartDate();
        						}
        					}
        				],
        				close: function() {
        					$("#editStartDateModal").dialog( "close" );
        				}
        			});
            		$('#saveStartDateModal').button('option', 'label', 'Save');
            		$('#cancelStartDateModal').button('option', 'label', 'Cancel');
            		
            		
            		
            		$("#editProcessDateModal").dialog({
        				title:'Edit Process Notes',
        				autoOpen: false,
        				height: 300,
        				width: 400,
        				modal: true,
        				buttons: [
        					{
        						id: "cancelProcessDateModal",
        						click: function() {
        							$("#editProcessDateModal").dialog( "close" );
        						}
        					},{
        						id: "saveProcessDateModal",
        						click: function($event) {
        							TICKET_OVERRIDE.saveNewProcessDate();
        						}
        					}
        				],
        				close: function() {
        					$("#editProcessDateModal").dialog( "close" );
        				}
        			});
            		$('#saveProcessDateModal').button('option', 'label', 'Save');
            		$('#cancelProcessDateModal').button('option', 'label', 'Cancel');
            		
            		
            		
            		
            		
            		
            		$("#editInvoiceIdModal").dialog({
        				title:'Edit Invoice ID',
        				autoOpen: false,
        				height: 300,
        				width: 400,
        				modal: true,
        				buttons: [
        					{
        						id: "cancelInvoiceIdModal",
        						click: function() {
        							$("#editInvoiceIdModal").dialog( "close" );
        						}
        					},{
        						id: "saveInvoiceIdModal",
        						click: function($event) {
        							TICKET_OVERRIDE.saveNewInvoiceId();
        						}
        					}
        				],
        				close: function() {
        					$("#editInvoiceIdModal").dialog( "close" );
        				}
        			});
            		$('#saveInvoiceIdModal').button('option', 'label', 'Save');
            		$('#cancelInvoiceIdModal').button('option', 'label', 'Cancel');

            		
            		
            		
            		
            		
            		$("#editInvoiceDateModal").dialog({
        				title:'Edit Invoice Date',
        				autoOpen: false,
        				height: 300,
        				width: 400,
        				modal: true,
        				buttons: [
        					{
        						id: "cancelInvoiceDateModal",
        						click: function() {
        							$("#editInvoiceDateModal").dialog( "close" );
        						}
        					},{
        						id: "saveInvoiceDateModal",
        						click: function($event) {
        							TICKET_OVERRIDE.saveNewInvoiceDate();
        						}
        					}
        				],
        				close: function() {
        					$("#editInvoiceDateModal").dialog( "close" );
        				}
        			});
            		$('#saveInvoiceDateModal').button('option', 'label', 'Save');
            		$('#cancelInvoiceDateModal').button('option', 'label', 'Cancel');

            		
            		
            		
            		
            		
            		$("#editActualPPCModal").dialog({
        				title:'Edit Ticket Amount',
        				autoOpen: false,
        				height: 300,
        				width: 400,
        				modal: true,
        				buttons: [
        					{
        						id: "cancelActualPPCModal",
        						click: function() {
        							$("#editActualPPCModal").dialog( "close" );
        						}
        					},{
        						id: "saveActualPPCModal",
        						click: function($event) {
        							TICKET_OVERRIDE.saveNewActualPPC();
        						}
        					}
        				],
        				close: function() {
        					$("#editActualPPCModal").dialog( "close" );
        				}
        			});
            		$('#saveActualPPCModal').button('option', 'label', 'Save');
            		$('#cancelActualPPCModal').button('option', 'label', 'Cancel');
       			},
        			
        			
       			initButtons:function() {
                	$("#doPopulate").click(function () {
            			var $ticketNbr = $('#ticketNbr').val();
            			GLOBAL_DATA.globalTicketId = $('#ticketNbr').val();
            			if ($ticketNbr != '') {            		
                    		TICKET_OVERRIDE.doPopulate($ticketNbr)
                    	}
                    });
       			},
                	
       			
       			
       			doAutoComplete:function(request, response) {
       				console.debug(request);
       				var $billToAddressId = GLOBAL_DATA['globalTicket'].billToAddress.addressId;
       				var $outbound={"term":request.term, "billTo":$billToAddressId};
       				var jqxhr = $.ajax({
    					type: 'GET',
    					url: "invoiceTypeAhead",
    					data: $outbound,
    					statusCode: {
    						200: function($data) {
    							response($data);
    						},
    	       				404: function($data) {
    	       					response($data);
    	        	    	},
    						403: function($data) {
    							response($data);
    						},
    						405: function($data) {
    							response($data);
    						},
    		       			500: function($data) {
    		       				response($data);
    	            		},  
    					},
    					dataType: 'json'
    				});
       			},
       			
       			initTypeAhead:function() {
       				var $invoiceComplete = $( "#invoiceNbr" ).autocomplete({
       					source: TICKET_OVERRIDE.doAutoComplete,
       	                minLength: 3,
       	                appendTo: "#someInvoice",
       	                select: function( event, ui ) {
       						$("#invoiceNbr").val(ui.item.id);
       						//var $ticketData = populateTicketData(ui.item.id);
       	                },
       	                response: function(event, ui) {
       	                    if (ui.content.length === 0) {
       	                    	$("#editInvoiceIdModal").find('.modalErr').html("No Matches").show().fadeOut(6000);
       	                    } else {
       	                    	$("#editInvoiceIdModal").find('.modalErr').html("");
       	                    }
       	                }
       	          	}).data('ui-autocomplete');
       				$invoiceComplete._renderMenu = function( ul, items ) {
       					var that = this;
       					$.each( items, function( index, item ) {
       						that._renderItemData( ul, item );
       					});
       					if ( items.length == 1 ) {
       						$("#invoiceNbr").val(items[0].id);
       						$("#invoiceNbr").autocomplete("close");
       						//var $ticketData = populateTicketData(items[0].id);
       					}
       				}	
       			},
       			
       			
                	
       			initFields:function() {
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
                        	GLOBAL_DATA.globalTicketId = $ticketNbr
        					$("#ticketNbr").val($ticketNbr);
                    		TICKET_OVERRIDE.doPopulate($ticketNbr);
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
        					GLOBAL_DATA.globalTicketId = $ticketNbr
        					$("#ticketNbr").val($ticketNbr);
        					$("#ticketNbr").autocomplete("close");
                    		TICKET_OVERRIDE.doPopulate($ticketNbr);
        				}
        			}
               	},
               	
               	
               	doEditApprovals:function($event) {
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
               	},
               	
               	
               	doEditStartDate:function($event) {
               		$('#editStartDateModal').find('input[name="newDate"]').val(GLOBAL_DATA['globalTicket'].startDate);
    				$("#editStartDateModal").dialog("open");

               	},
               	
               	doEditProcessDate : function($event) {
               		$('#editProcessDateModal').find('input[name="newDate"]').val(GLOBAL_DATA['globalTicket'].processDate);
               		$('#editProcessDateModal').find('input[name="newNote"]').val(GLOBAL_DATA['globalTicket'].processNotes);
    				$("#editProcessDateModal").dialog("open");
               	},
               	
               	
               	doEditInvoiceId : function($event) {
               		$('#editInvoiceIdModal').find('input[name="newInvoiceId"]').val(GLOBAL_DATA['globalTicket'].invoiceId);
               		$('#editInvoiceIdModal').find('input[name="newInvoiceDate"]').val(GLOBAL_DATA['globalTicket'].invoiceDate);
    				$("#editInvoiceIdModal").dialog("open");
               	},
               	
               	doEditInvoiceDate : function($event) {
               		$('#editInvoiceDateModal').find('input[name="overrideInvoiceDate"]').val(GLOBAL_DATA['globalTicket'].invoiceDate);
    				$("#editInvoiceDateModal").dialog("open");
               	},
               	
               	doEditActualPPC : function($event) {
               		$('#editActualPPCModal').find('input[name="overrideActualPPC"]').val(GLOBAL_DATA['globalTicket'].actPricePerCleaning.substring(1));
    				$("#editActualPPCModal").dialog("open");
               	},
               	
               	populateTicketDetail:function($data) {
           			GLOBAL_DATA['globalTicket'] = $data.ticketDetail;
    				$("#ticketId").html($data.ticketDetail.ticketId);
    				$("#actPricePerCleaning").html($data.ticketDetail.actPricePerCleaning);
    				$("#totalVolPaid").html($data.ticketDetail.totalVolPaid);
    				$("#actTax").html($data.ticketDetail.actTax);
    				$("#totalTaxPaid").html($data.ticketDetail.totalTaxPaid);
    				$("#ticketBalance").html($data.ticketDetail.balance);
    				$("#ticketType").html($data.ticketDetail.ticketType);
    				$("#startDate").html($data.ticketDetail.startDate);
    				//GLOBAL_DATA.defaultStartDate = $data.ticketDetail.startDate;
    				$("#fleetmaticsId").html($data.ticketDetail.fleetmaticsId);
					$("#actDlPct").html($data.ticketDetail.actDlPct);
					$("#actDlAmt").html($data.ticketDetail.actDlAmt);

    				
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
    					} else if ( ($data.ticketDetail.status == 'C') || ($data.ticketDetail.status == 'I') || ($data.ticketDetail.status == 'P') ) {
    						$processLabel = "Complete Date:";
    						$("#processNotesRow td").removeClass("bottomRow");
    						$("#completedRow").show();
    						if ( $data.ticketDetail.customerSignature == true ) {
    							TICKET_OVERRIDE.markValid($("#customerSignature"));
    						} else {
    							TICKET_OVERRIDE.markInvalid($("#customerSignature"));
    						}
    						if ( $data.ticketDetail.billSheet == true ) {
    							TICKET_OVERRIDE.markValid($("#billSheet"));
    						} else {
    							TICKET_OVERRIDE.markInvalid($("#billSheet"));
    						}
    						if ( $data.ticketDetail.mgrApproval == true ) {
    							TICKET_OVERRIDE.markValid($("#managerApproval"));
    						} else {
    							TICKET_OVERRIDE.markInvalid($("#managerApproval"));
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
    			},			
    			
    			
    			populatePanelSelect:function($data) {
    				$(".workPanel").hide();
    				$.each(GLOBAL_DATA.ticketSeqMap[$data.ticketDetail.status][1], function($index, $value) {
//    					console.debug("value: " + $value + " " + GLOBAL_DATA.ticketSeqMap[$value][0])
    					var $selector = "#" + GLOBAL_DATA.ticketSeqMap[$value][0];
//    					$($selector).fadeIn(3000);
        			});
    			},
    			
    			
    			populateInvoiceDetail:function($data) {
    				if ($data.invoiceDetail) {
    					$("#invoiceId").html($data.invoiceDetail.invoiceId);
    					$("#invoiceDate").html($data.ticketDetail.invoiceDate);
    					$("#sumInvPpc").html($data.invoiceDetail.sumInvPpc);
    					$("#sumInvPpcPaid").html($data.invoiceDetail.sumInvPpcPaid);
    					$("#sumInvTax").html($data.invoiceDetail.sumInvTax);
    					$("#sumInvTaxPaid").html($data.invoiceDetail.sumInvTaxPaid);
    					$("#invoiceBalance").html($data.invoiceDetail.balance);
    	                $("#invoiceTable").show();
    					$("#invoiceTable").fadeIn(4000);  
    				} else {
    					$("#invoiceTable").hide();				
    				}				
    			},
    			
    			populateSummary:function($data) {
    				$("#status").html(GLOBAL_DATA.ticketStatusMap[$data.ticketDetail.status] + " (" + $data.ticketDetail.status + ")");
    				$("#divisionDisplay").html($data.ticketDetail.divisionDisplay);
    				$("#jobId").html( '<a class="joblink" href="jobMaintenance.html?id='+ $data.ticketDetail.jobId + '">' + $data.ticketDetail.jobId + '</a>');
    				//$( "#jobId" ).on( "click", function($clickevent) {
    				//	 location.href="jobMaintenance.html?id=" + $data.ticketDetail.jobId;
    				//});
    				$("#serviceDescription").html($data.ticketDetail.serviceDescription);
    				$("#jobFrequency").html($data.ticketDetail.jobFrequency);
    				$("#invoiceStyle").html($data.ticketDetail.invoiceStyle);
    				$("#poNumber").html($data.ticketDetail.poNumber);
    			},

    			
    			populateDefaultValues:function($data) {
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
    			},
    			
    			
    			markValid:function($item) {
    	    		$item.removeClass("fa");
    	    		$item.removeClass("fa-ban");
    				$item.removeClass("inputIsInvalid");

    				$item.addClass("fa");
    	    		$item.addClass("fa-check-square-o");
    				$item.addClass("inputIsValid");
    			},
    			
    			markInvalid:function($item) {
    				$item.removeClass("fa");
    	    		$item.removeClass("fa-check-square-o");
    				$item.removeClass("inputIsValid");

    				$item.addClass("fa");
    	    		$item.addClass("fa-ban");
    				$item.addClass("inputIsInvalid");
    			},
    			
    			
    			doPopulate:function($ticketNbr) {    		
    		       	var jqxhr = $.ajax({
    		       		type: 'GET',
    		       		url: "ticketOverride/" + $ticketNbr,
    		       		//data: $ticketNbr,
    		       		success: function($data) {
    						$.each($data.data.ticketList, function(index, value) {
    							TICKET_OVERRIDE.addRow(index, value);
    						});
    						$(".workPanel").hide();
    		       			TICKET_OVERRIDE.populateTicketDetail($data.data);	       			
    		       			TICKET_OVERRIDE.populateSummary($data.data);
    		       			TICKET_OVERRIDE.populatePanelSelect($data.data);
    		       			TICKET_OVERRIDE.populateDefaultValues($data.data);
    						ADDRESS_UTILS.populateAddress("#jobSiteAddress", $data.data.ticketDetail.jobSiteAddress);
    						ADDRESS_UTILS.populateAddress("#billToAddress", $data.data.ticketDetail.billToAddress);
    		       			
        					$("#summaryTable").fadeIn(4000);
        					$("#selectPanel").fadeIn(4000);
        					$("#ticketTable").fadeIn(4000);
        					TICKET_OVERRIDE.populateInvoiceDetail($data.data);	  					
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
            	},
            	

            	addRow:function(index, $ticket) {	
    				var $rownum = index + 1;
           			rowTd = makeRow($ticket, $rownum);
           			row = '<tr class="dataRow">' + rowTd + "</tr>";
           			$('#displayTable').append(row);
    			},
    			
    			
    			clearAddForm:function() {
    				$.each( $('.addForm').find("input"), function(index, $inputField) {
    					$('input[name=customerSignature]').prop('checked', false);
    					$('input[name=billSheet]').prop('checked', false);
    					$('input[name=mgrApproval]').prop('checked', false);
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    					}
    				});
                },
                

                saveApprovals:function() {
    				var $outbound = {}
    				$outbound['customerSignature'] = $("#editApprovalsModal input[name='modalCustomerSignature']").prop("checked");
    				$outbound['managerApproval'] = $("#editApprovalsModal input[name='modalManagerApproval']").prop("checked");
    				$outbound['billSheet'] = $("#editApprovalsModal input[name='modalBillSheet']").prop("checked");

    				
                	$url = "ticketApprovalOverride/" + GLOBAL_DATA.globalTicketId;

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
    			},
    			
    			
    			saveNewStartDate:function() {
               		var $newStartDate = $('#editStartDateModal').find('input[name="newDate"]').val();
					var $overrideList =[ {'startDate':$newStartDate}];
					TICKET_OVERRIDE.doOverride($('#editStartDateModal'), "startDate", $overrideList);
    			},
    			
    			
    			saveNewProcessDate:function() {
               		var $newDate = $('#editProcessDateModal').find('input[name="newDate"]').val();
               		var $newNote = $('#editProcessDateModal').find('input[name="newNote"]').val();
					var $overrideList =[ {'processDate':$newDate},{'processNote':$newNote}];
					TICKET_OVERRIDE.doOverride($('#editProcessDateModal'), "processDate", $overrideList);
    			},
    			
    			
    			saveNewInvoiceId:function() {
    				var $generateNewInvoice = $('#editInvoiceIdModal').find('input[name="generateInvoice"]').is(":checked");
    				if ( $generateNewInvoice == true ) {
    					$overrideType = "newInvoice";
    				} else {
    					$overrideType = "invoice";
    				}
    				var $newInvoiceId = $('#editInvoiceIdModal').find('input[name="newInvoiceId"]').val();
    				var $newInvoiceDate = $('#editInvoiceIdModal').find('input[name="newInvoiceDate"]').val();
    				
    				var $overrideList =[ {'invoiceId':$newInvoiceId, 'invoiceDate':$newInvoiceDate, 'generateNewInvoice':$generateNewInvoice}];
    				TICKET_OVERRIDE.doOverride($('#editInvoiceIdModal'), $overrideType, $overrideList);
    			},
    			
    			
    			saveNewInvoiceDate:function() {
   					var $overrideType = "invoiceDate";
    				var $newInvoiceDate = $('#editInvoiceDateModal').find('input[name="overrideInvoiceDate"]').val();
    				
    				var $overrideList =[ {'invoiceDate':$newInvoiceDate}];
    				TICKET_OVERRIDE.doOverride($('#editInvoiceDateModal'), $overrideType, $overrideList);
    			},
    			
    			
    			saveNewActualPPC : function() {
    				var $overrideType = "actPricePerCleaning";
    				var $newActualPPC = $('#editActualPPCModal').find('input[name="overrideActualPPC"]').val();
    				
    				var $overrideList =[ {'actPricePerCleaning':$newActualPPC}];
    				TICKET_OVERRIDE.doOverride($('#editActualPPCModal'), $overrideType, $overrideList);
    			},
    			
    			
    			doOverride:function($modal, $type, $overrideList) {
    				var $outbound = {'type':$type, 'override':$overrideList};
					console.debug(JSON.stringify($outbound));
					
                	$url = "ticketOverride/" + GLOBAL_DATA.globalTicketId;

    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    							if ( $data.responseHeader.responseCode == 'SUCCESS') {
    								$modal.dialog("close");
    								$("#globalMsg").html("Update Complete").show().fadeOut(6000);
    								$("#doPopulate").click();
    				        		$("#ticketNbr").focus();
    							} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    								$modal.find('.modalErr').html($data.data.webMessages.GLOBAL_MESSAGE[0]).show().fadeOut(6000);
    							} else {
    								$modal.dialog("close");
    								$("#globalMsg").html("Unexepected Response, Contact support (" + $data.responseHeader.responseMessage + ")").show();
    							}
    						},
    	       				404: function($data) {
								$modal.dialog("close");
    	        	    		$("#globalMsg").html("Invalid Ticket Number").show().fadeOut(6000);
    	        	    	},
    						403: function($data) {
								$modal.dialog("close");
    							$("#globalMsg").html("Session Timeout. Log in and try again").show();
    						},
    						405: function($data) {
    							$("#editApprovalsModal").dialog("close");
    							$("#globalMsg").html("You're not allowed to perform this function").show();
    						},
    		       			500: function($data) {
								$modal.dialog("close");
    	            	    	$("#globalMsg").html("System Error: Contact Support").show();
    	            		},  
    					},
    					dataType: 'json'
    				});
    			},
    			
    			
    			doPpcChange:function($event) {
    				var $actPricePerCleaning = $("#COMPLETED input[name=actPricePerCleaning]").val();
    				var $actDlPct = $("#COMPLETED input[name=actDlPct]").val();
    				if ( isNaN($actPricePerCleaning) ) {
    					TICKET_OVERRIDE.markInvalid($("#validActPricePerCleaning"));
    				} else {
    					TICKET_OVERRIDE.markValid($("#validActPricePerCleaning"));
    					var $actDlAmt = ($actPricePerCleaning * $actDlPct);
    					$("#COMPLETED input[name=actDlAmt]").val($actDlAmt.toFixed(2));					
    				}
    			},
    			
    			
    			doActDlAmtChg:function($event) {
    				var $actPricePerCleaning = $("#COMPLETED input[name=actPricePerCleaning]").val();
    				var $actDlAmt = $("#COMPLETED input[name=actDlAmt]").val();
    				if ( isNaN($actDlAmt) ) {
    					TICKET_OVERRIDE.markInvalid($("#validActDlAmt"));
    				} else {
    					TICKET_OVERRIDE.markValid($("#validActDlAmt"));
    					var $actDlPct = ($actDlAmt / $actPricePerCleaning);
    					$("#COMPLETED input[name=actDlPct]").val($actDlPct);					
    					$("#COMPLETED span[class=actDlPct]").html(($actDlPct*100).toFixed(3));
    				}
    			},
    			
    			doTypeChg:function() {
    				if($('#type').val() == 'parcel') {
            			$('#row_dim').show(); 
           			} else {
               			$('#row_dim').hide(); 
            		} 
    			},
    			

    			doCancelUpdate:function($clickevent) {
    				$clickevent.preventDefault();
    				$(".clearMe").val("");
    				$(".err").html("");
    				TICKET_OVERRIDE.clearAddForm();
    			},
    			
    			
    			goUpdate:function($goButton) {
    				// the goButton -- $(this) -- tells us which panel is being submitted
    				// loop through all input in that panel and put the values in $outbound
    				var $panelName = $goButton.data('panel');
    				console.debug("PANEL: " + $panelName);
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


                	$url = "ticketOverride/" + GLOBAL_DATA.globalTicketId;

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
    			}
        	};
        	
        	TICKET_OVERRIDE.init();
			

        	

    });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
		<h1>Ticket Override</h1>
    	
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
	        		<th>PO #</th><td><span id="poNumber"></span></td>
        		</tr>
        		<tr>
	        		<th colspan="2">Service Description:</th><td colspan="8"><span id="serviceDescription"></span></td>
	    		</tr>
			</table>
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
		   			<th>Ticket Type</th>
 		   			<th>DL Amt</th>
		   			<th>DL Pct</th>
		   			<th>Start Date</th>
		   			<th>FM ID</th>
		   		</tr>
		   		<tr>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="ticketId"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;">
		   				<span id="actPricePerCleaning"></span>
		   				<ansi:hasPermission permissionRequired="TICKET_SPECIAL_OVERRIDE">
		    				<ansi:hasWrite>
		    					<webthing:edit styleClass="action-link editActualPPC">Edit</webthing:edit>
		    				</ansi:hasWrite>
		    			</ansi:hasPermission>
		   			</td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="totalVolPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="actTax"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="totalTaxPaid"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="ticketBalance"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="ticketType"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:8%;"><span id="actDlAmt"></span></td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="actDlPct"></span></td>
		   			<td style="border-bottom:solid 1px #000000; white-space:nowrap; width:10%;">
		   				<span id="startDate"></span>
	   					<webthing:edit styleClass="editStartDate action-link">Edit</webthing:edit>
		   			</td>
		   			<td style="border-bottom:solid 1px #000000; width:9%;"><span id="fleetmaticsId"></span></td>
		   		</tr>
		   		<tr id="processNotesRow">
		   			<td colspan="2"><span class="formLabel" id="processDateLabel"></span> <span id="processDate"></span></td>
		   			<td colspan="9">
		   				<span class="formLabel">Process Notes:</span> 
		   				<span id="processNotes"></span>
						<div style="float:right;">
							<webthing:edit styleClass="editProcessDate action-link">Edit</webthing:edit>		   					
		   				</div>
		   			</td>
		   		</tr>
		   		<tr id="completedRow">
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Customer Signature: </span> <i id="customerSignature" class="fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2"><span class="formLabel">Bill Sheet: </span> <i id="billSheet" class="fa" aria-hidden="true"></i></td>
		   			<td class="bottomRow" colspan="2">
		   				<span class="formLabel">Manager Approval: </span> <i id="managerApproval" class="fa" aria-hidden="true"></i>		   				
		   			</td>
		   			<td class="bottomRow" colspan="5">
						<div style="float:right;">
		   					<webthing:edit styleClass="editApprovals action-link">Edit</webthing:edit>
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
		   			<th>Inv Date</th>
		   			<th>Inv Amt</th>
		   			<th>Inv Paid</th>
		   			<th>Tax Amt</th>
		   			<th>Tax Paid</th>
		   			<th>Balance</th>
		   		</tr>
		   		<tr>
		   			<td>
		   				<span id="invoiceId"></span>
		   				<webthing:edit styleClass="action-link editInvoiceId">Edit</webthing:edit>
		   			</td>
		   			<td>
		   				<span id="invoiceDate"></span>
 				    	<ansi:hasPermission permissionRequired="TICKET_SPECIAL_OVERRIDE">
		    				<ansi:hasWrite>
		    					<webthing:edit styleClass="action-link editInvoiceDate">Edit</webthing:edit>
		    				</ansi:hasWrite>
		    			</ansi:hasPermission>
		   			</td>
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
    	
    	
 	    <div id="editStartDateModal">
    		<div class="err modalErr" ></div>
    		<table>
    			<tr>
    				<td><span class="formLabel">Start Date:</span></td>
    				<td><input type="text" name="newDate" class="dateField" /></td>
    			</tr>
    		</table>
    	</div>
    	
    	<div id="editProcessDateModal">
    		<div class="err modalErr" ></div>
    		<table>
    			<tr>
    				<td><span class="formLabel">Process Date:</span></td>
    				<td><input type="text" name="newDate" class="dateField" /></td>
    			</tr>
    			<tr>
    				<td><span class="formLabel">Process Note:</span></td>
    				<td><input type="text" name="newNote" /></td>
    			</tr>
    		</table>
    	</div>
    	
    	
    	
    	
    	<div id="editInvoiceIdModal">
    		<div class="err modalErr" ></div>
    		<table>
    			<tr>
    				<td style="width:100px;"><span class="formLabel" >Invoice ID:</span></td>
    				<td>
    					<input type="text" name="newInvoiceId" id="invoiceNbr"/><br />
    					<label for="generateInvoice">New</label>
    					<input type="checkbox" name="generateInvoice" id="generateInvoice" />
    				</td>
    			</tr>
    			<tr id="newInvoiceDateRow">
    				<td style="width:100px;"><span class="formLabel">Invoice Date:</span></td>
    				<td><input type="text" name="newInvoiceDate" id="newInvoiceDate" class="dateField" /></td>
    			</tr>  
    		</table>
    	</div>
    	
    	
    	
    	<ansi:hasPermission permissionRequired="TICKET_SPECIAL_OVERRIDE">
			<ansi:hasWrite>
				<div id="editInvoiceDateModal">
		    		<div class="err modalErr" ></div>
		    		<table>		    			
		    			<tr>
		    				<td style="width:100px;"><span class="formLabel">Invoice Date:</span></td>
		    				<td><input type="text" name="overrideInvoiceDate" id="overrideInvoiceDate" class="dateField" /></td>
		    			</tr>  
		    		</table>
		    	</div> 			
    		</ansi:hasWrite>
 		</ansi:hasPermission>
    	
    	
    	<ansi:hasPermission permissionRequired="TICKET_SPECIAL_OVERRIDE">
			<ansi:hasWrite>
				<div id="editActualPPCModal">
		    		<div class="err modalErr" ></div>
		    		<table>		    			
		    			<tr>
		    				<td style="width:100px;"><span class="formLabel">Ticket Amt:</span></td>
		    				<td><input type="text" name="overrideActualPPC" id="overrideActualPPC" /></td>
		    			</tr>  
		    		</table>
		    	</div> 			
    		</ansi:hasWrite>
 		</ansi:hasPermission>
    	
    	
    </tiles:put>

</tiles:insert>


