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
       	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" type="text/css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
	    <script type="text/javascript" src="js/callNote.js"></script>  
    	
    
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
        
        $(document).ready(function(){
        	var $filterDivisionId = '<c:out value="${ANSI_DIVISION_ID}" />';
        	var $filterPPC = '<c:out value="${ANSI_PPC_FILTER}" />';
        	if ( $filterPPC == 'yes' ) {
        		$filterIcon = "far fa-check-square";
        	} else {
        		$filterIcon = "fa fa-ban";
        	}
        	
			CALLNOTE.init();

			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
				return false;
      		});
            	       	
			function createTable() {
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
	        	        	{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        	{
	        	        		text:'Due Filter <i class="'+$filterIcon+'"></i>',
	        	        		action: function(e, dt, node, config) {
	        	        			if ( $filterPPC == 'yes' ) {
	        	        				$filterPPC = 'no';
	        	        			} else {
	        	        				$filterPPC = 'yes';
	        	        			}
	        	        			var $url = "invoiceLookup.html?divisionId=" + $filterDivisionId + "&ppcFilter=" + $filterPPC;
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
				        	"data":{"divisionId":$filterDivisionId,"<%=InvoiceLookupForm.PPC_FILTER%>":$filterPPC}
				        	},
				        columns: [
				        	
				            { title: "<bean:message key="field.label.invoiceId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.invoiceId != null){return (row.invoiceId+"");}
				            } },
				            { title: "<bean:message key="field.label.fleetmaticsInvoiceNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.fleetmaticsInvoiceNbr != null){return (row.fleetmaticsInvoiceNbr+"");}
				            } },
				            { title: "<bean:message key="field.label.divisionNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.div != null){return (row.div+"");}
				            } },
				            { title: "<bean:message key="field.label.billToName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.billToName != null){return (row.billToName+"");}
				            } },
				            { title: "<bean:message key="field.label.ticketCount" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.ticketCount != null){return (row.ticketCount+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoiceDate != null){return (row.invoiceDate+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceAmount" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoiceAmount != null){return (row.invoiceAmount+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceTax" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoiceTax != null){return (row.invoiceTax+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceTotal" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoiceTotal != null){return (row.invoiceTotal+"");}
				            } },
				            { title: "<bean:message key="field.label.invoicePaid" />",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
				            	if(row.invoicePaid != null){return (row.invoicePaid+"");}
				            } },
				            { title: "<bean:message key="field.label.invoiceBalance" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.invoiceBalance != null){return (row.invoiceBalance+"");}else{return(row.invoiceTotal+"");}
				            } },
				            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
				            	if ( row.printCount > 0 ) {
				            		$printText =  '<i class="fa fa-print invoicePrint tooltip" aria=hidden="true" data-invoiceId="'+row.invoiceId+'"><span class="tooltiptext">Reprint</span></i>';
				            	} else {
				            		$printText = ""; 
				            	}
				            	var $noteLink = '<webthing:notes xrefType="INVOICE" xrefId="' + row.invoiceId + '">Invoice Notes</webthing:notes>'
				            	{return "<ansi:hasPermission permissionRequired='INVOICE_READ'>" + $printText + "</ansi:hasPermission> " + $noteLink;}
				            } }
				            ],
				            "initComplete": function(settings, json) {
				            	//console.log(json);
				            	//doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	doFunctionBinding();
				            	CALLNOTE.lookupLink();
				            }
				    } );
//				$('.dataTables_filter input').unbind();        			
//				return dataTable;	        	
			}
        	init();
        			
            
            function init(){
					$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
					createTable();
            }; 
        	
        	        	
			function doFunctionBinding() {
				$( ".invoicePrint" ).off( "click");  // make sure we don't get a double call
				$( ".invoicePrint" ).on( "click", function($clickevent) {
					invoicePrint($clickevent);
				});
			}
			
			
			function invoicePrint($clickevent) {
				var $invoiceId = $clickevent.currentTarget.attributes['data-invoiceId'].value;
				INVOICE_PRINT.reprintInvoice($invoiceId);
			}
        });
        </script>
        <script type="text/javascript" src="js/invoicePrint.js"></script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Invoice <bean:message key="menu.label.lookup" /></h1>
    	
 	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
       	<colgroup>
        	<col style="width:5%;" />
        	<col style="width:6%;" />
        	<col style="width:5%;" />
        	<col style="width:18%;" />
        	<col style="width:5%;" />
        	<col style="width:10%;" />
        	<col style="width:8%;" />
        	<col style="width:8%;" />
        	<col style="width:8%;" />
        	<col style="width:8%;" />
        	<col style="width:8%;" />
        	<col style="width:8%" />
   		</colgroup>
        <thead>
            <tr>
                <th><bean:message key="field.label.invoiceId" /></th>
                <th><bean:message key="field.label.fleetmaticsInvoiceNbr" /></th>
    			<th><bean:message key="field.label.divisionNbr" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.ticketCount" /></th>
    			<th><bean:message key="field.label.invoiceDate" /></th>
    			<th><bean:message key="field.label.invoiceAmount" /></th>
    			<th><bean:message key="field.label.invoiceTax" /></th>
    			<th><bean:message key="field.label.invoiceTotal" /></th>
    			<th><bean:message key="field.label.invoicePaid" /></th>
    			<th><bean:message key="field.label.invoiceBalance" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th><bean:message key="field.label.invoiceId" /></th>
                <th><bean:message key="field.label.fleetmaticsInvoiceNbr" /></th>
    			<th><bean:message key="field.label.divisionNbr" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.ticketCount" /></th>
    			<th><bean:message key="field.label.invoiceDate" /></th>
    			<th><bean:message key="field.label.invoiceAmount" /></th>
    			<th><bean:message key="field.label.invoiceTax" /></th>
    			<th><bean:message key="field.label.invoiceTotal" /></th>
    			<th><bean:message key="field.label.invoicePaid" /></th>
    			<th><bean:message key="field.label.invoiceBalance" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </tfoot>
    </table>
    
		<webthing:scrolltop />
		<webthing:callNoteModals />
    
    </tiles:put>
		
</tiles:insert>

