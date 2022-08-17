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
		Employee Lookup
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
        	#alias-display {
        		display:none;
        	}
        	#alias-display .alias-message {
        		width:100%;
        		height:13px;
        	}
        	#alias-display .action-link {
        		cursor:pointer;
        	}
        	#confirm-modal {
        		display:none;
        	}
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
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
			.modal-window {
				display:none;
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
        	;EMPLOYEELOOKUP = {
        		init : function() {
        			EMPLOYEELOOKUP.makeModals();
        			EMPLOYEELOOKUP.makeEmployeeTable(); 
        			EMPLOYEELOOKUP.makeClickers();
        		},
        		
        		
        		
        		displayEmployeeModal : function($data, $passsthru) {
        			console.log("displayEmployeeModal");
        			$("#employee-edit input").val("");
        			$("#employee-edit select").val("");
        			$("#employee-edit .err").html("");
        			$("#employee-edit .attn").hide();
        			$("#employee-edit input[name='selectedEmployeeCode']").val($data.data.employee.employeeCode);
        			$("#employee-edit input[name='employeeCode']").val($data.data.employee.employeeCode);
        			$("#employee-edit select[name='companyCode']").val($data.data.employee.companyCode);
        			$("#employee-edit select[name='divisionId']").val($data.data.employee.divisionId);
        			$("#employee-edit input[name='firstName']").val($data.data.employee.firstName);
        			$("#employee-edit input[name='lastName']").val($data.data.employee.lastName);
        			$("#employee-edit input[name='middleInitial']").val($data.data.employee.middleInitial);
        			$("#employee-edit input[name='departmentDescription']").val($data.data.employee.departmentDescription);
        			$("#employee-edit select[name='status']").val($data.data.employee.status);
        			$("#employee-edit input[name='terminationDate']").val($data.data.employee.terminationDate);
        			if ( $data.data.employee.unionMember == 1 ) {
        				$("#employee-edit input[name='unionMember']").prop("checked", true);
        				$("#employee-edit .unionInput").prop('disabled',false);
        			} else {
        				$("#employee-edit input[name='unionMember']").prop("checked", false);
        				$("#employee-edit .unionInput").prop('disabled',true);
        			}
        			$("#employee-edit input[name='unionCode']").val($data.data.employee.unionCode);
        			if ( $data.data.employee.unionRate == null ) {
        				$("#employee-edit input[name='unionRate']").val("");
        			} else {
        				$("#employee-edit input[name='unionRate']").val($data.data.employee.unionRate.toFixed(2));	
        			}
        			$("#employee-edit input[name='processDate']").val($data.data.employee.processDate);
        			$("#employee-edit input[name='notes']").val($data.data.employee.notes);
        			$("#employee-edit").dialog("open");
        		},
        		
        		
        		
        		doConfirm : function() {
        			var $function = $("#confirm-save").attr("data-function");
        			if ( $function == "deleteAlias" ) {
        				var $employeeCode = $("#confirm-save").attr("data-id");
        				var $aliasName = $("#confirm-save").attr("data-name");
        				var $url = "payroll/alias/" + $employeeCode + "/" + $aliasName;
        				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:EMPLOYEELOOKUP.doConfirmAliasSuccess}, {});
        			} else if ( $function == "deleteEmployee") {
        				var $employeeCode = $("#confirm-save").attr("data-id");
        				var $url = "payroll/employee/" + $employeeCode;
        				ANSI_UTILS.makeServerCall("DELETE", $url, {}, {200:EMPLOYEELOOKUP.doConfirmEmployeeSuccess}, {});
        			} else {
        				$("#confirm-modal").dialog("close");
        				console.log("Function: " + $function);
        				$("#globalMsg").html("Invalid System State. Reload and try again").show();
        			}
        		},
        		
        		
        		
        		doConfirmAliasSuccess : function($data, $passthru) {
        			$("#alias-lookup").DataTable().ajax.reload();
        			$("#confirm-modal").dialog("close");
        			$("#alias-display .alias-message").html("Success").show().fadeOut(3000);
        		},
        		
        		
        		
        		doConfirmEmployeeSuccess : function($data, $passthru) {
        			$("#employeeLookup").DataTable().ajax.reload();
        			$("#confirm-modal").dialog("close");
        			$("#globalMsg").html("Success").show().fadeOut(3000);
        		},
        		
        		
        		
        		doNewAliasSuccess : function($data, $passthru) {
        			if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
	        			$("#alias-lookup").DataTable().ajax.reload();
	        			$("#alias-display input[name='employeeName']").val("");
	        			$("#alias-display .err").html("");
	        			$("#alias-display .alias-message").html("Success").show().fadeOut(3000);
        			} else {
        				var $message = $data.data.webMessages['employeeName'][0];
        				console.log($message);
        				$("#alias-display .alias-message").html($message).show().fadeOut(3000);
        			}
        		},
        		
        		
        		
        		
        		makeAliasTable : function($employeeCode) {
        			console.log("makeAliasTable: " + $employeeCode);
        			$("#alias-display").dialog("open");
        			$("#alias-lookup").DataTable( {
            			"aaSorting":		[[0,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId',
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
            	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}},
            	        	],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [0] },
            	            { className: "dt-center", "targets": [1] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "payroll/aliasLookup",
    			        	"type": "GET",
    			        	"data": {"employeeCode":$employeeCode},
    			        	},
    			        columns: [
    			        	{ title: "Employee Alias", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name' }, 
    			            { title: "Action",  width:"10%", searchable:true, searchFormat: "Equipment #####", 
    			            	data: function ( row, type, set ) { 
    			            		//var $editLink = '<span class="action-link edit-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:edit>Edit</webthing:edit></span>';
    			            		var $deleteLink = '<span class="action-link delete-link" data-id="'+row.employee_code+'" data-name="'+row.employee_name+'"><webthing:delete>Delete</webthing:delete></span>';
    			            		return '<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $deleteLink + '</ansi:hasPermission>'
    			            	}
    			            },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	console.log("alias drawCallback");
    			            	//$("#alias-lookup .edit-link").off("click");
    			            	$("#alias-lookup .delete-link").off("click");
    			            	$("#alias-lookup .cancel-new-alias").off("click");
    			            	$("#alias-lookup .save-new-alias").off("click");
    			            	$("#alias-lookup .delete-link").click(function($event) {
    			            		var $employeeCode = $(this).attr("data-id");
    			            		var $employeeName = $(this).attr("data-name");
    			            		$("#confirm-save").attr("data-function","deleteAlias");
    			            		$("#confirm-save").attr("data-id",$employeeCode);
    			            		$("#confirm-save").attr("data-name",$employeeName);
									$("#confirm-modal").dialog("open");
    			            	});
    			            	$("#alias-display .cancel-new-alias").click( function($event) {
    			            		$("#alias-display input[name='employeeName']").val("");
    			            	});
    			            	$("#alias-display .save-new-alias").click( function($event) {    			            		
    			            		var $employeeCode = $(this).attr("data-id");
    			            		var $employeeName = null
    			            		$.each( $("#alias-display input"), function($index, $value) {
    			            			// for some reason, datatables is putting two input boxes, so get the one that is populated
    			            			var $thisValue = $($value).val();
    			            			if ( $thisValue != null ) {
    			            				$employeeName = $thisValue;
    			            			}
    			            		});
    			            		var $url = "payroll/alias/" + $employeeCode;
    			            		$("#alias-display .err").html("");
    			            		console.log("new alias name: " + $employeeName);
    			            		ANSI_UTILS.makeServerCall("POST", $url, {"employeeName":$employeeName}, {200:EMPLOYEELOOKUP.doNewAliasSuccess}, {});
    			            	});

    			            },
    			            "footerCallback" : function( row, data, start, end, display ) {
    			            	console.log("alias footerCallback");
    			            	var api = this.api();
    			            	var $saveLink = '<span class="action-link save-new-alias" data-id="'+$employeeCode+'"><webthing:checkmark>Save</webthing:checkmark></span>';
    			            	var $cancelLink = '<span class="action-link cancel-new-alias"><webthing:ban>Cancel</webthing:ban></span>';
    			            	var $aliasInput = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><input type="text" name="employeeName" placeholder="New Alias" /></ansi:hasPermission>';
    			            	var $aliasError = '<span class="employeeNameErr err"></span>';
    			            	$( api.column(0).footer() ).html($aliasInput + " " + $aliasError);
    			            	$( api.column(1).footer() ).html('<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $saveLink + $cancelLink + '</ansi:hasPermission>');
    			            }
    			    } );
        		},
        		
        		
        		
        		makeClickers : function() {
        			$("#new-employee-button").click(function($clickevent) {
        				console.log("New employee button");
        				$("#employee-edit input").val("");
            			$("#employee-edit select").val("");
            			$("#employee-edit .err").html("");
            			$("#employee-edit .employee-code").html("New")
            			$("#employee-edit").dialog("open");
        			});
        			
        			
        			$("#employee-edit input[name='unionMember']").click(function($clickEvent) {
        				if ( $("#employee-edit input[name='unionMember']").prop('checked') ) {
        					$("#employee-edit .unionInput").prop('disabled',false);
        				} else {
        					$("#employee-edit .unionInput").prop('disabled',true);
        				}
        			});
        		},
        		
        		makeEmployeeTable : function() {
        			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $no = '<webthing:ban>No</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
        			
        			$("#employeeLookup").DataTable( {
            			"aaSorting":		[[4,'asc'],[3,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId',
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
            	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#employeeLookup').draw();}},
            	        	],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,10,12] },
            	            { className: "dt-center", "targets": [9,13] },
            	            { className: "dt-right", "targets": [11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "payroll/employeeLookup",
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
    			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' }, 
    			        	{ title: "Company Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'company_code' }, 
    			        	{ title: "Division", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
    			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_first_name' },
    			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_last_name' },
    			        	{ title: "MI", width:"5%", searchable:true, "defaultContent": "", data:'employee_mi' },
    			        	{ title: "Dept. Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'dept_description' },
    			        	{ title: "Status", width:"10%", searchable:true, searchFormat: "Active|Terminated|Deceased|On Leave", "defaultContent": "<i>N/A</i>", data:'employee_status' },
    			        	{ title: "Termination", width:"10%", searchable:true, searchFormat: "YYYY-MM-dd", "defaultContent": "", data:'formatted_termination_date' },
    			        	{ title: "Union", width:"5%", searchable:true, searchFormat: "1|0", "defaultContent":$unknown,
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.union_member != null ) {
    			        				if ( row.union_member == 1 ) {
    			        					$value = $yes;
    			        				}
    			        				if ( row.union_member == 0 ) {
    			        					$value = $no;
    			        				}
    			        			}
    			        			return $value;
    			        		}
    			        	},
    			        	{ title: "Union Code", width:"10%", searchable:true, "defaultContent": "", data:'union_code' },
    			        	{ title: "Union Rate", width:"10%", searchable:true, "defaultContent":"",
    			        		data:function(row, type, set) {
    			        			var $value = "";
    			        			if ( row.union_member == 1 ) {
    			        				$value = $unknown;
    			        				if ( row.union_rate != null ) {
    			        					$value = "$" + row.union_rate.toFixed(2);
    			        				}
    			        			}
    			        			return $value;
    			        		}
    			        	},
    			        	{ title: "Notes", width:"10%", searchable:true, "defaultContent": "", data:'notes' },    			        	
    			            { title: "Action",  width:"5%", searchable:false, "orderable": false, 
    			            	data: function ( row, type, set ) { 
    			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.employee_code+'"><webthing:view>Alias</webthing:view></span>';
    			            		var $editLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link edit-link" data-id="'+row.employee_code+'"><webthing:edit>Edit</webthing:edit></span></ansi:hasPermission>';
    			            		var $deleteLink = '';
    			            		if ( row.timesheet_count == 0 ) {
    			            			$deleteLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link delete-link" data-id="'+row.employee_code+'"><webthing:delete>Delete</webthing:delete></span></ansi:hasPermission>';
    			            		}
    			            		var $actionLink = $viewLink + $editLink + $deleteLink;
    			            		return $actionLink;
    			            	}
    			            },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#employeeLookup", EMPLOYEELOOKUP.makeEmployeeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	$(".view-link").off("click");
    			            	$(".edit-link").off("click");
    			            	$(".delete-link").off("click");
    			            	$(".view-link").click(function($clickevent) {
    			            		var $employeeCode = $(this).attr("data-id");
    			            		console.log("employee code: " + $employeeCode);
    			            		EMPLOYEELOOKUP.makeAliasTable($employeeCode);
    			            	});
    			            	$(".edit-link").click(function($clickevent) {
    			            		var $employeeCode = $(this).attr("data-id");
    			            		console.log("employee code: " + $employeeCode);
    			            		var $url = "payroll/employee/" + $employeeCode
    			            		var $callbacks = {
    			            				200:EMPLOYEELOOKUP.displayEmployeeModal
    			            			};
    			            		ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, {});
    			            	});
    			            	$(".delete-link").click(function($clickevent) {
    			            		var $employeeCode = $(this).attr("data-id");
    			            		console.log("employee code: " + $employeeCode);
    			            		$("#confirm-save").attr("data-function","deleteEmployee");
    			            		$("#confirm-save").attr("data-id",$employeeCode);
    			            		$("#confirm-save").attr("data-name",null);
									$("#confirm-modal").dialog("open");
    			            	});
    			            }
    			    } );
        		},
        		
        		
        		
        		makeLists : function($data, $passthru) {
        			EMPLOYEELOOKUP.companyCodeList = $data.data.companyCodeList;
        			EMPLOYEELOOKUP.divisionList = $data.data.divisionList;
        		},
        		
        		
        		
        		makeModals : function() {
        			console.log("makeModals");
        			$( "#alias-display" ).dialog({
        				title:'View Employee Alias',
        				autoOpen: false,
        				height: 500,
        				width: 600,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "alias_display_cancel",
        						click: function($event) {
       								$( "#alias-display" ).dialog("close");
        						}
        					}
        				]
        			});	
        			$("#alias_display_cancel").button('option', 'label', 'Done');    
        			
        			
        			$( "#employee-edit" ).dialog({
        				title:'Edit Employee',
        				autoOpen: false,
        				height: 520,
        				width: 800,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "employee-edit-cancel",
        						click: function($event) {
       								$( "#employee-edit" ).dialog("close");
        						}
        					},{
        						id:  "employee-edit-save",
        						click: function($event) {
       								EMPLOYEELOOKUP.saveEmployee();
        						}
        					}
        				]
        			});	
        			$("#employee-edit-cancel").button('option', 'label', 'Cancel');  
        			$("#employee-edit-save").button('option', 'label', 'Save');
        			
        			
        			
        			
        			$( "#confirm-modal" ).dialog({
        				title:'Confirm Delete',
        				autoOpen: false,
        				height: 200,
        				width: 300,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "confirm-cancel",
        						click: function($event) {
       								$( "#confirm-modal" ).dialog("close");
        						}
        					},{
        						id:  "confirm-save",
        						click: function($event) {
       								EMPLOYEELOOKUP.doConfirm();
        						}
        					}
        				]
        			});	
        			$("#confirm-cancel").button('option', 'label', 'Cancel');  
        			$("#confirm-save").button('option', 'label', 'Confirm');
        		},
        		
        		
        		
        		
        		saveEmployee : function() {
        			console.log("saveEmployee");
        			$("#employee-edit .err").html("");
        			var $selectedEmployeeCode = $("#employee-edit input[name='selectedEmployeeCode']").val();
        			var $unionMember = 0;
        			if ( $("#employee-edit input[name='unionMember']").prop("checked") == true ) {
        				$unionMember = 1; 
        			} 
        			var $outbound = {
       					'selectedEmployeeCode' : $("#employee-edit input[name='selectedEmployeeCode']").val(),
        				'employeeCode' : $("#employee-edit input[name='employeeCode']").val(),
	        			'companyCode' : $("#employee-edit select[name='companyCode']").val(),
	        			'divisionId' : $("#employee-edit select[name='divisionId']").val(),
	        			'firstName' : $("#employee-edit input[name='firstName']").val(),
	        			'lastName' : $("#employee-edit input[name='lastName']").val(),
	        			'middleInitial' : $("#employee-edit input[name='middleInitial']").val(),
	        			'departmentDescription' : $("#employee-edit input[name='departmentDescription']").val(),
	        			'status' : $("#employee-edit select[name='status']").val(),
	        			'terminationDate' : $("#employee-edit input[name='terminationDate']").val(),	        			
	        			'unionMember' : $unionMember,
	        			'unionCode' : $("#employee-edit input[name='unionCode']").val(),
	        			'unionRate' : $("#employee-edit input[name='unionRate']").val(),
	        			'processDate' : $("#employee-edit input[name='processDate']").val(),
	        			'notes' : $("#employee-edit input[name='notes']").val(),
        			}
        			var $url = "payroll/employee"
        			if ( $selectedEmployeeCode != null && $selectedEmployeeCode != "") {
        				$url = $url + "/" + $selectedEmployeeCode
        			}
        			ANSI_UTILS.makeServerCall("post", $url, JSON.stringify($outbound), {200:EMPLOYEELOOKUP.saveEmployeeSuccess}, {});
        		},
            	
        		
        		saveEmployeeSuccess : function($data, $passthru) {
        			console.log("saveEmployeeSuccess");
        			if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
	        			$("#employeeLookup").DataTable().ajax.reload();
	        			$("#employee-edit").dialog("close");
	        			$("#globalMsg").html("Success").show().fadeOut(3000);
        			} else if ($data.responseHeader.responseCode == 'EDIT_FAILURE' ) {
						$.each($data.data.webMessages, function($index, $value) {
							var $selector = "#employee-edit ." + $index + "Err";
							$($selector).html($value[0]);
						});
        			} else {
	        			$("#globalMsg").html("Invalid response code: " + $data.responseHeader.responseCode + ". Cntact Support").show();
        			}
        		},
        		
        		
        		
        		showAliasEdit : function($data, $passthru) {
        			console.log("showAliasEdit");
        		},
        	};
        	
        	EMPLOYEELOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Employee Lookup</h1> 

    	<webthing:lookupFilter filterContainer="filter-container" />
		<table id="employeeLookup">
		</table>
		<input class="prettyWideButton" id="new-employee-button" value="New" type="button" />
		
		<div id="alias-display">
			<div class="alias-message err"></div>
			<table id="alias-lookup">
				<thead></thead>
				<tbody></tbody>
				<tfoot>
					<tr>
						<td></td>
						<td></td>
					</tr>
				</tfoot>
			</table>			
		</div>
		
		
		<div id="confirm-modal">			
			<h2>Are you sure?</h2>
		</div>
		
		
		<jsp:include page="employeeCrudForm.jsp">
			<jsp:param name="id" value="employee-edit" />
		</jsp:include>
		
    </tiles:put>
		
</tiles:insert>

