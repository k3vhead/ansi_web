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
        Code Maintenance
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
			#confirmDelete {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:300px;
				text-align:center;
				padding:15px;
			}
			#displayTable {
				width:90%;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:650px;
				padding:15px;
			}
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
			#showhidden { 
				display:none;
				text-align:right; 				
				cursor:pointer;
			}
			.columnhider {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">  
        
        $(document).ready(function(){
        	
        	;CODEMAINTENANCE = {
        			
        		init : function() {	
        			CODEMAINTENANCE.clearAddForm();
        			CODEMAINTENANCE.createTable();
        			CODEMAINTENANCE.getCodes("list");
        			CODEMAINTENANCE.getTableFieldList(null, $("#addForm select[name='tableName']"));
        			CODEMAINTENANCE.makeAddForm();
        			CODEMAINTENANCE.makeDeleteModal();
        			CODEMAINTENANCE.showNew();
	        		},
	                
	                
                clearAddForm: function () {    	
                	
                	$.each( $('#addForm').find("select"), function(index, $inputField) {
                		$fieldName = $($inputField).attr('name');
                		$selectName = "#addForm select[name='" + $fieldName + "']"
                		select = $($selectName);
                		select.val("");
                		CODEMAINTENANCE.markValid($inputField);
                	});
                	
    				$.each( $('#addForm').find("input"), function(index, $inputField) {
    					$fieldName = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						CODEMAINTENANCE.markValid($inputField);
    					}
    				});
    				$('.err').html("");
      	            $('#addForm').data('tableName', null);
            		$("#addForm").data('fieldName', null);
            		$("#addForm").data('value', null);

                },
	        		
	        		
	    		createTable : function() {
	    			CODEMAINTENANCE.dataTable = $('#codeTable').DataTable( {
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
	        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {CODEMAINTENANCE.doFunctionBinding();}}
	        	        ],
	    				"pageLength": [
	    					[ 100 ],
	    				],
	        	        
	        	        "columnDefs": [
	         	            { "orderable": false, "targets": -1 },
	        	            { className: "dt-head-left", "targets": [0,1,2,3,4] },
	        	            { className: "dt-body-center", "targets": [5] },
	        	            { className: "dt-right", "targets": []},
	        	            { width: '10%', "targets": 5 },
	        	            { width: '30%', "targets": [0,1,2,3,4] },
	        	            { "sClass": "center", "targets": [4,5] }
	        	         ],
	        	        "paging": true,
				        "ajax": {
				        	"url": "codeLookup",
				        	"type": "GET",
				        	},
				        aaSorting:[1],
				        columns: [
				        	{ title: "Table Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.table_name != null){return (row.table_name+"");}
				            } },
				            { title: "Field Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.field_name != null){return (row.field_name+"");}
				            } },
				            { title: "Value", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.value != null){return (row.value+"");}
				            } },
				            { title: "Display Value" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.display_value != null){return (row.display_value+"");}
				            } },
				            { title: "Description", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
				            	if(row.description != null){return (row.description+"");}
				            } },
				            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {		
				            	var $editLink = '<span class="updAction" data-id="' + row.table_name + '"><webthing:edit>Edit</webthing:edit></span>';
				            	var $deleteLink = '<span class="delAction" data-id="' + row.table_name + '"><webthing:delete>Delete</webthing:delete></span>';
			            		$actionWithDelete = $editLink + " " + $deleteLink;
			            		$action = $editLink;
				            	if ( row.can_delete == 'true') {
				            		return $actionWithDelete;
			            		} else {
			            			return $action;
			            		}	
				            } }],
				            "initComplete": function(settings, json) {
				            	//console.log(json);
				            	//USERLOOKUP.doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	CODEMAINTENANCE.doFunctionBinding();
				            }
				    } );
	        	},
	    				
	            	
	    			
    			doFilter: function ($event) {
    				$event.preventDefault();
    				var $filtervalue = $event.currentTarget.attributes['data-filter'].value;				
    				$("#displayTable").find("tr:gt(0)").remove();
    				CODEMAINTENANCE.getCodes($filtervalue);
    			},
    			
    			
    			doFunctionBinding: function () {
    				$('.updAction').bind("click", function($clickevent) {
    					CODEMAINTENANCE.showEdit($clickevent);
    				});
    				$(".delAction").on("click", function($clickevent) {
    					var $table_name = $(this).data("id");
    					CODEMAINTENANCE.deleteThisCode($table_name);
    				});
    			},
				
		   		deleteThisCode : function ($table_name) {
	        		$("#deleteModal").attr("table_name", $table_name);
	        		$("#deleteModal").dialog("open");
				},         
				
			
				doDelete : function($table_name){			
					var $table_name = $('#deleteModal').data('table_name');
	            	var $field_name = $("#deleteModal").data('field_name');
	            	var $value = $("#deleteModal").data('value');
	            	$outbound = JSON.stringify({}); 
	            	
					$url = 'codeLookup/' + $table_name + "/" + $field_name + "/" + $value;
            		var jqxhr = $.ajax({
	            	    type: 'delete',
	            	    url: $url,
	            	    data: $outbound,
            	     	statusCode: {
               	    	 	200: function($data) {
	         	    		 	$("#deleteModal").dialog("close");
	         	    		 	$('#codeTable').DataTable().ajax.reload();
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
			    
				
				getCodes: function ($filter) {
					var $url = 'code/' + $filter;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						success: function($data) {
							//$.each($data.data.codeList, function(index, value) {
							//	CODEMAINTENANCE.addRow(index, value);
							//});
							$("#filterList").html("");
							var $newHTML = "";
							
							$.each($data.data.filterRecordList, function(index, value) {
								
								$newHTML = $newHTML + '<li><a class="myFilter" href="#" data-filter="' + value.tableName + '">' + value.tableName + '</a>'; 
								$newHTML = $newHTML + '<ul class="sub_menu">';
								
								$.each(value.fieldNameList, function(index, fieldName) {
									$newHTML = $newHTML + '<li><a class="myFilter" href="#" data-filter="' + value.tableName + '/' + fieldName + '">' + fieldName + '</a></li>';
								});
								$newHTML = $newHTML + '</ul></li>';
							});
							$("#filterList").html($newHTML);
							$('.myFilter').bind("click", function($clickevent) {						
								CODEMAINTENANCE.doFilter($clickevent);
							});
						},
						statusCode: {
							403: function($data) {
								$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
							} 
						},
						dataType: 'json'
					});
				},
			
    			
    			
    			getFieldNameVariable :  function (tableName, fieldName) {
    			    $selectedTable = document.getElementById(tableName).value;
    			    document.getElementById(fieldName).innerHTML = "Selected value is "+$selectedTable;
    			 },
    	            
    	            getTableFieldList: function (table_name, $select, $selectedValue) {
    	            	if ( table_name == null ) {
    	            		$url = "tableFieldList"
    	           			select = $("#addForm select[name='tableName']");
    	            	} else {
    	            		$url = "tableFieldList/" + table_name;
    	            		select = $("#addForm select[name='fieldName']");
    	            	}            	
    					var jqxhr = $.ajax({
    							type: 'GET',
    							url: $url,
    							data: {},

    							success: function($data) {						
    							if ( $data.responseHeader.responseCode == 'SUCCESS') {
    								if($select.prop) {
    									var options = $select.prop('options');
    								} else {
    									var options = $select.attr('options');
    								}
    								$('option', $select).remove();

    								options[options.length] = new Option("","");
    								$.each($data.data.nameList, function(index, value) {
    									options[options.length] = new Option(value, value);
    								});
    				            	
    				            	$select.val($selectedValue);
    							} else {
    								$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
    							}
    	    				},
    	    				statusCode: {
    	    					403: function($data) {
    	    						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    	    					} 
    	    				},
    	    				dataType: 'json'
    	    			});

    	            },
    				
 				
 				goUpdate : function($clickevent) {	
 					console.debug("Updating Code");
 					var $tableName = $("#addForm input[name='tableName']").val();
 					var $fieldName = $("#addForm input[name='fieldName']").val();
 					var $value = $("#addForm input[name='value']").val();
 					//console.debug("table_name: " + $table_name);
 						
 					var $outbound = {};
 					$outbound['tableName'] = $("#addForm select[name='tableName'] option:selected").val();
 					$outbound['fieldName'] = $("#addForm select[name='fieldName'] option:selected").val();	
 					$outbound['value'] = $("#addForm input[name='value']").val();
 					$outbound['displayValue'] = $("#addForm input[name='displayValue']").val();
 					$outbound['description'] = $("#addForm input[name='description']").val();		
 					console.debug($outbound);
 					

 					if ( $tableName == null || $tableName == '') {
 						$url = 'code/add';
 					} else {
 						$url = 'code/' + $tableName + "/" + $fieldName + '/' + $value;
 					}
 					console.debug($url);
 					
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
 			    					$("#addForm").dialog("close");
 			    					$('#codeTable').DataTable().ajax.reload();		
 			    					CODEMAINTENANCE.clearAddForm();		    					
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
	        		
        		makeAddForm : function() {	
    				$("#addForm" ).dialog({
    					title:'Add/Update Code',
    					autoOpen: false,
    					height: 300,
    					width: 600,
    					modal: true,
    					buttons: [
    						{
    							id: "closeAddForm",
    							click: function() {
    								$("#addForm").dialog( "close" );
    							}
    						},{
    							id: "goEdit",
    							click: function($event) {
    								CODEMAINTENANCE.goUpdate();
    							}
    						}	      	      
    					],
    					close: function() {
    						CODEMAINTENANCE.clearAddForm();
    						$("#addForm").dialog( "close" );
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
    							CODEMAINTENANCE.doDelete();
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
    			
                
                markValid: function ($inputField) {
                	$fieldName = $($inputField).attr('name');
                	$fieldGetter = "input[name='" + $fieldName + "']";
                	$fieldValue = $($fieldGetter).val();
                	$valid = '#' + $($inputField).data('valid');
    	            var re = /.+/;	            	 
                	if ( re.test($fieldValue) ) {
                		$($valid).removeClass("fa-ban");
                		$($valid).removeClass("inputIsInvalid");
                		$($valid).addClass("fa-check-square-o");
                		$($valid).addClass("inputIsValid");
                	} else {
                		$($valid).removeClass("fa-check-square-o");
                		$($valid).removeClass("inputIsValid");
                		$($valid).addClass("fa-ban");
                		$($valid).addClass("inputIsInvalid");
                	}
                },
    			
        		       		
    			    			
    			showEdit : function ($clickevent) {
    				var $table_name = $clickevent.currentTarget.attributes['data-id'].value;
					//var $fieldName = $clickevent.currentTarget.attributes['data-fieldName'].value;
    				console.debug("table_name: " + $table_name);
    				$("#goEdit").data("table_name", $table_name);
            		$('#goEdit').button('option', 'label', 'Save');
            		$('#closeAddForm').button('option', 'label', 'Close');
				//	$("#addFormTitle").html("Edit a Code");
            		
            		
    				var $url = 'code/'+ $table_name;
    				var jqxhr = $.ajax({
    					type: 'GET',
    					url: $url,
    					statusCode: {
    						200: function($data) {
    							var $code = $data.data.codeList[0];    	
    							$("#addForm select[name='tableName']").val($code.tableName);
    							var $table_name = $code.tableName;
    	    					CODEMAINTENANCE.getTableFieldList($table_name, $("#addForm select[name='fieldName']").val($code.fieldName));
    							//$("#addForm select[name='fieldName']").val($code.fieldName);
    							$("#addForm input[name='value']").val($code.value);
    							$("#addForm input[name='displayValue']").val($code.displayValue);
    							$("#addForm input[name='description']").val($code.description);	        		
    			        		$("#addForm .err").html("");
    			        		$("#addForm").dialog("open");
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
				
				
        		showNew : function() {

        			$(".showNew").click(function($event) {
        				$('#goEdit').data("table_name",null);
                		$('#goEdit').button('option', 'label', 'Save');
                		$('#closeAddForm').button('option', 'label', 'Close');
                		
        				$("#addForm select[name='table_name']").val("");
        				//$("#addForm input[name='field_name']").val("");
        				$("#addForm select[name='tableName']").change(function () {
						var $selectedTable = $('#addForm select[name="tableName"] option:selected').val();
						CODEMAINTENANCE.getTableFieldList($selectedTable, $("#addForm select[name='fieldName']"));
		            	});
        				/* $select = $("#addFormDiv select[name='tableName']");
        				$('option', $select).remove();
        				$select.append(new Option("",null));
        				$.each($data, function($index, $val) {
        				       $select.append(new Option(<display>,<value>));
        				}); */
        				$("#addForm input[name='value']").val("");
        				$("#addForm input[name='display_value]").val("");
        				$("#addForm input[name='description']").val("");		        		
                		$("#addForm .err").html("");
                		$("#addForm").dialog("open");
        			});
        			
        		},	
            
       	  };
       	  
       	  CODEMAINTENANCE.init();
            
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">


	 	<table id="codeTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;">
	       
	    </table>
    				<webthing:scrolltop />
		<ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    			<div class="addButtonDiv">
    				<input type="button" class="prettyWideButton showNew" value="New" />
    			</div>
			</ansi:hasWrite>
		</ansi:hasPermission>
    	
    	<div id="deleteModal">
    		Are You Sure You Want to Delete this Code?
	    	</div>
	    	
		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>
		    		<div id="addFormMsg" class="err"></div>
		    		<form action="#" method="post" id="addForm">
		    			<table>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Table:</span></td>
		    					<td>
		    						<%--
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />		    						
		    						 --%>
		    						 <select name="tableName" data-required="false" data-valid="validTableName">
		    						 	<option value=""></option>
		    						 </select>
		    						<i id="tableNameErr" class="fa errIcon" aria-hidden="true"></i>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Field:</span></td>
		    					<td>
		    						<select name="fieldName" data-required="false" data-valid="validFieldName">
		    							<option value=""></option>
		    						</select>
		    						<i id="fieldNameErr" class="fa errIcon" aria-hidden="true"></i>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Value:</span></td>
		    					<td>
		    						<input type="text" name="value" data-required="false" data-valid="validValue" />
		    						<i id="valueErr" class="fa errIcon" aria-hidden="true"></i>
		    					</td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Display:</span></td>
		    					<td>
		    						<input type="text" name="displayValue" data-required="false" data-valid="validDisplayValue" />
		    						<i id="displayValueErr" class="fa errIcon" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="displayValueErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Description:</span></td>
		    					<td><input type="text" name="description" /></td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
    </tiles:put>

</tiles:insert>

