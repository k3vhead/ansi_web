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
			.columnhider {
				cursor:pointer;
			}
        </style>
        
        <script type="text/javascript">  
        
        $(document).ready(function(){
        	
        	;CODEMAINTENANCE = {
        			
        		init : function() {	
        			//CODEMAINTENANCE.getCodes();
        			CODEMAINTENANCE.createTable();
        			CODEMAINTENANCE.doFunctionBinding();
        			CODEMAINTENANCE.clearAddForm();
        			CODEMAINTENANCE.makeButtons();
        		//	CODEMAINTENANCE.getTableList();
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
				            	//console.log(row);
					            	var $editLink = '<span class="edit-link" data-id="' + row.table_name + '"><webthing:edit>Edit</webthing:edit></span>';
					            		$actionData = "<ansi:hasPermission permissionRequired='USER_ADMIN_WRITE'>" + $editLink + "</ansi:hasPermission>"
				            		return $actionData;
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
			
			doFunctionBinding: function () {
				$('.updAction').bind("click", function($clickevent) {
					CODEMAINTENANCE.doUpdate($clickevent);
				});
				$('.delAction').bind("click", function($clickevent) {
					CODEMAINTENANCE.doDelete($clickevent);
				});
			},
			
			/* function makeRow($code, $rownum) {
				var row = "";
				row = row + '<td class="tablecol">' + $code.tableName + '</td>';
				row = row + '<td class="fieldcol">' + $code.fieldName + '</td>';
				row = row + '<td class="valuecol">' + $code.value + '</td>';
       			row = row + '<td class="displaycol">' + $code.displayValue + '</td>';
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
       			row = row + '<a href="#" class="updAction" data-tableName="' + $code.tableName +'" data-fieldName="' + $code.fieldName + '" data-value="' + $code.value + '" data-displayValue="' + $code.displayValue + '" data-seq="' + $code.seq +'" data-description="' + $description + '" data-status="' + $code.status + '"><span class="green fas fa-pencil-alt" ari-hidden="true"></span></a> | ';
       			row = row + '<a href="#" class="delAction" data-tableName="' + $code.tableName +'" data-fieldName="' + $code.fieldName + '" data-value="' + $code.value + '" data-displayValue="' + $code.displayValue + '" data-seq="' + $code.seq +'" data-description="' + $description + '" data-status="' + $code.status + '"><span class="red fas fa-trash-alt" aria-hidden="true"></span></a>';
       			row = row + '</td>';
       			</ansi:hasWrite>
       			</ansi:hasPermission>       			
				return row;
			} */
			
			doUpdate: function ($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				var $tableName = $clickevent.currentTarget.attributes['data-tableName'].value;
				var $fieldName = $clickevent.currentTarget.attributes['data-fieldName'].value;
				var $value = $clickevent.currentTarget.attributes['data-value'].value;
				var $displayValue = $clickevent.currentTarget.attributes['data-displayValue'].value;
				var $seq = $clickevent.currentTarget.attributes['data-seq'].value;
				var $description = $clickevent.currentTarget.attributes['data-description'].value;
				var $status = $clickevent.currentTarget.attributes['data-status'].value;
				$("#addFormTitle").html("Update a Code");
				$('#addForm').data('tableName',$tableName);
				$('#addForm').data('fieldName',$fieldName);
				$('#addForm').data('value',$value);
				
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

            	select = $("#addForm select[name='tableName']");
            	select.val($tableName);
            	
            	select = $("#addForm select[name='fieldName']");
            	getTableFieldList($tableName, select, $fieldName);
            	//$("#addForm input[name='tableName']").val($tableName);
            	//$("#addForm input[name='fieldName']").val($fieldName);
            	$("#addForm input[name='value']").val($value);
            	$("#addForm input[name='displayValue']").val($displayValue);
            	$("#addForm select[name='seq']").val($seq);
            	$("#addForm input[name='description']").val($description);
            	$("#addForm select[name='status']").val($status);
            	
				$.each( $('#addForm :input'), function(index, value) {
					markValid(value);
				});

             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});		
            	$("#addForm input[name='value']").select();
			},
			
			doDelete: function ($clickevent) {
				$clickevent.preventDefault();

				var $tableName = $clickevent.currentTarget.attributes['data-tableName'].value;
				var $fieldName = $clickevent.currentTarget.attributes['data-fieldName'].value;
				var $value = $clickevent.currentTarget.attributes['data-value'].value;
				var $displayValue = $clickevent.currentTarget.attributes['data-displayValue'].value;
				var $seq = $clickevent.currentTarget.attributes['data-seq'].value;
				var $description = $clickevent.currentTarget.attributes['data-description'].value;
				var $status = $clickevent.currentTarget.attributes['data-status'].value;

				$('#confirmDelete').data('tableName',$tableName);
				$('#confirmDelete').data('fieldName',$fieldName);
				$('#confirmDelete').data('value',$value);
            	$("#delTable").html($tableName);
            	$("#delField").html($fieldName);
            	$("#delValue").html($value);

				//var $rownum = $clickevent.currentTarget.attributes['data-row'].value;
            	//var $tableData = [];
                //$("#displayTable").find('tr').each(function (rowIndex, r) {
                //    var cols = [];
                //    $(this).find('th,td').each(function (colIndex, c) {
                //        cols.push(c.textContent);
                //    });
                //    $tableData.push(cols);
                //});
            	//$("#delTable").html($tableData[$rownum][0]);
            	//$("#delField").html($tableData[$rownum][1]);
            	//$("#delValue").html($tableData[$rownum][2]);

				//$('#confirmDelete').data('rownum',$rownum);
             	$('#confirmDelete').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});
			},
			
			
			makeButtons : function() {
			$("#addButton").click( function($clickevent) {
				$clickevent.preventDefault();
    			CODEMAINTENANCE.clearAddForm();
				$("#addFormTitle").html("Add a Code");
             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});			
             	$("#addForm select[name='tableName']").focus();
			});
			
			$("#cancelUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				CODEMAINTENANCE.clearAddForm();
				$('#addFormDiv').bPopup().close();
			});

			/* $("#goUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				$outbound = {};
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				});
				$outbound['tableName'] = $("#addForm select[name='tableName'] option:selected").val();
				$outbound['fieldName'] = $("#addForm select[name='fieldName'] option:selected").val();
				$outbound['seq'] = $("#addForm select[name='seq'] option:selected").val();
				$outbound['status'] = $("#addForm select[name='status'] option:selected").val();

				if ( $('#addForm').data('tableName') == null ) {
					$url = "code/add";
				} else {
					//$rownum = $('#addForm').data('rownum')
					//var $tableData = [];
	                //$("#displayTable").find('tr').each(function (rowIndex, r) {
	                //    var cols = [];
	                //    $(this).find('th,td').each(function (colIndex, c) {
	                //        cols.push(c.textContent);
	                //    });
	                //    $tableData.push(cols);
	                //});

	            	//var $tableName = $tableData[$rownum][0];
	            	//var $fieldName = $tableData[$rownum][1];
	            	//var $value = $tableData[$rownum][2];
	            	
      	            var $tableName = $('#addForm').data('tableName');
            		var $fieldName = $("#addForm").data('fieldName');
            		var $value = $("#addForm").data('value');

	            	$url = "code/" + $tableName + "/" + $fieldName + "/" + $value;
				}
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							if ( $url == "code/add" ) {
								var count = $('#displayTable tr').length - 1;
								addRow(count, $data.data.code);
							} else {
     				            $("#displayTable").find('tr').each(function (rowIndex, r) {
				                    var cols = [];
				                    $(this).find('th,td').each(function (colIndex, c) {
				                        cols.push(c.textContent);
				                    });
				                    if ( cols[0] == $tableName ) {
				                    	if ( cols[1] == $fieldName ) {
				                    		if ( cols[2] == $value ) {
								            	var $rowTd = makeRow($data.data.code, rowIndex);
				                    			var $rowFinder = "#displayTable tr:nth-child(" + rowIndex + ")";
				                    			$($rowFinder).html($rowTd);				                    			
				                    		}
				                    	}
				                    }
				                });

				            	
				            	
				            	
				            	//$($rowFinder).html($rowTd);
							}
							doFunctionBinding();
							clearAddForm();
							$('#addFormDiv').bPopup().close();
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
							}
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							if ( 'GLOBAL_MESSAGE' in $data.data.webMessages ) {
								$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
							}
						} else {
							
						}
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						}, 
	         	    	404: function($data) {
	         	    		$('#addFormDiv').bPopup().close();
	         	    		$("#globalMsg").html("Record does not exist").fadeIn(10).fadeOut(6000);
	        	    	} 
					},
					dataType: 'json'
				});
			}); */

            $("#cancelDelete").click( function($event) {
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

            $('#addForm').find("input").on('focus',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		CODEMAINTENANCE.markValid(this);
            	}
            });
            
            $('#addForm').find("input").on('input',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		CODEMAINTENANCE.markValid(this);
            	}
            });
            
            $("#addForm select[name='tableName']").change(function () {
				var $selectedTable = $('#addForm select[name="tableName"] option:selected').val();
				CODEMAINTENANCE.getTableFieldList($selectedTable, $("#addForm select[name='fieldName']"));
            });
            
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
            
         /*   getTableList: function (tableName, $select, $selectedValue) {
            	if ( tableName == null ) {
            		$url = "tableFieldList"
           			//select = $("#addForm select[name='tableName']");
            	} else {
            		$url = "tableFieldList/" + tableName;
            		//select = $("#addForm select[name='fieldName']");
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

            },*/
            
       	  };
       	  
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
    			<div class="addButtonDiv">
    				<input type="button" id="addButton" class="prettyWideButton" value="New" />
    			</div>
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
		    						 <select name="tableName" data-required="true" data-valid="validTable">
		    						 	<option value=""></option>
		    						 </select>
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Field:</span></td>
		    					<td>
		    						<select name="fieldName" data-required="true" data-valid="validField">
		    							<option value=""></option>
		    						</select>
		    						<i id="validField" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="fieldNameErr"></span></td>
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
		    						<input type="text" name="displayValue" data-required="true" data-valid="validDisplay" />
		    						<i id="validDisplay" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="displayErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Sequence:</span></td>
		    					<td>
		    						<select name="seq">
		    							<% for (int i = 1; i < 21; i++ ) { %>
		    							<option value="<%= i %>"><%= i %></option>
		    							<% } %>
		    						</select>
		    						<i class="fa fa-check-square-o inputIsValid" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="sequenceErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Description:</span></td>
		    					<td><input type="text" name="description" /></td>
		    					<td><span class="err" id="decriptionErr"></span></td>
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
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="Save" id="goUpdate" />
		    						<input type="button" class="prettyButton" value="Cancel" id="cancelUpdate" />
		    					</td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
	    	</ansi:hasWrite>
    	</ansi:hasPermission>
    </tiles:put>

</tiles:insert>

