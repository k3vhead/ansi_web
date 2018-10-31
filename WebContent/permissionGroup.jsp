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
			#displayTable {
				width:100%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:350px;
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
			.print-link {
				cursor:pointer;
			}
			.editJob {
				cursor:pointer;
				text-decoration:underline;
			}
			.jobLink {
				color:#000000;
			}
			.overrideAction {
				cursor:pointer;
				text-decoration:none;
				color:#000000;
			}
			#editPanel {
				display:none;
			}
			.formHdr {
				font-weight:bold;				
			}
			.editAction {
				cursor:pointer;
			}
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:350px;
				text-align:center;
				padding:15px;
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	var $ansiModal = '<c:out value="${ANSI_MODAL}" />';
        	
        
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
				return false;
       	    });
			
			
			$(".showNew").click(function($event) {
				$('#goEdit').data("permissionGroupId",null);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeEditPanel').button('option', 'label', 'Close');
        		
 //       		$("#editPanel display[name='']").val("");
				$("#editPanel input[name='Name']").val("");
				$("#editPanel input[name='Description']").val("");
				$("#editPanel input[name='Status']").val("");			        		
        		$("#editPanel .err").html("");
        		$("#editPanel").dialog("open");
			});
			
			


        	var dataTable = null;
        	
        	function createTable(){

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
        	            { className: "dt-right", "targets": []}
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
			            	$updateLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="addNewAction" data-id="'+row.permissionGroupId+'"><webthing:permissionIcon>Permissions</webthing:permissionIcon></ansi:hasWrite></ansi:hasPermission></a>';
			            	$editLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="editAction" data-id="'+row.permissionGroupId+'"><webthing:edit>Update</webthing:edit></a></ansi:hasWrite></ansi:hasPermission>';
			            	$deleteLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="delAction" data-id="'+row.permissionGroupId+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasWrite></ansi:hasPermission>';
			           		
			            	$action = $editLink + " " + $deleteLink + " " + $updateLink;
			            	$updates = $editLink + " " + $updateLink;
			            	if(row.userCount != null){
			            		if ( row.userCount >= 1 ) {
			            			return $updates;
			            		} else {
			            			return $action;
			            		}
			            	}			            	
			            				            	
			            	
//			            	$action = $editLink + " " + $deleteLink + " " + $updateLink;
			            	//if(row.count < 1) {
			            	//	$action = $action + " " + $deleteLink;
			            	//}				            	
//			            	return $action;	
			            }  }],
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
				
			function doFunctionBinding() {
				$( ".editAction" ).on( "click", function($clickevent) {
					showEdit($clickevent);
				});	
				$('.updAction').bind("click", function($clickevent) {
					doUpdate($clickevent);
				});
				$(".delAction").on("click", function($clickevent) {
					var $permissionGroupId = $(this).data("id");
					deleteThisPermissionGroup($permissionGroupId);
				});
				//$(".editJob").on( "click", function($clickevent) {
				//	console.debug("clicked a job")
				//	var $jobId = $(this).data("jobid");
				//	location.href="jobMaintenance.html?id=" + $jobId;
				//});
			}
				
				$("#editPanel" ).dialog({
					title:'Edit Group Permissions',
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
								updatePermissionGroup();
							}
						}	      	      
					],
					close: function() {
						clearAddForm();
						$("#editPanel").dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				});
				

				
				function updatePermissionGroup() {
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
			    					clearAddForm();		    					
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
				}
				
				
				function clearAddForm() {
					$.each( $('#editPanel').find("input"), function(index, $inputField) {
						$fieldName = $($inputField).attr('name');
						if ( $($inputField).attr("type") == "text" ) {
							$($inputField).val("");
							markValid($inputField);
						}
					});
					$('.err').html("");
					$('#editPanel').data('rownum',null);
	            }
	            
	            function markValid($inputField) {
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
	            }
				
	            
				

					
/*	            $("#deleteModal" ).dialog({
					title:'Are you sure you want to DELETE this Permission Group?',
					autoOpen: false,
					height: 300,
					width: 300,
					modal: true,
					buttons: [
						{
							id: "closeDeleteModal",
							click: function() {
								$("#deleteModal").dialog( "close" );
							}
						},{
							id: "doDelete",
							click: function() {
								$("#deleteModal").dialog("close");
							}
						}	      	      
					],
					close: function() {
						$("#deleteModal").dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				}); */
				
				
