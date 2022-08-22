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
		Tax Profile Lookup
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
			.new-profile-container {
				width:100%;
			}
			.profile-err {
				width:100%;
				height:14px;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;PROFILELOOKUP = {
       			init : function() {       				
       				PROFILELOOKUP.makeLookup(); 
       				PROFILELOOKUP.makeClickers();
           		},
           		
           		
           		deleteProfile : function() {
           			console.log("delete profile");
           			var $outbound = null;
           			var $profileId = $("#confirmation-modal input[name='profileId']").val();
           			var $url = "payroll/taxProfile/" + $profileId;
           			ANSI_UTILS.makeServerCall("DELETE", $url, $outbound, {200:PROFILELOOKUP.saveProfileSuccess}, {});
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
       								PROFILELOOKUP.deleteProfile();
        						}
        					}
        				]
        			});	
        			$("#confirm-cancel").button('option', 'label', 'No');  
        			$("#confirm-save").button('option', 'label', 'Yes');
           		},
           		
           		
           		
           		
           		initEditModal : function() {
           			console.log("initEditModal");
           			
           			$( "#edit-modal" ).dialog({
        				title:'Payroll Tax Profile',
        				autoOpen: false,
        				height: 400,
        				width: 600,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "edit-cancel",
        						click: function($event) {
       								$( "#edit-modal" ).dialog("close");
        						}
        					},{
        						id:  "edit-save",
        						click: function($event) {
       								PROFILELOOKUP.saveProfile();
        						}
        					}
        				]
        			});	
        			$("#edit-cancel").button('option', 'label', 'Cancel');  
        			$("#edit-save").button('option', 'label', 'Save');
        			
        			
           		},
           		
           		
           		
           		makeClickers : function() {
           			$("input[name='new-profile']").click(function($event) {
           				console.log("new profile");
           				PROFILELOOKUP.showNewProfile();
           			});
           		},
           		


           		showNewProfile : function() {
           			if ( ! $("#edit-modal").hasClass("ui-dialog-content")) {
           				PROFILELOOKUP.initEditModal();
           			}
       				$("#edit-modal .err").html("").show();           				
           			$.each( $("#edit-modal input"), function($index, $value) {
           				$($value).val("");
           			});
           			$.each( $("#edit-modal select"), function($index, $value) {
           				$($value).val("");
           			});
       				$("#edit-modal input[name='action']").val("ADD");
       				$("#edit-modal .profileId").html("");
           			$("#edit-modal").dialog("open");
           		},
           		
           		
           		
           		
           		makeLookup : function() {
           			var $delete = '<webthing:delete>Delete</webthing:delete>';
           			var $edit = '<webthing:edit>Edit</webthing:edit>';
           			
           				
           			$("#timesheetLookup").DataTable( {
               			"aaSorting":		[[0,'asc']],
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
               	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#timesheetLookup').draw();}}<ansi:hasPermission permissionRequired="PAYROLL_WRITE">,
               	        		{
    	        	        		text:'New',
    	        	        		action: function(e, dt, node, config) {
    	        	        			PROFILELOOKUP.showNewProfile()
    	        	        		}
    	        	        	}
               	        		</ansi:hasPermission>
               	        	],
               	        "columnDefs": [
               	            { "orderable": true, "targets": -1 },                	            
               	            { className: "dt-left", "targets": [1]},
               	            { className: "dt-center", "targets": [0,2,3,4,5,6]},
               	            { className: "dt-right", "targets": []}
               	         ],
               	        "paging": true,
       			        "ajax": {
       			        	"url": "payroll/taxProfileLookup",
       			        	"type": "GET",
       			        	"data": {},
       			        	},
       			        columns: [
       			        	{ title: "ID", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'profile_id' },
       			        	{ title: "Description", width:"25%", searchable:true, "defaultContent": "<i>N/A</i>", data:'profile_desc' },
       			            { title: "Regular Hours",  width:"10%", searchable:false, "defaultContent": "", data: 'regular_hours' },
       			            { title: "Regular Pay", width:"10%", searchable:false, "defaultContent": "", data: 'regular_pay' },
       			            { title: "OT Hours",  width:"10%", searchable:false, "defaultContent": "", data: 'ot_hours' },
       			            { title: "OT Pay",  width:"10%", searchable:false, "defaultContent": "", data: 'ot_pay' },
       			            { title: "Action", width:"10%", searchable:false, "orderable": false, defaultContent:"", data: function(row, type, set) {
       			            	var $parms = 'data-profile="'+row.profile_id+'"';
       			            	var $deleteLink = "";
       			            	if ( row.locale_count == 0 ) {
       			            		$deleteLink = '<span class="action-link delete-action" ' + $parms + '>' + $delete + '</span>';
       			            	}
       			            	var $editLink = '<span class="action-link edit-action"  ' + $parms + '>' + $edit + '</span>';
       			            	return '<ansi:hasPermission permissionRequired="PAYROLL_WRITE">' + $editLink + $deleteLink + '</ansi:hasPermission>';
       			            }},
       			         	{ title: "&nbsp;",  width:"15%", searchable:false, orderable:false, "defaultContent": "", data: function(row, type, set) { return '&nbsp;' }},
       			            ],
       			            "initComplete": function(settings, json) {
       			            	var myTable = this;
       			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#timesheetLookup", PROFILELOOKUP.makeLookup);
       			            	$("th").removeClass("dt-right");
       			            	$("th").removeClass("dt-left");
       			            	$("th").addClass("dt-center");        			            	
       			            },
       			            "drawCallback": function( settings ) {
       			            	$(".delete-action").off("click");
       			            	$(".edit-action").off("click");
       			            	$(".delete-action").click(function($event) {
       			            		var $profileId = $(this).attr("data-profile");

       			            		if ( ! $("#confirmation-modal").hasClass("ui-dialog-content")) {
       			           				PROFILELOOKUP.initConfirmationModal();
       			           			}
       			            		
       			            		$("#confirmation-modal input[name='profileId']").val($profileId);
       			            		$("#confirmation-modal").dialog("open");
       			            	});
       			            	$(".edit-action").click(function($event) {
       			            		var $profileId = $(this).attr("data-profile");
									var $url = "payroll/taxProfile/" + $profileId;    
									var $outbound = null;
       			            		ANSI_UTILS.makeServerCall("GET", $url, $outbound, {200:PROFILELOOKUP.showEditModal}, {});
       			            	});
       			            }
       			    } );
           		},
            		
            		
           		
           		
           		saveProfile : function() {
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
        				200:PROFILELOOKUP.saveProfileSuccess,
        				403:PROFILELOOKUP.saveProfileError,
           				404:PROFILELOOKUP.saveProfileError,
           				405:PROFILELOOKUP.saveProfileError,
           				500:PROFILELOOKUP.saveProfileError,
        			};
           			var $url = "payroll/taxProfile";
           			if ( $outbound['profileId'] != null && $outbound['profileId'] != "") {
           				$url = $url + "/" + $outbound['profileId'];
           			}
           			ANSI_UTILS.makeServerCall("POST", $url, JSON.stringify($outbound), $callbacks, {});
           		},
           		
           		
           		saveProfileError : function($data, $passthru) {
           			console.log("saveProfileError ")
           			if ( $data.status == 403 ) {
           				$("#edit-modal .profile-err").html("Session expired. Log in again").show();
           			} else {
           				$("#edit-modal .profile-err").html("System error " + $data.status + ". Contact Support").show();
           			}
           			
           		},
           		
           		
           		
           		saveProfileSuccess : function($data, $passthru) {
           			console.log("saveProfileSuccess");
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
           				$("#edit-modal .profile-err").html("Unexpected response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
           			}
           		},
           		
           		
           		
           		showEditModal : function($data, $passthru) {
           			console.log("showEditModal");
           			if ( ! $("#edit-modal").hasClass("ui-dialog-content")) {
           				PROFILELOOKUP.initEditModal();
           			}
           			
           			$("#edit-modal .err").html("");
           			$("#edit-modal input[name='action']").val("UPDATE");
           			$("#edit-modal .profileId").html($data.data.profileId);
           		 	$("#edit-modal input[name='profileId']").val($data.data.profileId);
           		 	$("#edit-modal input[name='profileDesc']").val($data.data.profileDesc);
           		 	$("#edit-modal input[name='regularHours']").val($data.data.regularHours);
           			$("#edit-modal input[name='regularPay']").val($data.data.regularPay);
           			$("#edit-modal input[name='otHours']").val($data.data.otHours);
           			$("#edit-modal input[name='otPay']").val($data.data.otPay);
           			$("#edit-modal").dialog("open");

           		},
            	
            	
            	
        	};
        	
        	PROFILELOOKUP.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Tax Profile Lookup</h1> 

    	<webthing:lookupFilter filterContainer="filter-container" />
		<table id="timesheetLookup"></table>
		<div class="new-profile-container">
			<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><input type="button" value="New Profile" class="prettyWideButton" name="new-profile" /></ansi:hasPermission>
		</div>
		
		
				            
		<div id="edit-modal">
			<div class="profile-err err"></div>
			<table class="edit-form">
				<tr>
					<td><span class="form-label">ID: </span></td>
					<td>
						<input type="hidden" name="profileId" />
						<span class="profileId"></span>
					</td>
					<td><span class="profileIdErr err"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">Description: </span></td>
					<td><input type="text" name="profileDesc" /></td>
					<td><span class="profileDescErr err"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">Regular Hours: </span></td>
					<td><input type="text" name="regularHours" placeholder="ABC" /></td>
					<td><span class="regularHoursErr err"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">Regular Pay: </span></td>
					<td><input type="text" name="regularPay" placeholder="ABC"  /></td>
					<td><span class="regularPayErr err"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">OT Hours: </span></td>
					<td><input type="text" name="otHours" placeholder="ABC" /></td>
					<td><span class="otHoursErr err"></span></td>
				</tr>
				<tr>
					<td><span class="form-label">OT Pay: </span></td>
					<td><input type="text" name="otPay" placeholder="ABC"  /></td>
					<td><span class="otPayErr err"></span></td>
				</tr>
			</table>
		</div>
		
		<div id="confirmation-modal">
			<h2>Are You Sure?</h2>
			<input type="hidden" name="profileId" />
		</div>
    </tiles:put>
		
</tiles:insert>

