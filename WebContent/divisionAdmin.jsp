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
			}
			#displayTable {
				width:90%;
				clear:both;
			}
			#addFormDiv {
				display:none;
			}
			#editFormDiv {
				display:none;
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
        
        $(document).ready(function(){
        	
	        ;DIVISIONADMIN = {
				
	    		init : function() {	
	    		//	CONTACTMAINTENANCE.clearAddForm();
	    			//CONTACTMAINTENANCE.clearEditForm();
	    			DIVISIONADMIN.getDivisionList();
	    			DIVISIONADMIN.doFunctionBinding();
	    		//	CONTACTMAINTENANCE.getTableFieldList(null, $("#addForm select[name='tableName']"));
	    			DIVISIONADMIN.makeAddForm();
	    			//DIVISIONADMIN.makeEditForm();
	    			DIVISIONADMIN.markValid();
	    			DIVISIONADMIN.makeDeleteModal();
	    			DIVISIONADMIN.showNew();
	        		},
						
			        addRow : function (index, $division) {	
			        	
		      			var $rownum = index + 1;
		       			rowTd = DIVISIONADMIN.makeRow($division, $rownum);
		       			row = '<tr class="dataRow">' + rowTd + "</tr>";
		       			$('#displayTable').append(row);
					},
		            
		            clearAddForm : function () {
						$.each( $('#addForm').find("input"), function(index, $inputField) {
							$fieldName = $($inputField).attr('name');
							if ( $($inputField).attr("type") == "text" ) {
								$($inputField).val("");
								DIVISIONADMIN.markValid($inputField);
							}
						});
						$('.err').html("");
						$('#addForm').data('rownum',null);
		            },
						
					doDelete : function ($clickevent) {
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
		             	$('#confirmDelete').dialog("open")
					},
						
						
						
					doFunctionBinding : function () {
						$('.editAction').bind("click", function($clickevent) {
							$divisionId = $(this).attr("data-id");
							var $rownum = $(this).attr("data-row");
							DIVISIONADMIN.doEdit($clickevent);
						});						
						
						$('.delAction').bind("click", function($clickevent) {
							DIVISIONADMIN.doDelete($clickevent);
						});
						$('.dataRow').bind("mouseover", function() {
							$(this).css('background-color','#CCCCCC');
						});
						$('.dataRow').bind("mouseout", function() {
							$(this).css('background-color','transparent');
						});
					},
						
					
					
					doEdit : function ($clickevent) {
						$clickevent.preventDefault();
						$rownum = $("#addForm").attr("data-row");
						var $divisionId = $clickevent.currentTarget.attributes["data-id"].value;
						console.log("div id: " + $divisionId);
						var $rownum = $clickevent.currentTarget.attributes['data-row'].value;
						$("#goUpdate").data("divisionId", $divisionId);
		        		$('#goUpdate').button('option', 'label', 'Save');
		        		$('#closeAddForm').button('option', 'label', 'Close');
						
						var $url = "division/" + $divisionId
						var jqxhr = $.ajax({
							type: 'GET',
							url: $url,
							statusCode: {
								200: function($data) {
									var $division = $data.data.divisionList[0];	
									$.each($division, function($fieldName, $value) {									
										$selector = "#addForm input[name=" + $fieldName + "]";
										if ( $($selector).length > 0 ) {
											$($selector).val($value);
										}
			        				});							
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
					        		$("#addForm .err").html("");		
					             	$("#addForm").dialog("option","title", "Edit Division").dialog("open");
								},
								403: function($data) {
									$("#addFormMsg").html("Session Timeout. Log in and try again").show();
								},
								500: function($data) {
		             	    		$("#addFormMsg").html("System error contact support").show();
		             	    	} 
							},
							dataType: 'json'
						});
					},
	        		
	        		
			        getDivisionList : function() {               	
			        
						var jqxhr = $.ajax({
							type: 'GET',
							url: 'division/list',
							data: {},
							statusCode: {
								200: function($data) {
								$.each($data.data.divisionList, function(index, value) {
									DIVISIONADMIN.addRow(index, value);
								});
								DIVISIONADMIN.doFunctionBinding();
								},
								403: function($data) {
									$("#globalMsg").html("Session Timeout. Log in and try again").show();
								},
								500: function($data) {
			         	    		$("#globalMsg").html("System error contact support").show();
			         	    	} 
							},
							dataType: 'json'
						});
			        },
					
					
					goDelete : function ($clickevent) {
			        //    $("#goDelete").click(function($event) {
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
								statusCode: {
									200: function($data) {
				            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
										if ( $data.responseHeader.responseCode == 'SUCCESS') {
											$rowfinder = "tr:eq(" + $rownum + ")"
											$("#displayTable").find($rowfinder).remove();
							              //  $("#displayTable").html( "divisionAdmin.html #diplayTable" );
											//$("#displayTable").html("");
											$('#displayTable').trigger('reflow');
											$('#confirmDelete').dialog("close");
											//DIVISIONADMIN.makeRow($data.data.division, $rownum);
										}
				            	     },
			             	    	403: function($data) {
			             	    		$('#confirmDelete').dialog("close");
										$("#globalMsg").html("Session Timeout. Log in and try again").show();
			             	    	}, 
				         	    	404: function($data) {
				         	    		$('#confirmDelete').dialog("close");
				         	    		$("#globalMsg").html("Record does not exist").fadeIn(10).show();
			             	    	},
			             	    	500: function($data) {
			             	    		$('#confirmDelete').dialog("close");
			             	    		$("#globalMsg").html("System error contact support").show();
			             	    	} 
			             	     },
			             	     dataType: 'json'
			             	});
			          //   });
			            
					},
						
					goUpdate : function () {
						var $divisionId = $("#addForm input[name='divisionId']").val();
						var $rownum = $('#addForm').data('rownum')
						/*	if ( $('#addForm').data('rownum') == null ) {
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
							} */
			
							

							if ( $divisionId == null || $divisionId == '') {
								$url = 'division/add';
							} else {
							/*	$rownum = $('#addForm').data('rownum')
								var $tableData = [];
				                $("#displayTable").find('tr').each(function (rowIndex, r) {
				                    var cols = [];
				                    $(this).find('th,td').each(function (colIndex, c) {
				                        cols.push(c.textContent);
				                    });
				                    $tableData.push(cols);
				                }); */

								//var $divisionId = $("#addForm input[name='divisionId']").val();
								$url = 'division/' + $divisionId;
							}
							
							var $outbound = {};				
							$outbound['divisionId'] = $("#addForm input[name='divisionId']").val();
							$outbound['divisionCode'] = $("#addForm input[name='divisionCode']").val();
							$outbound['divisionNbr'] = $("#addForm input[name='divisionNbr']").val();
							$outbound['description'] = $("#addForm input[name='description']").val();
							$outbound['defaultDirectLaborPct'] = $("#addForm input[name='defaultDirectLaborPct']").val();
							$outbound['maxRegHrsPerDay'] = $("#addForm input[name='maxRegHrsPerDay']").val();
							$outbound['maxRegHrsPerWeek'] = $("#addForm input[name='maxRegHrsPerWeek']").val();
							$outbound['overtimeRate'] = $("#addForm input[name='overtimeRate']").val();
							$outbound['status'] = $("#addForm select[name='status'] option:selected").val();
							$outbound['weekendIsOt'] = $("#addForm select[name='weekendIsOt'] option:selected").val();
							$outbound['hourlyRateIsFixed'] = $("#addForm select[name='hourlyRateIsFixed'] option:selected").val();

							
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
											DIVISIONADMIN.clearAddForm();
											$('#displayTable').html('');
											$('#addForm').dialog("close");
							    			DIVISIONADMIN.getDivisionList();
										//	$('#displayTable').replaceAll();
							             //   DIVISIONADMIN.makeRow($divisionId, $rownum);
					    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
					    				}
					    			},
									403: function($data) {
										$("#addFormMsg").html("Session Timeout. Log in and try again").show();
									},
									500: function($data) {
			             	    		$("#addFormMsg").html("System error contact support").show();
			             	    	} 
								},
								dataType: 'json'
							});
					},
					
					rebuildTable : function ($divisionId, $rownum) {
		                DIVISIONADMIN.makeRow($divisionId, $rownum);
						
					},
					
					makeAddForm : function () {
	    				$("#addForm" ).dialog({
	    					autoOpen: false,
	    					height: 550,
	    					width: 700,
	    					modal: true,
	        				closeOnEscape:true,
	    					buttons: [
	    						{
	    							id: "closeAddForm",
	    							click: function() {		
										DIVISIONADMIN.clearAddForm();	    
	    								$("#addForm").dialog( "close" );
	    							}
	    						},{
	    							id: "goUpdate",
	    							click: function($event) {
	    								DIVISIONADMIN.goUpdate();
	    							}
	    						}	      	      
	    					],
	    					close: function() {
	    						DIVISIONADMIN.clearAddForm();
	    						$("#addForm").dialog( "close" );
	    						//allFields.removeClass( "ui-state-error" );
	    					}
	    				});
					},
					
					makeDeleteModal : function () {
	    				$("#confirmDelete" ).dialog({
	    					title: "Are you sure you want to delete this division?",
	    					autoOpen: false,
	    					height: 150,
	    					width: 450,
	    					modal: true,
	        				closeOnEscape:true,
	    					buttons: [
	    						{
	    							id: "closeDeleteModal",
	    							click: function() {		
										DIVISIONADMIN.clearAddForm();
	    								$("#confirmDelete").dialog( "close" );
	    							}
	    						},{
	    							id: "goDelete",
	    							click: function($event) {
	    								DIVISIONADMIN.goDelete();
	    							}
	    						}	      	      
	    					],
	    					close: function() {
								DIVISIONADMIN.clearAddForm();
	    						$("#confirmDelete").dialog( "close" );
	    						//allFields.removeClass( "ui-state-error" );
	    					}
	    				});
		        		$('#goDelete').button('option', 'label', 'Confirm Delete');
		        		$('#closeDeleteModal').button('option', 'label', 'Close');
					},
						
					makeRow : function ($division, $rownum) {
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
		       				$divisionText = '<webthing:checkmark>Yes</webthing:checkmark>';
		       			} else if ( $division.status == 'Inactive' ) {
		       				$iconcolor="red";
		       				$divisionText = '<webthing:ban>No</webthing:ban>';
		       			} else {
		       				$iconcolor="red";
		       				$divisionText = '<webthing:questionmark>Invalid</webthing:questionmark>';       				
		       			}
		       			row = row + '<td class="status centered ' + $iconcolor + '">' + $divisionText + '</td>';
		       	    	<ansi:hasPermission permissionRequired="SYSADMIN_WRITE">
		       			row = row + '<td class="text-left">';
		       			row = row + '<a href="#" class="editAction" data-id="' + $division.divisionId + '"data-row="' + $rownum +'"><webthing:edit>Edit</webthing:edit></a>';
		       			if ( $division.userCount == 0 ) {
		       			row = row + '<a href="#" class="delAction" data-row="' + $rownum+'"><webthing:delete>Delete</webthing:delete></a>';
		       			}
		       			row = row + '</td>';
		       			
		       			
		       			$viewLink = '<a href="#" class="viewAction" data-id="'+$division.divisionId+'"><webthing:view>View</webthing:view></a>';
		       			
		       			</ansi:hasPermission>       			
						return row;
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
					
	        		showNew : function() {

	        			$(".showNew").click(function($event) {
	        				$('#goUpdate').data("rownum",null);
	                		$('#goUpdate').button('option', 'label', 'Save');
	                		$('#closeAddForm').button('option', 'label', 'Close');
	                		
	                		$("#addForm input[name='divisionId']").val("");
	                		$("#addForm input[name='divisionCode']").val("");
	                		$("#addForm input[name='divisionNbr']").val("");
	                		$("#addForm input[name='description']").val("");
			            	$("#addForm input[name='defaultDirectLaborPct']").val("");
			            	$("#addForm select[name='status']").val("");
			            	/*if ( $division.status == "Active" ) {
			            		$("#addForm select[name='status']").val("1");
			            	} else {
			            		$("#addForm select[name='status']").val("0");
			            	}*/
			            	$("#addForm input[name='maxRegHrsPerDay']").val("");
			            	$("#addForm input[name='maxRegHrsPerWeek']").val("");
			            	$("#addForm input[name='overtimeRate']").val("");
			            	$("#addForm select[name='weekendIsOt']").val("");
			            	/*if ( $division.weekendIsOt == null ) {
			            		$("#addForm select[name='weekendIsOt']").val("");
			            	} else {
			            		$("#addForm select[name='weekendIsOt']").val($division.weekendIsOt.toString());
			            	}*/
			            	$("#addForm select[name='hourlyRateIsFixed']").val("");
			            	/*if ( $division.hourlyRateIsFixed == null ) {
			            		$("#addForm select[name='hourlyRateIsFixed']").val("");
			            	} else {
			            		$("#addForm select[name='hourlyRateIsFixed']").val($division.hourlyRateIsFixed.toString());
			            	}*/
	                		$("#addForm .err").html("");
	                		$("#addForm").dialog("option","title", "New Division").dialog("open");
	        			});
	        			
	        		},	
	       	 	};
			
			DIVISIONADMIN.init();
			 
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
    		<input type="button" class="prettyWideButton showNew" value="New" />
    			
    		
    		
    	</div>
    	</ansi:hasWrite>
    		</ansi:hasPermission>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN">
    		<ansi:hasWrite>
		    	<div id="confirmDelete">
		    		<table id="delData">
		    			<tr>
		    				<td><span class="formLabel"><bean:message key="field.label.divisionCode" />:</span></td>
		    				<td id="delDivisionCode"></td>
		    			</tr>
		    		</table>
		    	</div>
		    	
		    	<div id="addFormDiv">
		    		<h2 id="addFormTitle"></h2>
		    		<div id="addFormMsg" class="err"></div>
		    		<form action="#" method="post" id="addForm">
		    			<table>
		    				<tr>
		    					<td><span class="formLabel"><bean:message key="field.label.divisionId" />:</span></td>
		    					<td><input style="border:none" type="text" name="divisionId" readonly/></td>
    							<td><span class="err" id="divisionIdErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.divisionNbrDA" />:</span></td>
		    					<td><input type="text" name="divisionNbr"/></td>
    							<td><span class="err" id="divisionNbrErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.divisionCode" />:</span></td>
		    					<td><input type="text" name="divisionCode"/></td>
    							<td><span class="err" id="divisionCodeErr"></span></td>
		    				</tr>
							<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.description" />:</span></td>
		    					<td><input type="text" name="description"/></td>
    							<td><span class="err" id="descriptionErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel"><bean:message key="field.label.defaultDirectLaborPctDefault" /> <bean:message key="field.label.defaultDirectLaborPctDL%" />:</span></td>
		    					<td><input type="text" name="defaultDirectLaborPct"/></td>
    							<td><span class="err" id="defaultDirectLaborPctErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Max Regular Hours Per Day:</span></td>
		    					<td><input type="text" name="maxRegHrsPerDay"/></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Max Regular Hours Per Week:</span></td>
		    					<td><input type="text" name="maxRegHrsPerWeek"/></td>
		    				</tr>
		    				<tr>
		    					<td><span class="formLabel">Overtime Rate:</span></td>
		    					<td><input type="text" name="overtimeRate"/></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Weekend is Overtime:</span></td>
		    					<td>
		    						<select name="weekendIsOt">		    						
		    							<option value=""></option>
			    						<option value="1">Yes</option>
			    						<option value="0">No</option>
		    						</select>
		    					</td>
		    					<td><span class="err" id="weekendIsOtErr"></span></td>
		    				</tr>
		    				<tr>
		    					<td><span class="required">*</span><span class="formLabel">Hourly Rate is Fixed:</span></td>
		    					<td>
		    						<select name="hourlyRateIsFixed">
			    						<option value=""></option>
			    						<option value="1">Yes</option>
			    						<option value="0">No</option>
		    						</select>
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
		    					</td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
	    	</ansi:hasWrite>
    	</ansi:hasPermission>
    </tiles:put>

</tiles:insert>

