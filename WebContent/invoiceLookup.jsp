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

<%@ page import="com.ansi.scilla.web.invoice.actionForm.InvoiceLookupForm" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        <bean:message key="page.label.invoice" /> <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
       	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
	    <script type="text/javascript" src="js/callNote.js"></script>  
    	<script type="text/javascript" src="js/lookup.js"></script>
    
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
			#divFilterReset {
				display:none;
			}
			#filter-container {
        		width:402px;
        		float:right;
        	}
			.invoicePrint {
				cursor:pointer;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.invoicePrint {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;INVOICELOOKUP = {
       			$filterDivisionId : '<c:out value="${ANSI_DIVISION_ID}" />',
               	$filterPPC : '<c:out value="${ANSI_PPC_FILTER}" />',
               	$filterIcon : null,
               	
               	init : function() {
               		if ( INVOICELOOKUP.$filterPPC == 'yes' ) {
               			INVOICELOOKUP.$filterIcon = "far fa-check-square";
                	} else {
                		INVOICELOOKUP.$filterIcon = "fa fa-ban";
                	}
               		
               		if ( INVOICELOOKUP.$filterDivisionId == null || INVOICELOOKUP.$filterDivisionId == '' ) {
               			$("#divFilterMsg").html("");
               			$("#divFilterReset").hide();
               		} else {               		
               			INVOICELOOKUP.getDivisionFilter();
               			$("#divFilterReset").show();
               		}
               		
               		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
              		});
               		
               		CALLNOTE.init();
               		
               		$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
               		INVOICELOOKUP.createTable();
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
    	        	        	{extend: 'colvis',	label: function () {INVOICELOOKUP.doFunctionBinding();}},
    	        	        	{
    	        	        		text:'Due Filter <i class="'+INVOICELOOKUP.$filterIcon+'"></i>',
    	        	        		action: function(e, dt, node, config) {
    	        	        			if ( INVOICELOOKUP.$filterPPC == 'yes' ) {
    	        	        				INVOICELOOKUP.$filterPPC = 'no';
    	        	        			} else {
    	        	        				INVOICELOOKUP.$filterPPC = 'yes';
    	        	        			}
    	        	        			var $url = "invoiceLookup.html?id=" + INVOICELOOKUP.$filterDivisionId + "&ppcFilter=" + INVOICELOOKUP.$filterPPC;
    	        	        			location.href=$url;
    	        	        		}
    	        	        	}
    	        	        ],
    	        	        "columnDefs": [
    	//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
    	        	            { className: "dt-left", "targets": [0,1,2,3] },
    	        	            { className: "dt-center", "targets": [4,5,11] },
    	        	            { className: "dt-right", "targets": [6,7,8,9,10]}
    	        	         ],
    	        	        "paging": true,
    				        "ajax": {
    				        	"url": "invoiceLookup",
    				        	"type": "GET",
    				        	"data":{"divisionId":INVOICELOOKUP.$filterDivisionId,"<%=InvoiceLookupForm.PPC_FILTER%>":INVOICELOOKUP.$filterPPC}
    				        	},
    				        columns: [
    				            { title: "<bean:message key="field.label.invoiceId" />", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data: "invoice_id"},
    				            { title: "<bean:message key="field.label.fleetmaticsInvoiceNbr" />", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: "fleetmatics_invoice_nbr"},
    				            { title: "<bean:message key="field.label.divisionNbr" />", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data: "div"},
    				            { title: "<bean:message key="field.label.billToName" />", width:"18%", searchable:true, "defaultContent": "<i>N/A</i>", data: "bill_to_name"},
    				            { title: "<bean:message key="field.label.ticketCount" />" , width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data: "ticket_count"},
    				            { title: "<bean:message key="field.label.invoiceDate" />", width:"10%", searchable:true, searchFormat: "YYYY-MM-dd", "defaultContent": "<i>N/A</i>", data: "invoice_date" },
    				            { title: "<bean:message key="field.label.invoiceAmount" />",  width:"8%", searchable:true, "defaultContent": "<i>N/A</i>", data:function ( row, type, set ) {
    				            	if ( row.invoice_balance != null ) {
    				            		return row.invoice_amount.toFixed(2);	
    				            	}				            	
    				            }},
    				            { title: "<bean:message key="field.label.invoiceTax" />",  width:"8%", searchable:true, "defaultContent": "<i>N/A</i>", data:function ( row, type, set ) {
    				            	if ( row.invoice_tax != null ) {
    				            		return row.invoice_tax.toFixed(2);	
    				            	}				            	
    				            }},
    				            { title: "<bean:message key="field.label.invoiceTotal" />",  width:"8%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    				            	if ( row.invoice_total != null ) {
    				            		return row.invoice_total.toFixed(2);	
    				            	}				            	
    				            } },
    				            { title: "<bean:message key="field.label.invoicePaid" />",  width:"8%", searchable:true, "defaultContent": "<i>-</i>", data:function ( row, type, set ) {
    				            	if ( row.invoice_paid != null ) {
    				            		return row.invoice_paid.toFixed(2);	
    				            	}				            	
    				            }},
    				            { title: "<bean:message key="field.label.invoiceBalance" />",  width:"8%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    				            	if ( row.invoice_balance != null ) {
    				            		return row.invoice_balance.toFixed(2);	
    				            	}
    				            }},
    				            { title: "<bean:message key="field.label.action" />", width:"8%",  data: function ( row, type, set ) {
    				            	if ( row.printCount > 0 ) {
    				            		$printText =  '<i class="fa fa-print invoicePrint tooltip" aria=hidden="true" data-invoiceId="'+row.invoice_id+'"><span class="tooltiptext">Reprint</span></i>';
    				            	} else {
    				            		$printText = ""; 
    				            	}
    				            	var $noteLink = '<webthing:notes xrefType="INVOICE" xrefId="' + row.invoice_id + '">Invoice Notes</webthing:notes>'
    				            	{return "<ansi:hasPermission permissionRequired='INVOICE_READ'>" + $printText + "</ansi:hasPermission> " + $noteLink;}
    				            } }
    				            ],
    				            "initComplete": function(settings, json) {
    				            	//console.log(json);
    				            	//INVOICELOOKUP.doFunctionBinding();
    				            	var myTable = this;
        			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#invoiceTable", INVOICELOOKUP.createTable);
    				            },
    				            "drawCallback": function( settings ) {
    				            	INVOICELOOKUP.doFunctionBinding();
    				            	CALLNOTE.lookupLink();
    				            }
    				    } );
