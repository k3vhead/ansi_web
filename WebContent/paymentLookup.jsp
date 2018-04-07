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
        	        "searchDelay":		800,
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
			        	
			            { title: "<bean:message key="field.label.paymentId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.paymentId != null){return (row.paymentId+"");}
			            } },
			            { title: "<bean:message key="field.label.paymentAmount" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentAmount != null){return (row.paymentAmount+"");}
			            } },
			            { title: "<bean:message key="field.label.paymentDate" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.paymentDate != null){return (row.paymentDate+"");}
			            } },
			            { title: "<bean:message key="field.label.paymentType" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentType != null){return (row.paymentType+"");}
			            } },
			            { title: "<bean:message key="field.label.checkNbr" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.checkNbr != null){return (row.checkNbr+"");}
			            } },
			            { title: "<bean:message key="field.label.checkDate" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.checkDate != null){return (row.checkDate+"");}
			            } },
			            { title: "<bean:message key="field.label.ticketId" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "<bean:message key="field.label.ticketAmount" />",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
			            	if(row.ticketAmount != null){return (row.ticketAmount+"");}
			            } },
			            { title: "<bean:message key="field.label.ticketTax" />",  "defaultContent": "<i>-</i>", data: function ( row, type, set ) {
			            	if(row.ticketTax != null){return (row.ticketTax+"");}
			            } },
			            { title: "<bean:message key="field.label.ticketDiv" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticketDiv != null){return (row.ticketDiv+"");}
			            } },
			            { title: "<bean:message key="field.label.invoiceId" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.invoiceId != null){return (row.invoiceId+"");}
			            } },
			            { title: "<bean:message key="field.label.billToName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "<bean:message key="field.label.jobSiteName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "<bean:message key="field.label.paymentNote" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.paymentNote != null){return (row.paymentNote+"");}
			            } },
			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	{return '<ansi:hasPermission permissionRequired="INVOICE"><ansi:hasWrite><i class="edit-action-link fas fa-pencil-alt" data-paymentId="' +row.paymentId+'" />"</ansi:hasWrite></ansi:hasPermission>';}
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
                <th><bean:message key="field.label.paymentId" /></th>
    			<th><bean:message key="field.label.paymentAmount" /></th>
    			<th><bean:message key="field.label.paymentDate" /></th>
    			<th><bean:message key="field.label.paymentType" /></th>
    			<th><bean:message key="field.label.checkNbr" /></th>
    			<th><bean:message key="field.label.checkDate" /></th>
    			<th><bean:message key="field.label.ticketId" /></th>
    			<th><bean:message key="field.label.ticketAmount" /></th>
    			<th><bean:message key="field.label.ticketTax" /></th>
    			<th><bean:message key="field.label.ticketDiv" /></th>
    			<th><bean:message key="field.label.invoiceId" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.jobSiteName" /></th>
    			<th><bean:message key="field.label.paymentNote" /></th>
    			<th><bean:message key="field.label.action" /></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th><bean:message key="field.label.paymentId" /></th>
    			<th><bean:message key="field.label.paymentAmount" /></th>
    			<th><bean:message key="field.label.paymentDate" /></th>
    			<th><bean:message key="field.label.paymentType" /></th>
    			<th><bean:message key="field.label.checkNbr" /></th>
    			<th><bean:message key="field.label.checkDate" /></th>
    			<th><bean:message key="field.label.ticketId" /></th>
    			<th><bean:message key="field.label.ticketAmount" /></th>
    			<th><bean:message key="field.label.ticketTax" /></th>
    			<th><bean:message key="field.label.ticketDiv" /></th>
    			<th><bean:message key="field.label.invoiceId" /></th>
    			<th><bean:message key="field.label.billToName" /></th>
    			<th><bean:message key="field.label.jobSiteName" /></th>
    			<th><bean:message key="field.label.paymentNote" /></th>
    			<th><bean:message key="field.label.action" /></th>
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

