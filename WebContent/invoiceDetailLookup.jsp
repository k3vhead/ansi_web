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

<%@ page import="com.ansi.scilla.web.invoice.actionForm.InvoiceLookupForm" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        <bean:message key="page.label.invoice" /> Detail <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
        <style type="text/css">
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
			#filter-container {
        		width:402px;
        		float:right;
        	}
        	#ticket-modal {
				display:none;	
			}
			.invoicePrint {
				color:#000000;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}			
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>    	
        <script type="text/javascript" src="js/lookup.js"></script>
        <script type="text/javascript" src="js/ticket.js"></script>
        <script type="text/javascript" src="js/invoicePrint.js"></script>
        <script type="text/javascript" src="js/callNote.js"></script>
        <script type="text/javascript">
        
        $(document).ready(function(){
				
			INVOICE_DETAIL_LOOKUP = {
				filterDivisionId : '<c:out value="${ANSI_DIVISION_ID}" />',
	        	filterPPC : '<c:out value="${ANSI_PPC_FILTER}" />',
	        	filterIcon : "fa fa-ban",
		        	
        		init : function() {
					if ( INVOICE_DETAIL_LOOKUP.filterPPC == 'yes' ) {
						INVOICE_DETAIL_LOOKUP.filterIcon = "far fa-check-square";
		        	} else {
		        		INVOICE_DETAIL_LOOKUP.filterIcon = "fa fa-ban";
		        	}
					
					CALLNOTE.init();
        			INVOICE_DETAIL_LOOKUP.createTable();
					TICKETUTILS.makeTicketViewModal("#ticket-modal")
					
					$('.ScrollTop').click(function() {
						$('html, body').animate({scrollTop: 0}, 800);
						return false;
		      		});
        		},
			
			
				createTable : function() {
	        		var dataTable =  $('#invoiceTable').DataTable( {
        				"aaSorting":		[[0,'desc']],
	        			"processing": 		true,
	        	        "serverSide": 		true,
	        	        "autoWidth": 		false,
	        	        "deferRender": 		true,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
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
	        	        	{extend: 'colvis',	label: function () {INVOICE_DETAIL_LOOKUP.doFunctionBinding();}}
	        	        ],
	        	        "columnDefs": [
	         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
	        	            { className: "dt-left", "targets": [0,1,2,6,7] },
	        	            { className: "dt-center", "targets": [3,,9,10,11] },
	        	            { className: "dt-right", "targets": [4,5,12,13,14,15,16,17]}
	        	         ],
	        	        "paging": true,
				        "ajax": {
				        	"url": "invoiceDetailLookup",
				        	"type": "GET",
				        	"data":{"divisionId":INVOICE_DETAIL_LOOKUP.filterDivisionId,"<%=InvoiceLookupForm.PPC_FILTER%>":INVOICE_DETAIL_LOOKUP.filterPPC}
				        	},
				        columns: [
				        	
				            { title: "<bean:message key="field.label.invoiceId" />", width:"3%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.invoice_id != null){return (row.invoice_id+"");}
				            } },
				            { title: "<bean:message key="field.label.divisionNbr" />", width:"4%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.div != null){return (row.div+"");}
				            } },
				            { title: "<bean:message key="field.label.billToName" />", width:"12%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.bill_to_name != null){return (row.bill_to_name+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceDate" />", width:"4%", searchable:true, searchFormat: "YYYY-MM-dd", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoice_date != null){return (row.invoice_date+"");}
				            } },
				            { title: "Invoice PPC",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.invoice_ppc != null){return (row.invoice_ppc.toFixed(2) +"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceTax" />", width:"4%", searchable:true, searchFormat: "#.##", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoice_tax != null){return (row.invoice_tax.toFixed(2)+"");}
				            } },
				            { title: "Job Site",  "defaultContent": "<i>N/A</i>", width:"12%", searchable:true,  data: function ( row, type, set ) {
				            	if(row.job_site_name != null){return (row.job_site_name+"");}
				            } },
				            { title: "Job Site Address",  "defaultContent": "<i>-</i>", width:"12%", searchable:true, data: function ( row, type, set ) {
				            	if(row.job_site_address != null){return (row.job_site_address+"");}
				            } },
				            { title: "Tickt",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, data: function ( row, type, set ) {
				            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
				            } },
				            { title: "Tkt<br />Type",  "defaultContent": "<i>N/A</i>", width:"2%", searchable:true, data: function ( row, type, set ) {
				            	if(row.ticket_type != null){return (row.ticket_type_display);}
				            } },
				            { title: "Tkt<br />Status",  "defaultContent": "<i>N/A</i>", width:"2%", searchable:true, data: function ( row, type, set ) {
				            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_display + '</span></span>');}
				            } },
				            { title: "Completed",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "YYYY-MM-dd", data: function ( row, type, set ) {
				            	if(row.completed_date != null){return (row.completed_date);}
				            } },
				            { title: "PO",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##", data: "po_number" },
				            { title: "PPC",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.ppc != null){return (row.ppc.toFixed(2));}
				            } },
				            { title: "Tax",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##",  data: function ( row, type, set ) {
				            	if(row.taxes != null){return (row.taxes.toFixed(2));}
				            } },
  				            { title: "Total",  "defaultContent": "<i>N/A</i>",width:"4%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.total != null){return (row.total.toFixed(2));}
				            } },
				            { title: "Paid",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.paid != null){return (row.paid.toFixed(2));}
				            } },
				            { title: "Due",  "defaultContent": "<i>N/A</i>", width:"4%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
				            	if(row.due != null){return (row.due.toFixed(2));}
				            } },
				            { title: "<bean:message key="field.label.action" />", width:"3%", searchable:false, data: function ( row, type, set ) {
				            	var $noteLink = '<webthing:notes xrefType="TICKET" xrefId="' + row.ticket_id + '">Ticket Notes</webthing:notes>'
				            	return $noteLink;
				            //	if ( row.printCount > 0 ) {
				            //		$printText =  '<i class="fa fa-print invoicePrint tooltip" aria=hidden="true" data-invoiceId="'+row.invoiceId+'"><span class="tooltiptext">Reprint</span></i>';
				            //	} else {
				            //		$printText = ""; 
				            //	}
				            //	{return "<ansi:hasPermission permissionRequired='INVOICE_READ'>" + $printText + "</ansi:hasPermission>";}
				            } }
				            ],
				            "initComplete": function(settings, json) {
				            	//console.log(json);
				            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#invoiceTable", INVOICE_DETAIL_LOOKUP.createTable);
				            },
				            "drawCallback": function( settings ) {
				            	INVOICE_DETAIL_LOOKUP.doFunctionBinding();
				            	CALLNOTE.lookupLink();
				            }
				    } );
				},
				
				
				
				
				doFunctionBinding : function() {
					$( ".invoicePrint" ).on( "click", function($clickevent) {
						INVOICE_DETAIL_LOOKUP.invoicePrint($clickevent);
					});
					$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});

				},
				
				
				
				
				invoicePrint : function($clickevent) {
					var $invoiceId = $clickevent.currentTarget.attributes['data-invoiceId'].value;
					INVOICE_PRINT.reprintInvoice($invoiceId);
				},
        	};
        	
        	INVOICE_DETAIL_LOOKUP.init();
        });
        </script>
                
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Invoice Detail <bean:message key="menu.label.lookup" /></h1>
    	
    	<webthing:lookupFilter filterContainer="filter-container" />
	 	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
	       	<thead></thead>
	       	<tbody></tbody>
	    </table>
    
		<webthing:scrolltop />
    
    	<webthing:ticketModal ticketContainer="ticket-modal" />
    	<webthing:callNoteModals />
    </tiles:put>
		
</tiles:insert>

