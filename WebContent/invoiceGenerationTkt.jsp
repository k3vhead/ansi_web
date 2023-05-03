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
<%@ taglib tagdir="/WEB-INF/tags/icon" prefix="icon" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Invoice Generation (Tkt)
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />    	
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
   	    <script type="text/javascript" src="js/callNote.js"></script> 
    
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	#invoiceDateModal {
        		display:none;
        	}
			#ticket-modal {
				display:none;	
			}
			#invalidTicketModal {
				display:none;
			}
			#invoiceListModal {
				display:none;
			}
			.formLabel {
				font-weight:bold;
			}
			.invoiceTable {
				width:90%;
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
	        ;INVOICE_GEN_TKT = {
	 			   dataTable : null,
	    	   	
				
	    		init : function() {	
    				$.each($('input'), function () {
    			        $(this).css("height","20px");
    			        $(this).css("max-height", "20px");
    			    });    				
    				INVOICE_GEN_TKT.createTable();    				
               		TICKETUTILS.makeTicketViewModal("#ticket-modal");
               		CALLNOTE.init();
			    },
			   	

        	
			    
        	
	        	createTable : function () {
					var $jobId = '<c:out value="${ANSI_JOB_ID}" />';
					var $divisionId = '<c:out value="${ANSI_DIVISION_ID}" />';
					var $startDate = '<c:out value="${ANSI_TICKET_LOOKUP_START_DATE}" />';
					var $statusFilter = '<c:out value="${ANSI_TICKET_LOOKUP_STATUS}" />';
	
	        		var dataTable = $('#ticketTable').DataTable( {
	        			"aaSorting":		[[1,'desc']],
	        			"processing": 		true,
	        	        "serverSide": 		true,
	        	        "autoWidth": 		false,
	        	        "deferRender": 		true,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        destroy:			true,
	        	        "searching": 		true,
	        	        "searchDelay":		800,
	        	        lengthMenu: [
	        	        	[ 10, 50, 100, 500, 1000 ],
	        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
	        	        ],
	        	        buttons: [
	        	        	'pageLength',
	        	        	'copy', 
	        	        	'csv', 
	        	        	'excel', 
	        	        	{extend: 'pdfHtml5', orientation: 'landscape'}, 
	        	        	'print',
	        	        	{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        	{
	        	        		text:'Generate Invoices',
	        	        		action: function(e, dt, node, config) {
	        	        			INVOICE_GEN_TKT.showDateModal();
	        	        		}
	        	        	}
	        	        ],
	        	        
	        	        "columnDefs": [
	         	            // { "orderable": false, "targets": -1 },
	        	            { className: "dt-head-left", "targets": [1,3,4,5,6,11,12] },
	        	            { className: "dt-body-center", "targets": [0,2,7,8,10,13] },
	        	            { className: "dt-right", "targets": [9,14]}
	        	         ],
	        	        "paging": true,
				        "ajax": {
				        	"url": "ticketInvoiceLookup",
				        	"type": "GET",
				        	"data": {}
				        	},
				        columns: [
				        	{ width:"4%", title: "Select", "defaultContent": "<i>N/A</i>", orderable:false, searchable:false, data: function ( row, type, set ) {	
				            	return '<input type="checkbox" name="ticketId" value="'+row.ticket_id+'">';
				            } },			            
				            { width:"4%", title: "<bean:message key="field.label.ticketId" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, data: function ( row, type, set ) {	
				            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
				            } },
				            { width:"5%", title: "<bean:message key="field.label.ticketType" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, data: "ticket_type_desc" },
				            { width:"4%", title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", searchable:true, orderable:true, data: function ( row, type, set ) {
				            	if ( row.tkt_act_div == null || row.tkt_act_div=='-' ) {
				            		div = row.div;
				            	} else {
				            		div = row.tkt_act_div;
				            	}
				            	return div;
				            } },
				            { width:"10%", title: "<bean:message key="field.label.billToName" />" , "defaultContent": "<i>N/A</i>", searchable:true, orderable:true, data: "bill_to_name"},
				            { width:"10%", title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", searchable:true, orderable:true, data: function ( row, type, set ) {
				            	if(row.job_site_name != null){return (row.job_site_name+"");}
				            } },
				            { width:"10%", title: "<bean:message key="field.label.jobSiteAddress" />",  "defaultContent": "<i>N/A</i>", searchable:true, orderable:true, data: "job_site_address"},
				            { width:"6%", title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, searchFormat: "YYYY-MM-dd", data: "display_start_date"},
				            { width:"5%", title: "<bean:message key="field.label.jobFrequency" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, data: "job_frequency"},
				            { width:"5%", title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.price_per_cleaning != null){return (row.price_per_cleaning.toFixed(2)+"");}
				            } },
				            { width:"5%", title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", orderable:true, data: "job_nbr"},
				            { width:"4%", title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", searchable:true, data: "view_job_id"},
				            { width:"16%", title: "<bean:message key="field.label.serviceDescription" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, data: "service_description"},
				            { width:"6%", title: "<bean:message key="field.label.processDate" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, searchFormat: "YYYY-MM-dd", data: "process_date" },
				            { width:"6%", title: "<bean:message key="field.label.amountDue" />", "defaultContent": "<i>N/A</i>", orderable:true, searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {	
				            	if(row.amount_due != null){return (row.amount_due.toFixed(2)+"");} 
				            } 
				            }],
				            "initComplete": function(settings, json) {
				            	//console.log(json);
				            	//doFunctionBinding();
				            	var myTable = this;
				            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", INVOICE_GEN_TKT.createTable);
				            },
				            "drawCallback": function( settings ) {
				            	INVOICE_GEN_TKT.doFunctionBinding();
				            	CALLNOTE.lookupLink();
				            }
				    } );
	        	},
				
				doFunctionBinding : function () {
					$(".print-link").on( "click", function($clickevent) {
						INVOICE_GEN_TKT.doPrint($clickevent);
					});
					$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});
	
				},
			


				generateInvoices : function() {
					console.log("generateInvoices");
					var $ticketList = [];
					$.each( $("input[name='ticketId']"), function($index, $value) {
						if ( $($value).prop("checked") ) {
							$ticketList.push( $($value).val() );
						}
					});
					var $outbound = {
						"invoiceDate":$("#invoiceDateModal input[name='invoiceDate']").val(),
						"ticketList":$ticketList,
					}
					console.log( JSON.stringify($outbound));
					var $callbacks = {
						200:INVOICE_GEN_TKT.generateInvoicesSuccess,
					};
					ANSI_UTILS.makeServerCall("POST", "invoiceGenerationTkt", JSON.stringify($outbound), $callbacks, {});
				},
				
				
				generateInvoicesSuccess : function($data, $passthru) {
					console.log("generateInvoicesSuccess");
					if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
						$("#invoiceDateModal").dialog("close");
						INVOICE_GEN_TKT.showInvoiceList($data, $passthru);
					} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
						INVOICE_GEN_TKT.showEditErrors($data, $passthru);
					} else {
						$("#invoiceDateModal").dialog("close");
						$("#globalMsg").html("Invalid response code: " + $data.responseHeader.responseCode + ". Contact Support").show().fadeOut(4000);
					}
				},
				
				showDateModal : function() {
					console.log("showDateModal");
					if ( ! $("#invoiceDateModal").hasClass("ui-dialog-content") ) {
						$("#invoiceDateModal" ).dialog({
							autoOpen: false,
							height: 175,
							width: 500,
							modal: true,
							closeOnEscape:false,
							title:"Generate Invoices",
							open: function(event, ui) {
								//$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							},
							buttons: [
								{
									id: "invoiceDateCancelButton",
									click: function($event) {
										$("#invoiceDateModal").dialog("close");
									}
								},{
									id: "invoiceDateSaveButton",
									click:function($event) {
										INVOICE_GEN_TKT.generateInvoices();
									}
								}
							]
						});
						$("#invoiceDateCancelButton").button('option', 'label', 'Cancel');
						$("#invoiceDateSaveButton").button('option', 'label', 'Go');
					}

					
					var $ticketCount = 0;
					$.each( $("input[name='ticketId']"), function($index, $value) {
						if ( $($value).prop("checked") ) {
							$ticketCount = $ticketCount + 1;
						}
					});
					
					if ( $ticketCount > 0 ) {
						$("#invoiceDateModal input").val("");
						$("#invoiceDateModal" .err).html("");
						$("#invoiceDateModal" ).dialog("open");
					} else {
						$("#globalMsg").html("Select at least 1 ticket").show().fadeOut(4000);
					}
				},
				
				
				
				showEditErrors : function($data, $passthru) {
					console.log("showEditErrors");
					if ( $data.data.ticketErrorList != null && $data.data.ticketErrorList.length > 0 ) {
						$("#invoiceDateModal").dialog("close");
						INVOICE_GEN_TKT.showTicketErrors($data, $passthru);
					} else {
						if ( 'invoiceDate' in $data.data.webMessages ) {
							$("#invoiceDateModal .invoiceDateErr").html($data.data.webMessages['invoiceDate'][0]).show().fadeOut(4000);
						}
						if ( 'ticketList' in $data.data.webMessages ) {
							$("#invoiceDateModal .ticketListErr").html($data.data.webMessages['ticketList'][0]).show().fadeOut(4000);
						}
					}
				},
				
				
				
				
				showInvoiceList : function($data, $passthru) {
					console.log("showInvoiceList");
					
					if ( ! $("#invoiceListModal").hasClass("ui-dialog-content") ) {
						$("#invoiceListModal" ).dialog({
							autoOpen: false,
							height: 300,
							width: 500,
							modal: true,
							closeOnEscape:false,
							title:"Invoices",
							open: function(event, ui) {
								//$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							},
							buttons: [
								{
									id: "invoiceListModalOKButton",
									click: function($event) {
										$("#ticketTable").DataTable().ajax.reload();
										$("#invoiceListModal").dialog("close");
									}
								}
							]
						});
						$("#invoiceListModalOKButton").button('option', 'label', 'OK');
					}

					$("#invoiceListModal .invoiceTable tbody").html("");
					$.each( $data.data.invoiceList, function($index, $value) {
						var $row = $("<tr>");
						$row.append( $("<td>").attr("style","text-align:left;width:25%;").append($value.invoiceId) );
						$row.append( $("<td>").attr("style","text-align:left;width:25%;").append($value.div) );
						$row.append( $("<td>").attr("style","text-align:center;width:25%;").append($value.ticketCount) );
						$row.append( $("<td>").attr("style","text-align:right;width:25%;").append($value.invoiceAmt.toFixed(2)) );
						$("#invoiceListModal .invoiceTable tbody").append($row);
					});
					
					
					$("#invoiceListModal" ).dialog("open");
					$("#globalMsg").html("Success").show().fadeOut(4000);
				},
				
				
				
				
				
				showTicketErrors : function($data, $passthru) {
					console.log("showTicketErrors");
					if ( ! $("#invalidTicketModal").hasClass("ui-dialog-content") ) {
						$("#invalidTicketModal" ).dialog({
							autoOpen: false,
							height: 300,
							width: 500,
							modal: true,
							closeOnEscape:false,
							title:"Invalid Tickets",
							open: function(event, ui) {
								//$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
							},
							buttons: [
								{
									id: "invalidTicketContinueButton",
									click: function($event) {
										$("#ticketTable").DataTable().ajax.reload();
										$("#invalidTicketModal").dialog("close");
									}
								}
							]
						});
						$("#invalidTicketContinueButton").button('option', 'label', 'Continue');
					}
					
					$("#invalidTicketModal" ).dialog("open");
					var $errorMsg = [];
					$.each($data.data.ticketErrorList, function($index, $value) {
						$errorMsg.push( $value['ticketId'] + " - " + $value['message']);							
					});
					$("#invalidTicketModal .ticketListErr").html( $errorMsg.join("<br />") ).show();

				},
	        }
			
			INVOICE_GEN_TKT.init();
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Invoice Generation (Tkt)</h1> 
    	  	
    	  	
	 	<webthing:lookupFilter filterContainer="filter-container" />
	
	 	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <thead>
	        </thead>
	        <tfoot>
	        </tfoot>
	    </table>
	    
	    <webthing:scrolltop />
	
	    <webthing:ticketModal ticketContainer="ticket-modal" />
		
		<div id="invoiceDateModal">
			<span class="formLabel">Invoice Date:</span> 
			<input type="date" name="invoiceDate" />
			<span class="err invoiceDateErr"></span><br />
			<span class="err ticketListErr"></span><br />
			 
		</div>
		
		<div id="invalidTicketModal">
			<div class="err">These tickets cannot be invoiced:</div>
			<div class="ticketListErr" style="margin-top:10px;"></div>
			<div class="err" style="margin-top:10px;">Continue to refresh the ticket list and try again</div>
		</div>
		
		<div id="invoiceListModal">
			<table class="invoiceTable">
				<colgroup>
					<col style="text-align:left;   width:25%;" />
					<col style="text-align:left;   width:25%;" />
					<col style="text-align:center; width:25%;" />
					<col style="text-align:right;  width:25%;" />
				</colgroup>
				<thead>
					<tr>
						<th>Invoice</th>
						<th>Div</th>
						<th>TicketCount</th>
						<th>Amount</th>
					</tr>					
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
    </tiles:put>
		
</tiles:insert>

