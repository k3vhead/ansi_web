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
        Division Admin
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
				width:400px;
				padding:15px;
			}
        </style>
        
        <script type="text/javascript">
        $(function() {        
			var jqxhr = $.ajax({
				type: 'GET',
				url: 'division/list',
				data: {},
				success: function($data) {
					$.each($data.data.divisionList, function(index, value) {
						addRow(index, value);
					});
					doFunctionBinding();
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
			
			function addRow(index, $division) {	
				var $rownum = index + 1;
       			//$('#displayTable tr:last').before(row);
       			rowTd = makeRow($division, $rownum);
       			row = '<tr class="dataRow">' + rowTd + "</tr>";
       			$('#displayTable').append(row);
			}
			
			function doFunctionBinding() {
				$('.updAction').bind("click", function($clickevent) {
					doUpdate($clickevent);
				});
				$('.delAction').bind("click", function($clickevent) {
					doDelete($clickevent);
				});
				$('.dataRow').bind("mouseover", function() {
					$(this).css('background-color','#CCCCCC');
				});
				$('.dataRow').bind("mouseout", function() {
					$(this).css('background-color','transparent');
				});
			}
			
			function makeRow($division, $rownum) {
				var row = "";
				row = row + '<td>' + $division.divisionId + '</td>';
				row = row + '<td>' + $division.defaultDirectLaborPct + '</td>';
				row = row + '<td>' + $division.divisionNbr + '</td>';
				row = row + '<td>' + $division.divisionCode + '</td>';
				row = row + '<td>' + $division.description + '</td>'
				row = row + '<td>' + $division.status + '</td>'
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
        		<ansi:hasWrite>
       			row = row + '<td>';
       			row = row + '<a href="#" class="updAction" data-row="' + $rownum +'"><span class="green fa fa-pencil" ari-hidden="true"></span></a> | ';
       			row = row + '<a href="#" class="delAction" data-row="' + $rownum +'"><span class="red fa fa-trash" aria-hidden="true"></span></a>';
       			row = row + '</td>';
       			</ansi:hasWrite>
       			</ansi:hasPermission>       			
				return row;
			}
			
			function doUpdate($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				var $rownum = $clickevent.currentTarget.attributes['data-row'].value;
				console.debug("Doing update " + $rownum);
				$("#addFormTitle").html("Update Division");
				$('#addForm').data('rownum',$rownum);
				
				var $rowId = eval($rownum) + 1;
            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
            	var $row = $($rowFinder)  
            	var tdList = $row.children("td");
            	var $tableName = $row.children("td")[0].textContent;
            	var $fieldName = $row.children("td")[1].textContent;
            	var $value = $row.children("td")[2].textContent;

            	$("#addForm input[name='tableName']").val($tableName);
            	$("#addForm input[name='fieldName']").val($fieldName);
            	$("#addForm input[name='value']").val($value);
				
            	
				$.each( $('#addForm :input'), function(index, value) {
					markValid(value);
				});

             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			}
			
			function doDelete($clickevent) {
				$clickevent.preventDefault();
				var rownum = $clickevent.currentTarget.attributes['data-row'].value;
				$('#confirmDelete').data('rownum',rownum);
             	$('#confirmDelete').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});
			}
			
			$("#addButton").click( function($clickevent) {
				$clickevent.preventDefault();
				$("#addFormTitle").html("Add Division");
             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			});
			
			$("#cancelUpdate").click( function($clickevent) {
				console.debug("Canceling update");
				$clickevent.preventDefault();
				clearAddForm();
				$('#addFormDiv').bPopup().close();
			});

			$("#goUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				console.debug("Doing update");
				$outbound = {};
				$.each( $('#addForm :input'), function(index, value) {
					if ( value.name ) {
						$fieldName = value.name;
						$id = "#addForm input[name='" + $fieldName + "']";
						$val = $($id).val();
						$outbound[$fieldName] = $val;
					}
				});
				$outbound['seq'] = $("#addForm select[name='seq'] option:selected").val();
				$outbound['status'] = $("#addForm select[name='status'] option:selected").val();

				if ( $('#addForm').data('rownum') == null ) {
					$url = "division/add";
				} else {
					$rownum = $('#addForm').data('rownum')
					var $tableData = [];
	                $("#displayTable").find('tr').each(function (rowIndex, r) {
	                    var cols = [];
	                    $(this).find('th,td').each(function (colIndex, c) {
	                        cols.push(c.textContent);
	                    });
	                    $tableData.push(cols);
	                });

	                var $tableName = $tableData[$rownum][0];
	            	var $fieldName = $tableData[$rownum][1];
	            	var $value = $tableData[$rownum][2];
	            	$url = "division/" + $Name + "/" + $Name + "/" + $Name;
				}
				
				console.debug(JSON.stringify($outbound))
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						console.debug($data);
						if ( $data.responseHeader.responseDivision == 'SUCCESS') {
							if ( $url == "division/add" ) {
								var count = $('#displayTable tr').length - 1;
								addRow(count, $data.data.division);
							} else {
				            	var $rownum = $('#addForm').data('rownum');
				                var $rowId = eval($rownum) + 1;
				            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
				            	var $rowTd = makeRow($data.data.division, $rownum);
				            	$($rowFinder).html($rowTd);
							}
							doFunctionBinding();
							clearAddForm();
							$('#addFormDiv').bPopup().close();
							$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]).fadeIn(10).fadeOut(6000);
						} else if ( $data.responseHeader.responseDivision == 'EDIT_FAILURE') {
							$.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});		
							$("#addFormMsg").html($data.data.webMessages['GLOBAL_MESSAGE'][0]);
						} else {
							
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
            	var $tableName = $tableData[$rownum][0];
            	var $fieldName = $tableData[$rownum][1];
            	var $value = $tableData[$rownum][2];
            	$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: 'division/delete',
            	    data: $outbound,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseDivision == 'SUCCESS') {
							$rowfinder = "tr:eq(" + $rownum + ")"
							$("#displayTable").find($rowfinder).remove();
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

            $('#addForm').find("input").on('focus',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		markValid(this);
            	}
            });
            
            $('#addForm').find("input").on('input',function(e) {
            	$required = $(this).data('required');
            	if ( $required == true ) {
            		markValid(this);
            	}
            });
            
            function clearAddForm() {
				$.each( $('#addForm').find("input"), function(index, $inputField) {
					$fieldName = $($inputField).attr('name');
					if ( $($inputField).attr("type") == "text" ) {
						$($inputField).val("");
						markValid($inputField);
					}
				});
				$('.err').html("");
				$('#addForm').data('rownum',null);
            }
            
            function markValid($inputField) {
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
            }
            $("#testadd").click(function($event) {
            	var $outbound = JSON.stringify({"parentId":null,"defaultDirectLaborPct":0.03,"divisionNbr":2,"divisionCode":"LW02","description":"Division Desc","status":1});
            	var jqxhr = $.ajax({
            	    type: 'post',
            	    url: 'division/add',
            	    data: $outbound,
            	    success: function($data) {
            	    	console.debug($data);
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		console.debug("403");            	    		
            	    		console.debug($data);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });
            
            $("#testupd").click(function($event) {
            	var $outbound = JSON.stringify({"divisionId":34,"defaultDirectLaborPct":0.04,"divisionNbr":1,"divisionCode":"LW01","description":"Division Desc updt","status":1});
            	var jqxhr = $.ajax({
            	    type: 'post',
            	    url: 'division/34',
            	    data: $outbound,
            	    success: function($data) {
            	    	console.debug($data);
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		console.debug('403');
                	    	console.debug($data);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });
            
            $("#testdel").click(function($event) {            	
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: 'division/34',
            	    data: null,
            	    success: function($data) {
            	    	console.debug($data);
            	     },
            	     statusCode: {
            	    	403: function($data) {
            	    		console.debug('403');
                	    	console.debug($data);
            	    	} 
            	     },
            	     dataType: 'json'
            	});
            });

        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Division Admin</h1>
    	
    	<table id="displayTable">
    		<tr>
    			<th>Division ID</th>
    			<th>Default Direct Labor Percentage</th>
				<th>Division Number</th>
    			<th>Division Code</th>
    			<th>Description</th>
    			<th>Status</th>
 			    <ansi:hasPermission permissionRequired="SYSADMIN">
    				<ansi:hasWrite>
    					<th>Action</th>
    				</ansi:hasWrite>
    			</ansi:hasPermission>
    		</tr>
    	</table>
    	<div class="addButtonDiv">
    		<input type="button" id="addButton" class="prettyWideButton" value="New" />
    	</div>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN">
    		<ansi:hasWrite>
		    	<div id="confirmDelete">
		    		Are You Sure You Want to Delete this Division?<br />
		    		<input type="button" id="cancelDelete" value="No" />
		    		<input type="button" id="doDelete" value="Yes" />
		    	</div>
		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>
		    		<div id="addFormMsg" class="err"></div>
		    		<form action="#" method="post" id="addForm">
		    			<table>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Division ID:</span></td>
		    					<td>
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
		    					<td><span class="required">*</span><span class="formLabel">Default Direct Labor Percentage:</span></td>
		    					<td>
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Division Number:</span></td>
		    					<td>
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel">Division Code:</span></td>
		    					<td>
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Description:</span></td>
		    					<td>
		    						<input type="text" name="tableName" data-required="true" data-valid="validTable" />
		    						<i id="validTable" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="tableNameErr"></span></td>
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

