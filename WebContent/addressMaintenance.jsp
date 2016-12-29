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
        Address Maintenance
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
			#delData {
				margin-top:15px;
				margin-bottom:15px;
			}
        </style>
        
        <script type="text/javascript">        
        $(function() {        
			var jqxhr = $.ajax({
				type: 'GET',
				url: 'address/list',
				data: {},
				success: function($data) {
					$.each($data.data.codeList, function(index, value) {
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
			
			getTableFieldList(null, $("#addForm select[name='tableName']"));
			
			function addRow(index, $address) {	
				var $rownum = index + 1;
       			//$('#displayTable tr:last').before(row);
       			rowTd = makeRow($address, $rownum);
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
			
			function makeRow($address, $rownum) {
				var row = "";
				if ( $address.addressId == null ) {
       				row = row + '<td></td>';
       			} else {
					row = row + '<td>' + $address.addressId + '</td>';
       			}
				if ( $address.name == null ) {
	       			row = row + '<td></td>';
	       		} else {
	       				row = row + '<td>' + $address.name + '</td>';
	       		}
				if ( $address.status == null ) {
       				row = row + '<td></td>';
       			} else {
					row = row + '<td>' + $address.status + '</td>';
       			}
				if ( $address.address1 == null ) {
       				row = row + '<td></td>';
       			} else {
       				row = row + '<td>' + $address.address1 + '</td>';
       			}
				if ( $address.address2 == null ) {
       				row = row + '<td></td>';
       			} else {
       				row = row + '<td>' + $address.address2 + '</td>';
       			}
				if ( $address.city == null ) {
       				row = row + '<td></td>';
       			} else {
       				row = row + '<td>' + $address.city + '</td>';
       			}
				if ( $address.county == null ) {
       				row = row + '<td></td>';
       			} else {
       				row = row + '<td>' + $address.county + '</td>';
       			}
				if ( $address.state == null ) {
       				row = row + '<td></td>';
       			} else {
					row = row + '<td>' + $address.state + '</td>';
       			}
				if ( $address.zip == null ) {
       				row = row + '<td></td>';
       			} else {
					row = row + '<td>' + $address.zip + '</td>';
       			}
       			
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
				$("#addFormTitle").html("Update an Address");
				$('#addForm').data('rownum',$rownum);
				
                var $rowId = eval($rownum) + 1;
            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")";
            	var $row = $($rowFinder);  
            	var tdList = $row.children("td");
            	var $addressId = $row.children("td")[0].textContent;
            	var $name = $row.children("td")[1].textContent;
            	var $status = $row.children("td")[2].textContent;
            	var $address1 = $row.children("td")[3].textContent;
            	var $address2 = $row.children("td")[4].textContent;
            	var $city = $row.children("td")[5].textContent;
            	var $county = $row.children("td")[6].textContent;
            	var $state = $row.children("td")[7].textContent;
            	var $zip = $row.children("td")[8].textContent;

            	select = $("#addForm select[name='tableName']");
            	select.val($tableName);
            	
            	select = $("#addForm select[name='fieldName']");
            	//getTableFieldList($tableName, select, $fieldName);
            	getTableFieldList(null, $("#addForm select[name='tableName']"));
            	//$("#addForm input[name='tableName']").val($tableName);
            	//$("#addForm input[name='fieldName']").val($fieldName);
            	$("#addForm input[name='name']").val($name);

            	$("#addForm input[name='address1']").val($address1);
            	$("#addForm input[name='address2']").val($address2);
            	$("#addForm input[name='city']").val($city);
            	$("#addForm input[name='county']").val($county);
            	$("#addForm input[name='state']").val($state);
            	$("#addForm input[name='zip']").val($zip);
            
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
				var $rownum = $clickevent.currentTarget.attributes['data-row'].value;
            	var $tableData = [];
                $("#displayTable").find('tr').each(function (rowIndex, r) {
                    var cols = [];
                    $(this).find('th,td').each(function (colIndex, c) {
                        cols.push(c.textContent);
                    });
                    $tableData.push(cols);
                });
            	$("#delTable").html($tableData[$rownum][0]);
            	$("#delField").html($tableData[$rownum][1]);
            	$("#delValue").html($tableData[$rownum][2]);

				$('#confirmDelete').data('rownum',$rownum);
             	$('#confirmDelete').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});
			}
			
			$("#addButton").click( function($clickevent) {
				$clickevent.preventDefault();
				$("#addFormTitle").html("Add an Address");
             	$('#addFormDiv').bPopup({
					modalClose: false,
					opacity: 0.6,
					positionStyle: 'fixed' //'fixed' or 'absolute'
				});				
			});
			
			$("#cancelUpdate").click( function($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				$('#addFormDiv').bPopup().close();
			});

			$("#goUpdate").click( function($clickevent) {
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
			
				$outbound['seq'] = $("#addForm select[name='seq'] option:selected").val();
				$outbound['status'] = $("#addForm select[name='status'] option:selected").val();

				if ( $('#addForm').data('rownum') == null ) {
					$url = "address/add";
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
	            	$url = "address/" + $addressId ;
				}
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							if ( $url == "address/add" ) {
								var count = $('#displayTable tr').length - 1;
								addRow(count, $data.data.code);
							} else {
				            	var $rownum = $('#addForm').data('rownum');
				                var $rowId = eval($rownum) + 1;
				            	var $rowFinder = "#displayTable tr:nth-child(" + $rowId + ")"
				            	var $rowTd = makeRow($data.data.code, $rownum);
				            	$($rowFinder).html($rowTd);
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
							$('#addFormDiv').bPopup().close();
						} else {
							$('#addFormDiv').bPopup().close();
						}
					},
					statusCode: {
						403: function($data) {
							$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
						} 
					},
					dataType: 'json'
				});
				$('#addFormDiv').bPopup().close();
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
            	var $addressId = $tableData[$rownum][0];

            	$outbound = JSON.stringify({});
            	$url = 'address/delete/' + $addressId;
            	//$outbound = JSON.stringify({'tableName':$tableName, 'fieldName':$fieldName,'value':$value});
            	var jqxhr = $.ajax({
            	    type: 'delete',
            	    url: $url,
            	    data: $outbound,
            	    success: function($data) {
            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
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
            
            $("#addForm select[name='tableName']").change(function () {
				var $selectedTable = $('#addForm select[name="tableName"] option:selected').val();
				getTableFieldList($selectedTable, $("#addForm select[name='fieldName']"));
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
            
            function getTableFieldList(tableName, $select, $selectedValue) {
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
						console.debug($data);
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
    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
    					} 
    				},
    				dataType: 'json'
    			});

            }
            
            
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Address Maintenance</h1>
    	
    	<table id="displayTable">
    		<tr>
    			<th>Address Id</th>
    			<th>Name</th>
    			<th>Status</th>
    			<th>Address1</th>
    			<th>Address2</th>
    			<th>City</th>
    			<th>County</th>
    			<th>State</th>
    			<th>Zip</th>
 			    <ansi:hasPermission permissionRequired="SYSADMIN">
    				<ansi:hasWrite>
    					<th>Action</th>
    				</ansi:hasWrite>
    			</ansi:hasPermission>
    		</tr>
    	</table>
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
		    				<td><span class="formLabel">AddressId:</span></td>
		    				<td id="delTable"></td>
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
								<td>Name:</td>
								<td colspan="3"><input type="text" name="name" style="width:90%" /></td>
							</tr>
							<tr>
								<td>Address:</td>
								<td colspan="3"><input type="text" name="address1" style="width:90%" /></td>
							</tr>
							<tr>
								<td>Address 2:</td>
								<td colspan="3"><input type="text" name="address2" style="width:90%" /></td>
							</tr>
							<tr>
								<td>City/State/Zip:</td>
								<td><input type="text" name="city" style="width:90%" /></td>
								<td><input type="text" name="state" style="width:90%" /></td>
								<td><input type="text" name="zip" style="width:90%" /></td>
							</tr>
							<tr>
								<td>County:</td>
								<td><input type="text" name="county" style="width:90%" /></td>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
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

