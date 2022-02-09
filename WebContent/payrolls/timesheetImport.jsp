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
		Payroll Timesheet Import
    </tiles:put>
        
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    
        <style type="text/css">
        	#display-div {
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
			.details-control {
				cursor:pointer;
			}
			.form-label {
				font-weight:bold;
			}
			.org-status-change {
				display:none;
				cursor:pointer;
			}
			.view-link {
				color:#404040;
			}		
        </style>
        <script type="text/javascript">    
	       	$(document).ready(function(){
	           	;TIMESHEET_IMPORT = {
	           		statusIsGood : '<webthing:checkmark>No Errors</webthing:checkmark>',
	           		statusIsBad : '<webthing:ban>Error</webthing:ban>',
	           		saveButton : '<webthing:save>Save</webthing:save>',
	           		edit : '<webthing:edit>Edit</webthing:edit>',
	           		view : '<webthing:view styleClass="details-control">Details</webthing:view>',
	           		employeeMap : {},
	           			           			
	           		init : function() {
	           			TIMESHEET_IMPORT.makeClickers();            			
	           		},
	           	           	           		
	           		formatDetail : function(row) {
	           			console.log("formatDetail");
	           			var $table = $("<table>");
	           			$table.attr("style","width:375px; margin-left:45px;");
	           			
	           			$expenseRow = $("<tr>");
		           			<!-- $expenseRow.append( $('<td>') );-->
		           			$expenseRow.append( $("<td>") );
		           			$expenseRow.append( $("<td>").append("Expenses (Amt) :"));
		           			$expenseRow.append( $("<td>").append(" ") );
		           			$expenseRow.append( $("<td aligh='right'>").append(row.expenses));
		           			$expenseRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsGood));           			
		           			$table.append($expenseRow);
	           			
	           			$otRow = $("<tr>");
		           			$expenseRow.append( $('<td>') );
		           			$otRow.append( $("<td>") );
		           			$otRow.append( $("<td>").append("Overtime(Hrs|Pay) :"));
		           			$otRow.append( $("<td>").append(row.otHours));
		           			$otRow.append( $("<td aligh='right'>").append(row.otPay));
		           			$otRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsGood));           			
		           			$table.append($otRow);
	           			
	           			$vacactionRow = $("<tr>");
		           			$expenseRow.append( $('<td>').append("&nbsp;") );
		           			$vacactionRow.append( $("<td>") );
		           			$vacactionRow.append( $("<td>").append("Vacation (Hrs|Pay)) :"));
		           			$vacactionRow.append( $("<td>").append(row.vacationHours));
		           			$vacactionRow.append( $("<td aligh='right'>").append(row.vacationPay));
		           			$vacactionRow.append( $("<td>").append(TIMESHEET_IMPORT.statusIsBad));           			
		           			$table.append($vacactionRow);
							
	           			return $table;
	
	           			
	   					//{ title : "Expenses", "defaultContent": "", data:'expenses' },
	   					//{ title : "OT Hours", "defaultContent": "", data:'otHours' },
	   					//{ title : "OT Pay", "defaultContent": "", data:'otPay' },
	   					//{ title : "Vacation Hours", "defaultContent": "", data:'vacationHours' },
	   					//{ title : "Vacation Pay", "defaultContent": "", data:'vacationPay' },
	   					//{ title : "Holiday Hours", "defaultContent": "", data:'holidayHours' },
	   					//{ title : "Holiday Pay", "defaultContent": "", data:'holidayPay' },
	   					//{ title : "Gross Pay", "defaultContent": "", data:'grossPay' },
	   					//{ title : "Expenses Submitted", "defaultContent": "", data:'expensesSubmitted' },
	   					//{ title : "Expenses Allowed", "defaultContent": "", data:'expensesAllowed' },
	   					//{ title : "Volume", "defaultContent": "", data:'volume' },
	   					//{ title : "Direct Labor", "defaultContent": "", data:'directLabor' },
	   					//{ title : "Productivity", "defaultContent": "", data:'productivity' },
	
	           		},
	           		       		
	           		makeClickers : function() {
	           			$("#open-button").click(function($event) {
	           				$("#prompt-div .err").html("");
	           				var file = document.getElementById('timesheet-file').files[0];
	           				var reader = new FileReader();
	           				if ( file == null ) { 
								$("#prompt-div .timesheetFileErr").html("Required Value").show();
								//if ( $("#prompt-div select[name='divisionId']").val().length == 0) { $("#prompt-div .divisionIdErr").html("Required Value").show(); }
								//if ( $("#prompt-div input[name='payrollDate']").val().length == 0 ) {$("#prompt-div .payrollDateErr").html("Required Value").show(); }
								//if ( $("#prompt-div select[name='state']").val().length == 0 ) {$("#prompt-div .stateErr").html("Required Value").show(); }
								//if ( $("#prompt-div input[name='city']").val().length == 0 ) {$("#prompt-div .cityErr").html("Required Value").show(); }
	           				} else {
		           				reader.readAsText(file, 'UTF-8');	           				
		           				reader.onload = TIMESHEET_IMPORT.saveFile;
		           				// reader.onprogress ...  (progress bar)
	           				}
	           			});
	           			
	           			$("#display-div input[name='cancelButton']").click(function($event) {
	           				$("#display-div").hide();
	           				$("#prompt-div").show();
	           				$("#prompt-div .timesheet-file").val('');
	           			});
	           		},
	           		           		           		
	           		processUploadFailure : function($data) {
	           			console.log("processUploadFailure");
	           			$("#prompt-div .err").html("");
	           			$.each($data.data.webMessages, function($index, $value) {
	           				var $selector = "#prompt-div ." + $index + "Err";
	           				$($selector).html($value[0]).show();
	           			});
	           		},
	           		           		
	           		processUploadSuccess : function($data) {
	           			console.log("processUploadSuccess");
	           			$("#prompt-div").hide();
	           			$("#display-div").show();
	           			console.log("showing display div.. ");
	           			console.log($data);
	           			console.log($data.data.division);
	           			$("#display-div .divisionId").html($data.data.division);
	           			$("#display-div .operationsManagerName").html($data.data.operationsManagerName);           			
	           			$("#display-div .payrollDate").html($data.data.weekEnding);
	           			$("#display-div .state").html($data.data.state);
	           			$("#display-div .city").html($data.data.city);
	           			$("#display-div .timesheetFile").html($data.data.fileName);
	
	           			// create and populate dicttionary object for use in model
	           			var dictionary = $data.data.employeeRecordList;
	   
	           			// populate the visible table on-screen
	           			var $table = $("#timesheet").DataTable({
	           				aaSorting : [[0,'asc']],
	            			processing : true,
	           				data : $data.data.employeeRecordList,
	           				searching : true,
	            	        searchDelay : 800,
	            	        paging: false,
	            	        destroy: true,
	           				columnDefs : [
	             	            { orderable : true, "targets": -1 },
	             	            //{ className : "dt-head-center", "targets":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]},
	            	            { className : "dt-left", "targets": [1] },
	            	            { className : "dt-center", "targets": [2,3,5,7] },
	            	            //{ className : "dt-right", "targets": [2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]}
	            	            { className : "dt-right", "targets": [0,4,6,8]}
	            	         ],
	           				columns : [
	           					{ title : "Row", "defaultContent": "", data:'row' },
	           					{ title : "Employee Name", "defaultContent": "", data:'employeeName' },
	           					{ title : "Status", "defaultContent":"", 
	           						data : function(row, type, set) {
										var $tag = TIMESHEET_IMPORT.statusIsGood;          							
	           							if ( row.errorsFound == true ) { $tag = TIMESHEET_IMPORT.statusIsBad; }
	           							return $tag;
	           						}
	           					},
	           					{ title : "Regular Hours", "defaultContent": "", data:'regularHours' },
	           					{ title : "Regular Pay", "defaultContent": "", data:'regularPay' },
	           					{ title : "OT Hours", "defaultContent": "", data:'otHours' },
	           					{ title : "OT Pay", "defaultContent": "", data:'otPay' },
	           					{ title : "Vacation Hours", "defaultContent": "", data:'vacationHours' },
	           					{ title : "Vacation Pay", "defaultContent": "", data:'vacationPay' },
	
	
	           					{ title : "Action", 
	    			            	data: function ( row, type, set ) { 
	    			            		var $editLink = '<span class="action-link edit-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.edit + '</span>';
	    			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.row+'">' + TIMESHEET_IMPORT.view + '</span>';
	    			            		//var $deleteLink = '<span class="action-link delete-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:delete>Delete</webthing:delete></span>';
	    			            		return  $editLink + $viewLink + TIMESHEET_IMPORT.saveButton;
	    			            	} },
	           				],
	           				"initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	// You're going to need this to add the field-level filters:
       			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", TIMESHEETLOOKUP.makeTimesheetLookup);

       			            	$.each($data.data.employeeRecordList, function($index, $value) {
       			            		console.log($value.row + ":" + $value.employeeName);
       			            		TIMESHEET_IMPORT.employeeMap[$value.row] = $value;
       			            	});
       			            },
	           				drawCallback : function( settings ) {
	           					$(".edit-link").off("click");
	           					$(".edit-link").on("click", function() {
	           						var $rowNumber = $(this).attr('data-id');
	           						TIMESHEET_IMPORT.showEmployeeModal($rowNumber, "edit");
	           					});
	           					
	           					$(".view-link").off("click");
	           					$(".view-link").on("click", function() {
	           						var $rowNumber = $(this).attr('data-id');
	           						TIMESHEET_IMPORT.showEmployeeModal($rowNumber, "view");
	           					});
	           				}
	           			});
	           		},
	        
	           		saveFile : function($event) {
	           			var results = $event.target.result;
	           			var fileName = document.getElementById('timesheet-file').files[0].name;
	           			var formData = new FormData();
	           			var file = document.getElementById('timesheet-file').files[0];
	           			formData.append('timesheetFile',file, fileName);
	           			//formData.append('divisionId', $("#prompt-div select[name='divisionId']").val());
	           			//formData.append('payrollDate', $("#prompt-div input[name='payrollDate']").val());
	           			//formData.append('state', $("#prompt-div select[name='state']").val());
	           			//formData.append('city', $("#prompt-div input[name='city']").val());
	           			
	           			var xhr = new XMLHttpRequest();
	           			xhr.open('POST',"payroll/timesheetImport", true);
	           			
	           			xhr.onload = function() {
	           				if ( xhr.status == 200 ) {
	           					var $data = JSON.parse(this.response);
	           					if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
	           						TIMESHEET_IMPORT.processUploadFailure($data);
	           					} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
	           						TIMESHEET_IMPORT.processUploadSuccess($data);
	           						console.log($data.data.Division);           						
	           					} else {
	           						$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support");
	           					}
	           				} else {
	           					$("#globalMsg").html("Response Code " + xhr.status + ". Contact Support");
	           				}
	           			};
	           			
	           			xhr.send(formData);
	           		},
	           		
	           		
	           		
	           		
	           		showEmployeeModal : function($rowNumber, $action) {
	           			console.log("showEmployeeModal: " + $rowNumber + " " + $action);
	           			$edit = $action == 'edit';
	           			var $message = "";
	           			$.each( TIMESHEET_IMPORT.employeeMap[$rowNumber], function($index, $value) {
	           				$message = $message + "\n" + $index + "|" + $value;
	           			});
	           			alert($message);
	           		}
	           		
	           	};
	           	
	           	TIMESHEET_IMPORT.init();
	            	
	        });        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Timesheet Import</h1> 
    	<div id="prompt-div">
    		<table>
    			<tr>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td>
    					<input type="file" id="timesheet-file" name="files[]" />
    				</td>
    				<td><span class="timesheetFileErr err"></span></td>
    			</tr>
    			<tr>
    				<td colspan="2" style="text-align:center;"><input type="button" value="Open" id="open-button" /></td>
    			</tr>    			
    		</table>
    	</div>

		<div id="display-div">
			<table style="width:100%;">
    			<tr>
    				<td><span class="form-label">Division:</span></td>
    				<td><span class="form-label">Operations Manager:</span></td>
    				<td><span class="form-label">Week Ending:</span></td>
    				<td><span class="form-label">State:</span></td>
    				<td><span class="form-label">City/Jurisdiction:</span></td>
    				<td><span class="form-label">Payroll File:</span></td>
    				<td rowspan="2">
    					<input type="button" value="Cancel" name="cancelButton" class="action-button" />
    					<input type="button" value="Save" id="open-button" />
    				</td>
    			</tr>
    			<tr>
    				<td><span class="divisionId"></span></td>
    				<td><span class="operationsManagerName"></span></td>
    				<td><span class="payrollDate"></span></td>
    				<td><span class="state"></span></td>
    				<td><span class="city"></span></td>
    				<td><span class="timesheetFile"></span></td>
    			</tr>
    			<tr>
    			</tr>    			
    		</table>
			<table id="timesheet">
			</table>
		</div>
	    <div id="timesheet-employee-modal">
	    	<payroll:timesheetEmployeeModal tableName="timesheet-employee-modal"/>
	    </div>
    </tiles:put>
		
</tiles:insert>

