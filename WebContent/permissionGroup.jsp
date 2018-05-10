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
			            	var $isActive = '<i class="fa fa-check-square-o inputIsValid" aria-hidden="true"></i>'
			            	var $isNotActive = '<i class="fa fa-ban inputIsInvalid" aria-hidden="true"></i>'
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
			            	$updateLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="addNewAction" data-id="'+row.permissionGroupId+'"><webthing:addNew>Permissions</webthing:addNew></ansi:hasWrite></ansi:hasPermission></a>';
			            	$editLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="editAction" data-id="'+row.permissionGroupId+'"><webthing:edit>Update</webthing:edit></a></ansi:hasWrite></ansi:hasPermission>';
			            	$deleteLink = '<ansi:hasPermission permissionRequired="SYSADMIN"><ansi:hasWrite><a href="#" class="delAction" data-id="'+row.permissionGroupId+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasWrite></ansi:hasPermission>';
			           			
			            				            	
			            	
			            	$action = $editLink + " " + $deleteLink + " " + $updateLink;
			            	//if(row.count < 1) {
			            	//	$action = $action + " " + $deleteLink;
			            	//}				            	
			            	return $action;	
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
				$('.delAction').bind("click", function($clickevent) {
					doDelete($clickevent);
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
					width: 300,
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
						$("#editPanel").dialog( "close" );
						//allFields.removeClass( "ui-state-error" );
					}
				});
				
				
				function updatePermissionGroup() {
					console.debug("Updating Permissions");
					var $permissionGroupId = $("#goEdit").data("permissionGroupId");
					console.debug("permissionGroupId: " + $permissionGroupId);
					
					if ( $permissionGroupId == null || $permissionGroupId == '') {
						$url = 'permissionGroup/add';
					} else {
						$url = 'permissionGroup/' + $permissionGroupId;
					}
					console.debug($url);
						
					var $outbound = {};
					$outbound['permissionGroupId'] = $("#editPanel input[name='permissionGroupId']").val();
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
			    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
			    					$('#permissionGroupTable').DataTable().ajax.reload();
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
				
				
/*				function doDelete($clickevent) {
					$clickevent.preventDefault();
					var $permissionGroupId = $clickevent.currentTarget.attributes['data-row'].value;
	            	var $tableData = [];
	                $("#permissionGroupTable").find('tr').each(function (rowIndex, r) {
	                    var cols = [];
	                    $(this).find('th,td').each(function (colIndex, c) {
	                        cols.push(c.textContent);
	                    });
	                    $tableData.push(cols);
	                });
	            	$("#delPermissionGroupId").html($tableData[$rownum][0]);
	            	$("#delName").html($tableData[$rownum][1]);
	            	$("#delDescription").html($tableData[$rownum][2]);
	            	$("#delStatus").html($tableData[$rownum][3]);
	            	$("#delUserCount").html($tableData[$rownum][4]);

					$('#confirmDelete').data('rownum',$rownum);
	             	$('#confirmDelete').bPopup({
						modalClose: false,
						opacity: 0.6,
						positionStyle: 'fixed' //'fixed' or 'absolute'
					});
				}				
				
				$("#cancelDelete").click( function($event) {
	            	$event.preventDefault();
	            	$('#confirmDelete').bPopup().close();
	            });         

	            $("#doDelete").click(function($event) {
	            	$event.preventDefault();
	            	var $tableData = [];
	                $("#permissionGroupTable").find('tr').each(function (rowIndex, r) {
	                    var cols = [];
	                    $(this).find('th,td').each(function (colIndex, c) {
	                        cols.push(c.textContent);
	                    });
	                    $tableData.push(cols);
	                });

	                var $rownum = $('#confirmDelete').data('rownum');
	            	var $permissionGroupId = $tableData[$rownum][0];
	            	var $name = $tableData[$rownum][1];
	            	var $description = $tableData[$rownum][2];
	            	var $status = $tableData[$rownum][3];
	            	var $userCount= $tableData[$rownum][4];
	            	
	            	$outbound = JSON.stringify({});
	            	$url = 'permissionGroup/' + $permissionGroupId;
	            	
	            	var jqxhr = $.ajax({
	            	    type: 'delete',
	            	    url: $url,
	            	    data: $outbound,
	            	    success: function($data) {
	            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								$rowfinder = "tr:eq(" + $rownum + ")"
								$("#permissionGroupTable").find($rowfinder).remove();
								$('#confirmDelete').bPopup().close();
							}
	            	     },
	            	     statusCode: {
	             	    	403: function($data) {
	             	    		$('#confirmDelete').bPopup().close();
	             	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
	             	    	}, 
		         	    	404: function($data) {
		         	    		$('#confirmDelete').bPopup().close();
		         	    		$("#globalMsg").html("Record does not exist").fadeIn(10).fadeOut(6000);
	             	    	},
	             	    	500: function($data) {
	             	    		$('#confirmDelete').bPopup().close();
	             	    		$("#globalMsg").html("Unhandled Exception").fadeIn(10).fadeOut(6000);
	             	    	} 
	             	     },
	             	     dataType: 'json'
	             	});
	             });
		
				
			
*/	






				function doDelete($clickevent) {
					$clickevent.preventDefault();
//	            	var $permissionGroupId = $clickEvent.currentTarget.attributes['data-id'].value;
//					$('#confirmDelete').data('permissionGroup', $permissionGroupId);
	             	$('#confirmDelete').bPopup({
						modalClose: false,
						opacity: 0.6,
						positionStyle: 'fixed' 
					});
				}
				
				
				
				$("#cancelDelete").click( function($event) {
	            	$event.preventDefault();
	            	$('#confirmDelete').bPopup().close();
	            });         

	            $("#doDelete").click(function($event) {
	            	$event.preventDefault();
	            	var $permissionGroupId = $clickEvent.currentTarget.attributes['data-id'].value;
					var $data_id = $('#confirmDelete').data('permissionGroupId');
					var $deleteUrl = 'permissionGroup/' + $permissionGroupId;
					$outbound = JSON.stringify({'permissionGroup':$permissionGroupId});

				
	            	var jqxhr = $.ajax({
	            	    type: 'delete',
	            	    url: $deleteUrl,
	            	    data: $outbound,
	            	    success: function($data) {
	            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
							if ( $data.responseHeader.responseCode == 'SUCCESS') {
								$("#permissionGroupTable").DataTable().row($rowid).remove();
								$("#permissionGroupTable").DataTable().draw();
								$('#confirmDelete').bPopup().close();
							}
	            	     },
	            	     statusCode: {
	            	    	403: function($data) {
	            	    		$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
	            	    	} 
	            	     },
	            	     dataType: 'json'
	            	});
	            });
			
				
					
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
								var $permissionGroup = $data.data.permissionGroupLookup[0];
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
    			<td><input type="text" name="permissionGroupId" /></td>
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
    	
    	<div id="confirmDelete">
	    		<span class="formLabel">Are You Sure You Want to Delete this Permission Group?</span><br />
	    		<table id="delData">
	    			<tr>
	    				<td><span class="formLabel">Permission Group ID:</span></td>
	    				<td id="delPermissionGroupId"></td>
	    			</tr>
	    		</table>
	    		<input type="button" id="cancelDelete" value="<bean:message key="field.label.no" />" />
	    		<input type="button" id="doDelete" value="<bean:message key="field.label.yes" />" />
	    	</div>
   
	    <p align="center">
	    	<br>
	    	<a href="#" title="Scroll to Top" class="ScrollTop"><bean:message key="field.label.scrollToTop" /></a>
	    	</br>
	    </p>

    </tiles:put>
		
</tiles:insert>