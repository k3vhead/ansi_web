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
        <bean:message key="field.label.userLookupTitle" />
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#displayTable {
				width:100%;
			}
			#user-form-modal {
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
			.print-link {
				cursor:pointer;
			}
			.edit-link {
				cursor:pointer;
			}
			.dataTables_wrapper {
				padding-top:10px;
			}
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	
        	;USERLOOKUP = {
        		permissionGroupId : '<c:out value="${ANSI_PERMISSION_GROUP_ID}" />',
        		permissionGroupName : '<c:out value="${ANSI_PERMISSION_GROUP_NAME}" />',
        		datatable : null,
        		
        		init : function() {
        			USERLOOKUP.enableClicks();
        			USERLOOKUP.createTable();
        			USERLOOKUP.makeModal();
        			USERLOOKUP.initSelects();
        		},
        		
        		
        		clearEditForm : function() {
        			$("#addForm input").val("");
        			$("#addForm select[class='userField']").val("");
        			$("#addForm .errField").html("");
        			$("#addForm .displayField").html("");
        		},
        		
        		
        		doFunctionBinding : function() {
					$('.edit-link').click(function() {
        				var $userId = $(this).data("id");
        				USERLOOKUP.getUser($userId);
        			});
				},
				
				
        		enableClicks : function() {
        			$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
        				return false;
               	    });
        			
        			$("#clearFilter").click(function($event) {
        				USERLOOKUP.permissionGroupId='';
        				USERLOOKUP.permissionGroupName='';
        				$("#filterLabel").html('');
        				USERLOOKUP.dataTable.destroy();
        				USERLOOKUP.createTable();
        			});
        			
        			$("#newUserButton").click(function($event) {
        				USERLOOKUP.clearEditForm();
        				$("#user-form-modal").attr("data-updatetype","add");
        				$("#user-form-modal").dialog("open");
        			});
        		},
        		
        		
        		createTable : function() {
            		USERLOOKUP.dataTable = $('#userTable').DataTable( {
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
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {USERLOOKUP.doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-head-left", "targets": [1,2,3,4,5,6] },
            	            { className: "dt-body-center", "targets": [0,7,8] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "userLookup",
    			        	"type": "GET",
    			        	"data": {"permissionGroupId":USERLOOKUP.permissionGroupId}
    			        	},
    			        aaSorting:[1],
    			        columns: [
    			        	{ title: "<bean:message key="field.label.userId" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.userId != null){return (row.userId+"");}
    			            } },
    			            { title: "<bean:message key="field.label.lastName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.lastName != null){return (row.lastName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.firstName" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.firstName != null){return (row.firstName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.email" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.email != null){return (row.email+"");}
    			            } },
    			            { title: "<bean:message key="field.label.phone" />" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.phone != null){return (row.phone+"");}
    			            } },
    			            { title: "<bean:message key="field.label.cityUL" />", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.city != null){return (row.city+", " + row.state);}
    			            } },
    			            { title: "<bean:message key="field.label.permissionGroupName" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.permissionGroupName != null){return (row.permissionGroupName+"");}
    			            } },
    			            { title: "<bean:message key="field.label.status" />",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	var status = '<span style="font-style:italic;">N/A</span>';    			            	
    			            	if(row.userStatus != null){
    			            		if ( row.userStatus == 1 ) {
    			            			status = '<webthing:checkmark>Active</webthing:checkmark>';
    			            		}
    			            		if ( row.userStatus == 0 ) {
    			            			status = '<webthing:ban>Inactive</webthing:ban>';
    			            		}
    			            		if ( row.userStatus == -1 ) {
    			            			status = '<webthing:lock>Locked</webthing:lock>';
    			            		}
    			            	}
    			            	return status;
    			            } },
    			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	//console.log(row);
   				            	var $editLink = '<span class="edit-link" data-id="' + row.userId + '"><webthing:edit>Edit</webthing:edit></span>';
   				            	$actionData = "<ansi:hasPermission permissionRequired='USER_ADMIN_WRITE'>" + $editLink + "</ansi:hasPermission>"
    			            	return $actionData;
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	//console.log(json);
    			            	//USERLOOKUP.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	USERLOOKUP.doFunctionBinding();
    			            }
    			    } );
            	},
            	
            	
            	displayEditErrors : function($data) {
            		$.each($data.data.webMessages, function(index, val) {
            			var $errField = "#addForm .errField." + index;						
            			$($errField).html(val[0]);
					});
            	},
            	
            	
            	
            	initSelects : function() {
            		var $select = $("#user-form-modal select[name='status']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$select.append(new Option("Active", "1"));
					$select.append(new Option("Inactive", "0"));
					$select.append(new Option("Locked", "-1"));
					
					
					
					var $url = "permissionGroup/list";
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								var $select = $("#user-form-modal select[name='permissionGroupId']");
								$('option', $select).remove();
								$select.append(new Option("",""));
								$.each($data.data.permGroupItemList, function(index, val) {
									if ( val.status == 1 ) {
								    	//$select.append(new Option(val.name + "(" + val.description + ")", val.permissionGroupId));
										$select.append(new Option(val.name, val.permissionGroupId));
									}
								});
							},					
							403: function($data) {
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							}
						},
						dataType: 'json',
						async:false
					});
            	},
            	
            	
            	makeModal : function() {
            		$( "#user-form-modal" ).dialog({
						title:'Edit User',
						autoOpen: false,
						height: 500,
						width: 700,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "user-cancel-button",
								click: function($event) {
									$( "#user-form-modal" ).dialog("close");
								}
							},
							{
								id: "user-save-button",
								click: function($event) {
									USERLOOKUP.saveUser();
								}
							}
						]
					});	
					$("#user-save-button").button('option', 'label', 'Save');
					$("#user-cancel-button").button('option', 'label', 'Cancel');
            	},
            	
            	
            	
            	getUser : function($userId) {
            		var $url = "user/" + $userId
            		var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: null,
						statusCode: {
							200: function($data) {								
								USERLOOKUP.populateModal($data)								
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
            	
            	
            	populateModal : function($data) {
            		USERLOOKUP.clearEditForm();
            		var $user = $data.data.userList[0];
            		$("#addForm input[name='address1']").val($user.address1);
            		$("#addForm input[name='address2']").val($user.address2);
            		$("#addForm input[name='city']").val($user.city);
            		$("#addForm input[name='state']").val($user.state);
            		$("#addForm input[name='zip']").val($user.zip);
            		$("#addForm input[name='email']").val($user.email);
            		$("#addForm input[name='firstName']").val($user.firstName);
            		$("#addForm input[name='lastName']").val($user.lastName);
            		$("#addForm input[name='password']").val("");
            		$("#addForm select[name='permissionGroupId']").val($user.permissionGroupId);
            		$("#addForm input[name='phone']").val($user.phone);
            		$("#addForm select[name='status']").val($user.userStatus);
            		$("#addForm input[name='title']").val($user.title);
            		$("#addForm input[name='userId']").val($user.userId);
            		$("#user-form-modal").dialog("open");
            	},
            	
            	
            	saveUser : function() {
            		if ( $("#addForm input[name='userId']").val() == null || $("#addForm input[name='userId']").val() == "") {
	            			$url = "user/add";
	            	} else {
	            		$url = "user/" + $("#addForm input[name='userId']").val()
	            	}
            		
            		$outbound = {}
            		$outbound['address1'] = $("#addForm input[name='address1']").val();
            		$outbound['address2'] = $("#addForm input[name='address2']").val();
            		$outbound['city'] = $("#addForm input[name='city']").val();
            		$outbound['state'] = $("#addForm input[name='state']").val();
            		$outbound['zip'] = $("#addForm input[name='zip']").val();
            		$outbound['email'] = $("#addForm input[name='email']").val();
            		$outbound['firstName'] = $("#addForm input[name='firstName']").val();
            		$outbound['lastName'] = $("#addForm input[name='lastName']").val();
            		$outbound['password'] = $("#addForm input[name='password']").val();
            		$outbound['permissionGroupId'] = $("#addForm select[name='permissionGroupId']").val();
            		$outbound['phone'] = $("#addForm input[name='phone']").val();
            		$outbound['status'] = $("#addForm select[name='status']").val();
            		$outbound['title'] = $("#addForm input[name='title']").val();
            		$outbound['userId'] = $("#addForm input[name='userId']").val();
            		
            		var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200: function($data) {								
								if ( $data.responseHeader.responseCode == "SUCCESS") {
									$("#user-form-modal").dialog("close");
			    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
			    					$('#userTable').DataTable().ajax.reload();
								} else {
									USERLOOKUP.displayEditErrors($data);
								}
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
        	};
        	
        	USERLOOKUP.init();

        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.user" /><bean:message key="menu.label.lookup" /></h1>    	
    	<c:if test="${not empty ANSI_PERMISSION_GROUP_NAME}">
    		<div id="filterLabel">
	    		<span class="orange"><bean:message key="field.label.permissionGroupFilter" />: <c:out value="${ANSI_PERMISSION_GROUP_NAME}" /></span>
	    		<span id="clearFilter"><i class="fa fa-ban inputIsInvalid this_is_a_link" aria-hidden="true"></i></span><br />
    		</div>
    	</c:if>


	 	<table id="userTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:5%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:5%;" />
	    		<col style="width:8%;" />
	    	</colgroup>
	        <thead>
	            <tr>
	                <th><bean:message key="field.label.userId" /></th>
	    			<th><bean:message key="field.label.lastName" /></th>
	    			<th><bean:message key="field.label.firstName" /></th>
	    			<th><bean:message key="field.label.email" /></th>
	    			<th><bean:message key="field.label.phone" /></th>
	    			<th><bean:message key="field.label.cityUL" /></th>
	    			<th><bean:message key="field.label.permissionGroupName" /></th>
	    			<th><bean:message key="field.label.status" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th><bean:message key="field.label.userId" /></th>
	    			<th><bean:message key="field.label.lastName" /></th>
	    			<th><bean:message key="field.label.firstName" /></th>
	    			<th><bean:message key="field.label.email" /></th>
	    			<th><bean:message key="field.label.phone" /></th>
	    			<th><bean:message key="field.label.cityUL" /></th>
	    			<th><bean:message key="field.label.permissionGroupName" /></th>
	    			<th><bean:message key="field.label.status" /></th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </tfoot>
	    </table>
	    <input type="button" value="New" id="newUserButton" class="prettyWideButton" />
	    <webthing:scrolltop />


		<ansi:hasPermission permissionRequired='USER_ADMIN_WRITE'>
		<div id="user-form-modal">
			<form id="addForm">
				<input type="hidden" name="userId" class="userField" />
				<table>
					<tr>
						<td><span class="formLabel">User Id:</span></td>
						<td><span class="displayField userId"></span></td>
						<td><span class="err errField userId"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">First Name:</span></td>
						<td><input type="text" class="userField" name="firstName"/></td>
						<td><span class="err errField firstName"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Last Name:</span></td>
						<td><input type="text" class="userField" name="lastName"/></td>
						<td><span class="err errField lastName"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Email:</span></td>
						<td><input type="text" class="userField" name="email"/></td>
						<td><span class="err errField email"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Phone:</span></td>
						<td><input type="text" class="userField" name="phone"/></td>
						<td><span class="err errField phone"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Address 1:</span></td>
						<td><input type="text" class="userField" name="address1"/></td>
						<td><span class="err errField address1"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Address 2:</span></td>
						<td><input type="text" class="userField" name="address2"/></td>
						<td><span class="err errField address2"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">City:</span></td>
						<td><input type="text" class="userField" name="city"/></td>
						<td><span class="err errField city"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">State:</span></td>
						<td><input type="text" class="userField" name="state"/></td>
						<td><span class="err userField state"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Zip:</span></td>
						<td><input type="text" class="userField" name="zip"/></td>
						<td><span class="err errField zip"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Title:</span></td>
						<td><input type="text" class="userField" name="title"/></td>
						<td><span class="err userField userId"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Password:</span></td>
						<td><input type="password" class="userField" name="password"/></td>
						<td><span class="err errField password"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Permission Group:</span></td>
						<td><select class="userField" name="permissionGroupId"></select></td>
						<td><span class="err errField permissionGroupId"></span></td>
					</tr>
					<tr>
						<td><span class="formLabel">Status:</span></td>
						<td><select class="userField" name="status"></select></td>
						<td><span class="err errField status"></span></td>
					</tr>
				</table>
			</form>
		</div>
		</ansi:hasPermission>
    </tiles:put>
		
</tiles:insert>

