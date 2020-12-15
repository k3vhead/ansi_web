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
        	#bcr_edit_modal {
        		display:none;
        	}
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
			.ticket-week-display {
				display:none;
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
        		ticketTable : {},
        		
        		init : function() {
        			BUDGETCONTROL.makeSelectionLists();
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeAccordion();
        			BUDGETCONTROL.makeEditModal();
        		},
        		
        		
        		actualDLError : function($data) {
        			console.log("actualDLError");
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
        		
        		
        		
        		doFunctionBinding : function() {
        			console.log("doFunctionBinding");
        			$(".ticket-clicker").on("click", function($clickevent) {
    					$clickevent.preventDefault();
    					var $ticketId = $(this).attr("data-id");
    					TICKETUTILS.doTicketViewModal("#ticket-modal",$ticketId);
    					$("#ticket-modal").dialog("open");
    				});	
        			
        			$(".bcr-edit-clicker").on("click", function($clickevent) {
        				$clickevent.preventDefault();
        				var $ticketId = $(this).attr("data-ticketid");
        				console.log("edit clicked: " + $ticketId);
        				$("#bcr_edit_modal .ticketId").html($ticketId);
        				$("#bcr_edit_modal").dialog("open");
        			});
        		},
        		
        		
        		
        		doTicketLookup : function($destination,$url, $outbound) {
        			console.log("doTIcketLookup");
        			console.log($destination);
        			console.log($url);
        			console.log($outbound)
        			
        			BUDGETCONTROL.ticketTable[$destination] = $($destination).DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#ticketTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,9,13] },
            	            { className: "dt-center", "targets": [12] },
            	            { className: "dt-right", "targets": [5,6,7,8,10,11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET",
    			        	"data": $outbound,
    			        	},
    			        columns: [
    			        	{ title: "Account", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.job_site_name != null){return ('<a href="#" class="bcr-edit-clicker" data-ticketid="'+row.ticket_id+'">'+row.job_site_name+'</a>');}
    			            } },
    			            { title: "Ticket Number", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_id != null){return ('<a href="#" data-id="'+row.ticket_id+'" class="ticket-clicker">'+row.ticket_id+'</a>');}
    			            } },
    			            { title: "Claim Week", width:"4%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.claim_week != null ){return row.claim_week;}    			            	
    			            	//return BUDGETCONTROL.makeClaimWeekSelect(row.ticket_id, $data.data.workCalendar);
    			            } },
    			            { title: "D/L", width:"6%", searchable:true, "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.dl_amt != null){return (row.dl_amt.toFixed(2)+"");}
    			            } },
    			       //     { title: "+Exp", width:"6%", searchable:true, searchFormat: "First Last Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.ansi_contact != null){return (row.ansi_contact+"");}
    			       //     	return '<input type="text" style="width:20px;"/>';
    			       //     } },
    			       //     { title: "Total D/L", width:"6%", searchable:true, searchFormat: "YYYY-MM-dd hh:mm", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			       //     	//if(row.start_time != null){return (row.start_time+"");}
    			       //     	return 'x';
    			       //     } },
    			            { title: "Total Volume",  width:"6%", searchable:true, searchFormat: "Type Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2)+"");}
    			            } },		
    			            { title: "Volume Claimed",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_claimed != null){return (row.volume_claimed.toFixed(2)+"");}
    			            } },
    			            { title: "Volume Remaining",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return (row.volume_remaining.toFixed(2)+"");}
    			            } },
    			            { title: "Notes",  width:"10%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	var $displayNote = '';
    			            	if(row.notes != null && row.notes != ''){$displayNote = '<span class="tooltip">'+row.notes_display+'<span class="tooltiptext">'+row.notes+'</span></span>';}
    			            	return $displayNote;
    			            } },
    			            { title: "Billed Amount",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return (row.billed_amount.toFixed(2)+"");}
    			            } },
    			            { title: "Diff Clm/Bld",  width:"6%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return (row.claimed_vs_billed.toFixed(2)+"");}
    			            } },
    			            { title: "Ticket Status",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.ticket_status != null){return (row.ticket_status+"");}
    			            } },
    			            { title: "Service",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.service_tag_id != null){return (row.service_tag_id+"");}
    			            } },
    			            { title: "Equipment",  width:"4%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.equipment_tags != null){return (row.equipment_tags+"");}
    			            } },
    			            { title: "Employee",  width:"13%", searchable:true, searchFormat: "Name #####", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.employee != null){return (row.employee+"");}
    			            } }
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	//CALL_NOTE_LOOKUP.doFunctionBinding();
    			            	BUDGETCONTROL.doFunctionBinding();
    			            }
    			    } );
        		},
        		
        		
        		
        		
        		initializeActualDLPanel : function($data) {
        			console.log("initializeActualDLPanel");
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
						$actualDL.addClass("actualDL");
						$actualDL.attr("data-week",$value.weekOfYear);
						$actualDL.attr("data-year",$data.data.workYear);
						var $omDL = $('<input type="text" />');
						$omDL.attr("name","omDL-" + $value.weekOfYear);	
						$omDL.addClass("omDL");
						$omDL.attr("data-week",$value.weekOfYear)
						$omDL.attr("data-year",$data.data.workYear);
						$row.append($label);
						$row.append($week);
						$row.append($firstOfWeek);
						$row.append($lastOfWeek);
						$row.append( $("<td>").append($actualDL));
						$row.append( $("<td>").append($omDL));
						$("#actual_dl_totals tbody").append($row);
					});
        			
        		
        			$("#actual_dl_totals input").blur(function($event) {
        				var $that = $(this);
        				var $week = $that.attr("data-week");
        				var $value = $that.val();
        				if ( isNaN($value) || $value == '' || $value == null) {
        					$value = "0.00";
        				}
        				$value = parseFloat($value).toFixed(2);
        				$that.val( $value );
						if ( $that.hasClass("actualDL") ) {
							var $selector = "#bcr_totals .actual_dl_week" + $week;
							var $looper = "#bcr_totals .actual_dl";
							var $totalCell = "#bcr_totals .actual_dl_total";
						} else if ( $that.hasClass("omDL") ) {
							var $selector = "#bcr_totals .actual_om_dl_week" + $week;
							var $looper = "#bcr_totals .actual_om_dl";
							var $totalCell = "#bcr_totals .actual_om_dl_total";
						} else {
							//$("#globalMsg").html("Invalid system state. Reload and try again").show();
							// we're just going to ignore this
						}
						$($selector).html($value);
						var $rowTotal = 0.00;
						$.each( $($looper), function($index, $value) {
							$rowTotal = $rowTotal + parseFloat($($value).html());
						});
						$($totalCell).html($rowTotal.toFixed(2));
						
        			});
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
        		
        		
        		
        		makeClaimWeekSelect : function($ticket_id, $workCalendar) {
        			var $select = $("<select>");
        			$select.attr("data-ticketid",$ticket_id);
        			var $defaultOption = $("<option>")
       				$defaultOption.attr("value","");
       				$defaultOption.append("");
       				$select.append($defaultOption);
        			$.each( $workCalendar, function($index, $value) {
        				var $option = $("<option>");
        				$option.attr("value",$value.weekOfYear);
        				$option.append($value.workYear + "-" + $value.weekOfYear);
        				$select.append($option);
        			});
        			return $select[0].outerHTML;
        		},
        		
        		
        		makeClickers : function() {
        			
        		},
        		
        		
        		makeDateList : function($data) {
        			console.log("makeDateList");
        			var $dateField = $("#bcr_title_prompt select[name='workDate']");
        			$dateField.append(new Option("",""));
       				$.each($data.data.displayMonthList, function(index, val) {    
       					var $option = $("<option>")
       					$option.attr("value",val.workDate);
       					$option.attr("data-firstofmonth", val.firstOfMonth);
       					$option.attr("data-lastofmonth",val.lastOfMonth)
       					$option.append(val.displayDate);
       					$dateField.append($option);
       					//$dateField.append(new Option(val.displayDate, val.workDate));
       				});
        		},
        		
        		
        		makeDivList : function($data) {
        			console.log("makeDivList");
        			var $divisionField = $("#bcr_title_prompt select[name='divisionId']");
        			$divisionField.append(new Option("",""));
       				$.each($data.data.divisionList, function(index, val) {
       					var $displayValue = val.divisionNbr + "-" + val.divisionCode;
       					$divisionField.append(new Option($displayValue, val.divisionId));
       				});
        		},
        		
        		
        		makeEditModal : function() {
        			$( "#bcr_edit_modal" ).dialog({
        				title:'Edit Claim',
        				autoOpen: false,
        				height: 400,
        				width: 500,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "bcr_claim_edit_cancel",
        						click: function($event) {
        							$( "#bcr_edit_modal" ).dialog("close");    							
        						}
        					},{
        						id:  "bcr_claim_edit_save",
        						click: function($event) {
        							BUDGETCONTROL.editSave();        							
        						}
        					}
        				]
        			});	
        			$("#bcr_claim_edit_cancel").button('option', 'label', 'Cancel');
        			$("#bcr_claim_edit_save").button('option', 'label', 'Save');
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
        						id:  "bcr_title_prompt_cancel",
        						click: function($event) {
        							location.href="dashboard.html";        							
        						}
        					},{
        						id:  "bcr_title_prompt_save",
        						click: function($event) {
        							BUDGETCONTROL.titleSave();        							
        						}
        					}
        				]
        			});	
        			$("#bcr_title_prompt_cancel").button('option', 'label', 'Cancel');
        			$("#bcr_title_prompt_save").button('option', 'label', 'Go');
        			
        			
        		},
        		
        		
        		
        		// get a list of divisions, and calendars for initial modal
        		makeSelectionLists : function() {
        			ANSI_UTILS.doServerCall("GET", "bcr/init", null, BUDGETCONTROL.makeSelectionListSuccess, BUDGETCONTROL.makeSelectionListFail);
        		},
        		
        		
        		// process a failure in getting initial div list and calendar
        		makeSelectionListFail : function($data) {
        			console.log("makeSelectionListFail");
        			$("#globalMsg").html("Failure intializing page. Contact Support").show();
        		},
        		
        		
        		// process a successful retrieval of initial div list and calendar
        		makeSelectionListSuccess : function($data) {
        			console.log("makeSelectionListSuccess");
        			BUDGETCONTROL.makeDivList($data);
        			BUDGETCONTROL.makeDateList($data);
        			
        			BUDGETCONTROL.makeModals();
        			$( "#bcr_title_prompt" ).dialog("open");
        			$("#bcr_title_prompt .workDayDisplay").html("");
        			$("#workDaySelector").change(function() {
        				var $workDay = $("#workDaySelector").val();
        				if ( $workDay == null || $workDay == "") {
           					$("#bcr_title_prompt .workDateDisplay").html("");
            			} else {
            				var $selected = $("#workDaySelector option:selected");
            				$("#bcr_title_prompt .workDateDisplay").html($selected.attr("data-firstofmonth") + " - " + $selected.attr("data-lastofmonth"));
            			}
        			});        			
        		},
        		
        		
        		
        		
        		populateActualDLPanel : function($data) {
        			console.log("populateActualDLPanel");
        			$.each($data.data.weekActualDL, function($index, $value) {
        				console.log("Week: " + $index + " " + $value.actualDL);
        				
        				var $actualDL = parseFloat($value.actualDL).toFixed(2);
        				var $actualOM = parseFloat($value.omDL).toFixed(2);
        				
        				// populate actual dl panel
        				var $actual = 'input[name="actualDL-'+$index+'"]';
        				var $om = 'input[name="omDL-'+$index+'"]';
        				$($actual).val($actualDL);
        				$($om).val($actualOM);
        				
        				
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
					
					
					var $bcRowLabels = [
							{"label":"Volume Claimed:","className":"volume_claimed"},
							{"label":"Claimed Volume Remaining:","className":"claimed_volume_remaining"},
							{"label":"<break>"},
							{"label":"Total Billed:","className":"total_billed"},
							{"label":"Variance:","className":"variance"},
							{"label":"<break>"},
							{"label":"Total D/L Claimed:","className":"total_dl_claimed"},
							{"label":"Actual D/L:","className":"actual_dl"},
							{"label":"Actual OM D/L:","className":"actual_om_dl"},
							{"label":"Total Actual D/L:","className":"total_actual_dl"},
							{"label":"<break>"}
						];
					
					var $totalVolRow = $("<tr>");					
					$totalVolRow.append( $("<td>").append("Total Volume:") );
					$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$.each($data.data.workCalendar, function($index, $value) {
						$totalVolRow.append( $("<td>").addClass("aligned-right").addClass("total_volume").addClass("week"+$value.weekOfYear).append("0.00") );
					});
					$totalVolRow.append( $("<td>").addClass("aligned-right").append("0.00") );
					$("#bcr_totals .bcr_totals_display tbody").append($totalVolRow);
					
					$.each($bcRowLabels, function($index, $value) {
						var $bcRow = $("<tr>");
						if ( $value['label'] == "<break>") {
							$bcRow.append( $('<td colspan="6">').append("&nbsp;") );
						} else {
							console.log($value);
							$bcRow.append( $("<td>").append($value['label']) );
							$bcRow.append( $("<td>").addClass("aligned-right").append("n/a") );
							$.each($data.data.workCalendar, function($index, $calendarValue) {
								var $className = $value['className']+"_"+"week"+$calendarValue.weekOfYear;
								$bcRow.append( $("<td>").addClass($value['className']).addClass('week'+$calendarValue.weekOfYear).addClass("aligned-right").addClass($className).append("0.00") );
							});
							$bcRow.append( $("<td>").addClass("aligned-right").addClass($value['className']+'_total').append("0.00") );
						}
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
        		
        		
        		
        		populateTicketTables : function($data) {
        			console.log("populateTicketTables");
        			var $weekList = []
        			$.each($data.data.workCalendar, function($index, $value) {
        				var $destination = "#ticketTable" + $value.weekOfMonth;
        				var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear,"workWeek":$value.weekOfYear};
        				$weekList.push($value.weekOfYear);
        				BUDGETCONTROL.doTicketLookup($destination, "bcr/ticketList", $outbound);
        			});
        			var $workWeeks = $weekList.join(",");
        			var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear, "workWeek":$workWeeks};
        			BUDGETCONTROL.doTicketLookup("#ticketTable","bcr/ticketList", $outbound);
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
        			
        			BUDGETCONTROL.populateTicketTables($data);
        			BUDGETCONTROL.populateTitlePanel($data);
        			BUDGETCONTROL.initializeActualDLPanel($data);
        			
					$weekList = [];        			
        			$.each($data.data.workCalendar, function($index, $value) {
        				$weekList.push($value.weekOfYear);
        			});
        			var $workWeeks = $weekList.join(",");
        			var $outbound = {"divisionId":$data.data.divisionId, "workYear":$data.data.workYear, "workWeek":$workWeeks};        			
        			ANSI_UTILS.doServerCall("GET", "bcr/actualDL", $outbound, BUDGETCONTROL.populateActualDLPanel, BUDGETCONTROL.actualDLError);

        			BUDGETCONTROL.populateBudgetControlTotalsPanel($data);

					$.each($data.data.workCalendar, function($index, $value) {
						var $panelId = "#bcr_panels .ticket-display-week" + $value.weekOfMonth;
						var $labelId = "h4 .week" + $value.weekOfMonth;
						console.log($labelId);
						$($labelId).html("Week " + $value.weekOfYear + " | " + $value.firstOfWeek + "-" + $value.lastOfWeek);
						$($panelId).show();
					});
        			
					$("#bcr_panels .thinking").hide();
					$("#bcr_panels .display").show();
        			$("#bcr_title_prompt").dialog("close");
        			$("#titleHeader").click();
        			
        		}
        	};
        	

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
	       				<bcr:employees />
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem">    			
        		<h4 class="accHdr">All Tickets</h4>
        		<div id="bcr_tickets">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week1">    			
        		<h4 class="accHdr"><span class="week1">Week 1</span></h4>
        		<div id="bcr_tickets_week1">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable1" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week2">    			
        		<h4 class="accHdr"><span class="week2">Week 2</span></h4>
        		<div id="bcr_tickets_week2">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable2" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week3">    			
        		<h4 class="accHdr"><span class="week3">Week 3</span></h4>
        		<div id="bcr_tickets_week3">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable3" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week4">    			
        		<h4 class="accHdr"><span class="week4">Week 4</span></h4>
        		<div id="bcr_tickets_week4">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable4" />	       				
	       			</div>
        		</div>
       		</li>
       		<li class="accordionItem ticket-week-display ticket-display-week5">    			
        		<h4 class="accHdr"><span class="week5">Week 5</span></h4>
        		<div id="bcr_tickets_week1">
        			<div class="thinking"><webthing:thinking style="width:100%" /></div>
	        		<div class="display">
	        			<bcr:ticketTable id="ticketTable5"  />	       				
	       			</div>
        		</div>
       		</li>
   		</ul>
   		
   	    <webthing:lookupFilter filterContainer="filter-container" />
    	
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
	    			<td><select id="workDaySelector" name="workDate"></select></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td>&nbsp;</td>
	    			<td><span class="workDateDisplay"></span></td>
	    		</tr>
	    	</table>
	    </div>
	    
	    
	    
	    <div id="bcr_edit_modal">
	    	<table>
	    		<tr>
	    			<td><span class="form-label">Account:</span></td>
	    			<td></td>
	    			<td><span class="err divisionIdErr"></span></td>
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Ticket Number:</span></td>
	    			<td><span class="ticketId"></span></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Claim Week:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">D/L:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Total Volume:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Volume Claimed:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Volume Remaining:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Notes:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Billed Amount:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Ticket Status:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Service:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Equipment:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		<tr>
	    			<td><span class="form-label">Employee:</span></td>
	    			<td></td>
	    			<td><span class="err workDateErr"></span></td>	    			
	    		</tr>
	    		
	    	</table>
	    </div>
    </tiles:put>
		
</tiles:insert>

