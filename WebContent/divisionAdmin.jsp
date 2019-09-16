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
        <bean:message key="page.label.division" /> <bean:message key="menu.label.maintenance" />
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
				clear:both;
			}
			#addFormDiv {
				display:none;
				background-color:#FFFFFF;
				color:#000000;
				width:700px;
				padding:15px;
			}
			#displayTable th {
				text-align:left
			}	
			.text-center {
				text-align:center;
			}
			.text-left {
				text-align:left;
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
						$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
					},
					500: function($data) {
         	    		$("#globalMsg").html("Unhandled Exception").fadeIn(10).fadeOut(6000);
         	    	} 
				},
				dataType: 'json'
			});
			
			$('.ScrollTop').click(function() {
				$('html, body').animate({scrollTop: 0}, 800);
      	  		return false;
      	    });
			
			function addRow(index, $division) {	
				var $rownum = index + 1;
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
				row = row + '<td class="text-left">' + $division.divisionId + '</td>';
				row = row + '<td class="text-left">' + $division.divisionNbr + "-" + $division.divisionCode + '</td>';
				if ( $division.description == null ) {
       				row = row + '<td>&nbsp;</td>';
       			} else {
       				row = row + '<td class="text-left">' + $division.description + '</td>';
       			} 
				row = row + '<td class="text-center">' + $division.defaultDirectLaborPct + '</td>';
				row = row + '<td class="text-center">' + $division.maxRegHrsPerDay + "/" + $division.maxRegHrsPerWeek + "</td>";
				if ( $division.overtimeRate == null ) {
					$otRate = "N/A";
				} else {
					$otRate = $division.overtimeRate
				}
				row = row + '<td class="text-center">' + $otRate + "</td>";
				if ( $division.weekendIsOt == 1 ) {
					$otIcon = '<webthing:checkmark>Yes</webthing:checkmark>'
				} else {
					$otIcon = '<webthing:ban>No</webthing:ban>'
				}
				row = row + '<td class="text-center">' + $otIcon + "</td>";
				if ( $division.hourlyRateIsFixed == 1 ) {
					$hourlyRateIcon = '<webthing:checkmark>Yes</webthing:checkmark>'
				} else {
					$hourlyRateIcon = '<webthing:ban>No</webthing:ban>'
				}
				row = row + '<td class="text-center">' + $hourlyRateIcon + "</td>";
				if ( $division.status == 'Active' ) {
       				$iconcolor="green";
       				$divisionText = '<i class="fa fa-check-square" aria-hidden="true"></i>';
       			} else if ( $division.status == 'Inactive' ) {
       				$iconcolor="red";
       				$divisionText = '<i class="fa fa-minus-circle" aria-hidden="true"></i>';
       			} else {
       				$iconcolor="red";
       				$divisionText = '<i class="fa fa-question-circle" aria-hidden="true"></i>';       				
       			}
       			row = row + '<td class="status centered ' + $iconcolor + '">' + $divisionText + '</td>';
       	    	<ansi:hasPermission permissionRequired="SYSADMIN">
        		<ansi:hasWrite>
       			row = row + '<td class="text-left">';
       			row = row + '<a href="#" class="updAction" data-id="' + $division.divisionId + '"data-row="' + $rownum +'"><span class="green fas fa-pencil-alt" ari-hidden="true"></span></a> | ';
       			if ( $division.userCount == 0 ) {
       			row = row + '<a href="#" class="delAction" data-row="' + row.permissionGroupId +'"><span class="red fas fa-trash-alt" aria-hidden="true"></span></a>';
       			}
       			row = row + '</td>';
       			
       			
       			$viewLink = '<a href="#" class="viewAction" data-id="'+row.permissionGroupId+'"><webthing:view>View</webthing:view></a>';
       			
       			</ansi:hasWrite>
       			</ansi:hasPermission>       			
				return row;
			}
			
			function doUpdate($clickevent) {
				$clickevent.preventDefault();
				clearAddForm();
				var $divisionId = $clickevent.currentTarget.attributes["data-id"].value;
				console.log("div id: " + $divisionId);
				var $rownum = $clickevent.currentTarget.attributes['data-row'].value;
				$("#addFormTitle").html("Update Division");
				$('#addForm').data('rownum',$rownum);
				
				var $url = "division/" + $divisionId
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: {},
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
							var $division = $data.data.divisionList[0];
						
							$("#addForm input[name='divisionId']").val($division.divisionId);
			            	$("#addForm input[name='divisionCode']").val($division.divisionCode);
			            	$("#addForm input[name='divisionNbr']").val($division.divisionNbr);
			            	$("#addForm input[name='description']").val($division.description);
			            	$("#addForm input[name='defaultDirectLaborPct']").val($division.defaultDirectLaborPct);
			            	if ( $division.status == "Active" ) {
			            		$("#addForm select[name='status']").val("1");
			            	} else {
			            		$("#addForm select[name='status']").val("0");
			            	}
			            	$("#addForm input[name='maxRegHrsPerDay']").val($division.maxRegHrsPerDay);
			            	$("#addForm input[name='maxRegHrsPerWeek']").val($division.maxRegHrsPerWeek);
			            	$("#addForm input[name='overtimeRate']").val($division.overtimeRate);
			            	if ( $division.weekendIsOt == null ) {
			            		$("#addForm select[name='weekendIsOt']").val("");
			            	} else {
			            		$("#addForm select[name='weekendIsOt']").val($division.weekendIsOt.toString());
			            	}
			            	if ( $division.hourlyRateIsFixed == null ) {
			            		$("#addForm select[name='hourlyRateIsFixed']").val("");
			            	} else {
			            		$("#addForm select[name='hourlyRateIsFixed']").val($division.hourlyRateIsFixed.toString());
			            	}
			            	
			            	
							
			            	
							$.each( $('#addForm :input'), function(index, value) {
								markValid(value);
							});

			             	$('#addFormDiv').bPopup({
								modalClose: false,
								opacity: 0.6,
								positionStyle: 'fixed' //'fixed' or 'absolute'
							});	
						} else {
							$("#globalMsg").html("Invalid system state - reload and try again");
						}
					},
					statusCode: {
						403: function($data) {
							$("#addFormMsg").html($data.responseJSON.responseHeader.responseMessage);
						},
						500: function($data) {
             	    		$("#addFormMsg").html("Unhandled Exception").fadeIn(10).fadeOut(6000);
             	    	} 
					},
					dataType: 'json'
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
            	$("#delDivisionId").html($tableData[$rownum][0]);
            	$("#delDivisionCode").html($tableData[$rownum][1]);
            	$("#delDivisionNbr").html($tableData[$rownum][2]);

				$('#confirmDelete').data('rownum',$rownum);
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

				$outbound['status'] = $("#addForm select[name='status'] option:selected").val();
				$outbound['weekendIsOt'] = $("#addForm select[name='weekendIsOt'] option:selected").val();
				$outbound['hourlyRateIsFixed'] = $("#addForm select[name='hourlyRateIsFixed'] option:selected").val();
				

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

	            	var $divisionId= $tableData[$rownum][0];
	            	$url = "division/" + $divisionId;
				}
				
				var jqxhr = $.ajax({
					type: 'POST',
					url: $url,
					data: JSON.stringify($outbound),
					success: function($data) {
						if ( $data.responseHeader.responseCode == 'SUCCESS') {
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
							$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						} else if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
							$('.err').html("");
							 $.each($data.data.webMessages, function(key, messageList) {
								var identifier = "#" + key + "Err";
								msgHtml = "<ul>";
								$.each(messageList, function(index, message) {
									msgHtml = msgHtml + "<li>" + message + "</li>";
								});
								msgHtml = msgHtml + "</ul>";
								$(identifier).html(msgHtml);
							});	
							$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
						} else {
							
						}
					},
					statusCode: {
						403: function($data) {
							$("#addFormMsg").html($data.responseJSON.responseHeader.responseMessage);
						},
						500: function($data) {
             	    		$("#addFormMsg").html("Unhandled Exception").fadeIn(10).fadeOut(6000);
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
            	var $divisionId = $tableData[$rownum][0];
            	var $divisionCode = $tableData[$rownum][1];
            	var $divisionNbr = $tableData[$rownum][2];
            	var $defaultDirectLaborPct = $tableData[$rownum][3];
            	var $description= $tableData[$rownum][4];
            	var $status = $tableData[$rownum][5];
            	
            	$outbound = JSON.stringify({});
            	$url = 'division/' + $divisionId;
            	
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
 
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1><bean:message key="page.label.division" /> <bean:message key="menu.label.maintenance" /></h1>
    	
    	<table id="displayTable">
    		<colgroup>
	        	<col style="width:5%;" />
	        	<col style="width:5%;" />
	    		<col style="width:30%;" />    		
	    		<col style="width:10%;" />
	    		<col style="width:10%;" />
	    		<col style="width:5%;" />
	    		<col style="width:5%;" />
	    		<col style="width:10%;" />
	    		<col style="width:5%;" />
	    		<col style="width:10%;" />
	   		</colgroup>
    		<tr>
    			<th class="text-left"><bean:message key="field.label.divisionId" /></th>
    			<th class="text-left">Div</th>
    			<th class="text-left"><bean:message key="field.label.description" /></th>
    			<th class="text-center" style="text-align:center;"><bean:message key="field.label.defaultDirectLaborPctDefault" /><br><bean:message key="field.label.defaultDirectLaborPctDL%" /></br></th>
    			<th class="text-center" style="text-align:center;">Max Reg Hrs Day/Week</th>
    			<th class="text-center" style="text-align:center;">OT Rate</th>
    			<th class="text-center" style="text-align:center;">Wknd is OT</th>
    			<th class="text-center" style="text-align:center;">Fixed Hourly Rate</th>
    			<th class="text-center" style="text-align:center;"><bean:message key="field.label.status" /></th>
 			    <ansi:hasPermission permissionRequired="SYSADMIN">
    				<ansi:hasWrite>
    					<th class="text-left"><bean:message key="field.label.action" /></th>
    				</ansi:hasWrite>
    			</ansi:hasPermission>
    		</tr>
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
		    		<span class="formLabel">Are You Sure You Want to Delete this Division?</span><br />
		    		<table id="delData">
		    			<tr>
		    				<td><span class="formLabel"><bean:message key="field.label.divisionCode" />:</span></td>
		    				<td id="delDivisionCode"></td>
		    			</tr>
		    		</table>
		    		<input type="button" id="cancelDelete" value="<bean:message key="field.label.no" />" />
		    		<input type="button" id="doDelete" value="<bean:message key="field.label.yes" />" />
		    	</div>
		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>
		    		<div id="addFormMsg" class="err"></div>
		    		<form action="#" method="post" id="addForm">
		    			<table>
		    				<tr>
		    					<td><span class="formLabel"><bean:message key="field.label.divisionId" />:</span></td>
		    					<td>
		    						<input style="border:none" type="text" name="divisionId" readonly/>
		    						<i id="validDivisionId" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="divisionIdErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.divisionNbrDA" />:</span></td>
		    					<td>
		    						<input type="text" name="divisionNbr" data-required="true" data-valid="validDivisionNbr" />
		    						<i id="validDivisionNbr" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="divisionNbrErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.divisionCode" />:</span></td>
		    					<td>
		    						<input type="text" name="divisionCode" data-required="true" data-valid="validDivisionCode" />
		    						<i id="validDivisionCode" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="divisionCodeErr"></span></td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.description" />:</span></td>
		    					<td>
		    						<input type="text" name="description" data-required="true" data-valid="validDescription" />
		    						<i id="validDescription" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="descriptionErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.defaultDirectLaborPctDefault" /> <bean:message key="field.label.defaultDirectLaborPctDL%" />:</span></td>
		    					<td>
		    						<input type="text" name="defaultDirectLaborPct" data-required="true" data-valid="validDefaultDirectLaborPct" />
		    						<i id="validDefaultDirectLaborPct" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="defaultDirectLaborPctErr"></span></td>
		    				</tr>
		    				
		    				
		    				
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Max Regular Hours Per Day:</span></td>
		    					<td>
		    						<input type="text" name="maxRegHrsPerDay" data-required="true" data-valid="validMaxRegHrsPerDay" />
		    						<i id="validMaxRegHrsPerDay" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="maxRegHrsPerDayErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Max Regular Hours Per Week:</span></td>
		    					<td>
		    						<input type="text" name="maxRegHrsPerWeek" data-required="true" data-valid="validMaxRegHrsPerWeek" />
		    						<i id="validMaxRegHrsPerWeek" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="maxRegHrsPerWeekErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Overtime Rate:</span></td>
		    					<td>
		    						<input type="text" name="overtimeRate" data-required="true" data-valid="validOvertimeRate" />
		    						<i id="validOvertimeRate" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="overtimeRateErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel"><span class="required">*</span>Weekend is Overtime:</span></td>
		    					<td>
		    						<select name="weekendIsOt" data-required="true" data-valid="validWeekendIsOt">
		    							<option value=""></option>
			    						<option value="1">Yes</option>
			    						<option value="0">No</option>
		    						</select>
		    						<i id="validWeekendIsOt" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="weekendIsOtErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel"><span class="required">*</span>Hourly Rate is Fixed:</span></td>
		    					<td>
		    						<select name="hourlyRateIsFixed" data-required="true" data-valid="validHourlyRateIsFixed">
			    						<option value=""></option>
			    						<option value="1">Yes</option>
			    						<option value="0">No</option>
		    						</select>
		    						<i id="validhourlyRateIsFixed" class="fa" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="hourlyRateIsFixedErr"></span></td>
		    				</tr>
		    				
		    				
		    				
		    				
		    				
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.status" />:</span></td>
		    					<td>
		    						<select name="status">
		    							<option value="1"><bean:message key="field.label.active" /></option>
		    							<option value="0"><bean:message key="field.label.inactive" /></option>
		    						</select>
		    						<i class="fa fa-check-square-o inputIsValid" aria-hidden="true"></i>
		    					</td>
		    					<td><span class="err" id="statusErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td colspan="2" style="text-align:center;">
		    						<input type="button" class="prettyButton" value="<bean:message key="field.label.save" />" id="goUpdate" />
		    						<input type="button" class="prettyButton" value="<bean:message key="field.label.cancel" />" id="cancelUpdate" />
		    					</td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
	    	</ansi:hasWrite>
    	</ansi:hasPermission>
    </tiles:put>

</tiles:insert>

