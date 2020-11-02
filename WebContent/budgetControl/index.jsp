<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib tagdir="/WEB-INF/tags/bcr" prefix="bcr" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Budget Control
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/callNote.css" />
    	<link rel="stylesheet" href="css/accordion.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/ticket.js"></script> 
        <style type="text/css">
        	#bcr_title_prompt {
        		display:none;
        	}
			#bcr_panels .display {
				display:none;
			}
        	.aligned-center {
        		text-align:center;
        	}
        	.aligned-right {
        		text-align:right;
        	}
			.bcr_totals_display .column-header {
				font-weight:bold;
			}
			.bcr_totals_display th {
				text-align:right;
			}
        	.form-label {
				font-weight:bold;
			}			
			.table-header {
				text-align:center;
				font-weight:bold;
			}
			
			
			
			
			
			
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#ndl-crud-form {
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
			
			#ticket-modal {
				display:none;	
			}
			.ticket-clicker {
				color:#000000;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;BUDGETCONTROL = {      
        		workDate : null,
        		division : null,
        		
        		init : function() {
        			ANSI_UTILS.makeDivisionList(BUDGETCONTROL.makeDivListSuccess, BUDGETCONTROL.makeDivListFail);
        			BUDGETCONTROL.makeAccordion();
        			BUDGETCONTROL.makeClickers();
        		},
        		
        		
        		dateCallFail : function($data) {
        			console.log("dateCallFail");
        			$("#globalMsg").html("Failure Retrieving Dates. Contact Support");
        		},
        		
        		
        		
        		dateCallSuccess: function($data) {
        			console.log("dateCallSuccess");
        			BUDGETCONTROL.workDate = $data.data;
        			 $("#bcr_title_prompt .workDateDisplay").html($data.data.firstOfMonth + " - " + $data.data.lastOfMonth);
        		},
        		
        		
        		
        		
        		makeAccordion : function() {
        			$('ul.accordionList').accordion({
						//autoHeight: true,
						heightStyle: "content",
						alwaysOpen: true,
						header: 'h4',
						fillSpace: false,
						collapsible: true,
						active: true
					});
        		},
        		
        		
        		
        		makeClickers : function() {
        			
        		},
        		
        		
        		
        		makeDivListFail : function($data) {
        			console.log("makeDivListFail");
        			$("#globalMsg").html("Failure Retrieving Divisions. Contact Support").show();
        		},
        		
        		
        		
        		makeDivListSuccess : function($data) {
        			console.log("makeDivListSuccess");
        			var $divisionField = $("#bcr_title_prompt select[name='divisionId']");
        			$divisionField.append(new Option("",""));
       				$.each($data.data.divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$divisionField.append(new Option($displayValue, val.divisionId));
       				});
       				
        			BUDGETCONTROL.makeModals();
        			$( "#bcr_title_prompt" ).dialog("open");
        			$("#bcr_title_prompt .workDayDisplay").html("");
        			$("#workDaySelector").change(function() {
        				var $workDay = $("#workDaySelector").val();
        				console.log("Date changed: " + $workDay);
        				if ( $workDay == null || $workDay == "") {
        					$("#bcr_title_prompt .workDateDisplay").html("");
        				} else {
        					$("#bcr_title_prompt .workDateErr").html("");
	        				$outbound = {"workDate":$workDay,"format":"yyyy-MM-dd"};
	        				ANSI_UTILS.doServerCall("GET", "workdate", $outbound, BUDGETCONTROL.dateCallSuccess, BUDGETCONTROL.dateCallFail);
        				}
        			});
        		},
        		
        		
        		
        		makeModals : function() {
        			TICKETUTILS.makeTicketViewModal("#ticket-modal");
        			
        			
        			$( "#bcr_title_prompt" ).dialog({
        				title:'Budget Control',
        				autoOpen: false,
        				height: 200,
        				width: 500,
        				modal: true,
        				closeOnEscape:false,
        				open: function(event, ui) {
        					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				},
        				buttons: [
        					{
        						id:  "bcr_title_prompt_save",
        						click: function($event) {
        							BUDGETCONTROL.titleSave();        							
        						}
        					}
        				]
        			});	
        			$("#bcr_title_prompt_save").button('option', 'label', 'Go');
        			
        			
        		},
        		
        		
        		
        		populateActualDLPanel : function($data) {
        			var $weekLabel = ["1st","2nd","3rd","4th","5th"];

        			$.each($data.data.workCalendar, function($index, $value) {
						var $row = $("<tr>");
						var $label = $("<td>").append($weekLabel[$index] + " Wk in Mo");
						var $week = $("<td>").append($value.weekOfYear);
						var $firstOfWeek = $("<td>").append($value.firstOfWeek);
						$firstOfWeek.addClass("aligned-center");
						var $lastOfWeek = $("<td>").append($value.lastOfWeek);
						$lastOfWeek.addClass("aligned-center");
						var $actualDL = $('<input type="text" />');
						$actualDL.attr("name","actualDL-" + $value.weekOfYear);	
						var $omDL = $('<input type="text" />');
						$omDL.attr("name","actualDL-" + $value.weekOfYear);	
						$row.append($label);
						$row.append($week);
						$row.append($firstOfWeek);
						$row.append($lastOfWeek);
						$row.append( $("<td>").append($actualDL));
						$row.append( $("<td>").append($omDL));
						$("#actual_dl_totals tbody").append($row);
					});
        		},
        		
        		
        		
        		
        		populateBudgetControlTotalsPanel : function($data) {
        			var $headerRow1 = $("<tr>");
					$headerRow1.append($("<td>").append("&nbsp;"));
					$headerRow1.append($("<td>").append("&nbsp;")); 
					var $headerRow2 = $("<tr>");
					$headerRow2.append($("<td>").addClass("column-header").append("Week:"));
					$headerRow2.append($("<td>").addClass("column-header").addClass("aligned-right").append("Unclaimed")); 
					$.each($data.data.workCalendar, function($index, $value) {
						var $startDate = $value.firstOfWeek.substring(0, 5);
						var $endDate = $value.lastOfWeek.substring(0, 5);
						var $dates = $("<td>").append($startDate + "-" + $endDate);
						$dates.addClass("aligned-right");
						$headerRow1.append($dates);

						var $week = $("<td>").addClass("column-header").append($value.weekOfYear);
						$week.addClass("aligned-right");
						$headerRow2.append($week);
					});
					$headerRow1.append( $("<th>").append("Month") );
					$headerRow2.append( $("<th>").append("Total") );
					$("#bcr_totals .bcr_totals_display thead").append($headerRow1);
					$("#bcr_totals .bcr_totals_display thead").append($headerRow2);
					
					
					var $bcRowLabels = ["Volume Claimed","Claimed Volume Remaining",
								"Total Billed","Variance",
								"Total D/L Claimed","Actual D/L","Actual OM D/L","Total Actual D/L"];
					
					var $totalVolRow = $("<tr>");					
					$totalVolRow.append( $("<td>").append("Total Volume:") );
					$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$.each($data.data.workCalendar, function($index, $value) {
						$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					});
					$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$("#bcr_totals .bcr_totals_display tbody").append($totalVolRow);
					
					$.each($bcRowLabels, function($index, $value) {
						var $bcRow = $("<tr>");
						$bcRow.append( $("<td>").append($value+":") );
						$bcRow.append( $("<td>").addClass("aligned-right").append("n/a") );
						$.each($data.data.workCalendar, function($index, $value) {
							$bcRow.append( $("<td>").addClass("aligned-right").append("0.00") );
						});
						$bcRow.append( $("<td>").addClass("aligned-right").append("0.00") );
						$("#bcr_totals .bcr_totals_display tbody").append($bcRow);
					});
					
					$.each(["D/L Percentage","Actual D/L Percentage"], function($index, $value) {
						var $bcRow = $("<tr>");
						$bcRow.append( $("<td>").append($value+":") );
						$bcRow.append( $("<td>").append("&nbsp;") );
						$.each($data.data.workCalendar, function($index, $value) {
							$bcRow.append( $("<td>").addClass("aligned-right").append("0.00%") );
						});
						$bcRow.append( $("<td>").addClass("aligned-right").append("0.00%") );
						$("#bcr_totals .bcr_totals_display tbody").append($bcRow);
					});
					
        		},
        		
        		
        		
        		
        		populateTitlePanel : function($data) {
					$("#bcr_summary .dateCreated").html($data.data.dateCreated);
					$("#bcr_summary .dateModified").html($data.data.dateModified);
					$("#bcr_summary .workYear").html($data.data.workYear);
					$("#bcr_summary .workMonth").html($data.data.workMonth);
					$("#bcr_summary .workMonthName").html($data.data.workMonthName);
					$("#bcr_summary .firstOfMonth").html($data.data.firstOfMonth);
					$("#bcr_summary .lastOfMonth").html($data.data.lastOfMonth);
					$("#bcr_summary .div").html($data.data.div);
					$("#bcr_summary .managerFirstName").html($data.data.managerFirstName);
					$("#bcr_summary .managerLastName").html($data.data.managerLastName);
        		},
        		
        		
        		
        		titleSave : function() {
        			console.log("titleSave");
        			$("#bcr_title_prompt .err").html("");
        			var $divisionId = $("#bcr_title_prompt select[name='divisionId']").val();
        			console.log("Div: " + $divisionId);
        			$outbound = {"divisionId":$divisionId, "workDate":$("#workDaySelector").val()};
        			console.log(JSON.stringify($outbound));
        			ANSI_UTILS.doServerCall("POST", "bcr/title", JSON.stringify($outbound), BUDGETCONTROL.titleSaveSuccess, BUDGETCONTROL.titleSaveFail);
					
        		},
        		
        		
        		
        		titleSaveFail : function($data) {
        			console.log("titleSaveFail");
        			$.each($data.data.webMessages, function($key, $value) {        				
        				$("#bcr_title_prompt ."+$key+"Err").html($value[0]);
        			});
        		},
        		
        		
        		titleSaveSuccess : function($data) {
        			console.log("titleSaveSuccess");
        			BUDGETCONTROL.populateTitlePanel($data);
        			BUDGETCONTROL.populateActualDLPanel($data);
        			BUDGETCONTROL.populateBudgetControlTotalsPanel($data);

					$("#bcr_panels .thinking").hide();
					$("#bcr_panels .display").show();
        			$("#bcr_title_prompt").dialog("close");
        			$("#titleHeader").click();
        			
        		}
        	};
        	
        	
        	
        	
        	;BCR_PLACEHOLDER = {
        		datatable : null,
        		
        		init : function() {
        			BUDGETCONTROL.createTable();
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeModals();
        			CALLNOTE.init();
        		},
        		
        		
        		
				createTable : function() {
            		BUDGETCONTROL.dataTable = $('#displayTable').DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [0,1,2] },
            	            { className: "dt-center", "targets": [3,16] },
            	            { className: "dt-right", "targets": [4,5,6,7,8,9,10,11,12,13,14,15]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "claims/ticketStatusLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	
    			            { width:"4%", title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { width:"23%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return (row.job_site_name);}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_description + '</span></span>');}
    			            } },
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_amt != null){return (parseFloat(row.claimed_dl_amt).toFixed(2));}
    			            } },
    			            { title: "+ Expenses" , "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_exp != null){return (parseFloat(row.claimed_dl_exp).toFixed(2));}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_dl_total != null){return (parseFloat(row.claimed_dl_total).toFixed(2)+"");}
    			            } },
    			            { title: "Total Volume", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (parseFloat(row.total_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Volume Claimed", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_volume != null){return (parseFloat(row.claimed_volume).toFixed(2)+"");}
    			            } },
    			            { title: "Passthru",  "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.passthru_volume != null){return (parseFloat(row.passthru_volume).toFixed(2)+"");}
    			            } },			            
    			            { title: "Volume Claimed Total", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (parseFloat(row.claimed_volume_total).toFixed(2)+"");}
    			            } },
    			            { title: "Remaining Volume", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (parseFloat(row.volume_remaining).toFixed(2)+"");}
    			            } },
    			            { title: "Invoiced Amount", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (parseFloat(row.billed_amount).toFixed(2)+"");}
    			            } },
    			            { title: "Diff CLM/BLD", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (parseFloat(row.claimed_vs_billed).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Paid", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.paid_amt != null){return (parseFloat(row.paid_amt).toFixed(2)+"");}
    			            } },
    			            { title: "Amount Due", "defaultContent": "<i>N/A</i>", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			            	if(row.amount_due != null){return (parseFloat(row.amount_due).toFixed(2)+"");}
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  searchable:false, data: function ( row, type, set ) {	
    			            	{
    				            	var $claim = '';
    				            	if (row.ticket_status=='D' || row.ticket_status=='C') {
    				            		$claim = '<ansi:hasPermission permissionRequired='CLAIMS_READ'><a href="#" class="claimAction" data-id="'+row.ticket_id+'">Ticket Note<webthing:invoiceIcon styleClass="green">Budget Control</webthing:invoiceIcon></a></ansi:hasPermission>';
    				            	}
    				            	var $notesLink = '<webthing:notes xrefType="TICKET" xrefId="'+row.ticket_id+'">Ticket Note</webthing:notes>';
    				            	console.log($notesLink);
    			            		return $claim + $notesLink;
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	LOOKUPUTILS.makeFilters(this, "#filter-container", "#displayTable", BUDGETCONTROL.createTable);
    			            	//BUDGETCONTROL.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	BUDGETCONTROL.doFunctionBinding();
    			            	CALLNOTE.lookupLink();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doFunctionBinding : function () {
					$( ".claimAction" ).on( "click", function($clickevent) {
						var $ticketId = $(this).attr("data-id");
						location.href="budgetControlLookup.html?id="+$ticketId
					});
					$(".ticket-clicker").on("click", function($clickevent) {
						$clickevent.preventDefault();
						var $ticketId = $(this).attr("data-id");
						TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
						$("#ticket-modal").dialog("open");
					});
				},
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		
            	},
            	
            	
            	
            	makeModals : function() {
            		TICKETUTILS.makeTicketViewModal("#ticket-modal")
            	},
	    		
        	}
      	  	

        	BUDGETCONTROL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Budget Control</h1>
    	
    	<ul id="bcr_panels" class="accordionList">
    		<li class="accordionItem">    			
        		<h4 class="accHdr" id="titleHeader">Title</h4>
        		<div id="bcr_summary">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
        			<div class="display">
        				<bcr:title />        				
        			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Actual Direct Labor Totals</h4>
        		<div id="actual_dl_totals">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
        			<div class="display">
        				<bcr:actualDLTotals />
        			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Budget Control Totals</h4>
        		<div id="bcr_totals">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	       				<bcr:budgetControlTotals />
	       			</div>
        		</div>
       		</li>
    		<li class="accordionItem">    			
        		<h4 class="accHdr">Employees</h4>
        		<div id="bcr_employees">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	       				Employee List
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem">    			
        		<h4 class="accHdr">Tickets</h4>
        		<div id="bcr_tickets">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	       				TicketList
	       			</div>
        		</div>
       		</li>
   		</ul>
   		
   	    <webthing:lookupFilter filterContainer="filter-container" />
    	
	 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	       	<thead>
	        </thead>
	        <tfoot>
	        </tfoot>
	    </table>
	    <input type="button" value="New" class="prettyWideButton" id="new-NDL-button" />
	    
	    <webthing:scrolltop />
    
	    <webthing:ticketModal ticketContainer="ticket-modal" />
	    
	    
	    <div id="bcr_title_prompt">
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Division:</span></td>
	    			<td><select name="divisionId"></select></td>
	    			<td><span class="err divisionIdErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Date:</span></td>
	    			<td><input id="workDaySelector" type="date" name="workDate" /></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td>&nbsp;</td>
	    			<td><span class="workDateDisplay"></span></td>
	    		</tr>
	    	</table>
	    </div>
    </tiles:put>
		
</tiles:insert>

