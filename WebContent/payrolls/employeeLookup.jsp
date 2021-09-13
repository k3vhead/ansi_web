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
    
        <style type="text/css">
        	#alias-display {
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
        		},
        		
        		
        		
        		displayEmployeeModal : function($data, $passsthru) {
        			console.log("displayEmployeeModal");
        			$("#employee-edit input").val("");
        			$("#employee-edit select").val("");
        			$("#employee-edit .err").html("");
        			$("#employee-edit .employee-code").html($data.data.employee.employeeCode)
        			$("#employee-edit input[name='employeeCode']").val($data.data.employee.employeeCode);
        			$("#employee-edit select[name='companyCode']").val($data.data.employee.companyCode);
        			$("#employee-edit select[name='divisionId']").val($data.data.employee.divisionId);
        			$("#employee-edit input[name='firstName']").val($data.data.employee.firstName);
        			$("#employee-edit input[name='lastName']").val($data.data.employee.lastName);
        			$("#employee-edit input[name='middleInitial']").val($data.data.employee.middleInitial);
        			$("#employee-edit input[name='departmentDescription']").val($data.data.employee.departmentDescription);
        			$("#employee-edit select[name='status']").val($data.data.employee.status);
        			$("#employee-edit input[name='terminationDate']").val($data.data.employee.terminationDate);
        			$("#employee-edit input[name='notes']").val($data.data.employee.notes);
        			$("#employee-edit").dialog("open");
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
            	            { className: "dt-center", "targets": [] },
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
    			            //{ title: "Action",  width:"10%", searchable:true, searchFormat: "Equipment #####", 
    			            //	data: function ( row, type, set ) { 
    			            //		var $actionLink = '<span class="action-link" data-id="'+row.employee_code+'"><webthing:view>View</webthing:view></span>';
    			            //		$actionLink = "";
    			            //		return $actionLink;
    			            //	}
    			            //},
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	//$(".action-link").off("click");
    			            	//$(".action-link").click(function($clickevent) {
    			            	//	var $employeeCode = $(this).attr("data-id");
    			            	//	console.log("employee code: " + $employeeCode);
    			            	//	EMPLOYEELOOKUP.makeAliasTable($employeeCode);
    			            	//});
    			            }
    			    } );
        		},
        		
        		
        		
        		makeEmployeeTable : function() {
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
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,9,10] },
            	            { className: "dt-center", "targets": [] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "payroll/employeeLookup",
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
    			        	{ title: "Employee Code", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' }, 
    			        	{ title: "Company Code", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'company_code' }, 
    			        	{ title: "Division", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
    			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_first_name' },
    			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_last_name' },
    			        	{ title: "MI", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_mi' },
    			        	{ title: "Dept. Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'dept_description' },
    			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
    			        	{ title: "Termination", width:"10%", searchable:true, "defaultContent": "", data:'formatted_date' },
    			        	{ title: "Notes", width:"10%", searchable:true, "defaultContent": "", data:'notes' },
    			            { title: "Action",  width:"10%", searchable:false,  
    			            	data: function ( row, type, set ) { 
    			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.employee_code+'"><webthing:view>Alias</webthing:view></span>';
    			            		var $editLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link edit-link" data-id="'+row.employee_code+'"><webthing:edit>Edit</webthing:edit></span></ansi:hasPermission>';
    			            		var $actionLink = $viewLink + $editLink;
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
        				height: 500,
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
        		},
        		
        		
        		
        		
        		saveEmployee : function() {
        			console.log("saveEmployee");
        			$("#employee-edit .err").html("");
					var $employeeCode = $("#employee-edit input[name='employeeCode']").val();					
        			var $outbound = {
	        			'companyCode' : $("#employee-edit select[name='companyCode']").val(),
	        			'divisionId' : $("#employee-edit select[name='divisionId']").val(),
	        			'firstName' : $("#employee-edit input[name='firstName']").val(),
	        			'lastName' : $("#employee-edit input[name='lastName']").val(),
	        			'middleInitial' : $("#employee-edit input[name='middleInitial']").val(),
	        			'departmentDescription' : $("#employee-edit input[name='departmentDescription']").val(),
	        			'status' : $("#employee-edit select[name='status']").val(),
	        			'terminationDate' : $("#employee-edit input[name='terminationDate']").val(),
	        			'notes' : $("#employee-edit input[name='notes']").val(),
        			}
        			var $url = "payroll/employee"
        			if ( $employeeCode != null && $employeeCode != "") {
        				$url = $url + "/" + $employeeCode
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
        		}
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
		
		<div id="alias-display">
			<table id="alias-lookup">
			</table>
		</div>
		
		<div id="employee-edit" class="modal-window">
			<table>
				<tr>
					<td class="form-label">Employee Code:</td>
					<td>
						<input type="hidden" name="employeeCode" />
						<span class="employee-code"></span>
					</td>
					<td><span class="err employeeCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Company:</td>
					<td>
						<select name="companyCode">
							<option value=""></option>
							<ansi:selectPayrollCompany active="true" />
						</select>
					</td>
					<td><span class="err companyCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Division:</td>
					<td>
						<select name="divisionId">
							<option value=""></option>
							<ansi:selectOrganization active="true" type="DIVISION" />
						</select>
					</td>
					<td><span class="err divisionIdErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Name:</td>
					<td>
						<input name="firstName" type="text" placeholder="First" />
						<input name="middleInitial" type="text" placeholder="MI" style="width:15px;" />
						<input name="lastName" type="text" placeholder="Last" />
					</td>
					<td><span class="err nameErr"></span></td>
				</tr>				
				<tr>
					<td class="form-label">Department:</td>
					<td><input name="departmentDescription" type="text" /></td>
					<td><span class="err departmentErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Status</td>
					<td>
						<select name="status">
							<option value=""></option>
							<option value="ACTIVE">Active</option>
							<option value="TERMINATED">Terminated</option>
						</select>
					</td>
					<td><span class="err statusErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Termination Date:</td>
					<td><input name="terminationDate" type="date" /></td>
					<td><span class="err terminationDateErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Notes:</td>
					<td><input name="notes" type="text" /></td>
					<td><span class="err notesErr"></span></td>
				</tr>
			</table>
		</div>
    </tiles:put>
		
</tiles:insert>

