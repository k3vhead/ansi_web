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
        Ticket View
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
        		var dataTable = $('#ticketTable').DataTable( {
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
        	            [ 10, 50, 100, 500, -1 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', 'Show all' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        "paging": true,
			        "ajax": {
			        	"url": "ticketTable",
			        	"type": "GET"
			        	},
			        columns: [
			            { title: "Ticket", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "Site", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "Street 1",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+"");}
			            } },
			            { title: "City", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobFreq != null){return (row.jobFreq+"");}
			            } },
			            { title: "Last Done", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "Start Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "J #", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNbr != null){return (row.jobNbr+"");}
			            } },
			            { title: "Freq", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobFreq != null){return (row.jobFreq+"");}
			            } },
			            { title: "Budget", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
			            } },
			            { title: "PPC", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
			            } },
			            { title: "COD", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return (row.jobId+"");} 
			            } },
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

						var $url = 'ticketTable/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#ticketId").val(($data.data.codeList[0]).ticketId);
				        		$("#status").val(($data.data.codeList[0]).status);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#jobFreq").val(($data.data.codeList[0]).jobFreq);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#jobFreq").val(($data.data.codeList[0]).jobFreq);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		
				        		$("#tId").val(($data.data.codeList[0]).ticketId);
				        		$("#updateOrAdd").val("update");
				        		$("#addTicketTableForm").dialog( "open" );
							},
							statusCode: {
								403: function($data) {
									$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
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
    	<h1>Ticket View</h1>
    	
 	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:1300px;width:1300px;">
        <colgroup>
        	<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:15%;" />
    		<col style="width:15%;" />
    		<col style="width:12%;" />
    		<col style="width:8%;" />
    		<col style="width:8%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
   		</colgroup>        
        <thead>
            <tr>
                <th>TICKET</th>
    			<th>STATUS</th>
    			<th>Site</th>
    			<th>Street 1</th>
    			<th>City</th>
    			<th>Last Done</th>
    			<th>Start Date</th>
    			<th>J#</th>
    			<th>FRQ</th>
    			<th>BUDGET</th>
    			<th>PPC</th>
    			<th>COD</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>TICKET</th>
    			<th>STATUS</th>
    			<th>Site</th>
    			<th>Street 1</th>
    			<th>City</th>
    			<th>Last Done</th>
    			<th>Start Date</th>
    			<th>J#</th>
    			<th>FRQ</th>
    			<th>BUDGET</th>
    			<th>PPC</th>
    			<th>COD</th>			
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

