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
        Ticket Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
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
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
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
				var $jobId = '<c:out value="${ANSI_JOB_ID}" />';

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
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
        	        ],
        	        
        	        "columnDefs": [
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-head-left", "targets": [0,2,3,4,5,6,10,11,12,13] },
        	            { className: "dt-body-center", "targets": [1,7,9,-1] },
        	            { className: "dt-right", "targets": [8]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "ticketTable",
			        	"type": "GET",
			        	"data": {"jobId":$jobId}
			        	},
			        columns: [
			            { title: "Ticket", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ticketStatus != null){return (row.ticketStatus+"");}
			            } },
			            { title: "Div", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.divisionNbr != null){return (row.divisionNbr+"");}
			            } },
			            { title: "Bill To" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "Job Site", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "Job Address",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+"");}
			            } },
			            { title: "Start Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "Freq", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
			            } },
			            { title: "PPC", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
			            } },
			            { title: "Job #", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNbr != null){return (row.jobNbr+"");}
			            } },
			            { title: "Job ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return ('<span class="editJob" data-jobid="'+ row.jobId +'">'+row.jobId+"</span>");} 
			            } },
			            { title: "Service Description", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
			            } },
			            { title: "Process Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.processDate != null){return (row.processDate+"");}
			            } },
			            { title: "Invoice", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.invoiceId != null){return (row.invoiceId+"");} 
			            } },
			            { title: "FM ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.fleetmaticsId != null){return (row.fleetmaticsId+"");} 
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	if ( row.ticketId == null ) {
			            		$actionData = "";
			            	} else {
				            	var $editLink = "<a href='ticketReturn.html?id="+row.ticketId+"' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.ticketId+"'></a>";
				            	if ( row.ticketStatus == 'N' || row.ticketStatus == 'D' ) {
				            		var $ticketData = 'data-id="' + row.ticketId + '"';
				            		$printLink = '<i class="print-link fa fa-print" aria-hidden="true" ' + $ticketData + '></i>'
				            	} else {
				            		$printLink = "";
				            	}
				            	$actionData = "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite>" + $editLink + ' ' + $printLink + "</ansi:hasWrite></ansi:hasPermission>"
			            	}
			            	return $actionData;
			            } }],
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
					$(".print-link").on( "click", function($clickevent) {
						doPrint($clickevent);
					});
					$(".editJob").on( "click", function($clickevent) {
						console.debug("clicked a job")
						var $jobId = $(this).data("jobid");
						location.href="jobMaintenance.html?id=" + $jobId;
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
				        		$("#ticketStatus").val(($data.data.codeList[0]).ticketStatus);
				        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobFreq").val(($data.data.codeList[0]).jobFreq);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
				        		$("#processDate").val(($data.data.codeList[0]).processDate);
				        		$("#invoiceId").val(($data.data.codeList[0]).invoiceId);
				        		$("#fleetmaticsId").val(($data.data.codeList[0]).fleetmaticsId);
				        		
				        		$("#tId").val(($data.data.codeList[0]).ticketId);
				        		$("#updateOrAdd").val("update");
				        		$("#addTicketTableForm").dialog( "open" );
							},
							statusCode: {
								403: function($data) {
									$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
								} 
							},
							dataType: 'json'
						});
					//console.log("Edit Button Clicked: " + $rowid);
				}
				
				function doPrint($clickevent) {
					var $ticketId = $clickevent.currentTarget.attributes['data-id'].value;
					console.debug("ROWID: " + $ticketId);
					var a = document.createElement('a');
                    var linkText = document.createTextNode("Download");
                    a.appendChild(linkText);
                    a.title = "Download";
                    a.href = "ticketPrint/" + $ticketId;
                    a.target = "_new";   // open in a new window
                    document.body.appendChild(a);
                    a.click();				}
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Ticket Lookup</h1>    	
 	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <colgroup>
        	<col style="width:5%;" />
    		<col style="width:5%;" />
    		<col style="width:4%;" />
    		<col style="width:8%;" />
    		<col style="width:8%;" />
    		<col style="width:7%;" />
    		<col style="width:6%;" />
    		<col style="width:5%;" />
    		<col style="width:5%;" />
    		<col style="width:4%;" />
    		<col style="width:4%;" />
    		<col style="width:16%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:5%;" />
    	</colgroup>
        <thead>
            <tr>
                <th>Ticket</th>
    			<th>Status</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Start Date</th>
    			<th>Freq</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Job</th>
    			<th>Service Description</th>
    			<th>Process Date</th>
    			<th>Invoice</th>
    			<th>FM ID</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Ticket</th>
    			<th>Status</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Start Date</th>
    			<th>Freq</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Job</th>
    			<th>Service Description</th>
    			<th>Process Date</th>
    			<th>Invoice</th>
    			<th>FM ID</th>
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

