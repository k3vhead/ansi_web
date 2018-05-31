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
			#summaryTable {
				width:100%;
				width:1300px;
				max-width:1300px;
			}
			#ticketData {
				width:100%;
				display:none;
			}
			#ticketDRV {
				max-width:1300px;
				width:1300px;
			}
			#xlsDownloadDiv {
				float:right;
				margin-right:30px;
				display:none;
			}
			#xlsDownload {
				text-decoration:none;				
			}
			#xlsDownload:visited {
				color:#000000;
			}
			.summaryHdr {
				font-weight:bold;
			}
			.right {
				text-align:right;
			}
			.dataTables_wrapper .dataTables_scroll div.dataTables_scrollBody {
				padding:0;
				margin:0;
				text-align:left;
				width:1300px;
				max-width:1300px;
			}
        </style>       
       
        <script type="text/javascript" src="js/ansi_utils.js"></script>
       
        <script type="text/javascript">
        $(document).ready(function() {
			// Populate the division list
        	$divisionList = ANSI_UTILS.getDivisionList();
			$("#divisionId").append(new Option("",""));
			$.each($divisionList, function(index, val) {
				var $displayValue = val.divisionNbr + "-" + val.divisionCode;
				$("#divisionId").append(new Option($displayValue, val.divisionId));
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
        		var $downloadUrl = "ticketDRV?format=xls&divisionId=" + $selectedDiv + "&month=" + $selectedMonth;
        		$("#xlsDownload").attr("href", $downloadUrl);
        		
        		var jqxhr = $.ajax({
    				type: 'GET',
    				url: 'ticketDRV',
    				data: $outbound,
    				success: function($data) {
    					populateSummary($data.data);
    					populateDataTable($data.data);
    					$("#ticketData").fadeIn(4000);
    					$("#xlsDownloadDiv").fadeIn(4000);
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
        	        //lengthMenu: [
        	        //    [ 10, 50, 100, 500, 1000, -1 ],
        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
        	        //],
        	        buttons: [
        	        //	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
        	        ],
        	        "columnDefs": [
        	        	{ "orderable": true, "targets": "_all" },
        	            { className: "dt-left", "targets": [0,2,3,4,5,6,11,-1] },
        	            { className: "dt-right", "targets": [0,9,10] },
        	            { className: "dt-center", "targets": [1,7,8] },
        	         ],
        	        "paging": false,
        	        data: $data.responseItemList,
			        columns: [
			            { title: "<bean:message key="field.label.ticketNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.ticketId != null){return (row.ticketId+"");}
			            } },
			            { title: "<bean:message key="field.label.ticketNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.status != null){return (row.status+"");}
			            } },
			            { title: "<bean:message key="field.label.name" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "<bean:message key="field.label.address1TV" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.address1 != null){return (row.address1+"");}
			            } },
			            { title: "<bean:message key="field.label.city" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.city != null){return (row.city+"");}
			            } },
			            { title: "<bean:message key="field.label.lastDone" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.lastDone != null){return (row.lastDone+"");}
			            } },
			            { title: "<bean:message key="field.label.startDate" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.startDate != null){return (row.startDate+"");}
			            } },
			            { title: "<bean:message key="field.label.jobNbr" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) { 	
			            	if(row.jobNum != null){return (row.jobNum+"");}
			            } },
			            { title: "<bean:message key="field.label.frequency" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.frequency != null){return (row.frequency+"");}
			            } },
			            { title: "<bean:message key="field.label.budget" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.budget!= null){return (row.budget+"");}
			            } },
			            { title: "<bean:message key="field.label.pricePerCleaning" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.ppc != null){return (row.ppc+"");}
			            } },
			            { title: "<bean:message key="field.label.cod" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.cod != null){return (row.cod+"");} 
			            } },
			            { title: "<bean:message key="field.label.jobId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
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
            //	$.each($('input'), function () {
			//	        $(this).css("height","20px");
			//	        $(this).css("max-height", "20px");
			//	    });
					
            	//populateDataTable();
            }; 
            function initComplete (){
              var r = $('#TicketDRV tfoot tr');
              r.find('th').each(function(){
                //$(this).css('padding', 8);
              });
              $('#TicketDRV thead').append(r);
              //$('#divisionSelect').css('text-align', 'center');
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
    	<h1>Division and Month Select</h1>
    	<div id="xlsDownloadDiv">
    		<a href="#" id="xlsDownload"><webthing:excel styleClass="fa-2x" style="cursor:pointer">Download</webthing:excel></a>
    	</div>
		<select id="divisionId">			
		</select>
		<select id="month">  
			<option value=""></option>
			<option value="<%= Calendar.JANUARY %>"><bean:message key="field.label.january" /></option>
			<option value="<%= Calendar.FEBRUARY %>"><bean:message key="field.label.february" /></option>
			<option value="<%= Calendar.MARCH %>"><bean:message key="field.label.march" /></option>
			<option value="<%= Calendar.APRIL %>"><bean:message key="field.label.april" /></option>
			<option value="<%= Calendar.MAY %>"><bean:message key="field.label.may" /></option>
			<option value="<%= Calendar.JUNE %>"><bean:message key="field.label.june" /></option>
			<option value="<%= Calendar.JULY %>"><bean:message key="field.label.july" /></option>
			<option value="<%= Calendar.AUGUST %>"><bean:message key="field.label.august" /></option>
			<option value="<%= Calendar.SEPTEMBER %>"><bean:message key="field.label.september" /></option>
			<option value="<%= Calendar.OCTOBER %>"><bean:message key="field.label.october" /></option>
			<option value="<%= Calendar.NOVEMBER %>"><bean:message key="field.label.november" /></option>
			<option value="<%= Calendar.DECEMBER %>"><bean:message key="field.label.december" /></option>
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
    
    
 	<table id="ticketDRV" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;max-width:1300px;width:1300px;">
        <colgroup>
        	<col style="width:5%;" />
    		<col style="width:5%;" />
    		<col style="width:12%;" />
    		<col style="width:12%;" />
    		<col style="width:12%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:6%;" />
    		<col style="width:7%;" />
    		<col style="width:7%;" />
    		<col style="width:8%;" />
    		<col style="width:7%;" />
    		<col style="width:7%;" />
   		</colgroup>        
        <thead>
            <tr>
                <th><bean:message key="field.label.ticketNbr" /></th>
    			<th><bean:message key="field.label.status" /></th>
    			<th><bean:message key="field.label.name" /></th>
    			<th><bean:message key="field.label.address1TV" /></th>
    			<th><bean:message key="field.label.city" /></th>
    			<th><bean:message key="field.label.lastDone" /></th>
    			<th><bean:message key="field.label.startDate" /></th>
    			<th><bean:message key="field.label.jobNbr" /></th>
    			<th><bean:message key="field.label.frequency" /></th>
    			<th><bean:message key="field.label.budget" /></th>
    			<th><bean:message key="field.label.pricePerCleaning" /></th>
    			<th><bean:message key="field.label.cod" /></th>
    			<th><bean:message key="field.label.jobId" /></th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th><bean:message key="field.label.ticketNbr" /></th>
    			<th><bean:message key="field.label.status" /></th>
    			<th><bean:message key="field.label.name" /></th>
    			<th><bean:message key="field.label.address1TV" /></th>
    			<th><bean:message key="field.label.city" /></th>
    			<th><bean:message key="field.label.lastDone" /></th>
    			<th><bean:message key="field.label.startDate" /></th>
    			<th><bean:message key="field.label.jobNbr" /></th>
    			<th><bean:message key="field.label.frequency" /></th>
    			<th><bean:message key="field.label.budget" /></th>
    			<th><bean:message key="field.label.pricePerCleaning" /></th>
    			<th><bean:message key="field.label.cod" /></th>
    			<th><bean:message key="field.label.jobId" /></th>
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

