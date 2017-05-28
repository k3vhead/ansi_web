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
        Invoice Print
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
        	#printModal {
        		display:none;
        	}
			.action-link {
				cursor:pointer;
			}
			#pdfDownload {
				display:none;
			}
			#hangOn {
				display:none;
			}
        </style>
        
        <script type="text/javascript">        
        $( document ).ready(function() {
        	var $invoiceGenMessage = '<c:out value="${invoice_gen_message}" />'
        	if ( $invoiceGenMessage != '' ) {
        		$("#globalMsg").html($invoiceGenMessage).show().fadeOut(6000);
        	}

        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });

        	$("#printAll").click(function($event) {
        		INVOICE_PRINT.printModal("all");
        	});
        
	         var jqxhr = $.ajax({
	   			type: 'GET',
	   			url: 'invoicePrintLookup/',
	   			data: null,
	   			success: function($data) {   				
	   				populateDataTable($data.data);
	   			},
	   			statusCode: {
	   				403: function($data) {
	   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
	   				},
	   				404: function($data) {
	   					$("#globalMsg").html("System Error: Contact Support").show();
	   				},
	   				500: function($data) {
	        	    		$("#globalMsg").html("System Error: Contact Support").show();
	        	    	} 
	   			},
	   			dataType: 'json'
	   		});        	
        	
        	
         
			$('#invoiceTable').dataTable().fnDestroy();
			
	           
			var dataTable = null;
        	
			function populateDataTable($data) {
				var dataTable = $('#invoiceTable').DataTable( {
	        			"bDestroy":			true,
	        			"processing": 		true,
	        			"autoWidth": 		false,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        "searching": 		true,
	        	        //lengthMenu: [
	        	        //    [ 10, 50, 100, 500, 1000, -1 ],
	        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
	        	        //],
	        	        buttons: [
	        	        //	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        ],
	        	        "columnDefs": [
	        	        	{ "orderable": true, "targets": "_all" },
	        	            { className: "dt-left", "targets": [0] },
	        	            { className: "dt-right", "targets": [3,4] },
	        	            { className: "dt-center", "targets": [1,2,5] },
	        	         ],
	        	        "paging": false,
	        	        data: $data.invoiceList,
				        columns: [
				            { title: "Division", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.div != null){return (row.div+"");}
				            } },
				            { title: "Invoices", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.invoiceCount != null){return (row.invoiceCount+"");}
				            } },
				            { title: "Tickets", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.ticketCount != null){return (row.ticketCount+"");}
				            } },
				            { title: "Tax",  "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.taxTotal != null){return (row.taxTotal+"");}
				            } },
				            { title: "PPC", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.invoiceTotal != null){return (row.invoiceTotal+"");}
				            } },
				            { title: "Action", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.invoiceCount == "0"){
				            		return "";
				            	} else {
				            		var $dataDiv = 'data-division="' + row.divisionId + '"';
				            		var $listLink = '<i class="action-link fa fa-list" aria-hidden="true" data-action="list" ' + $dataDiv + '></i>';
				            		var $printLink = '<i class="action-link fa fa-print" aria-hidden="true" data-action="print" ' + $dataDiv + '></i>';
				            		return $listLink + ' | ' + $printLink;
				            	}
				            } }
				            ],
				            "initComplete": function(settings, json) {
				            	doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	doFunctionBinding();
				            }
				    } );
	        	}
        	
        		init();
        			
        	
	            function init(){
	            //	$.each($('input'), function () {
				//	        $(this).css("height","20px");
				//	        $(this).css("max-height", "20px");
				//	    });
						
	            	//populateDataTable();
	            };
	            
	            function initComplete (){
					var r = $('#invoiceTable tfoot tr');
					r.find('th').each(function(){
						$(this).css('padding', 8);
					});
					$('#invoiceTable thead').append(r);
	            }
            
				function doFunctionBinding() {
					$( ".action-link" ).on( "click", function($clickevent) {
			        	var $divisionId = $clickevent.currentTarget.attributes['data-division'].value;
			        	var $action = $clickevent.currentTarget.attributes['data-action'].value;
			        	if ( $action=='list') {
			        		location.href="invoiceLookup.html?id=" + $divisionId;
			        	} else if ( $action=='print') {
			        		INVOICE_PRINT.printModal($divisionId);
			        	} else {
			        		$("#globalMessage").html("Invalid action. Reload the page and start again");
			        	}
					});
				}

				
				INVOICE_PRINT.init("printModal")

        
			});

		</script>
		<script type="text/javascript" src="js/invoicePrint.js"></script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Invoice Print</h1>

    	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
    	    	<col style="width:15%;" />
        		<col style="width:15%;" />
        		<col style="width:15%;" />
	        	<col style="width:15%;" />
	        	<col style="width:15%;" />
	        	<col style="width:15%;" />
	   		</colgroup>        
	        <thead>
	            <tr>
	                <th>Division</th>
	    			<th>Invoices</th>
	    			<th>Tickets</th>
	    			<th>Tax</th>
	    			<th>PPC</th>
	    			<th>Action</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>Division</th>
	    			<th>Invoices</th>
	    			<th>Tickets</th>
	    			<th>Tax</th>
	    			<th>PPC</th>
	    			<th>Action</th>
	            </tr>
	        </tfoot>
    	</table>
    	<input type="button" value="Print All Invoices" id="printAll" class="prettyWideButton" />
    
    
    	<a id="pdfDownload"></a>
    	
		<webthing:invoicePrint modalName="printModal" />
    </tiles:put>

</tiles:insert>