//				$("#cancelDelete").click( function($event) {
//	            	$event.preventDefault();
//	            	$('#deleteModal').dialog("close");
//	            });   
            
            
	            
				
//				function doDelete($clickevent) {
//					$clickevent.preventDefault();
//	            	var $permissionGroupId = $clickevent.currentTarget.attributes['data-id'].value;
//					$('#confirmDelete').data('delPermissionGroupId', $permissionGroupId);
//	             	$('#confirmDelete').bPopup({
//						modalClose: false,
//						opacity: 0.6,
//						positionStyle: 'fixed' 
//					});
//				}
				
				
				
//				$("#cancelDelete").click( function($event) {
//	            	$event.preventDefault();
//	            	$('#confirmDelete').bPopup().close();
//	            });     	            



			function deletePermissionGroup () {
				$permissionGroupId = $("#deleteModal").attr("permissionGroupId");
				var $url = 'permissionGroup/'+ $permissionGroupId;
				var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $url,
//            	    data: $outbound,
/*	            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							$("#permissionGroupTable").DataTable().row($permissionGroupId).remove();
							$("#permissionGroupTable").DataTable().draw();
//							$('#confirmDelete').bPopup().close();
						}
            	     },
*/	            	     statusCode: {
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
            //}
				
				
				
				
				
				//var $outbound = {};
				//$permissionGroupId = $("#deleteModal").attr("permissionGroupId");
				//$outbound ["permissionGroupId"] = $permissionGroupId;
			//	$outbound ["action"] = "DELETE_PERMISSIONGROUP";
				//$(".permissionGroupId").remove();
				
//				doPermissionGroupUpdate($permissionGroupId, $outbound, deletePermissionGroupSuccess, deletePermissionGroupErr);
			}
			
/*			function doPermissionGroupUpdate ($permissionGroupId, $outbound, $successCallback, $errCallback) {
				var $url = "permissionGroup/" + $permissionGroupId;
				console.log($outbound);						
				
				var jqxhr3 = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					statusCode: {
						200:function($data) {
						//	QUOTEMAINTENANCE.populateJobHeader($data.data.jobHeaderList)
						//	QUOTEMAINTENANCE.makeJobExpansion();
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
			} */

			function deletePermissionGroupErr ($statusCode) {
				var $messages = {
						403:"Session Expired. Log in and try again",
						404:"System Error Activate 404. Contact Support",
						500:"System Error Activate 500. Contact Support"
				}
				$("#deleteModal").dialog("close");
				$("#globalMsg").html( $messages[$statusCode] );
			}
			
			function deletePermissionGroupSuccess ($data) {
				console.log($data);
				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {	
					$.each($data.data.webMessages, function($index, $val) {								
						var $fieldName = "." + $index + "Err";
						var $selector = "#deleteModal " + $fieldName;
						$($selector).html($val[0]).show().fadeOut(3000);
					});							
				} else {
					permissionGroup = $data.data.permissionGroupId;
					// call this:  populateJobPanel : function($jobId, $destination, $data
					// var $destination = "#job" + $data.data.job.jobId + " .job-data-row";
					//QUOTEMAINTENANCE.populateJobPanel($data.data.job.jobId, $destination, $data.data);
					//QUOTEMAINTENANCE.populateJobHeader($data.quoteList[0].jobHeaderList)
					$("#deleteModal").dialog("close");
					$("#globalMsg").html("Permission Group Deleted").show().fadeOut(3000);
				}
			}
			
			
			function deleteThisPermissionGroup ($permissionGroupId, $type) {
        		console.log("clicked a permissionGroup deleteThisPermissionGroup: " + $permissionGroupId);
        		$("#deleteModal").attr("permissionGroupId", $permissionGroupId);
        		$("#deleteModal").dialog("open");
			}
			
			
			$( "#deleteModal" ).dialog({
				title:'Delete Permission Group',
				autoOpen: false,
				height: 125,
				width: 450,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "deleteCancelButton",
						click: function($event) {
							$( "#deleteModal" ).dialog("close");
						}
					},
					{
						id: "deleteSaveButton",
						click: function($event) {
							deletePermissionGroup();
						}
					}
				]
			});	
			$("#deleteSaveButton").button('option', 'label', 'Delete');
			$("#deleteCancelButton").button('option', 'label', 'Cancel');
			
			
			//if ( $value.canDelete == true ) {
			//		$panelButtonContainer.append('<webthing:delete styleClass="deleteThisPermissionGroup">Delete</webthing:delete>');
			//	}
		
		
			$(".deleteThisPermissionGroup").click(function($event) {
        		//var $jobId = this.parentElement.attributes['data-jobid'].value;
        		var $permissionGroupId = $(this).closest("div.panel-button-container")[0].attributes['data-permissionGroupId'].value;
        		deleteThisPermissionGroup($permissionGroupId);
        	});
			
			
			
			
			


			
	/*		$("#cancelDelete").click( function($event) {
            	$event.preventDefault();
            	$('#confirmDelete').bPopup().close();
            });         

            $("#doDelete").click(function($event) {
            	$event.preventDefault();
            	var $tableData = [];
                $("#displayTable").find('tr').each(function (rowIndex, r) {
                    var cols = [];
                    $(this).find('th,td').each(function (colIndex, c) {
                        cols.push(c.textContent);
                    });
                    $tableData.push(cols);
                });

            	var $rownum = $('#confirmDelete').data('rownum');
            	//var $tableName = $tableData[$rownum][0];
            	//var $fieldName = $tableData[$rownum][1];
            	//var $value = $tableData[$rownum][2];
            	var $tableName = $('#confirmDelete').data('tableName');
            	var $fieldName = $("#confirmDelete").data('fieldName');
            	var $value = $("#confirmDelete").data('value');
            	$outbound = JSON.stringify({});            	
            	$url = 'code/' + $tableName + "/" + $fieldName + "/" + $value;
            	//$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $url,
            	    data: $outbound,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							//$rowfinder = "tr:eq(" + $rownum + ")"
							//$("#displayTable").find($rowfinder).remove();
			                $("#displayTable").find('tr').each(function (rowIndex, r) {
			                    var cols = [];
			                    $(this).find('th,td').each(function (colIndex, c) {
			                        cols.push(c.textContent);
			                    });
			                    if ( cols[0] == $tableName ) {
			                    	if ( cols[1] == $fieldName ) {
			                    		if ( cols[2] == $value ) {
			                    			this.remove();
			                    		}
			                    	}
			                    }
			                });
							$('#confirmDelete').bPopup().close();
						}
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
            	    	}, 
	         	    	404: function($data) {
	         	    		$('#confirmDelete').bPopup().close();
	         	    		$("#globalMsg").html("Record does not exist").fadeIn(10).fadeOut(6000);
	        	    	} 
            	     },
            	     dataType: 'json'
            	});
            });	



			var confirmDelete = function (permissionGroupId) {
				$("#permissionGroupId").val(permissionGroupId);
				$("#deleteModal").modal('show');
			}
			
			var deletePermissionGroup = function () {
				$("#deleteSomethingElse").show();
				var empId = $("#permissionGroupId").val();
				
				$.ajax({
					
					type: "POST",
					url:  'permissionGroup/'+ $permissionGroupId;
					data: { permissionGroup: permissionGroupId },
					success: function () {
						$("#deleteSomethingElse").hide();
						$("#deleteModal").modal("hide");
						$("row_" + permissionGroupId).remove();
					}
				})
			}
*/











			
				
					
	            function showEdit($clickevent) {
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
								//console.log($data);
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
				        		$("#editPanel").dialog("open");
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
				}
				
				if ( $ansiModal != '' ) {
					$(".showNew").click();
				}
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Permissions</h1>
	 	<table id="permissionGroupTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:5%;" />
	    		<col style="width:30%;" />
	    		<col style="width:35%;" />
	    		<col style="width:10%;" />
	    		<col style="width:10%;" />
	    		<col style="width:10%;" />
	    	</colgroup>
	        <thead>
	            <tr>
	                <th>ID</th>
	    			<th>Name</th>
	    			<th>Description</th>
	    			<th>Status</th>
	    			<th>User Count</th>
	    			<th>Action</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>ID</th>
	    			<th>Name</th>
	    			<th>Description</th>
	    			<th>Status</th>
	    			<th>User Count</th>
	    			<th>Action</th>
	            </tr>
	        </tfoot>
	    </table>
	    
	    <input type="button" class="prettyWideButton showNew" value="New" />
	    
	    <div id="editPanel">
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
   
	    <webthing:scrolltop />

    </tiles:put>
		
</tiles:insert>