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
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
			}
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#state-menu {
			  max-height: 300px;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			select	{
				width:80px !important;
				max-width:80px !important;
			}
			
			#addTicketTableForm{
				display: none;
			}
		

			
        </style>
        
        <script type="text/javascript">        
        
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
        	            [ 10, 25, 50, -1 ],
        	            [ '10 rows', '25 rows', '50 rows', 'Show all' ]
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
			            { title: "Ticket ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "Bill To" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.billToName != null){return (row.billToName+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteName != null){return (row.jobSiteName+"");}
			            } },
			            { title: "Address",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobSiteAddress != null){return (row.jobSiteAddress+"");}
			            } },
			            { title: "Start Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "Frequency", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.jobFrequency != null){return (row.jobFrequency+"");}
			            } },
			            { title: "PPC", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.pricePerCleaning != null){return (row.pricePerCleaning+"");}
			            } },
			            { title: "Job #", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNbr != null){return (row.jobNbr+"");}
			            } },
			            { title: "Job ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return (row.jobId+"");} 
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
			            { title: "Action",  data: function ( row, type, set ) {	
			            	//console.log(row);
			            	if(row.count > 0)
			            		return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.ticketId+"'></a></ansi:hasWrite></ansi:hasPermission>";
			            	else
			            		return "<ansi:hasPermission permissionRequired='SYSADMIN'><ansi:hasWrite><a href='#' class=\"editAction ui-icon ui-icon-pencil\" data-id='"+row.ticketId+"'></a>|<a href='#' data-id='"+row.ticketId+"'  class='delAction ui-icon ui-icon-trash'></a></ansi:hasWrite></ansi:hasPermission>";
			            	
			            		
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
        	
        	$("#addButton").button().on( "click", function() {
        		$("#updateOrAdd").val("add");
        	    $("#addTicketTableForm").dialog( "open" );
        	      	
            });
        	
        	$( "#addTicketTableForm" ).dialog({
      	      autoOpen: false,
      	      height: 450,
      	      width: 500,
      	      modal: true,
      	      buttons: {
      	        "Update Ticket": addTicketTable,
      	        Cancel: function() {
      	        	$( "#addTicketTableForm" ).dialog( "close" );
      	        }
      	      },
      	      close: function() {
      	    	  $( "#addTicketTableForm" ).dialog( "close" );
      	        //allFields.removeClass( "ui-state-error" );
      	      }
      	    });

        	function addTicketTable() {
        		$outbound = {};
        		$outbound["ticketId"]		=	$("#ticketId").val();
        		$outbound["status"]		=	$("#status").val();
        		$outbound["billToName"]	=	$("#billToName").val();
        		$outbound["jobSiteName"]	=	$("#jobSiteName").val();
        		$outbound["jobSiteAddress"]		=	$("#jobSiteAddress").val();
        		$outbound["startDate"]		=	$("#startDate").val();
        		$outbound["pricePerCleaning"]	=	$("#pricePerCleaning").val();
        		$outbound["jobNbr"]		=	$("#jobNbr").val();
        		$outbound["jobId"]		=	$("#jobId").val();
        		$outbound["serviceDescription"]		=	$("#serviceDescription").val();
        		$outbound["processDate"]		=	$("#processDate").val();
        		$outbound["invoiceId"]		=	$("#invoiceId").val();
        		
        		if($("#updateOrAdd").val() =="add"){
				$url = "ticketTable/add";
				console.log($outbound);
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {

							clearAddForm();
							$( "#addTicketTableForm" ).dialog( "close" );
								if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
									$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
								}
							
							$("#ticketTable").DataTable().draw();
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {

							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
							}
							$( "#addTicketTableForm" ).dialog( "close" );
						} else {
							//alert("success other");
						}
					},
					error: function($data) {
						alert("fail");
						//console.log($data);
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
        		} else if($("#updateOrAdd").val() == "update"){
        			$url = "ticketTable/" + $("#tId").val();
        			
    				console.log($outbound);
    				var jqxhr = $.ajax({
    					type: 'POST',
    					url: $url,
    					data: JSON.stringify($outbound),
    					success: function($data) {
    						if ( $data.responseHeader.responseCode == 'SUCCESS') {
    							//alert("success");
    							console.log($data);
    							//createData();
    					
    							clearAddForm();
    							$( "#addTicketTableForm" ).dialog( "close" );
	    							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
	    								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
	    							}
    							
    							$("#ticketTable").DataTable().draw();
    						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
    							//alert("success fail");
    							$.each($data.data.webMessages, function(key, messageList) {
    								var identifier = "#" + key + "Err";
    								msgHtml = "<ul>";
    								$.each(messageList, function(index, message) {
    									msgHtml = msgHtml + "<li>" + message + "</li>";
    								});
    								msgHtml = msgHtml + "</ul>";
    								$(identifier).html(msgHtml);
    							});		
    							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
    								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
    							}
    							$( "#addTicketTableForm" ).dialog( "close" );
    						} else {
    							//alert("success other");
    						}
    					},
    					error: function($data) {
    						alert("fail");
    						//console.log($data);
    					},
    					statusCode: {
    						403: function($data) {
    							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    						} 
    					},
    					dataType: 'json'
    				});        			
        		}
				$( "#addTicketTableForm" ).dialog( "close" );
        	}
        	
            function clearAddForm() {
	        	$("#ticketId").val("");
	        	$("#status option[value='']").attr('selected', true);
	        	$("#billToName").val("");
	        	$("#jobSiteName").val("");
	        	$("#jobSiteAddress").val("");
	        	$("#startDate").val("");
	        	$("#pricePerCleaning")[0].selectedIndex = 0;
	        	$("#jobNbr").selectmenu("refresh");
	        	$("#jobId")[0].selectedIndex = 0;
	        	$("#serviceDescription").selectmenu("refresh");
	        	$("#processsDate").val("");
	        	$("#invoiceId").selectmenu("refresh");
            }
				
				function doFunctionBinding() {
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});
					//console.log("Functions Bound");
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

					console.debug("ERR1");
						var $url = 'ticketTable/' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								console.debug("ERR3");
								
				        		$("#ticketId").val(($data.data.codeList[0]).ticketId);
				        		$("#status").val(($data.data.codeList[0]).status);
				        		$("#billToName").val(($data.data.codeList[0]).billToName);
				        		$("#jobSiteName").val(($data.data.codeList[0]).jobSiteName);
				        		$("#jobSiteAddress").val(($data.data.codeList[0]).jobSiteAddress);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#pricePerCleaning").val(($data.data.codeList[0]).pricePerCleaning);
				        		$("#jobNbr").val(($data.data.codeList[0]).jobNbr);
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		$("#serviceDescription").val(($data.data.codeList[0]).serviceDescription);
				        		$("#processDate").val(($data.data.codeList[0]).processDate);
				        		$("#invoiceId").val(($data.data.codeList[0]).invoiceId);
				        		
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
					}
				});
					//console.log("Edit Button Clicked: " + $rowid);
        </script>        
    </tiles:put>
    
    <tiles:put name="content" type="string">
    	<h1>Ticket Lookup</h1>
    	
 	<table id="ticketTable" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:980px;width:980px;">
        <thead>
            <tr>
                <th>Ticket Id</th>
    			<th>Status</th>
    			<th>Bill To</th>
    			<th>Name</th>
    			<th>Address</th>
    			<th>Start Date</th>
    			<th>Frequency</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Job</th>
    			<th>Service Description</th>
    			<th>Process Date</th>
    			<th>Invoice</th>
    			<th>Action</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Ticket Id</th>
    			<th>Status</th>
    			<th>Bill To</th>
    			<th>Name</th>
    			<th>Address</th>
    			<th>Start Date</th>
    			<th>Frequency</th>
    			<th>PPC</th>
    			<th>Job #</th>
    			<th>Job</th>
    			<th>Service Description</th>
    			<th>Process Date</th>
    			<th>Invoice</th>
    			<th>Action</th>
    			
            </tr>
        </tfoot>
    </table>
    <ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" id="addButton" class="prettyWideButton" value="New" />
    			</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
    </tiles:put>

		<div id="addTicketTableForm" title="Add/Update TicketTable" class="ui-widget" style="display:none;">
		  <p class="validateTips">All form fields are required.</p>
		 
		  <form id="addForm">
		   <table>
			<tr>
				<td><b>Ticket ID</b></td>
				<td colspan="3"><input type="text" name="ticketId" id="ticketId" style="width:315px" /></td>
			</tr>
			<tr>
				<td><b>Status</b></td>
				<td colspan="3">
					<select name="status" id="status" style="width:85px !important;max-width:85px !important;">
						<option value="0">Bad</option>
						<option value="1">Good</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Bill To:</td>
				<td colspan="3"><input type="text" name="billToName" id="billToName" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Name:</td>
				<td colspan="3"><input type="text" name="jobSiteName" id="jobSiteName" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Address:</td>
				<td colspan="3"><input type="text" name="jobSiteAddress" id="jobSiteAddress" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Start Date:</td>
				<td colspan="3"><input type="text" name="startDate" id="startDate" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Frequency:</td>
				<td colspan="3"><input type="text" name="jobFrequency" id="jobFrequency" style="width:315px" /></td>
			</tr>
			<tr>
				<td>PPC:</td>
				<td colspan="3"><input type="text" name="pricePerCleaning" id="pricePerCleaning" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Job #:</td>
				<td colspan="3"><input type="text" name="jobNbr" id="jobNbr" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Job ID:</td>
				<td colspan="3"><input type="text" name="jobId" id="jobId" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Service Description:</td>
				<td colspan="3"><input type="text" name="serviceDescription" id="serviceDescription" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Process Date:</td>
				<td colspan="3"><input type="text" name="processDate" id="processDate" style="width:315px" /></td>
			</tr>
			<tr>
				<td>Invoice ID:</td>
				<td colspan="3"><input type="text" name="invoiceId" id="invoiceId" style="width:315px" /></td>
			</tr>
		</table>
		  </form>
		</div>
		
		<input  type="text" id="updateOrAdd" style="display:none"><input  type="text" id="tId" style="display:none">
</tiles:insert>

