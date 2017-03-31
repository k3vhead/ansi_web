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
        Invoice <bean:message key="menu.label.lookup" />
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
        		var dataTable = $('#invoiceTable').DataTable( {
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
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "columnDefs": [
//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
        	            { className: "dt-left", "targets": [0,1,2] },
        	            { className: "dt-center", "targets": [3,4] },
        	            { className: "dt-right", "targets": [5,6,7,8,9]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "invoiceLookup",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "Invoice", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.invoiceId != null){return (row.invoiceId+"");}
			            } },
			            { title: "Div", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.div != null){return (row.div+"");}
			            } },
			            { title: "Bill To", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "Tickets" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketCount != null){return (row.ticketCount+"");}
			            } },
			            { title: "Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceDate != null){return (row.invoiceDate+"");}
			            } },
			            { title: "Amount",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceAmount != null){return (row.invoiceAmount+"");}
			            } },
			            { title: "Tax",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceTax != null){return (row.invoiceTax+"");}
			            } },
			            { title: "Total",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceTotal != null){return (row.invoiceTotal+"");}
			            } },
			            { title: "Paid",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
			            	if(row.invoicePaid != null){return (row.invoicePaid+"");}
			            } },
			            { title: "Balance",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceBalance != null){return (row.invoiceBalance+"");}else{return(row.invoiceTotal+"");}
			            } },
			            //{ title: "Action",  data: function ( row, type, set ) {	
			            //	{return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";}
			            //} }
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
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

						var $url = 'invoiceLookup/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		$("#jobStatus").val(($data.data.codeList[0]).jobStatus);
				        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobFrequency").val(($data.data.codeList[0]).startDate);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
				        		$("#poNumber").val(($data.data.codeList[0]).processDate);
				        		
				        		$("#jId").val(($data.data.codeList[0]).jobId);
				        		$("#updateOrAdd").val("update");
				        		$("#addinvoiceTableForm").dialog( "open" );
							},
							statusCode: {
								403: function($data) {
									$("#useridMsg").html("Session Timeout. Log in and try again");
								} 
							},
							dataType: 'json'
						});
					//console.log("Edit Button Clicked: " + $rowid);
				}
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Invoice <bean:message key="menu.label.lookup" /></h1>
    	
 	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:800px;width:800px;">
       	<colgroup>
        	<col style="width:5%;" />
        	<col style="width:5%;" />
        	<col style="width:20%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
        	<col style="width:10%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Invoice</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Tickets</th>
    			<th>Invoice Date</th>
    			<th>Amount</th>
    			<th>Tax</th>
    			<th>Total</th>
    			<th>Paid</th>
    			<th>Balance</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Invoice</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Tickets</th>
    			<th>Invoice Date</th>
    			<th>Amount</th>
    			<th>Tax</th>
    			<th>Total</th>
    			<th>Paid</th>
    			<th>Balance</th>
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

