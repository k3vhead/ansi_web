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
        Job Lookup
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
        		var dataTable = $('#jobTable').DataTable( {
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
        	            { className: "dt-left", "targets": [0,2,3,4,5,6,10,11] },
        	            { className: "dt-center", "targets": [1,7,9,12,-1] },
        	            { className: "dt-right", "targets": [8]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "jobTable",
			        	"type": "GET"
			        	},
			        columns: [
			        	
			            { title: "Job", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return (row.jobId+"");}
			            } },
			            { title: "Quote", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.quoteId != null){return ('<a href="quoteMaintenance.html?id='+ row.quoteId+ '" style="color:#404040">' + row.quoteId+'</a>');}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobStatus != null){return (row.jobStatus+"");}
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
			            { title: "Service Description", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.serviceDescription != null){return (row.serviceDescription+"");}
			            } },
			            { title: "PO #", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.poNumber != null){return (row.poNumber+"");}
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	{return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='jobMaintenance.html?id="+row.jobId+"' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.jobId+"'></a></ansi:hasWrite></ansi:hasPermission>";}
			            	
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
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

						var $url = 'jobTable/' + $rowid;
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
				        		$("#addJobTableForm").dialog( "open" );
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
    	<h1>Job Lookup</h1>
    	
 	<table id="jobTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
       	<colgroup>
        	<col style="width:4%;" />
        	<col style="width:4%;" />
    		<col style="width:5%;" />    		
    		<col style="width:6%;" />
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:6%;" />
    		<col style="width:5%;" />
    		<col style="width:7%;" />
    		<col style="width:5%;" />
    		<col style="width:24%;" />
    		<col style="width:4%;" />
    		<col style="width:6%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Job</th>
                <th>Quote</th>
    			<th>Status</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Start Date</th>
    			<th>Freq</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Service Description</th>
    			<th>PO #</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Job</th>
                <th>Quote</th>
    			<th>Status</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Start Date</th>
    			<th>Freq</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Service Description</th>
    			<th>PO #</th>
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

