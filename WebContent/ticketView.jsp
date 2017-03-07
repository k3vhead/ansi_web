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
			#headerTable { 
				display:none; 
			}			
			#divisionSelect{
				border:solid 1px #000000;
			}
        </style>       
       
        <script type="text/javascript">   
        
        $(function() {       	
        	$("#divisionId").change(function () {
        		if (month.selected) {
        			doPopulate
        		}
        	});
        	
        	$("#month").change(function () {
        		if (divisionId.selected) {
        			doPopulate
        		}
        	});
        	
        	function doPopulate () {
        	
        	var jqxhr = $.ajax({
				type: 'GET',
				url: '"/ansi_web/ticketDRV?month=nn&divisionId=nn"',
				data: {},
				success: function($data) {
					$.each($data.data.divisionList, function(index, value) {
						addRow(index, value);
					});
					doFunctionBinding();
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
        	
        	
        	
        	function makeRow($division, $rownum) {
				var row = "";
				row = row + '<td>' + $ticketDRV.divisionId + '</td>'; 
				row = row + '<td>' + $ticketDRV.month + '</td>';    			
				return row;
			}
        			
        	
        	
        	var jqxhr = $.ajax({
				type: 'GET',
				url: '"/ansi_web/ticketDRV?month=nn&divisionId=nn"',
				dataSrc: {},
				success: function($data) {
					$.each($data.data.ticketDRV, function(index, value) {
						addRow(index, value);
					});
					doFunctionBinding();
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
			
			function addRow(index, $ticketDRV) {	
				var $rownum = index + 1;
       			rowTd = makeRow($ticketDRV, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#summaryTable').append(row);
			}
			
			function doFunctionBinding() {
				$('.updAction').bind("click", function($clickevent) {
					doUpdate($clickevent);
				});
				$('.delAction').bind("click", function($clickevent) {
					doDelete($clickevent);
				});
				$('.dataRow').bind("mouseover", function() {
					$(this).css('background-color','#CCCCCC');
				});
				$('.dataRow').bind("mouseout", function() {
					$(this).css('background-color','transparent');
				});
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
                
        $(document).ready(function(){
        	  $('.ScrollTop').click(function() {
        	    $('html, body').animate({scrollTop: 0}, 800);
        	  return false;
        	    });
        	});
        
        	$(document).ready(function() {
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
        		
        </script>        
    </tiles:put>
    
    <tiles:put name="content" type="string">
    	<h1 >Division and Month Select</h1>
		<table style="border:solid 1px #000000; margin-top:8px;" id="divisionId" style="font-size:9pt;max-width:200px;">
			<tbody>
			<select id="divisionId" class="divisionId" onchange="doPopulate">			
  					<option value="9">9</option>
  					<option value="12">12</option>
  					<option value="15">15</option>
  					<option value="18">18</option>
  					<option value="19">19</option>
  					<option value="23">23</option>
  					<option value="31">31</option>
  					<option value="32">32</option>
  					<option value="44">44</option>
  					<option value="65">65</option>
  					<option value="66">66</option>
  					<option value="67">67</option>
  					<option value="71">71</option>
  					<option value="77">77</option>
  					<option value="78">78</option>
  					<option value="89">89</option>
				</select>
		<select id="month" class="month" onchange="doPopulate">  
  					<option value="1">1</option>
  					<option value="2">2</option>
  					<option value="3">3</option>
  					<option value="4">4</option>
  					<option value="5">5</option>
  					<option value="6">6</option>
  					<option value="7">7</option>
  					<option value="8">8</option>
  					<option value="9">9</option>
  					<option value="10">10</option>
  					<option value="11">11</option>
  					<option value="12">12</option>
  				</select>
  			<input id="doPopulate" type="button" value="Select" />
				<tr><td>&nbsp;</td></tr>
			</tbody>
		</table>  
		
		
        <h2 align=center>Ticket View DRV</h2>
		<table id="summaryTable">
    		<tr>
    			<th>Start Date</th>
    			<th>End Date</th>
				<th>Ticket Count</th>
    			<th>Division</th>
    			<th>Run Date</th>
    			<th>Total Volume</th>
    			<th>Total D/L</th>
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

