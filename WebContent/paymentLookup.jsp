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
        Payment <bean:message key="menu.label.lookup" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
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
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.edit-action-link {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
      	  		$('.ScrollTop').click(function() {
      	   		 $('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    	});
      		});
            	       	
        	$(document).ready(function() {
        	var dataTable = null;
        	
        	function createTable(){
        		var dataTable = $('#paymentTable').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', 
        	        	{extend: 'pdfHtml5', orientation: 'landscape'}, 
        	        	'print',
        	        	{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "columnDefs": [
//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
        	            { className: "dt-left", "targets": [11,12,13] },
        	            { className: "dt-center", "targets": [0,2,3,4,5,6,9,10,14] },
        	            { className: "dt-right", "targets": [1,7,8]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "paymentLookup",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "Payment", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.paymentId != null){return (row.paymentId+"");}
			            } },
			            { title: "Amount", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentAmount != null){return (row.paymentAmount+"");}
			            } },
			            { title: "Date" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.paymentDate != null){return (row.paymentDate+"");}
			            } },
			            { title: "Type", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentType != null){return (row.paymentType+"");}
			            } },
			            { title: "Check #",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.checkNbr != null){return (row.checkNbr+"");}
			            } },
			            { title: "Check Date",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.checkDate != null){return (row.checkDate+"");}
			            } },
			            { title: "Ticket",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "Ticket PPC",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
			            	if(row.ticketAmount != null){return (row.ticketAmount+"");}
			            } },
			            { title: "Ticket Tax",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
			            	if(row.ticketTax != null){return (row.ticketTax+"");}
			            } },
			            { title: "Div", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticketDiv != null){return (row.ticketDiv+"");}
			            } },
			            { title: "Invoice",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceId != null){return (row.invoiceId+"");}
			            } },
			            { title: "Bill To", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "Job Site", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "Note",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentNote != null){return (row.paymentNote+"");}
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	{return '<i class="edit-action-link ui-icon ui-icon-pencil" data-paymentId="' +row.paymentId+'" />"';}
			            } }
			            ],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	doFunctionBinding();
			            }
			    } );
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
					$( ".edit-action-link" ).on( "click", function($clickevent) {
			        	var $paymentId = $clickevent.currentTarget.attributes['data-paymentId'].value;
		        		location.href="payment.html?id=" + $paymentId;
					});
				}
				

        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payment <bean:message key="menu.label.lookup" /></h1>
    	
 	<table id="paymentTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
       	<colgroup>
			<col style="width:5%;" />
			<col style="width:6%;" />
			<col style="width:5%;" />
			<col style="width:5%;" />
			<col style="width:5%;" />
			<col style="width:6%;" />
			<col style="width:5%;" />
			<col style="width:6%;" />
			<col style="width:5%;" />
			<col style="width:5%;" />
			<col style="width:5%;" />
			<col style="width:12%;" />
			<col style="width:12%;" />
			<col style="width:12%;" />
			<col style="width:5%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Payment</th>
    			<th>Amount</th>
    			<th>Date</th>
    			<th>Type</th>
    			<th>Check #</th>
    			<th>Check Date</th>
    			<th>Ticket</th>
    			<th>Ticket PPC</th>
    			<th>Ticket Tax</th>
    			<th>Div</th>
    			<th>Invoice</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Payment Note</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Payment</th>
    			<th>Amount</th>
    			<th>Date</th>
    			<th>Type</th>
    			<th>Check #</th>
    			<th>Check Date</th>
    			<th>Ticket</th>
    			<th>Ticket PPC</th>
    			<th>Ticket Tax</th>
    			<th>Div</th>
    			<th>Invoice</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Payment Note</th>
    			<th>Action</th>
            </tr>
        </tfoot>
    </table>
    
    <p align="center">
    	<br>
    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
    	</br>
    </p>
    
    </tiles:put>
		
</tiles:insert>

