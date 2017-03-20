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

<%@ page import="java.util.Calendar" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Ticket View (DRV)
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
			#summaryTable {
				width:90%;
			}
			#ticketData {
				display:hidden;
			}
			#divisionSelect{
				border:solid 1px #000000;
			}
			.summaryHdr {
				font-weight:bold;
			}
			.right {
				text-align:right;
			}
        </style>       
       
        <script type="text/javascript" src="js/ansi_utils.js"></script>
       
        <script type="text/javascript">
        $(document).ready(function() {
			// Populate the division list
        	$divisionList = ANSI_UTILS.getDivisionList();
			$("#divisionId").append(new Option("",""));
			$.each($divisionList, function(index, val) {
				$("#divisionId").append(new Option(val.divisionCode, val.divisionId));
			});

			
			$("select").change(function () {
				var $selectedMonth = $('#month option:selected').val();  
				var $selectedDiv = $('#divisionId option:selected').val();
        		if ($selectedMonth != '' && $selectedDiv != '') {
        			doPopulate($selectedDiv, $selectedMonth)
        		}
        	});
        	
        	        	
        	function doPopulate($selectedDiv, $selectedMonth) {
        		var $outbound = {'month':$selectedMonth,'divisionId':$selectedDiv};
        		console.debug($outbound);
        		
        		var jqxhr = $.ajax({
    				type: 'GET',
    				url: 'ticketDRV',
    				data: $outbound,
    				success: function($data) {
    					populateSummary($data.data);
    					populateDataTable($data.data);
    					$("#ticketData").fadeIn(4000);
    				},
    				statusCode: {
    					403: function($data) {
    						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    					},
    					500: function($data) {
             	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
             	    	} 
    				},
    				dataType: 'json'
    			});
            	
        	}
        	
        	function populateSummary($data) {
				$("#division").html($data.division.divisionNbr + "-" + $data.division.divisionCode);
				$("#startDate").html($data.startDate);
				$("#endDate").html($data.endDate);
				$("#ticketCount").html($data.ticketCount);
				$("#totalDL").html($data.totalDL);
				$("#totalVolume").html($data.totalVolume);
				$("#runDate").html($data.runDate);
				
        	}
        	
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    });
             
             
             
			$('#ticketDRV').dataTable().fnDestroy();
			
           
        	var dataTable = null;
        	
        	function populateDataTable($data) {
        		var dataTable = $('#ticketDRV').DataTable( {
        			"bDestroy":			true,
        			"processing": 		true,
        			"autoWidth": 		false,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        lengthMenu: [
        	            [ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ],
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
        	        ],
        	        "columnDefs": [
        	        	{ "orderable": false, "targets": "_all" },
        	            { className: "dt-left", "targets": [0,2,3,4,5,6,11,-1] },
        	            { className: "dt-right", "targets": [0,9,10] },
        	            { className: "dt-center", "targets": [1,7,8] },
        	         ],
        	        "paging": true,
        	        data: $data.responseItemList,
			        columns: [
			            { title: "Ticket", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "Address",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address1 != null){return (row.address1+"");}
			            } },
			            { title: "City", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.city != null){return (row.city+"");}
			            } },
			            { title: "Last Done", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.lastDone != null){return (row.lastDone+"");}
			            } },
			            { title: "Start Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "Job #", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNum != null){return (row.jobNum+"");}
			            } },
			            { title: "Freq", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.frequency != null){return (row.frequency+"");}
			            } },
			            { title: "Budget", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.budget!= null){return (row.budget+"");}
			            } },
			            { title: "PPC", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ppc != null){return (row.ppc+"");}
			            } },
			            { title: "COD", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.cod != null){return (row.cod+"");} 
			            } },
			            { title: "Job Id", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.jobId != null){return (row.jobId+"");} 
			            } },
			            ],
			            "initComplete": function(settings, json) {
			            },
			            "drawCallback": function( settings ) {
			            }
			    } );
        	}
        	
        	init();
        			
            function init(){
            	$.each($('input'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
					
            	//populateDataTable();
            }; 
            function initComplete (){
              var r = $('#TicketDRV tfoot tr');
              r.find('th').each(function(){
                $(this).css('padding', 8);
              });
              $('#TicketDRV thead').append(r);
              $('#divisionSelect').css('text-align', 'center');
            }
            
            

        	
        	$("#doPopulate").click(function() { 
        		var dataTable = $('#ticketDRV').DataTable();
        		//dataTable.ajax.reload();
        		var $selectedMonth = $('#month option:selected').val();  
				var $selectedDiv = $('#divisionId option:selected').val();
        		if ($selectedMonth != '' && $selectedDiv != '') {
        			doPopulate($selectedDiv, $selectedMonth)
        		}
        	});
            

        })
       </script>
        

	</tiles:put>
    <tiles:put name="content" type="string">
    	<h1 >Division and Month Select</h1>
		<select id="divisionId">			
		</select>
		<select id="month">  
			<option value=""></option>
			<option value="<%= Calendar.JANUARY %>">January</option>
			<option value="<%= Calendar.FEBRUARY %>">February</option>
			<option value="<%= Calendar.MARCH %>">March</option>
			<option value="<%= Calendar.APRIL %>">April</option>
			<option value="<%= Calendar.MAY %>">May</option>
			<option value="<%= Calendar.JUNE %>">June</option>
			<option value="<%= Calendar.JULY %>">July</option>
			<option value="<%= Calendar.AUGUST %>">August</option>
			<option value="<%= Calendar.SEPTEMBER %>">September</option>
			<option value="<%= Calendar.OCTOBER %>">October</option>
			<option value="<%= Calendar.NOVEMBER %>">November</option>
			<option value="<%= Calendar.DECEMBER %>">December</option>
 		</select>
 		<input id="doPopulate" type="button" value="Go" />
		
		<div id="ticketData">
        <h2>Ticket View DRV</h2>
		<table id="summaryTable">
			<tr>
    			<td class="summaryHdr">Created:</td>
    			<td><span id="runDate"></span></td>
    			<td>&nbsp;</td>
    			<td class="summaryHdr right">Division</td>
    			<td class="right"><span id="division"></span></td>
			</tr>
			<tr>
				<td class="summaryHdr" colspan="2">Start Dates Used</td>
				<td>&nbsp;</td>
				<td class="summaryHdr right">Total Volume For the Month</td>
				<td class="right"><span id="totalVolume"></span></td>
			</tr>
    		<tr>
    			<td class="summaryHdr">From:</td>
    			<td><span id="startDate"></span></td>
    			<td class="tableSplitter">&nbsp;</td>
    			<td class="summaryHdr right">Total D/L for the Month</td>
    			<td class="right"><span id="totalDL"></span></td>
    		</tr>
    		<tr>
    			<td class="summaryHdr">To:</td>
    			<td><span id="endDate"></span></td>
    			<td class="tableSplitter">&nbsp;</td>
    			<td class="summaryHdr right">Tickets:</td>
    			<td class="right"><span id="ticketCount"></span></td>
    		</tr>
    	</table>
    
    
 	<table id="ticketDRV" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:1300px;width:1300px;">
        <colgroup>
        	<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:12%;" />
    		<col style="width:12%;" />
    		<col style="width:12%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:8%;" />
    		<col style="width:8%;" />
    		<col style="width:8%;" />
    		<col style="width:7%;" />
    		<col style="width:7%;" />
   		</colgroup>        
        <thead>
            <tr>
                <th>Ticket</th>
    			<th>Status</th>
    			<th>Name</th>
    			<th>Address</th>
    			<th>City</th>
    			<th>Last Done</th>
    			<th>Start Date</th>
    			<th>Job #</th>
    			<th>Frq</th>
    			<th>Budget</th>
    			<th>PPC</th>
    			<th>COD</th>
    			<th>Job ID</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Ticket</th>
    			<th>Status</th>
    			<th>Name</th>
    			<th>Address</th>
    			<th>City</th>
    			<th>Last Done</th>
    			<th>Start Date</th>
    			<th>Job #</th>
    			<th>Frq</th>
    			<th>Budget</th>
    			<th>PPC</th>
    			<th>COD</th>
    			<th>Job ID</th>
            </tr>
        </tfoot>
    </table>
    
    <p align="center">
    	<br>
    	<a href="#" title="Scroll to Top" class="ScrollTop">Scroll To Top</a>
    	</br>
    </p>
    </div>
    </tiles:put>
		
</tiles:insert>

