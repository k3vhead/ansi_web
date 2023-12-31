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
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
		Timesheet Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    	<script type="text/javascript" src="js/payroll.js"></script> 
    
        <style type="text/css">
        	#confirmation-modal {
        		display:none;
        		text-align:center;
        	}
        	#edit-modal {
        		display:none;
        	}
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#organization-display {
        		display:none;
        	}
        	#organization-display table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-display th {
        		text-align:left;
        	}
        	#organization-edit table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-edit th {
        		text-align:left;
        	}
        	.action-link {
        		text-decoration:none;
        		cursor:pointer;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}
			.grayback {
				background-color:#CFCFCF;
			}
			.new-timesheet-container {
				width:100%;
			}
			.timesheet-err {
				width:100%;
				height:14px;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;TIMESHEETLOOKUP = {
        		
        		
       			init : function() {       				
       				TIMESHEETLOOKUP.makeTimesheetLookup(); 
       				TIMESHEETLOOKUP.makeClickers();
       				
       				var $employeeJson = '{"employeeName":"Test Dude", "employeeCode":5012, "vendorEmployeeCode":"2468"}';
       				var $Y = JSON.parse($employeeJson);
       				console.log($Y);
           		},
           		
           		
           		deleteTimesheet : function() {
           			console.log("delete timesheet");
           			var $outbound = {};
           			$.each( $("#confirmation-modal input"), function($index, $value) {
           				$outbound[$value.name] = $($value).val();
           			});
           			console.log($outbound);
           			ANSI_UTILS.makeServerCall("DELETE", "payroll/timesheet", JSON.stringify($outbound), {200:TIMESHEETLOOKUP.saveTimesheetSuccess}, {});
           		},
           		
           		
           		initConfirmationModal : function() {
					console.log("initConfirmationModal");
           			
           			$( "#confirmation-modal" ).dialog({
        				title:'Confirm Delete',
        				autoOpen: false,
        				height: 200,
        				width: 350,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "confirm-cancel",
        						click: function($event) {
       								$( "#confirmation-modal" ).dialog("close");
        						}
        					},{
        						id:  "confirm-save",
        						click: function($event) {
       								TIMESHEETLOOKUP.deleteTimesheet();
        						}
        					}
        				]
        			});	
        			$("#confirm-cancel").button('option', 'label', 'No');  
        			$("#confirm-save").button('option', 'label', 'Yes');
           		},
           		
           		
           		
           		
           		
           		
           		
           		
           		makeClickers : function() {
           			$("input[name='new-timesheet']").click(function($event) {
           				console.log("new timesheet");
           				TIMESHEETLOOKUP.showNewTimeSheet();
           			});
           		},
           		
           		
           		
           		toggleColumns : function($columnDisplay, $columnList) {
           			console.log("toggleColumns: " + $columnDisplay + " " + $columnList);
           			var $myTable = $('#timesheetLookup').DataTable();
					$.each($columnList, function(index, columnNumber) {
						$myTable.columns(columnNumber).visible($columnDisplay);
					});
           		},
           		
           		resetColumns : function($columnList) {
           			console.log("resetColumns: " + $columnList);
           			var $myTable = $('#timesheetLookup').DataTable();
           			$myTable.columns().every( function(colIdx) {
           				var $column = this;
           				$column.visible(false);
           			});
           			$.each($columnList, function(index, columnNumber) {
						$myTable.columns(columnNumber).visible(true);
					});
           		},
           		
           		showAllColumns : function() {
           			console.log("showAllColumns: ");
           			var $myTable = $('#timesheetLookup').DataTable();
           			$myTable.columns().every( function(colIdx) {
           				var $column = this;
           				$column.visible(true);
           			});
           		},
           		
           		
           		showNewTimeSheet : function() {
           			console.log("showNewTimeSheet");
           			if ( ! $("#edit-modal").hasClass("ui-dialog-content")) {
           				PAYROLL_UTILS.initEditModal("#edit-modal",TIMESHEETLOOKUP.saveTimesheet);
           			}
       				$("#edit-modal .err").html("").show();  
       				$("#edit-modal .totalpay-display").html( "");
           			$.each( $("#edit-modal input"), function($index, $value) {
           				$($value).val("");
           			});
           			$.each( $("#edit-modal select"), function($index, $value) {
           				$($value).val("");
           			});
       				$("#edit-modal input[name='action']").val("ADD");
       				// In order to select a unique employee, we need to know which company we're talking about.
       				// So, for the division to be selected before allowing an employee name to be entered.
       				$("#edit-modal .update-field").prop("disabled", true); // this turns off all the employee fields, including the division 
       				$("#edit-modal .payfield").prop("disabled", true);  // this turns off all the numbers 
       				$("#edit-modal select[name='divisionId']").prop("disabled",false); // this turns the division selector back on
       				$("#edit-modal select[name='divisionId']").off("change");
       				$("#edit-modal select[name='divisionId']").change(function() {
						var $div = $("#edit-modal select[name='divisionId']").val();
						if ( $div == null || $div == "" ) {
		       				$("#edit-modal .update-field").prop("disabled", true); // this turns off on the employee fields, including the division 
		       				$("#edit-modal .payfield").prop("disabled", true);  // this turns off all the numbers 
		       				$("#edit-modal select[name='divisionId']").prop("disabled",false); // this turns the division selector back on
						} else {
		       				$("#edit-modal .update-field").prop("disabled", false); // this turns on on the employee fields, including the division 
		       				$("#edit-modal .payfield").prop("disabled", false);  // this turns on all the numbers 
		       				$("#edit-modal input[name='employeeCode']").prop("disabled", true);
		       				$("#edit-modal input[name='vendorEmployeeCode']").prop("disabled", true);
						}
	       				$("#edit-modal input[name='employeeCode']").val("");
	       				$("#edit-modal input[name='vendorEmployeeCode']").val("");
	       				$("#edit-modal input[name='employeeName']").val("");
       				});
           			$("#edit-modal").dialog("open");
           		},
           		
           		makeTimesheetLookup : function() {
           			var $delete = '<webthing:delete>Delete</webthing:delete>';
           			var $edit = '<webthing:edit>Edit</webthing:edit>';
           			
           			var $colDivision = 0;
		        	var $colWeekEnding = 1;
		        	var $colState = 2;
		        	var $colCity = 3;
		        	var $colEmployeeCode = 4;
		        	var $colVendorEmpCode = 5;
		        	var $colEmployeeName = 6;
		            var $colRegularHours = 7;
		            var $colRegularPay = 8;
		            var $colExpenses = 9;
		            var $colOTHours = 10;
		            var $colOTPay = 11;
		            var $colVacationHours = 12;
		            var $colVacationPay = 13;
		            var $colHolidayHours = 14;
		            var $colHolidayPay = 15;
		            var $colGrossPay = 16;
		            var $colExpensesSubmitted = 17;
		            var $colExpensesAllowed = 18;
		            var $colVolume = 19;
		            var $colDirectLabor = 20;
		            var $colProductivity = 21;
		            var $colAction = 22;
		            
		            var $defaultDisplay = [
				            	$colDivision,
				            	$colWeekEnding,
				            	$colState,
				            	$colCity,
				            	$colEmployeeCode,
				            	$colVendorEmpCode,
				            	$colEmployeeName,
				            	$colRegularHours,
				            	$colRegularPay,
				            	$colExpenses,
				            	$colOTHours,
				            	$colVacationHours,
				            	$colGrossPay,
				            	$colVolume,
				            	$colDirectLabor,
				            	$colAction];
		            var $expensesDisplay = false;
		            var $expensesCols = [$colExpenses,$colExpensesSubmitted,$colExpensesAllowed];
		            var $otDisplay = false;
		            var $otCols = [$colOTHours,$colOTPay];
		            var $vacationDisplay = false;
		            var $vacationCols = [$colVacationHours,$colVacationPay,$colHolidayHours,$colHolidayPay];
		            
		            	
		            	
           			$("#timesheetLookup").DataTable( {
               			"aaSorting":		[[0,'asc'],[1,'asc'],[2,'asc']],
               			"processing": 		true,
               	        "serverSide": 		true,
               	        "autoWidth": 		false,
               	        "deferRender": 		true,
               	        "scrollCollapse": 	true,
               	        "scrollX": 			true,
               	        "pageLength":		50,
               	        rowId: 				function(row) {return "row" + row.division_id+"_"+row.week_ending+"_"+row.state+"_"+row.employee_code+"_"+row.city},
               	        destroy : 			true,		// this lets us reinitialize the table
               	        dom: 				'Bfrtip',
               	        "searching": 		true,
               	        "searchDelay":		800,
               	        lengthMenu: [
               	        	[ 10, 50, 100, 500, 1000 ],
               	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
               	        ],
               	        buttons: [
               	        		'pageLength',
               	        		'copy', 
               	        		'csv', 
               	        		'excel', 
               	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
               	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#timesheetLookup').draw();}},
               	        		{
    	        	        		text:'Expenses',
    	        	        		action: function(e, dt, node, config) {
    	        	        			$expensesDisplay = ! $expensesDisplay;
    	        	        			TIMESHEETLOOKUP.toggleColumns($expensesDisplay, $expensesCols);	        	        			
    	        	        		}
    	        	        	},{
    	        	        		text:'OT',
    	        	        		action: function(e, dt, node, config) {
    	        	        			$otDisplay = ! $otDisplay;
    	        	        			TIMESHEETLOOKUP.toggleColumns($otDisplay, $otCols);	        	        			
    	        	        		}
    	        	        	},{
    	        	        		text:'Vacation/Holidays',
    	        	        		action: function(e, dt, node, config) {
    	        	        			$vacationDisplay = ! $vacationDisplay;
    	        	        			TIMESHEETLOOKUP.toggleColumns($vacationDisplay, $vacationCols);	        	        			
    	        	        		}
    	        	        	},{
    	        	        		text:'Show All',
    	        	        		action: function(e, dt, node, config) {
    	        	        			$expenseDisplay = true;
    	        	        			$vacationDisplay = true;
    	        	        			$otDisplay = true;
    	        	        			TIMESHEETLOOKUP.showAllColumns();	        	        			
    	        	        		}
        	                    },{
    	        	        		text:'Reset',
    	        	        		action: function(e, dt, node, config) {
    	        	        			$expenseDisplay = false;
    	        	        			$vacationDisplay = false;
    	        	        			$otDisplay = false;
    	        	        			TIMESHEETLOOKUP.resetColumns($defaultDisplay);	        	        			
    	        	        		}
    	        	        	}<ansi:hasPermission permissionRequired="PAYROLL_WRITE">,{
    	        	        		text:'New',
    	        	        		action: function(e, dt, node, config) {
    	        	        			TIMESHEETLOOKUP.showNewTimeSheet()
    	        	        		}
    	        	        	}
    	        	        	</ansi:hasPermission>
               	        	],
               	        "columnDefs": [
                	            { "orderable": true, "targets": -1 },
                	            //{ className: "dt-head-center", "targets":[
                	            //	$colDivision,		$colWeekEnding,		$colState,
           			        //	$colCity,			$colEmployeeCode,	$colEmployeeName,
           			        //    $colRegularHours,	$colRegularPay,		$colExpenses,
           			        //    $colOTHours,	 	$colOTPay,			$colVacationHours,
           			        //    $colVacationPay,	$colHolidayHours,	$colHolidayPay,
           			        //    $colGrossPay,		$colExpensesSubmitted,	$colExpensesAllowed,
           			        //    $colVolume,			$colDirectLabor,	$colProductivity,
           			        //    $colAction
                	            //]},
               	            { className: "dt-left", "targets": [
               	            	$colDivision,		$colWeekEnding,			$colCity,		
               	            	$colEmployeeName
               	            ]},
               	            { className: "dt-center", "targets": [
               	            	$colState,			$colAction,				$colEmployeeCode,
               	            	$colVendorEmpCode
               	            ]},
               	            { className: "dt-right", "targets": [
               	            	$colRegularHours,	$colRegularPay,			$colExpenses,
           			            $colOTHours,		$colOTPay,				$colVacationHours,
           			            $colVacationPay,	$colHolidayHours,		$colHolidayPay,
           			            $colGrossPay,		$colExpensesSubmitted,	$colExpensesAllowed,
           			            $colVolume,			$colDirectLabor,		$colProductivity
							]}
               	         ],
               	        "paging": true,
       			        "ajax": {
       			        	"url": "payroll/timesheetLookup",
       			        	"type": "GET",
       			        	"data": {},
       			        	},
       			        columns: [
       			        	{ title: "Division", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
       			        	{ title: "Week Ending", width:"10%", searchable:true, searchFormat:"yyyy-mm-dd", "defaultContent": "<i>N/A</i>", data:'week_ending_display' },
       			        	{ title: "State", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'state' },
       			        	{ title: "City", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'city' },
       			        	{ title: "Employee Code", width:"7%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' },
       			        	{ title: "Vendor Emp. Code", width:"7%", searchable:true, "defaultContent": "<i>N/A</i>", data:'vendor_employee_code' },
       			        	{ title: "Employee Name", width:"10%", searchable:true, searchFormat:"Last, First", "defaultContent": "<i>N/A</i>", data:'employee_name' },
       			            { title: "Regular Hours",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.regular_hours == null ? "" : row.regular_hours.toFixed(2); } },
       			            { title: "Regular Pay", width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.regular_pay == null ? "" : row.regular_pay.toFixed(2); } },
       			            { title: "Expenses",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.expenses == null ? "" : row.expenses.toFixed(2); } },
       			            { title: "OT Hours",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.ot_hours == null ? "" : row.ot_hours.toFixed(2); } },
       			            { title: "OT Pay",  visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.ot_pay == null ? "" : row.ot_pay.toFixed(2); } },
       			            { title: "Vacation Hours",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.vacation_hours == null ? "" : row.vacation_hours.toFixed(2); } },
       			            { title: "Vacation Pay", visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.vacation_pay == null ? "" : row.vacation_pay.toFixed(2); } },
       			            { title: "Holiday Hours",  visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.holiday_hours == null ? "" : row.holiday_hours.toFixed(2); } },
       			            { title: "Holiday Pay", visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.holiday_pay == null ? "" : row.holiday_pay.toFixed(2); } },
       			            { title: "Gross Pay",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.gross_pay == null ? "" : row.gross_pay.toFixed(2); } },
       			            { title: "Expenses Submitted",  visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.expenses_submitted == null ? "" : row.expenses_submitted.toFixed(2); } },
       			            { title: "Expenses Allowed",  visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.expenses_allowed == null ? "" : row.expenses_allowed.toFixed(2); } },
       			            { title: "Volume",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.volume == null ? "" : row.volume.toFixed(2); } },
       			            { title: "Direct Labor",  width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.direct_labor == null ? "" : row.direct_labor.toFixed(2); } },
       			            { title: "Productivity",  visible:false, width:"10%", searchable:false, "defaultContent": "", data: function ( row, type, set ) { return row.productivity == null ? "" : row.productivity.toFixed(2); } },
       			            { title: "Action", searchable:false, "orderable": false, defaultContent:"", data: function(row, type, set) {
       			            	var $city = row.city;
       			            	if ( $city == null ) {
       			            		$city = '';
       			            	}
       			            	var $parms = 'data-div="'+row.division_id+'" data-date="'+row.week_ending+'" data-state="'+row.state+'" data-employee="'+row.employee_code+'" data-city="'+$city+'"';
       			            	var $deleteLink = '<span class="action-link delete-action" ' + $parms + '>' + $delete + '</span>';
       			            	var $editLink = '<span class="action-link edit-action"  ' + $parms + '>' + $edit + '</span>';
       			            	return '<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $editLink + $deleteLink + '</ansi:hasPermission>';
       			            }},
       			            ],
       			            "initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", TIMESHEETLOOKUP.makeTimesheetLookup);
       			            	$("th").removeClass("dt-right");
       			            	$("th").removeClass("dt-left");
       			            	$("th").addClass("dt-center");        			            	
       			            },
       			            "drawCallback": function( settings ) {
       			            	$(".delete-action").off("click");
       			            	$(".edit-action").off("click");
       			            	$(".delete-action").click(function($event) {
       			            		var $divisionId = $(this).attr("data-div");
       			            		var $weekEnding = $(this).attr("data-date");
       			            		var $state = $(this).attr("data-state");
       			            		var $employeeCode = $(this).attr("data-employee");
       			            		var $city = $(this).attr("data-city");

       			            		if ( ! $("#confirmation-modal").hasClass("ui-dialog-content")) {
       			           				TIMESHEETLOOKUP.initConfirmationModal();
       			           			}
       			            		
									$("#confirmation-modal input[name='divisionId']").val($divisionId);
									$("#confirmation-modal input[name='weekEnding']").val($weekEnding);
									$("#confirmation-modal input[name='state']").val($state);
									$("#confirmation-modal input[name='employeeCode']").val($employeeCode);
									$("#confirmation-modal input[name='city']").val($city);									
       			            		
       			            		$("#confirmation-modal").dialog("open");
       			            	});
       			            	$(".edit-action").click(function($event) {
       			            		var $divisionId = $(this).attr("data-div");
       			            		var $weekEnding = $(this).attr("data-date");
       			            		var $state = $(this).attr("data-state");
       			            		var $employeeCode = $(this).attr("data-employee");
       			            		var $city = $(this).attr("data-city");
       			            		var $outbound = {
    										"divisionId":$divisionId,
            			            		"weekEnding":$weekEnding,
            			            		"state":$state,
            			            		"employeeCode":$employeeCode,
            			            		"city":$city,
    									};
       			            		
       			            		ANSI_UTILS.makeServerCall("GET", "payroll/timesheet", $outbound, {200:TIMESHEETLOOKUP.showEditModal}, {});
       			            	});
       			            }
       			    } );
           		},
            		
            		
           		
           		
           		saveTimesheet : function() {
           			console.log("Save timesheet");
           			$("#edit-modal .err").html("").show();
           			var $outbound = {};
           			$.each( $("#edit-modal input"), function($index, $value) {
           				$outbound[$value.name] = $($value).val();
           			});
           			$.each( $("#edit-modal select"), function($index, $value) {
           				$outbound[$value.name] = $($value).val();
           			});
           			var $callbacks = {
        				200:TIMESHEETLOOKUP.saveTimesheetSuccess,
        				403:TIMESHEETLOOKUP.saveTimesheetError,
           				404:TIMESHEETLOOKUP.saveTimesheetError,
           				405:TIMESHEETLOOKUP.saveTimesheetError,
           				500:TIMESHEETLOOKUP.saveTimesheetError,
        			};
           			ANSI_UTILS.makeServerCall("POST", "payroll/timesheet", JSON.stringify($outbound), $callbacks, {});
           		},
           		
           		
           		saveTimesheetError : function($data, $passthru) {
           			console.log("saveTimesheetError ")
           			if ( $data.status == 403 ) {
           				$("#edit-modal .timesheet-err").html("Session expired. Log in again").show();
           			} else {
           				$("#edit-modal .timesheet-err").html("System error " + $data.status + ". Contact Support").show();
           			}
           			
           		},
           		
           		
           		
           		saveTimesheetSuccess : function($data, $passthru) {
           			console.log("saveTimesheetSuccess");
           			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
           				$.each($data.data.webMessages, function($index, $value) {
           					var $selector = "#edit-modal ." + $index + "Err";
           					$($selector).html($value[0]);
           				});
           			} else if ( $data.responseHeader.responseCode == 'SUCCESS' || $data.responseHeader.responseCode == 'EDIT_WARNING') {
               			$("#timesheetLookup").DataTable().ajax.reload();
               			// close both modals because we don't know which one called this method, but only after they've been init'd
               			if ( $("#edit-modal").hasClass("ui-dialog-content")) {
               				$("#edit-modal").dialog("close");
               			}
               			if ( $("#confirmation-modal").hasClass("ui-dialog-content")) {
               				$("#confirmation-modal").dialog("close");
               			}
               			if ($data.responseHeader.responseCode == 'SUCCESS') {
               				$("#globalMsg").html("Success").show().fadeOut(3000);
               			} else {
               				ANSI_UTILS.showWarnings("timesheet_warnings", $data.data.webMessages);
               			}
               			
               			
           			} else {
           				$("#edit-modal .timesheet-err").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
           			}
           		},
           		
           		
           		
           		showEditModal : function($data, $passthru) {
           			console.log("showEditModal");
           			if ( ! $("#edit-modal").hasClass("ui-dialog-content")) {
           				PAYROLL_UTILS.initEditModal("#edit-modal",TIMESHEETLOOKUP.saveTimesheet);
           			}
           			
           			$("#edit-modal .err").html("");
           			$("#edit-modal .totalpay-display").html( "");
           			$("#edit-modal input[name='action']").val("UPDATE");
           		 	$("#edit-modal select[name='divisionId']").val($data.data.divisionId);
           		 	$("#edit-modal input[name='weekEnding']").val($data.data.weekEnding);
           		 	$("#edit-modal input[name='divisionId']").val($data.data.divisionId);
           			$("#edit-modal select[name='state']").val($data.data.state);
           			$("#edit-modal input[name='city']").val($data.data.city);
           			$("#edit-modal input[name='employeeCode']").val($data.data.employeeCode);
           			$("#edit-modal input[name='vendorEmployeeCode']").val($data.data.vendorEmployeeCode);
           			$("#edit-modal input[name='employeeName']").val($data.data.employeeName);
           			$.each(PAYROLL_UTILS.timesheetFields, function($index, $value) {
           				if ( $value.hoursAndPay ) {
           					$fieldName = $value.name + "Hours";
	           				var $selector = "#edit-modal input[name='"+$fieldName+"']";
	           				var $displayValue = $data.data[$fieldName] == null ? "" : $data.data[$fieldName].toFixed(2);
	           				$($selector).val($displayValue);
           					$fieldName = $value.name + "Pay";
	           				var $selector = "#edit-modal input[name='"+$fieldName+"']";
	           				var $displayValue = $data.data[$fieldName] == null ? "" : $data.data[$fieldName].toFixed(2);
	           				$($selector).val($displayValue);
           					
           				} else {           				
	           				var $selector = "#edit-modal input[name='"+$value.name+"']";
	           				var $displayValue = $data.data[$value.name] == null ? "" : $data.data[$value.name].toFixed(2);
	           				$($selector).val($displayValue);
           				}
           			});
           			$("#edit-modal .update-field").prop("disabled", true);
           			PAYROLL_UTILS.calculateTotalPay("#edit-modal");
           			$("#edit-modal").dialog("open");

           		},
            	
            	
            	
        	};
        	
        	TIMESHEETLOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Timesheet Lookup</h1> 

    	<webthing:lookupFilter filterContainer="filter-container" />
		<table id="timesheetLookup"></table>
		<div class="new-timesheet-container">
			<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><input type="button" value="New Timesheet" class="prettyWideButton" name="new-timesheet" /></ansi:hasPermission>
		</div>
		
		
		<jsp:include page="timesheetEmployee.jsp">
			<jsp:param name="id" value="edit-modal" />
		</jsp:include>
		
		
		
		<div id="confirmation-modal">
			<h2>Are You Sure?</h2>
			<input type="hidden" name="divisionId" />
			<input type="hidden" name="weekEnding" />
			<input type="hidden" name="state" />
			<input type="hidden" name="employeeCode" />
			<input type="hidden" name="city" />
		</div>
    </tiles:put>
		
</tiles:insert>

