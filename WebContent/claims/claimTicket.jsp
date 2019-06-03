<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Claim Entry
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/ticket.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/claims.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
    
        <style type="text/css">
        	#claim-by-ticket {
        		display:none;
        	}
        	#claim-by-washer {
        		display:none;
        	}
        	#direct-labor-table {
        		width:100%;        		
        	}
        	#direct-labor-table td {
        		border:solid 1px #000000;
        	}
        	#new-dl-button {
        		cursor:pointer;
        	}
        	#new-pe-button {
        		cursor:pointer;
        	}
        	#omnotes-modal {
        		display:none;
        	}
			#passthru-expense-table {
				width:100%;
			}
			#ticketDetailContainer {
				margin-top:8px;
			}
        	#ticketDetailContainer .ticket-detail-table {
        		width:100%;
        	}
			#ticket-modal {
				display:none;	
			}
			.dark-gray {
				color:#404040;
			}
			.dt-center {
				text-align:center;
			}
			.dt-left { 
				text-align:left;
			}
			.dt-right {
				text-align:right;
			}
			.form-label {
				font-weight:bold;
			}
        	.lookup-container {
        		margin-top:20px;
				margin-left:80px;
				width:800px;
			}
			.lookup-table-container {
				width:100%;
				border:solid 1px #404040;
				padding:12px;
			}
			.omnotes-view {
				cursor:pointer;
				display:none;
			}
			.spacer-row {
				border:0;
			}
			.table-label-text {
				font-weight:bold;
			}
			.ticket-clicker {
				color:#000000;
				text-decoration:underline;
				cursor:pointer;
			}	
			.ticket-detail-hdr {
				font-weight:bold;
				text-align:center;
				border-top:solid 2px #000000;
				border-left:solid 1px #000000;
				border-right:solid 1px #000000;
				border-bottom:solid 1px #000000;
			}		
			.ticket-detail-data {
				border:solid 1px #000000;
				padding-left:3px;
				padding-right:3px;
			}
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;CLAIMTICKET = {
        		datatable : null,
        		ticketFilter : '<c:out value="${CLAIM_ENTRY_TICKET_ID}" />',
        		washerFilter : '<c:out value="${CLAIM_ENTRY_WASHER_ID}" />',
        		formSelector : {
        			"DIRECT_LABOR":"#direct-labor-form",
        			"PASSTHRU_EXPENSE":"#passthru-expense-form"
        		},
        		
        		init : function() {
        			if ( CLAIMTICKET.ticketFilter != null && CLAIMTICKET.ticketFilter != '') {
        				$("#claim-by-ticket").show();
        			}
        			if ( CLAIMTICKET.washerFilter != null && CLAIMTICKET.washerFilter != '') {
        				$("#claim-by-washer").show();
        			}
        			CLAIMTICKET.makeClickers();
        			CLAIMTICKET.makeModals();
        			//CLAIMTICKET.makeTicketComplete();
        			CLAIMTICKET.makeAutoComplete('#direct-labor-form input[name="washerName"]','#direct-labor-form input[name="washerId"]');
        			CLAIMTICKET.makeAutoComplete('#passthru-expense-form input[name="washerName"]','#passthru-expense-form input[name="washerId"]');
        			ANSI_UTILS.getCodeList("ticket_claim_passthru","passthru_expense_type",CLAIMTICKET.makeExpenseTypeList);
        			if ( CLAIMTICKET.ticketFilter != '' ) {
            			CLAIMTICKET.getDetail();
            			//$( "#ticketNbr" ).val(CLAIMTICKET.ticketFilter);	        			
        			}
        		},
        		
        		
        		
        		getDetail : function() {
        			var $url = "claims/claimEntry/" + CLAIMTICKET.ticketFilter;
        			var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: null,
						statusCode: {
							200: function($data) {
								console.log($data);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									CLAIMTICKET.populateDetail($data.data);
								} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
								} else {
									$("#globalMsg").html("System Error: Contact Support").show();
								}
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							}, 
							404: function($data) {
								$("#globalMsg").html("System Error 404: Contact Support").show();
							}, 
							405: function($data) {
								$("#globalMsg").html("System Error 405: Contact Support").show();
							}, 
							500: function($data) {
								$("#globalMsg").html("System Error 500: Contact Support").show();
							} 
						},
						dataType: 'json'
					});
        		},

        		
        		
        		
        		
        		makeAutoComplete : function($displaySelector, $idSelector) {
            		var $washerAutoComplete = $($displaySelector).autocomplete({
						source: "washerTypeAhead?",
						select: function( event, ui ) {
							$( $idSelector ).val(ui.item.id);								
   				      	},
						response: function(event, ui) {
							if (ui.content.length === 0) {
								$("#direct-labor-form .washerIdErr").html("No Washer Found");
					        } else {
					        	$("#direct-labor-form .washerIdErr").html("");
					        }
						}
					});	
            	},
        		
        		
        		
        		
        		makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$('.dateField').datepicker({
		                prevText:'&lt;&lt;',
		                nextText: '&gt;&gt;',
		                showButtonPanel:true
		            });
					
					
            	},
            	
            	
            	
            	
        		makeExpenseTypeList : function($data) {
        			$selectorName = "#passthru-expense-form select[name='passthruExpenseType']";
					var $select = $($selectorName);
					$('option', $select).remove();

					$select.append(new Option("",""));
					$.each($data.codeList, function(index, val) {
					    $select.append(new Option(val.displayValue, val.value));
					});
        		},
        		
        		
        		
            	
            	makeModals : function () {
        			TICKETUTILS.makeTicketViewModal("#ticket-modal")
        			
        			$( "#omnotes-modal" ).dialog({
						title:'OM Notes',
						autoOpen: false,
						height: 300,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "omnotes-cancel-button",
								click: function($event) {
									$( "#omnotes-modal" ).dialog("close");
								}
							}
						]
					});	
					$("#omnotes-cancel-button").button('option', 'label', 'OK');
					
					
            	},
					
					
					
					
            	
            	
        		
        		makeTicketComplete: function() {
        			var $ticketComplete = $( "#ticketNbr" ).autocomplete({
        				source: "ticketTypeAhead",
                        minLength: 3,
                        appendTo: "#someTicket",
                        select: function( event, ui ) {
                        	var $ticketNbr = ui.item.id;
                        	CLAIMTICKET.ticketFilter = $ticketNbr
        					$("#ticketNbr").val($ticketNbr);
                        	CLAIMTICKET.getDetail();
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
        					CLAIMTICKET.ticketFilter = $ticketNbr;
        					$("#ticketNbr").val($ticketNbr);
        					$("#ticketNbr").autocomplete("close");
        					CLAIMTICKET.getDetail();
        				}
        			}
        		},
        		
        		
        		
            	populateDetail : function($data) {
        			var $numberFieldList = [
								"totalVolume",
								"claimedVolume",
								"availableVolume",
								"budget",
								"claimedDirectLaborAmt",
								"availableDirectLabor"]
					
        			$("#ticketDetailContainer .ticket-detail-table .jobSiteAddress").html($data.jobSiteAddress);
        			$("#ticketDetailContainer .ticket-detail-table .ticketId").html($data.ticketId);
        			$("#ticketDetailContainer .ticket-detail-table .ticketStatus").html('<span class="tooltip">'+$data.ticketStatus+'<span class="tooltiptext">'+$data.ticketStatusDesc+'</span></span>');

        			$.each($numberFieldList, function($idx, $val) {
        				var $selector = "#ticketDetailContainer .ticket-detail-table ." + $val;
        				if ( $data[$val] == null ) {
        					$($selector).html(null);
        				} else {
        					$($selector).html( $data[$val].toFixed(2));
        				}
        			});
        			
        			$("#ticketDetailContainer .ticket-detail-table .ticketId").attr("data-id", $data['ticketId']);
        			$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});
        			
        			
        			if ( $data.omNotes == "" || $data.omNotes == null ) {
        				$("#ticketDetailContainer .omnotes-view").hide();
        			} else {
	        			$("#omnotes-modal .omNotes").html($data.omNotes);  
	        			$("#ticketDetailContainer .omnotes-view").show();
	        			$("#ticketDetailContainer .omnotes-view").click(function() {
	            			$("#omnotes-modal").dialog("open");
	            		});
        			}

        			
        		},
            	
        		
        		
        		
        		saveDirectLabor : function() {
        			$(".claim-entry-form td.err span").html(""); // clear the existing error messages
        			var $outbound = {"type":"DIRECT_LABOR"};
        			var $url = "claims/claimEntry/" + CLAIMTICKET.ticketFilter;
        			console.log("Save Direct Labor");
        			
        			$.each( $("#direct-labor-form input"), function($idx, $field) {
        				var $key = $($field).attr("name");
        				var $value = $($field).val();
        				$outbound[$key] = $value;        				
        			});
    				console.debug($outbound);
    				
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    							var $form = CLAIMTICKET.formSelector[$outbound["type"]];
    		    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    		    					$.each($data.data.webMessages, function (key, value) {
    		    						var $selectorName = $form + " ." + key + "Err";
    		    						//$($selectorName).show();
    		    						$($selectorName).html(value[0]);
    		    					});
    		    				} else {
    		    					$("#globalMsg").html("Update Successful").show().fadeOut(4000);
    		    				}
    						},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    						404: function($data) {
    							$("#globalMsg").html("Invalid Ticket").show();
    						},
    						405: function($data) {
    							$("#globalMsg").html("System Error DL 405; Contact Support");
    						},
    						500: function($data) {
    							$("#globalMsg").html("System Error DL 500; Contact Support");
    						}
    					},
    					dataType: 'json'
    				});
        		},
        		
        		
        		
        		
        		savePassthruExpense : function() {
        			$(".claim-entry-form td.err span").html(""); // clear the existing error messages
        			var $outbound = {"type":"PASSTHRU_EXPENSE"};
        			var $url = "claims/claimEntry/" + CLAIMTICKET.ticketFilter;
        			console.log("Save Direct Labor");
        			
        			$.each( $("#passthru-expense-form input"), function($idx, $field) {
        				var $key = $($field).attr("name");
        				var $value = $($field).val();
        				$outbound[$key] = $value;        				
        			});
        			$outbound['passthruExpenseType'] = $("#passthru-expense-form select[name='passthruExpenseType']").val();
    				console.debug($outbound);
    				
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					statusCode: {
    						200: function($data) {
    							var $form = CLAIMTICKET.formSelector[$outbound["type"]];
    		    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    		    					$.each($data.data.webMessages, function (key, value) {
    		    						var $selectorName = $form + " ." + key + "Err";
    		    						//$($selectorName).show();
    		    						console.log($selectorName + " -> " + value[0]);
    		    						$($selectorName).html(value[0]);
    		    					});
    		    				} else {
    		    					$("#globalMsg").html("Update Successful").show().fadeOut(4000);
    		    				}
    						},
    						403: function($data) {
    							$("#globalMsg").html("Session Timeout. Log in and try again");
    						},
    						404: function($data) {
    							$("#globalMsg").html("Invalid Ticket").show();
    						},
    						405: function($data) {
    							$("#globalMsg").html("System Error DL 405; Contact Support");
    						},
    						500: function($data) {
    							$("#globalMsg").html("System Error DL 500; Contact Support");
    						}
    					},
    					dataType: 'json'
    				});
        		},
        	}
      	  	

        	CLAIMTICKET.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Claim Entry <span id="claim-by-ticket" />by Ticket<span id="claim-by-washer" />by Washer</h1>
    	<%-- 
    	<div>
       		<span class="formLabel">Ticket:</span>
       		<input id="ticketNbr" name="ticketNbr" type="text" maxlength="10" />
       		<input id="doPopulate" type="button" value="Search" />
       	</div>
       	 --%>
		<div id="ticketDetailContainer">
			<table class="ticket-detail-table" cellSpacing="0" cellPadding="0">
				<tr>
					<td class="ticket-detail-hdr">Account (Job Site)</td>
					<td class="ticket-detail-hdr">Ticket</td>
					<td class="ticket-detail-hdr">Status</td>
					<td class="ticket-detail-hdr">Total Volume</td>
					<td class="ticket-detail-hdr">Claimed Volume</td>
					<td class="ticket-detail-hdr">Available Volume</td>
					<td class="ticket-detail-hdr">Budget</td>
					<td class="ticket-detail-hdr">Claimed DL</td>
					<td class="ticket-detail-hdr">Available DL</td>
					<td class="ticket-detail-hdr">OM Notes</td>
				</tr>
				<tr>
					<td class="ticket-detail-data dt-left"><span class="jobSiteAddress"></span></td>
					<td class="ticket-detail-data dt-center"><span class="ticketId ticket-clicker"></span></td>
					<td class="ticket-detail-data dt-center"><span class="ticketStatus"></span></td>
					<td class="ticket-detail-data dt-right"><span class="totalVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="claimedVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="availableVolume"></span></td>
					<td class="ticket-detail-data dt-right"><span class="budget"></span></td>
					<td class="ticket-detail-data dt-right"><span class="claimedDirectLaborAmt"></span></td>
					<td class="ticket-detail-data dt-right"><span class="availableDirectLabor"></span></td>
					<td class="ticket-detail-data dt-center"><webthing:view styleClass="dark-gray omnotes-view">View</webthing:view></td>
				</tr>								
			</table>
		</div>    	

		
	    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
    
   		<div id="omnotes-modal">
   			<span class="omNotes"></span>
   		</div>
   		

   		
   		
   		
   		
   		

    </tiles:put>
		
</tiles:insert>

