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
				display:none;
			}
			#divisionSelect{
				border:solid 1px #000000;
			}
			.summaryHdr {
				font-weight:bold;
			}
        </style>       
       
        <script type="text/javascript" src="js/ansi_utils.js"></script>
       
        <script type="text/javascript">
		$(function() {
			console.debug("Starting");
			// Populate the division list
        	$divisionList = ANSI_UTILS.getDivisionList();
        	console.debug("Got the list");
			$("#divisionId").append(new Option("",""));
			$.each($divisionList, function(index, val) {
				$("#divisionId").append(new Option(val.divisionCode, val.divisionId));
			});
			// Make the dropdowns pretty
			//$("#divisionId").selectmenu();
			//$("#month").selectmenu();

			
			$("select").change(function () {
				var $selectedMonth = $('#month option:selected').val();  
				var $selectedDiv = $('#divisionId option:selected').val();
        		if ($selectedMonth != '' && $selectedDiv != '') {
        			doPopulate($selectedDiv, $selectedMonth)
        		}
        	});
        	
        	$("#doPopulate").click(function() { 
        		var $selectedMonth = $('#month option:selected').val();  
				var $selectedDiv = $('#divisionId option:selected').val();
        		if ($selectedMonth != '' && $selectedDiv != '') {
        			doPopulate($selectedDiv, $selectedMonth)
        		} else {
        			console.debug("Not populating");
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
    				},
    				statusCode: {
    					403: function($data) {
    						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    					},
    					500: function($data) {
             	    		$("#globalMsg").html("Unhandled Exception").fadeIn(10).fadeOut(6000);
             	    	} 
    				},
    				dataType: 'json'
    			});
            	
        	}
        	
        	function populateSummary($data) {
				$("#division").html($data.division.divisionNbr + "-" + $data.division.divisionCode);
				$("#startDate").html($data.startDate);
				$("#totalVolume").html($data.totalVolume);
				$("#runDate").html($data.runDate);
				$("#summaryTable").fadeIn(4000);
        	}
        	
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    });
        	
        	function addRow(index, $ticketDRV) {	
				var $rownum = index + 1;
       			rowTd = makeRow($ticketDRV, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#summaryTable').append(row);
			}
        });
		


		</script>
       
       
       <!-- 
       
               	function makeRow($division, $rownum) {
				var row = "";
				row = row + '<td>' + $ticketDRV.divisionId + '</td>'; 
				row = row + '<td>' + $ticketDRV.month + '</td>';    			
				return row;
			}
        			
        	
        	
			
			
			
			function makeRow($ticketDRV, $rownum) {
				var row = "";
				row = row + '<td>' + $ticketDRV.startDate + '</td>';
				row = row + '<td>' + $ticketDRV.endDate + '</td>';
				row = row + '<td>' + $ticketDRV.ticketCount + '</td>';
				row = row + '<td>' + $ticketDRV.divisionCode + '</td>';
				row = row + '<td>' + $ticketDRV.runDate + '</td>';
				row = row + '<td>' + $ticketDRV.totalVolume + '</td>';
				row = row + '<td>' + $ticketDRV.totalDL + '</td>';  			
				return row;
			}	
                
        	});
        
        	var dataTable = null;
        	
        	function createTable(){
        		var dataTable = $('#ticketDRV').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        "bFilter":			true,
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
        	        	{ "orderable": false, "targets": "_all" },
        	            { className: "dt-left", "targets": [0,2,3,4,5,6,11,-1] },
        	            { className: "dt-right", "targets": [0,9,10] },
        	            { className: "dt-center", "targets": [1,7,8] },
        	         ],
        	        "paging": false,
			        "ajax": {
			        	"url": "/ansi_web/ticketDRV?month=nn&divisionId=nn",
			        	"dataSrc": "data.responseItemList"
			        	},
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
            
            function initComplete (){
              var r = $('#TicketDRV tfoot tr');
              r.find('th').each(function(){
                $(this).css('padding', 8);
              });
              $('#TicketDRV thead').append(r);
              $('#divisionSelect').css('text-align', 'center');
            }
                
				function doFunctionBinding() {
					$( ".editAction" ).on( "click", function($clickevent) {
						 doEdit($clickevent);
					});
				}
				
				function doEdit($clickevent) {
					var $rowid = $clickevent.currentTarget.attributes['data-id'].value;

						var $url = '/ansi_web/ticketDRV?month=nn&divisionId=nn' + $rowid;
						//console.log("YOU PASSED ROW ID:" + $rowid);
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							success: function($data) {
								//console.log($data);
								
				        		$("#ticketId").val(($data.data.codeList[0]).ticketId);
				        		$("#status").val(($data.data.codeList[0]).status);
				        		$("#name").val(($data.data.codeList[0]).name);
				        		$("#address1").val(($data.data.codeList[0]).address1);
				        		$("#city").val(($data.data.codeList[0]).city);
				        		$("#lastDone").val(($data.data.codeList[0]).lastDone);
				        		$("#startDate").val(($data.data.codeList[0]).startDate);
				        		$("#jobNum").val(($data.data.codeList[0]).jobNum);
				        		$("#frequency").val(($data.data.codeList[0]).frequency);
				        		$("#budget").val(($data.data.codeList[0]).budget);
				        		$("#ppc").val(($data.data.codeList[0]).ppc);
				        		$("#cod").val(($data.data.codeList[0]).cod);
				        		$("#jobId").val(($data.data.codeList[0]).jobId);
				        		
				        		$("#t_id").val(($data.data.codeList[0]).ticket_Id);
				        		$("#updateOrAdd").val("update");
				        		$("#addTicketDRVForm").dialog( "open" );
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
        	})
        }
       });
       
       
        -->
        

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
		
		
        <h2>Ticket View DRV</h2>
		<table id="summaryTable">
			<tr>
    			<td class="summaryHdr">Created:</td>
    			<td><span id="runDate"></span></td>
    			<td>&nbsp;</td>
    			<td class="summaryHdr">Division</td>
    			<td><span id="division"></span></td>
			</tr>
    		<tr>
    			<td class="summaryHdr">From:</td>
    			<td><span id="startDate"></span></td>
    			<td class="tableSplitter">&nbsp;</td>
    			<td class="summaryHdr">Total Volume for the Month</td>
    			<td><span id="totalVolume"></span></td>
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
    			<th>Address 1</th>
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
    			<th>Address 1</th>
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
    
    </tiles:put>
		
</tiles:insert>