//    				$('.dataTables_filter input').unbind();        			
//    				return dataTable;	        	
    			},
    			
    			
    			
    			doFunctionBinding : function() {
    				$( ".invoicePrint" ).off( "click");  // make sure we don't get a double call
    				$( ".invoicePrint" ).on( "click", function($clickevent) {
    					INVOICELOOKUP.invoicePrint($clickevent);
    				});
    			},
    			
    			
    			getDivisionFilter : function() {
    				console.log("getDivisionFilter");
    				var $url = "division/" + INVOICELOOKUP.$filterDivisionId
    				var $callbacks = {
    					200:INVOICELOOKUP.getDivisionFilterSuccess,
    					404:INVOICELOOKUP.getDivisionFilterFailure,
    				};
    				ANSI_UTILS.makeServerCall("GET", $url, null, $callbacks, {});
    			},
    			
    			getDivisionFilterFailure : function($data, $passthru) {
    				console.log("getDivisionFilterFailure");
    				$("#divFilterMsg").html("Unexpected response (invalid division id)")
    			},
    			
    			
				getDivisionFilterSuccess : function($data, $passthru) {
					console.log("getDivisionFilterSuccess");
					$("#divFilterMsg").html("Filtered by division: " + $data.data.divisionList[0].divisionNbr + "-" + $data.data.divisionList[0].divisionCode)
    			},
    			
    			
    			invoicePrint : function($clickevent) {
    				var $invoiceId = $clickevent.currentTarget.attributes['data-invoiceId'].value;
    				INVOICE_PRINT.reprintInvoice($invoiceId);
    			},
    			
        	};
        	
        	INVOICELOOKUP.init();
        	
        });
        </script>
        <script type="text/javascript" src="js/invoicePrint.js"></script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Invoice <bean:message key="menu.label.lookup" /></h1>
    	
    	<webthing:lookupFilter filterContainer="filter-container" />
    	<span id="divFilterMsg" class="orange"></span>&nbsp;<span id="divFilterReset"><a href="invoiceLookup.html" style="text-decoration:none;"><webthing:ban>Reset</webthing:ban></a></span>
	 	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">	       	
	    </table>
    
		<webthing:scrolltop />
		<webthing:callNoteModals />
    
    </tiles:put>
		
</tiles:insert>

