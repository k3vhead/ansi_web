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
			#addForm {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:600px;
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
			#showNew{
				display:none;
			}
			.showNew {
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
        			CODEMAINTENANCE.getCodes("list");
        			//CODEMAINTENANCE.getCodes();
        			//CODEMAINTENANCE.doFilter();
        			CODEMAINTENANCE.createTable();
        			CODEMAINTENANCE.doFunctionBinding();
        			CODEMAINTENANCE.getTableFieldList(null, $("#addForm select[name='table_name']"));
        			CODEMAINTENANCE.clearAddForm();
        			CODEMAINTENANCE.makeaddForm();
        			CODEMAINTENANCE.showNew();
    				if ( CODEMAINTENANCE.ansiModal != '' ) {
						$(".showNew").click();
					}    
	        		},
	                
                
                clearAddForm: function () {
                	$.each( $('#addForm').find("select"), function(index, $inputField) {
                		$field_name = $($inputField).attr('name');
                		$selectName = "#addForm select[name='" + $field_name + "']"
                		select = $($selectName);
                		select.val("");
                		CODEMAINTENANCE.markValid($inputField);
                	});
                	
    				$.each( $('#addForm').find("input"), function(index, $inputField) {
    					$field_name = $($inputField).attr('name');
    					if ( $($inputField).attr("type") == "text" ) {
    						$($inputField).val("");
    						CODEMAINTENANCE.markValid($inputField);
    					}
    				});
    				$('.err').html("");
      	            $('#addForm').data('table_name', null);
            		$("#addForm").data('field_name', null);
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
	        	        
	        	        "columnDefs": [
	         	            { "orderable": false, "targets": -1 },
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
				            	$editLink = '<a href="#" class="editAction" data-id="'+row.table_name+'"><webthing:edit>Edit</webthing:edit></a>';
				      			$deleteLink = '<a href="#" class="delAction" data-id="'+row.table_name+'"><webthing:delete>Delete</webthing:delete></a>';					            	
				            	
				            	$action = $editLink;
				            	if(row.count < 1) {
				            		$action = $action + " " + $deleteLink;
				            	}				            	
				            	return $action;	
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
				
		   	/*	deleteThisPermissionGroup : function ($permissionGroupId, $name) {
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
				}, */
        	
			
		/*	doFilter: function ($event) {
				$event.preventDefault();
				var $filtervalue = $event.currentTarget.attributes['data-filter'].value;				
				$("#displayTable").find("tr:gt(0)").remove();
				getCodes($filtervalue);
			},
        
			 function addRow(index, $code) {	
				var $rownum = index + 1;
       			//$('#displayTable tr:last').before(row);
       			rowTd = makeRow($code, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#displayTable').append(row);
			} */

			
			doFilter: function ($event) {
				//$event.preventDefault();
				var $filtervalue = $event.currentTarget.attributes['data-filter'].value;				
				$("#displayTable").find("tr:gt(0)").remove();
				CODEMAINTENANCE.getCodes($filtervalue);
			},
			
			doFunctionBinding: function () {
				$( ".editAction" ).bind( "click", function($clickevent) {
					CODEMAINTENANCE.doUpdate($clickevent);
				});	
				$('.updAction').bind("click", function($clickevent) {
					CODEMAINTENANCE.doUpdate($clickevent);
				});
				$('.delAction').bind("click", function($clickevent) {
					CODEMAINTENANCE.doDelete($clickevent);
				});
			},
			
			/* function makeRow($code, $rownum) {
				var row = "";
				row = row + '<td class="tablecol">' + $code.table_name + '</td>';
				row = row + '<td class="fieldcol">' + $code.field_name + '</td>';
				row = row + '<td class="valuecol">' + $code.value + '</td>';
       			row = row + '<td class="displaycol">' + $code.display_value + '</td>';
       			row = row + '<td class="seqcol centered">' + $code.seq + '</td>';
       			if ( $code.description == null ) {
       				$description = "";
       				row = row + '<td class="desccol"></td>';
       			} else {
       				$description = $code.description;
       				row = row + '<td class="desccol">' + $code.description + '</td>';
       			}       			
       			if ( $code.status == 1 ) {
       				$iconcolor="green";
       				$codeText = '<i class="fa fa-check-square" aria-hidden="true"></i>';
       			} else if ( $code.status == 0 ) {
       				$iconcolor="red";
       				$codeText = '<i class="fa fa-minus-circle" aria-hidden="true"></i>';
       			} else {
       				$iconcolor="red";
       				$codeText = '<i class="fa fa-question-circle" aria-hidden="true"></i>';       				
       			}
       			row = row + '<td class="statuscol centered ' + $iconcolor + '">' + $codeText + '</td>';
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
        		<ansi:hasWrite>
       			row = row + '<td class="centered actioncol">';
       			row = row + '<a href="#" class="updAction" data-table_name="' + $code.table_name +'" data-field_name="' + $code.field_name + '" data-value="' + $code.value + '" data-display_value="' + $code.display_value + '" data-seq="' + $code.seq +'" data-description="' + $description + '" data-status="' + $code.status + '"><span class="green fas fa-pencil-alt" ari-hidden="true"></span></a> | ';
       			row = row + '<a href="#" class="delAction" data-table_name="' + $code.table_name +'" data-field_name="' + $code.field_name + '" data-value="' + $code.value + '" data-display_value="' + $code.display_value + '" data-seq="' + $code.seq +'" data-description="' + $description + '" data-status="' + $code.status + '"><span class="red fas fa-trash-alt" aria-hidden="true"></span></a>';
       			row = row + '</td>';
       			</ansi:hasWrite>
       			</ansi:hasPermission>       			
				return row;
			} */
				

			goUpdate : function () {
				console.debug("Updating Code List");
				var $table_name = $("#addForm input[name='table_name']").val();
				var $field_name = $("#addForm input[name='field_name']").val();
				var $value = $("#addForm input[name='value']").val();
				var $display_value = $("#addForm input[name='display_value']").val();
				var $description = $("#addForm input[name='description']").val();
				console.debug("table_name: " + $table_name);
				

				if ( $table_name == null || $table_name == '') {
					$url = 'codeLookup/add';
				} else {
					$url = 'codeLookup/';
				}
				console.debug($url);
					
				var $outbound = {};
				$outbound['table_name'] = $("#addForm input[name='table_name']").val();
				$outbound['field_name'] = $("#addForm input[name='field_name']").val();
				$outbound['value'] = $("#addForm input[name='value']").val();	
				$outbound['display_value'] = $("#addForm input[name='display_value']").val();		
				$outbound['description'] = $("#addForm input[name='description']").val();	        		
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
							CODEMAINTENANCE.deletePermissionGroup();
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
		
		
			/*makeDeletePermissionButton : function () {
				$(".deleteThisPermissionGroup").click(function($event) {
	        		//var $jobId = this.parentElement.attributes['data-jobid'].value;
	        		var $permissionGroupId = $(this).closest("div.panel-button-container")[0].attributes['data-permissionGroupId'].value;
	        		CODEMAINTENANCE.deleteThisPermissionGroup($permissionGroupId);
	        	});
			},*/
			
			
			makeaddForm : function() {	
				$("#addForm" ).dialog({
					autoOpen: false,
					height: 300,
					width: 500,
					modal: true,
					buttons: [
						{
							id: "closeaddForm",
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
				
					
            
            
            
            doUpdate: function ($clickevent) {
				$clickevent.preventDefault();
				CODEMAINTENANCE.clearAddForm();
				var $table_name = $("#addForm input[name='table_name']").val();
				var $field_name = $("#addForm input[name='field_name']").val();
				var $value = $("#addForm input[name='value']").val();
				var $display_value = $("#addForm input[name='display_value']").val();
				var $description = $("#addForm input[name='description']").val();
				//var $table_name = $clickevent.currentTarget.attributes['data-table_name'].value;
				//var $field_name = $clickevent.currentTarget.attributes['data-field_name'].value;
				//var $value = $clickevent.currentTarget.attributes['data-value'].value;
				//var $display_value = $clickevent.currentTarget.attributes['data-display_value'].value;
				//var $description = $clickevent.currentTarget.attributes['data-description'].value;
				$("#addFormTitle").html("Update a Code");
				$('#addForm').data('table_name',$table_name);
				$('#addForm').data('field_name',$field_name);
				$('#addForm').data('value',$value);
				$("#goEdit").data("table_name", $table_name);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeaddForm').button('option', 'label', 'Close');
				
                //var $rowId = eval($rownum) + 1;
            	//var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
            	//var $row = $($rowFinder)  
            	//var tdList = $row.children("td");
            	//var $tableName = $row.children("td")[0].textContent;
            	//var $fieldName = $row.children("td")[1].textContent;
            	//var $value = $row.children("td")[2].textContent;
            	//var $display = $row.children("td")[3].textContent;
            	//var $seq = $row.children("td")[4].textContent;
            	//var $description = $row.children("td")[5].textContent;
            	//var $status = $row.children("td")[6].textContent;

            	select = $("#addForm select[name='table_name']");
            	select.val($table_name);
            	
            	select = $("#addForm select[name='field_name']");
            	CODEMAINTENANCE.getTableFieldList($table_name, select, $field_name);
            	//$("#addForm input[name='tableName']").val($tableName);
            	//$("#addForm input[name='fieldName']").val($fieldName);
            	$("#addForm input[name='value']").val($value);
            	$("#addForm input[name='display_value']").val($display_value);
            	$("#addForm input[name='description']").val($description);
            	
				$.each( $('#addForm :input'), function(index, value) {
					CODEMAINTENANCE.markValid(value);
				});
				$("#addForm").dialog("option","title", "Update Code List").dialog("open");
            	$("#addForm input[name='value']").select();
				
            },
            
            
            
            
         /*   showEdit : function ($clickevent) {
				var $table_name = $clickevent.currentTarget.attributes['data-id'].value;
				console.debug("table_name: " + $table_name);
				$("#goEdit").data("table_name", $table_name);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeaddForm').button('option', 'label', 'Close');
        		
        		
				var $url = 'code/' + $table_name;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							var $code = $data.data.code[0];
							 $.each($table_name, function($fieldName, $value) {									
								$selector = "#addForm input[name=" + $fieldName + "]";
								if ( $($selector).length > 0 ) {
									$($selector).val($value);
								}
	        				});
			            	select = $("#addForm select[name='table_name']");
			            	select.val($table_name);
			            	
			            	select = $("#addForm select[name='field_name']");
			            	CODEMAINTENANCE.getTableFieldList($tableName, select, $fieldName); 
							$("#addForm input[name='tableName']").val($code.tableName);
							$("#addForm input[name='fieldName']").val($code.fieldName);
							$("#addForm input[name='value']").val($code.value);
							$("#addForm input[name='displayValue']").val($code.displayValue);
							$("#addForm input[name='description']").val($code.description);	        		
			        		$("#addForm .err").html("");
			        		$("#addForm").dialog("option","title", "Edit Code List").dialog("open");
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
			}, */

				
			showNew : function () {
			$(".showNew").click(function($event) {
				$('#goEdit').data("table_name",null);
        		$('#goEdit').button('option', 'label', 'Save');
        		$('#closeaddForm').button('option', 'label', 'Close');
        		

    			//CODEMAINTENANCE.getTableFieldList(null, $("#addForm select[name='tableName']"));
        		
 //       		$("#addForm display[name='']").val("");
				$("#addForm input[name='table_name']").val("");
				$("#addForm input[name='field_name']").val("");
				$("#addForm input[name='value']").val("");	
				$("#addForm input[name='display_value']").val("");	
				$("#addForm input[name='Description']").val("");		        		
        		$("#addForm .err").html("");
        		$("#addForm").dialog("option","title", "New Code").dialog("open");
			});
			}, 
        	
			getCodes: function ($filter) {
				var $url = 'code/' + $filter;
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: {},
					success: function($data) {
						$.each($data.data.codeList, function(index, value) {
							//addRow(index, value);
						});
						CODEMAINTENANCE.doFunctionBinding();
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
            
            getTableFieldList: function (table_name, $select, $selectedValue) {
            	if ( table_name == null ) {
            		$url = "tableFieldList"
           			//select = $("#addForm select[name='table_name']");
            	} else {
            		$url = "tableFieldList/" + table_name;
            		//select = $("#addForm select[name='field_name']");
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
            
       	  };
        	

		  //CODEMAINTENANCE.getCodes("list");
       	  
       	  CODEMAINTENANCE.init();
            
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">


	 	<table id="codeTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;">
	        <colgroup>
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:13%;" />
	    		<col style="width:8%;" />
	    	</colgroup>
	        <thead>
	            <tr>
	                <th>Table Name</th>
	    			<th>Field Name</th>
	    			<th>Value</th>
	    			<th>Display Value</th>
	    			<th>Description</th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <th>Table Name</th>
	    			<th>Field Name</th>
	    			<th>Value</th>
	    			<th>Display Value</th>
	    			<th>Description</th>
	    			<th><bean:message key="field.label.action" /></th>
	            </tr>
	        </tfoot>
	    </table>
    				<webthing:scrolltop />
		<ansi:hasPermission permissionRequired="SYSADMIN">
			<ansi:hasWrite>
    				<input type="button" class="prettyWideButton showNew" value="New" />
			</ansi:hasWrite>
		</ansi:hasPermission>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN">
    		<ansi:hasWrite>
		    	<div id="confirmDelete">
		    		<span class="formLabel">Are You Sure You Want to Delete this Code?</span><br />
		    		<table id="delData">
		    			<tr>
		    				<td><span class="formLabel">Table:</span></td>
		    				<td id="delTable"></td>
		    			</tr>
		    			<tr>
		    				<td><span class="formLabel">Field:</span></td>
		    				<td id="delField"></td>
		    			</tr>
		    			<tr>
		    				<td><span class="formLabel">Value:</span></td>
		    				<td id="delValue"></td>
		    			</tr>
		    		</table>
		    		<input type="button" id="cancelDelete" value="No" />
		    		<input type="button" id="doDelete" value="Yes" />
		    	</div>
		    	
		    	<div id="addForm">
		    		<h2 id="addFormTitle"></h2>
		    		<div id="addFormMsg" class="err"></div>
		    			<table>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Table:</span></td>
		    					<td>
		    						 <input type="text" name="table_name" data-required="true" data-valid="validTable_name" />
		    						<i id="validTable_name" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="table_nameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Field Name:</span></td>
		    					<td>
		    						<input type="text" name="field_name" data-required="true" data-valid="validField_name" />
		    						<i id="validField_name" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="field_nameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Value:</span></td>
		    					<td>
		    						<input type="text" name="value" data-required="true" data-valid="validValue" />
		    						<i id="validValue" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="valueErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Display:</span></td>
		    					<td>
		    						<input type="text" name="display_value" data-required="true" data-valid="validDisplay" />
		    						<i id="validDisplay" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="displayErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Description:</span></td>
		    					<td><input type="text" name="description" /></td>
		    					<td><span class="err" id="decriptionErr"></span></td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
	    	</ansi:hasWrite>
    	</ansi:hasPermission>
    </tiles:put>

</tiles:insert>

