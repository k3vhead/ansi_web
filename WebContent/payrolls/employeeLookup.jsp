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
    			            { title: "Action",  width:"10%", searchable:true, searchFormat: "Equipment #####", 
    			            	data: function ( row, type, set ) { 
    			            		var $actionLink = '<span class="action-link" data-id="'+row.employee_code+'"><webthing:view>View</webthing:view></span>';
    			            		$actionLink = "";
    			            		return $actionLink;
    			            	}
    			            },
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
            	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}},
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
    			        	{ title: "Division", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'division' },
    			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_first_name' },
    			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_last_name' },
    			        	{ title: "MI", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_mi' },
    			        	{ title: "Dept. Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'dept_description' },
    			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
    			        	{ title: "Termination", width:"10%", searchable:true, "defaultContent": "", data:'employee_termination_date' },
    			        	{ title: "Notes", width:"10%", searchable:true, "defaultContent": "", data:'notes' },
    			            { title: "Action",  width:"10%", searchable:true, searchFormat: "Equipment #####", 
    			            	data: function ( row, type, set ) { 
    			            		var $actionLink = '<span class="action-link" data-id="'+row.employee_code+'"><webthing:view>View</webthing:view></span>';
    			            		return $actionLink;
    			            	}
    			            },
    			            ],
    			            "initComplete": function(settings, json) {
    			            	var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#ticketTable", CALL_NOTE_LOOKUP.makeTable);
    			            },
    			            "drawCallback": function( settings ) {
    			            	$(".action-link").off("click");
    			            	$(".action-link").click(function($clickevent) {
    			            		var $employeeCode = $(this).attr("data-id");
    			            		console.log("employee code: " + $employeeCode);
    			            		EMPLOYEELOOKUP.makeAliasTable($employeeCode);
    			            	});
    			            }
    			    } );
        		},
        		
        		
        		makeModals : function() {
        			console.log("makeModals");
        			$( "#alias-display" ).dialog({
        				title:'View Employee Alias',
        				autoOpen: false,
        				height: 500,
        				width: 1200,
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
        			
        			
        		},
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		getOrganizationDetail : function($action, $organizationId, $filter) {
        			// $filter says whether to retrieve all children (false) or just the children of this org (true)
        			console.log("getOrganizationDetail: " + $organizationId);
        			var $url = "organization/" + EMPLOYEELOOKUP.orgType + "/" + $organizationId;
        			var $callbacks = {
        				200 : EMPLOYEELOOKUP.showOrgDetail,
        				404 : EMPLOYEELOOKUP.getOrgsFail,
        			}        			
        			$outbound = {"filter":$filter};
        			ANSI_UTILS.makeServerCall("GET", $url, $outbound, $callbacks, {"action":$action});
        		},
        		
        		
        		
        		getOrgs : function() {
        			console.log("getOrgs");
        			var $url = "organization/" + EMPLOYEELOOKUP.orgType;
        			var $callbacks = {
        				200 : EMPLOYEELOOKUP.makeOrgTable,
        				404 : EMPLOYEELOOKUP.getOrgsFail,
        			}
        			var $passthru = {
       					"destination":"#org-table", 
       					"action":["view","edit"],
       					"source":"orgList",
        			}
        			ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, $passthru);
        		},
        		
        		
        		
        		getOrgsFail : function($data, $passthru) {
        			console.log("getOrgsFail");
        			$("#globalMsg").html("Invalid organization type");        			
        		},
        		
        		
        		
        		makeClickers : function() {
        			$("#organization-edit .org-status-change").on("click", function($event) {
        				console.log("changing status");
        				var $organizationId = $(this).attr("data-id");
        				var $classList = $(this).attr('class').split(" ");
        				var $newStatus = null;
        				$.each($classList, function($index, $value) {
        					if ( $value == "status-is-active") {
        						$newStatus = false;
        					} else if ( $value == "status-is-inactive") {
        						$newStatus = true;
        					} else if ( $value == "status-is-unknown") {
        						$newStatus = true;
        					}
        				});
        				var $url = "organization/" + EMPLOYEELOOKUP.orgType + "/" + $organizationId;
        				var $outbound = {"status":$newStatus};
        				var $callbacks = {
        					200 : EMPLOYEELOOKUP.statusChangeSuccess,
        				};
        				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
        			});
        			
        			
        			$("#organization-edit input[name='name']").on("blur", function($event) {
        				console.log("changing name");
        				var $organizationId = $(this).attr("data-id");
        				var $oldName = $(this).attr("data-name");
        				var $newName = $("#organization-edit input[name='name']").val();
        				
        				if ( $oldName != $newName) {  
        					$("#organization-edit input[name='name']").attr("data-name", $newName);
	        				var $url = "organization/" + EMPLOYEELOOKUP.orgType + "/" + $organizationId;
	        				var $outbound = {"name":$newName};
	        				var $callbacks = {
	        					200 : EMPLOYEELOOKUP.statusChangeSuccess,
	        				};
	        				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
        				}
        			});
        		},
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		makeOrgTable : function($data, $passthru) {
        			console.log("makeOrgTable");  
        			console.log($passthru);
        			var $active = '<webthing:checkmark>Active</webthing:checkmark>';
        			var $inactive = '<webthing:ban>Inactive</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Unknown</webthing:questionmark>';
        			
        			var $edit = '<webthing:edit>Edit</webthing:edit>';
	       			var $delete = '<webthing:delete>Delete</webthing:delete></a>';
	       			var $view = '<webthing:view>View</webthing:view>';
	       			
	       			
					
					
					
        			var $buttonArray = [        	        	
        	        	'copy', 
        	        	{extend:'csv', filename:'*_' + EMPLOYEELOOKUP.orgTypeDisplay}, 
        	        	{extend:'excel', filename:'*_' + EMPLOYEELOOKUP.orgTypeDisplay}, 
        	        	{extend:'pdfHtml5', orientation: 'landscape', filename:'*_' + EMPLOYEELOOKUP.orgTypeDisplay}, 
        	        	'print',
        	        	{extend:'colvis', label: function () {
	        	        		doFunctionBinding();
	        	        		$('#org-table').draw();
        	        		}
        	        	}
        	        ];
        			
        			$($passthru['destination']).DataTable( {
		    			data : $data.data[$passthru['source']],
		    			"aaSorting":		[[1,'asc']],
		    			paging : false,
		    			autoWidth : false,
	        	        deferRender : true,
	        	        searching: false, 
	        	        scrollX : false,
	        	        rowId : 'organizationId',	// this sets an id for the row: <tr id="123"> ... </tr>   where 123 is the org id
	        	        dom : 'Bfrtip',
	        	        destroy : true,		// this lets us reinitialize the table for different tickets
            	        buttons: $buttonArray,
		    			columns : [
		    				{ width:"10%", title:"ID", className:"dt-center", orderable:true, data:'organizationId' },
		    				{ width:"25%", title:"Name", className:"dt-left", orderable:true, data:'name' },
		    				{ width:"25%", title:"Parent", className:"dt-left", orderable:true, data:'parentName' },
		    				{ width:"10%", title:"Parent Type", className:"dt-left", orderable:true, data:'parentType' },
		    				{ width:"10%", title:"Status", className:"dt-center", orderable:true, 
		    					data:function(row,type,set) {
		    						if ( row.status == 0 ) { $status = $inactive}
		    						else if ( row.status == 1 ) { $status = $active }
		    						else { $status = $unknown }
		    						return $status;
		    					}
		    				},
		    				{ width:"10%", title:"Action", className:"dt-center", orderable:true, visible:$passthru['action'].length > 0,
		    					data:function(row,type,set) {
		    						var $viewLink = "";
		    						var $editLink = "";
		    						var $parentLink = "";
		    						
		    						$.each($passthru['action'], function($index, $value) {
		    							if ( $value == "view" )  { $viewLink = '<a href="#" class="action-link view-link" data-id="'+row.organizationId+'"><webthing:view>View</webthing:view></a>'; }
		    							if ( $value == "edit" )  { $editLink = '<a href="#" class="action-link edit-link" data-id="'+row.organizationId+'"><webthing:edit>Edit</webthing:edit></a>'; }
		    							if ( $value == "parent") { 
		    								if ( row.parentId == $data.data.organization.organizationId) {
		    									$parentLink = EMPLOYEELOOKUP.icons['componentIsYes'];
			    							} else {
			    								$parentLink = '<a href="#" class="action-link parent-link" data-orgtype="'+row.type+'" data-id="'+row.organizationId+'">' + EMPLOYEELOOKUP.icons['componentIsNo'] + '</a>';
			    							}
		    							}
		    						});
	    							
		    						return $viewLink + '<ansi:hasPermission permissionRequired="SYSADMIN_WRITE">' + $editLink + $parentLink + '</ansi:hasPermission>';
		    					},		    					
		    				},
	    				],
	    				"drawCallback": function( settings ) {
	    					console.log("drawCallback");
	    					$(".action-link").off("click");
	    					$(".view-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						EMPLOYEELOOKUP.getOrganizationDetail("view", $organizationId, true);
	    					});
	    					$(".edit-link").click(function($clickevent) {
	    						var $organizationId = $(this).attr("data-id");
	    						EMPLOYEELOOKUP.getOrganizationDetail("edit", $organizationId, false);
	    					});
	    					$(".parent-link").click(function($clickevent) {	    						
	    						EMPLOYEELOOKUP.parentChange($(this));
	    					});
	    				},
        			});
        			
	       			// var myTable $($passthru['destination']).DataTable();
					// myTable.columns(columnNumber).visible(true);
        		},
        		
        		

        		parentChange : function($clickEvent) {
        			var $organizationId = $clickEvent.attr("data-id");
					var $organizationType = $clickEvent.attr("data-orgtype");
					var $newParent = $("#organization-edit input[name='name']").attr("data-id");
					console.log("changing parent: " + $organizationId + " " + $organizationType + " " + $newParent);
					var $url = "organization/" + $organizationType + "/" + $organizationId;
    				var $outbound = {"parentId":$newParent};
    				var $callbacks = {
    					200 : EMPLOYEELOOKUP.parentChangeSuccess,
    				};
    				ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {"event":$clickEvent});

        		},
        		
        		
        		
        		
        		parentChangeSuccess : function($data, $passthru) {
        			console.log("parentChangeSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#organization-edit .organization-msg").html("Update successful").show().fadeOut(3000);
        				$passthru["event"].html(EMPLOYEELOOKUP.icons['componentIsYes'])
            			//EMPLOYEELOOKUP.showOrgDetail($data, {"action":"edit"});
        			} else {
        				//this happens if the data attributes on the lookup are weird.
        				$("#organization-edit .organization-msg").html("Update error. Reload page and try again").show();
        			}
        		},
        		
        		
        		
        		
        		showOrgDetail : function($data, $passthru) {
        			console.log("showOrgDetail");
        			
        			var $organization = $data.data.organization;
        			var $active = '<webthing:checkmark>Active</webthing:checkmark>';
        			var $inactive = '<webthing:ban>Inactive</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Unknown</webthing:questionmark>';

        			if ( $organization.status == 0 ) { 
        				$status = $inactive;
        			} else if ( $organization.status == 1 ) { 
        				$status = $active;
        			} else { 
        				$status = $unknown; 
       				}

        			// populate the display modal
        			$("#organization-display .organization-id").html($organization.organizationId);
        			$("#organization-display .name").html($organization.name);
        			$("#organization-display .status").html($status);
        			$("#organization-display .parent-name").html($organization.parentName);
        			$("#organization-display .parent-type").html($organization.parentType);
        			
        			// populate the edit modal
        			$("#organization-edit .organization-id").html($organization.organizationId);
        			$("#organization-edit input[name='name']").val($organization.name);
        			$("#organization-edit input[name='name']").attr("data-id", $organization.organizationId);
        			$("#organization-edit input[name='name']").attr("data-name", $organization.name);
        			$("#organization-edit .org-status-change").hide();
        			$("#organization-edit .org-status-change").attr("data-id", $organization.organizationId);
        			if ( $organization.status == 0 ) { 
        				$("#organization-edit .status-is-inactive").show();
        			} else if ( $organization.status == 1 ) { 
        				$("#organization-edit .status-is-active").show();
        			} else { 
        				$("#organization-edit .status-is-unknown").show();
       				}
        			$("#organization-edit .parent-name").html($organization.parentName);
        			$("#organization-edit .parent-type").html($organization.parentType);
        			
        			var $destination = {
           				"view" : "#organization-display",
           				"edit" : "#organization-edit",
           			};
            			
        			if ( $passthru["action"] == "view" ) {
        				$displayDetail = {
               				"destination":$destination[$passthru["action"]] + " .organization-children",
               				"action":[],
               				"source":"childList",
               			}
        			} else if ( $passthru["action"] == "edit" ) {
        				$displayDetail = {
               				"destination":$destination[$passthru["action"]] + " .organization-children",
               				"action":["parent"],
               				"source":"childList",
               			}
        			} else {
        				$("#globalMsg").html("System Error. Invalid action. Contact Support");
        			}
        			// populate the child list        			
        			EMPLOYEELOOKUP.makeOrgTable($data, $displayDetail)
        			
        			// display one of the modals
        			$($destination[$passthru["action"]]).dialog("open");
        		},
        		
        		
        		
        		
            	
        		statusChangeSuccess : function($data, $passthru) {
        			console.log("statusChangeSuccess");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#organization-edit .organization-msg").html("Update Successful").show().fadeOut(3000);
        				$("#organization-edit .org-status-change").hide();
            			if ( $data.data.organization.status == 0 ) { 
            				$("#organization-edit .status-is-inactive").show();
            			} else if ( $data.data.organization.status == 1 ) { 
            				$("#organization-edit .status-is-active").show();
            			} else { 
            				$("#organization-edit .status-is-unknown").show();
           				}            			
            			EMPLOYEELOOKUP.getOrgs();
        			} else {
        				$("#organization-edit .organization-msg").html("System Error. Reload the page and try again").show();
        			}
        			
        			
        		},
            	
            	
            	
        	};
        	
        	EMPLOYEELOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Employee Lookup</h1> 

    	
		<table id="employeeLookup">
		</table>
		
		<div id="alias-display">
			<table id="alias-lookup">
			</table>
		</div>
    </tiles:put>
		
</tiles:insert>

