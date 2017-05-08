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
        Quote Lookup
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
			.quotePrint {
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
        		var dataTable = $('#quoteTable').DataTable( {
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
        	            { className: "dt-left", "targets": [0,2,3,4,5,7] },
        	            { className: "dt-center", "targets": [1,8,-1] },
        	            { className: "dt-right", "targets": [9]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "quoteTable",
			        	"type": "GET"
			        	},
			        columns: [
			            { title: "ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.quoteId != null){return (row.quoteId+"");}
			            } },
			            { title: "Quote", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.quoteCode != null){return (row.quoteCode+"");}
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
			            { title: "Manager", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.managerName != null){return (row.managerName+"");}
			            } },
			            { title: "Proposal Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.proposalDate != null){return (row.proposalDate+"");}
			            } },
			            { title: "Job Count", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.quoteJobCount != null){return (row.quoteJobCount+"");}
			            } },
			            { title: "PPC Sum", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.quotePpcSum != null){return (row.quotePpcSum+"");} 
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	editText = '<a href="quoteMaintenance.html?id='+row.quoteId+'" class="editAction ui-icon ui-icon-pencil" data-id="'+row.quoteId+'"></a>';
			            	printText =  '<i class="fa fa-print quotePrint" aria=hidden="true" data-id="'+row.quoteId+'"></i>';
			            	{return '<ansi:hasPermission permissionRequired="QUOTE">'+ editText +  ' ' + printText + '<ansi:hasWrite></ansi:hasWrite></ansi:hasPermission>';}
			            	
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

						var $url = 'quoteTable/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#quoteId").val(($data.data.codeList[0]).quoteId);
				        		$("#quoteCode").val(($data.data.codeList[0]).quoteCode);
				        		$("#divisionNbr").val(($data.data.codeList[0]).divisionNbr);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#managerName").val(($data.data.codeList[0]).managerName);
				        		$("#proposalDate").val(($data.data.codeList[0]).proposalDate);
				        		$("#quoteJobCount").val(($data.data.codeList[0]).quoteJobCount);
				        		$("#quotePpcSum").val(($data.data.codeList[0]).quotePpcSum);
				        		
				        		$("#qId").val(($data.data.codeList[0]).quoteId);
				        		$("#updateOrAdd").val("update");
				        		$("#addQuoteTableForm").dialog( "open" );
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
    	<h1>Quote Lookup</h1>
    	
 	<table id="quoteTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <colgroup>
        	<col style="width:4%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:16%;" />
    		<col style="width:16%;" />
    		<col style="width:14%;" />
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:5%;" />
    		<col style="width:8%;" />
    		<col style="width:5%;" />  
    	</colgroup> 
        <thead>
            <tr>
                <th>ID</th>
    			<th>Quote</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Manager</th>
    			<th>Proposal Date</th>
    			<th>Job Count</th>
    			<th>PPC Sum</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>ID</th>
    			<th>Quote</th>
    			<th>Div</th>
    			<th>Bill To</th>
    			<th>Job Site</th>
    			<th>Job Address</th>
    			<th>Manager</th>
    			<th>Proposal Date</th>
    			<th>Job Count</th>
    			<th>PPC Sum</th>
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

