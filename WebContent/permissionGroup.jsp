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
        Permissions
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:350px;
				padding:15px;
			}
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:350px;
				text-align:center;
				padding:15px;
			}
			#deleteModal {
        		display:none;
        	}
			#displayTable {
				width:100%;
			}
			#editPanel {
				display:none;
			}
			#modalMessage-wrapper {
				height:18px;
			}
			#permissionsModal {
				width: 1248px;
				margin: 0 auto;
				position: relative;
			}
			#permissionTable {
				width:1224px;
			}
			.center{
			  	text-align: center;
			}
			.display-none{
    			display: none;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.jobLink {
				color:#000000;
			}
			.maxWidth {
				width:100%;
			}
			.permission-selector-text {
				font-size:85%;
			}
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			.editAction {
				cursor:pointer;
			}
			.formHdr {
				font-weight:bold;				
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
			}
			.perm {
				display:none;
			}
			.print-link {
				cursor:pointer;
			}
			.showNew {
				cursor:pointer;
			}
			
			
			
	
			#main_menu > li{
			    position: relative;
			    display: inline;
			}
			
			.sub-menu{
			    position: absolute;
			    left: 0;
			    top: 15px;
			    list-style-type: none;
  				margin: 0;
			    padding: 0;
			    width: 500px;
			}
			
			.sub-menu li{
			    display: inline;
			    margin: 0;
			}
			li div { 
				vertical-align:middle;
			}
			li div span { 
				float:left; 
			}
			ul {
  				list-style-type: none;
			}
			.hilite {
    			background-color: gray;
			}
			tr:hover {
				cursor: pointer;
			}
			td:hover {
				cursor: pointer;
			}
			
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function() {
        	
        	;PERMISSIONGROUP = {
					ansiModal : '<c:out value="${ANSI_MODAL}" />',
					dataTable : null,
					permissionTable : null,		// this is the permission edit table
        			
        	
        			init : function (){
        				PERMISSIONGROUP.clearAddForm();
        				PERMISSIONGROUP.createTable();
        				PERMISSIONGROUP.doFunctionBinding();
        				PERMISSIONGROUP.makeDeleteModal();
        				PERMISSIONGROUP.makeDeletePermissionButton();
        				PERMISSIONGROUP.makeEditPanel();
        				PERMISSIONGROUP.makePermissionsModal();
        				PERMISSIONGROUP.markValid();
        				PERMISSIONGROUP.showNew();
        				if ( PERMISSIONGROUP.ansiModal != '' ) {
							$(".showNew").click();
						}
        			},
				
			
			clearAddForm : function () {
				$.each( $('#editPanel').find("input"), function(index, $inputField) {
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						PERMISSIONGROUP.markValid($inputField);
					}
				});
				$('.err').html("");
				$('#editPanel').data('rownum',null);
            },
            
        	
        	createTable : function (){

        		var dataTable = $('#permissionGroupTable').DataTable( {
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
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
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-head-left", "targets": [1,2] },
        	            { className: "dt-body-center", "targets": [0,3] },
        	            { className: "dt-right", "targets": []},
        	            { width: '10%', "targets": 0 },
        	            { width: '30%', "targets": 1 },
        	            { width: '35%', "targets": 2 },
        	            { width: '10%', "targets": [3,4,5] },
        	            { "sClass": "center", "targets": [4,5] }
        	            
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": "permissionGroupLookup",
			        	"type": "GET",
			        	"data": {}
			        	},
			        aaSorting:[1],
			        columns: [
			        	{ title: "ID", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
			            	if(row.permissionGroupId != null){return (row.permissionGroupId+"");}
			            } },
			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.name != null){return (row.name+"");}
			            } },
			            { title: "Description", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	if(row.description != null){return (row.description+"");}
			            } },
			            { title: "Status", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	var $isActive = '<webthing:checkmark styleClass="inputIsValid">Active</webthing:checkmark>';
			            	var $isNotActive = '<webthing:ban styleClass="inputIsInvalid">Inactive</webthing:ban>';
			            	if(row.permissionGroupStatus != null){
			            		if ( row.permissionGroupStatus == 1 ) {
			            			return $isActive;
			            		} else {
			            			return $isNotActive;
			            		}
			            	}
			            } },
			           { title: "User Count" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
			            	$userLink = '<a href="userLookup.html?id=' + row.permissionGroupId + '" style="color:#404040;">' + row.userCount+ '</a>';
			            	if(row.userCount != null){return $userLink;}
			            } },
			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
			            	$updateLink = '<ansi:hasPermission permissionRequired="PERMISSIONS_WRITE"><a href="#" class="updAction" data-id="'+row.permissionGroupId+'" data-name="'+row.name+'"><webthing:permissionIcon>Permissions</webthing:permissionIcon></ansi:hasPermission></a>';
			            	$editLink = '<ansi:hasPermission permissionRequired="PERMISSIONS_WRITE"><a href="#" class="editAction" data-id="'+row.permissionGroupId+'" data-name="'+row.name+'"><webthing:edit>Update</webthing:edit></a></ansi:hasPermission>';
			            	$deleteLink = '<ansi:hasPermission permissionRequired="PERMISSIONS_WRITE"><a href="#" class="delAction" data-id="'+row.permissionGroupId+'" data-name="'+row.name+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasPermission>';
			           		
			            	$action = $editLink + " " + $updateLink + " " + $deleteLink;
			            	$updates = $editLink + " " + $updateLink;
			            	if(row.userCount != null){
			            		if ( row.userCount >= 1 ) {
			            			return $updates;
			            		} else {
			            			return $action;
			            		}
			            	}			     
			            }  }],
			            "initComplete": function(settings, json) {
			            	//console.log(json);
			            	//doFunctionBinding();
			            },
			            "drawCallback": function( settings ) {
			            	PERMISSIONGROUP.doFunctionBinding();
			            }
			    } );
        	},
				
	   		deleteThisPermissionGroup : function ($permissionGroupId, $name) {
        		$("#deleteModal").attr("permissionGroupId", $permissionGroupId);
        		$("#deleteModal").dialog("option","title", " " + $name).dialog("open");
			},         

			deletePermissionGroup : function () {
				$permissionGroupId = $("#deleteModal").attr("permissionGroupId");
				var $url = 'permissionGroup/'+ $permissionGroupId;
				var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $url,            	     
            	    statusCode: {
            	    	 200: function($data) {
            	    		 	$("#deleteModal").dialog("close");
            	    		 	$('#permissionGroupTable').DataTable().ajax.reload();
		    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
							},

            	    		403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again");
							},
							404: function($data) {
								$("#globalMsg").html("Invalid Selection").show().fadeOut(100000);
							},
							500: function($data) {
								$("#globalMsg").html("System Error; Contact Support");
							}
	             	     },
            	     dataType: 'json'
            	});
			},
	
			doFunctionBinding : function () {
				$( ".editAction" ).on( "click", function($clickevent) {
					var $name = $(this).attr("data-name");
					PERMISSIONGROUP.showEdit($clickevent);
				});	
				$('.updAction').bind("click", function($clickevent) {
					$permissionGroupId = $(this).attr("data-id");
					var $name = $(this).attr("data-name");
					PERMISSIONGROUP.getTotalList($permissionGroupId, $name);
				});
				$(".delAction").on("click", function($clickevent) {
					var $name = $(this).attr("data-name");
					var $permissionGroupId = $(this).data("id");
					PERMISSIONGROUP.deleteThisPermissionGroup($permissionGroupId, $name);
				});
			},
						
			doPost : function ($permissionGroupId, $functionalArea, $newPermission){
				console.log("doPost");
				console.log($permissionGroupId + "\t" + $functionalArea + "\t" + $newPermission)
	    		var $url = 'permission/' + $permissionGroupId;
				//console.log("YOU PASSED ROW ID:" + $rowid);
				$outbound = {"functionalArea": $functionalArea, "permission": $newPermission};
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),				
					statusCode: {
						200: function($data) {
							if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
								$("#modalMessage").html($data.data.webMessages.GLOBAL_MESSAGE[0]).show().fadeOut(3000);
							} else {
								$("#modalMessage").html("Success!").show().fadeOut(3000);
							}
							
						},
						403: function($data) {
							$("permissionsModal").dialog("close");
							$("#globalMsg").html("Session Timeout. Log in and try again").show();
						},
						404: function($data) {
							$("permissionsModal").dialog("close");
							$("#globalMsg").html("Inconsistent System State. Reload and try again").show();
						},
						500: function($data) {
							$("permissionsModal").dialog("close");
							$("#globalMsg").html("System Error 500. Contact Support").show();
						},
					},
					dataType: 'json'
				});
	    	},	
			


			getTotalList : function ($permissionGroupId, $name) {
				$url = "permission/" + $permissionGroupId;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					height: 125,
					width: 450,
					data: {},
					statusCode: {
						200: function($data) {
							PERMISSIONGROUP.makeTable($permissionGroupId, $data.data);
							$("#permissionsModal").dialog("option","title","Set Permissions for " + $name).dialog("open");
						},					
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
						404: function($data) {
							$("#globalMsg").html("System Error Reorder 404. Contact Support");
						},
						500: function($data) {
							$("#globalMsg").html("System Error Reorder 500. Contact Support");
						}
					},
					dataType: 'json'
				});
			},
			
			
			
			
			
			makeEditPanel : function() {	
				$("#editPanel" ).dialog({
					autoOpen: false,
					height: 300,
					width: 500,
					modal: true,
					buttons: [
						{
							id: "closeEditPanel",
							click: function() {
								$("#editPanel").dialog( "close" );
							}
						},{
							id: "goEdit",
							click: function($event) {
								PERMISSIONGROUP.updatePermissionGroup();
							}
						}	      	      
					],
					close: function() {
						PERMISSIONGROUP.clearAddForm();
						$("#editPanel").dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				});
			},
			
			
			makeDeleteModal : function() {
			$( "#deleteModal" ).dialog({
				autoOpen: false,
				height: 150,
				width: 450,
				modal: true,
				closeOnEscape:true,
				buttons: [
					{
						id: "deleteSaveButton",
						click: function($event) {
							PERMISSIONGROUP.deletePermissionGroup();
						}
					},
					{
						id: "deleteCancelButton",
						click: function($event) {
							$( "#deleteModal" ).dialog("close");
						}
					},
				]
			});	
			$("#deleteSaveButton").button('option', 'label', 'Delete');
			$("#deleteCancelButton").button('option', 'label', 'Cancel');
			},
		
		
			makeDeletePermissionButton : function () {
				$(".deleteThisPermissionGroup").click(function($event) {
	        		//var $jobId = this.parentElement.attributes['data-jobid'].value;
	        		var $permissionGroupId = $(this).closest("div.panel-button-container")[0].attributes['data-permissionGroupId'].value;
	        		PERMISSIONGROUP.deleteThisPermissionGroup($permissionGroupId);
	        	});
			},
		
		
			
	    	makePermissionsModal : function() {
				$( "#permissionsModal" ).dialog({
					autoOpen: false,
					height: 450,
					width: 1224,
					modal: true,
					closeOnEscape:true,
					buttons: [
						{
							id: "permissionsCancelButton",
							click: function($event) {
								$( "#permissionsModal" ).dialog("close");
							}
						},
					]
				});	
				$("#permissionsCancelButton").button('option', 'label', 'Done');
	    	},	
			

	    	makeSelect : function($permissionGroupId, $functionalArea, $permissionList) {
	    		var $select = $("<select>");
	    		$select.addClass("permission-selector");
	    		$select.attr("data-functionalArea",$functionalArea);
	    		$select.attr("data-permissionGroupId",$permissionGroupId);
	    		$select.append(new Option("",""));
				$.each($permissionList, function(index, val) {
					//var $option = new Option(val["permissionName"], val["permissionName"]);
					var $option = $("<option>");
					$option.attr("value",val["permissionName"]);
					if ( val.included == true ) {
						$option.attr("selected","selected");	
					}
					$option.append(val["permissionName"]);
				    $select.append($option);
				});	    		
				var $html = $($select).prop('outerHTML');
	    		return $html;
	    	},
	    	
	    	
	    	makeTable : function ($permissionGroupId, $data) {
	    		console.log($data);
	    		$tableData = [];
	    		$.each($data.functionalAreas, function($key, $value) {
	    			var $row = {};
	    			$row["functionalArea"] = $value.permissionName;
	    			$row["description"] = $value.description;
	    			$row["permissionList"] = $data.permissionList[$value.permissionName];
	    			$tableData.push($row);
	    		});
	    		console.log($tableData);
	    		
	    		PERMISSIONGROUP.permissionTable = $("#permissionTable").DataTable( {
	    			data : $tableData,
	    			pageLength : 50,
	    			autoWidth : false,
        	        deferRender : true,
        	        destroy : true,		// this lets us reinitialize the table for different permission groups
	    			columns : [
	    				{ width:"225px", title:"Functional Area", className:"dt-head-left",	data:'functionalArea', orderable:true, },
	    				{ width:"550px", title:"Description",     className:"dt-head-left",	data:'description', 	 orderable:true, 	defaultContent: "<i>N/A</i>"},
	    				{ 
	    					width:"225px", 
	    					title:"Permission", 
	    					className:"dt-head-left",
	    					defaultContent:"<i>N/A</i>",
	    					data: function ( $row, $type, $set ) { 	
			            		return PERMISSIONGROUP.makeSelect($permissionGroupId, $row.functionalArea, $row.permissionList);
			            	}
	    				},	    				
	    			],
	    			"drawCallback": function( settings ) {
	    				$("#permissionTable_wrapper").addClass("maxWidth");
	    				$(".permission-selector").change(function($event) {
	    					var $that = $(this);
	    					var $permissionGroupId = $that.attr("data-permissiongroupid");
	    					var $functionalArea = $that.attr("data-functionalarea");
	    					var $newPermission = $that.val();
	    					PERMISSIONGROUP.doPost($permissionGroupId, $functionalArea, $newPermission);	    					
	    				});
		            },
	    		});
	    		
	    	},
	    	
	    	
	    	
	    	


			makeTableXXXX : function ($permissionGroupId, $data) {
				$("#permissionsModal").html('<div id="modalMessage" class="err"></div>');
				$("#permissionsModal").attr("data-permissionGroupId", $permissionGroupId);
				
				var $funcAreaTable = $("<table>");
				$funcAreaTable.attr("style","border:solid 1px #000000; margin-left:30px; margin-top:10px;margin-bottom:10px;");
				
				var $rowNum = 0;
				
				$.each($data.permissionList, function($index, $value) {
					var $funcAreaTR = $("<tr>");
					var $funcAreaTD = $("<td>");
					$funcAreaTD.attr("class","funcarea");
					$funcAreaTD.attr("data-id",$value[0].permissionName);
					$funcAreaTD.append($value[0].permissionName);
					
					$funcAreaTR.append($funcAreaTD);    
					
					$.each($data.permissionList, function($permIdx, $permValue) {
						var $permTD = $("<td>");
						var $myColumn = $rowNum+1;
						if ( $data.permissionList[$permIdx].length == 2 ) {
							// handle groups with only 1 option
							$myColumn = $rowNum;
						} 
						var $myPermission = $data.permissionList[$permIdx][$myColumn];
						if ($myPermission == null ) {
							$permTD.append("&nbsp;");
						}else{						
							$permTD.append($myPermission.permissionName);
							$permTD.addClass("perm" + $myPermission.permissionName);
							$permTD.addClass($data.permissionList[$permIdx][0].permissionName);
							$permTD.addClass("perm");
							$permTD.attr("data-permissionname", $myPermission.permissionName);
							$permTD.attr("data-funcarea", $data.permissionList[$permIdx][0].permissionName);
							if ($myPermission.included == true) {
								$permTD.addClass("hilite");
							}
						}
						
						$funcAreaTR.append($permTD);
											
					});
					
					
					$funcAreaTable.append($funcAreaTR);
					$rowNum++;
				});
				
				$("#permissionsModal").append($funcAreaTable);
			},

            
			markValid : function ($inputField) {
            	$fieldName = $($inputField).attr('name');
            	$fieldGetter = "input[name='" + $fieldName + "']";
            	$fieldValue = $($fieldGetter).val();
            	$valid = '#' + $($inputField).data('valid');
	            var re = /.+/;	            	 
            	if ( re.test($fieldValue) ) {
            		$($valid).removeClass("fa");
            		$($valid).removeClass("fa-ban");
            		$($valid).removeClass("inputIsInvalid");
            		$($valid).addClass("far");
            		$($valid).addClass("fa-check-square");
            		$($valid).addClass("inputIsValid");
            	} else {
            		$($valid).removeClass("far");
            		$($valid).removeClass("fa-check-square");
            		$($valid).removeClass("inputIsValid");
            		$($valid).addClass("fa");
            		$($valid).addClass("fa-ban");
            		$($valid).addClass("inputIsInvalid");
            	}
            },
				
					
            showEdit : function ($clickevent) {
            	
            	$name = $("#editModal").attr("data-name");
		        var $name = $(this).attr("data-name");
				var $permissionGroupId = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("permissionGroupId: " + $permissionGroupId);
				$("#goEdit").data("permissionGroupId: " + $permissionGroupId);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeEditPanel').button('option', 'label', 'Close');
        		
        		
				var $url = 'permissionGroup/'+ $permissionGroupId;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							var $permissionGroup = $data.data.permGroupItemList[0];
							$.each($permissionGroup, function($fieldName, $value) {									
								$selector = "#editPanel input[name=" + $fieldName + "]";
								if ( $($selector).length > 0 ) {
									$($selector).val($value);
								}
	        				});
							$("#editPanel input[name='permissionGroupId']").val($permissionGroup.permissionGroupId);
							$("#editPanel input[name='name']").val($permissionGroup.name);
							$("#editPanel input[name='description']").val($permissionGroup.description);
							$("#editPanel input[name='status']").val($permissionGroup.status);			        		
			        		$("#editPanel .err").html("");
			        		$("#editPanel").dialog("option","title", "Edit Permission Group").dialog("open");
						},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
						404: function($data) {
							$("#globalMsg").html("Invalid Request");
						},
						500: function($data) {
							$("#globalMsg").html("System Error; Contact Support");
						}
					},
					dataType: 'json'
				});
			},

				
			showNew : function () {
			$(".showNew").click(function($event) {
				$('#goEdit').data("permissionGroupId",null);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeEditPanel').button('option', 'label', 'Close');
        		
 //       		$("#editPanel display[name='']").val("");
				$("#editPanel input[name='Name']").val("");
				$("#editPanel input[name='Description']").val("");
				$("#editPanel input[name='Status']").val("");			        		
        		$("#editPanel .err").html("");
        		$("#editPanel").dialog("option","title", "New Permission Group").dialog("open");
			});
			},
				

			updatePermissionGroup : function () {
				console.debug("Updating Permissions");
				var $permissionGroupId = $("#editPanel input[name='permissionGroupId']").val();
				console.debug("permissionGroupId: " + $permissionGroupId);
				

				if ( $permissionGroupId == null || $permissionGroupId == '') {
					$url = 'permissionGroup/add';
				} else {
					$url = 'permissionGroup/' + $permissionGroupId;
				}
				console.debug($url);
					
				var $outbound = {};
				$outbound['name'] = $("#editPanel input[name='name']").val();
				$outbound['description'] = $("#editPanel input[name='description']").val();
				$outbound['status'] = $("#editPanel select[name='status']").val();		        		
				console.debug($outbound);
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					statusCode: {
						200: function($data) {
		    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
		    					$.each($data.data.webMessages, function (key, value) {
		    						var $selectorName = "#" + key + "Err";
		    						$($selectorName).show();
		    						$($selectorName).html(value[0]).fadeOut(10000);
		    					});
		    				} else {	    				
		    					$("#editPanel").dialog("close");
		    					$('#permissionGroupTable').DataTable().ajax.reload();		
		    					PERMISSIONGROUP.clearAddForm();		    					
		    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
		    				}
						},
						403: function($data) {
							$("#globalMsg").html("Session Timeout. Log in and try again");
						},
						404: function($data) {
							$("#globalMsg").html("Invalid Selection").show().fadeOut(100000);
						},
						500: function($data) {
							$("#globalMsg").html("System Error; Contact Support");
						}
					},
					dataType: 'json'
				});
			},
				
				
        	}

            PERMISSIONGROUP.init();
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Permissions</h1>
	 	<table id="permissionGroupTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	    </table>
	    
	    <input type="button" class="prettyWideButton showNew" value="New" />
	    
	    <div id="editPanel">
	    <div class="modal-header">
	    <h5 class="modal-title" id="name"></h5>
	    </div>
    	<table>
    		<tr>
    			<td><span class="formHdr">ID</span></td>
    			<td><input type="text" name="permissionGroupId" style="border-style: hidden" readOnly/></td>
    			<td><span class="err" id="permissionGroupIdErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Name</span></td>
    			<td></i><input type="text" name="name" /></td>
    			<td><span class="err" id="nameErr"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formHdr">Description</span></td>
    			<td><input type="text" name="description" /></td>
    		</tr>
    		<tr>
				<td><span class="required">*</span><span class="formLabel">Status:</span></td>
				<td>
					<select name="status">
						<option value="1">Active</option>
						<option value="0">Inactive</option>
					</select>
					<i class="fa fa-check-square-o inputIsValid" aria-hidden="true"></i>
				</td>
				<td><span class="err" id="statusErr"></span></td>
			</tr> 		
    	</table>
    </div>
    	
    	<div id="deleteModal">
    		Are You Sure You Want to Delete this Permission Group?
	    	</div>
	    	
	    	
	    <div id="permissionsModal">
	    	<div id="modalMessage-wrapper">
				<span id="modalMessage" class="err"></span>
			</div>
			<div id="modalMessage" class="err"></div>
    		<table id="permissionTable"></table>
		</div>
   
	    <webthing:scrolltop />

    </tiles:put>
		
</tiles:insert>