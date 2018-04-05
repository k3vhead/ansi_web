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
        Permission Group Lookup
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
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
				return false;
       	    });

        	var dataTable = null;
        	
        	function createTable(){

        		var dataTable = $('#permissionGroupTable').DataTable( {
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
        	            { className: "dt-head-left", "targets": [1,2] },
        	            { className: "dt-body-center", "targets": [0,3,4] },
        	            { className: "dt-right", "targets": []}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "permissionGroupLookup",
			        	"type": "GET",
			        	"data": {}
			        	},
			        aaSorting:[1],
			        columns: [
			        	{ title: "ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.permissionGroupId != null){return (row.permissionGroupId+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "Description", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.description != null){return (row.description+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	var $isActive = '<i class="fa fa-check-square-o inputIsValid" aria-hidden="true"></i>'
			            	var $isNotActive = '<i class="fa fa-ban inputIsInvalid" aria-hidden="true"></i>'
			            	if(row.permissionGroupStatus != null){
			            		if ( row.permissionGroupStatus == 1 ) {
			            			return $isActive;
			            		} else {
			            			return $isNotActive;
			            		}
			            	}
			            } },
			            { title: "User Count" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	$userLink = '<a href="userLookup.html?id=' + row.permissionGroupId + '" style="color:#404040;">' + row.userCount+ '</a>';
			            	if(row.userCount != null){return $userLink;}
			            } },
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	$actionData = "";
			            	$actionData = "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite>" + "<!-- an action link goes here -->" + "</ansi:hasWrite></ansi:hasPermission>"
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
					//$(".editJob").on( "click", function($clickevent) {
					//	console.debug("clicked a job")
					//	var $jobId = $(this).data("jobid");
					//	location.href="jobMaintenance.html?id=" + $jobId;
					//});
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
				        		$("#amountDue").val(($data.data.codeList[0]).amountDue);
				        		
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
                    a.click();				
                }
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Permission Group Lookup</h1>    	
	 	<table id="permissionGroupTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:5%;" />
	    		<col style="width:30%;" />
	    		<col style="width:35%;" />
	    		<col style="width:10%;" />
	    		<col style="width:10%;" />
	    		<col style="width:10%;" />
	    	</colgroup>
	        <thead>
	            <tr>
	                <th>ID</th>
	    			<th>Name</th>
	    			<th>Description</th>
	    			<th>Status</th>
	    			<th>User Count</th>
	    			<th>Action</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>ID</th>
	    			<th>Name</th>
	    			<th>Description</th>
	    			<th>Status</th>
	    			<th>User Count</th>
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

