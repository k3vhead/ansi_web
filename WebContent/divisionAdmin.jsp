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
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script>
    	<link rel="stylesheet" href="css/lookup.css" type="text/css" />
        <style type="text/css">
        	#addFormDiv {
        		display:none;
        	}
			#confirmDelete {
				display:none;
			}
			#displayTable {
				width:90%;
				clear:both;
			}
			#editFormDiv {
				display:none;
			}
			#displayTable th {
				text-align:left
			}	
			#filter-container {
        		width:402px;
        		float:right;
        	}	
        	#viewForm {
        		display;none;
        	}		
			.delAction {
				text-decoration:none;
			}
			.editAction {
				text-decoration:none;
			}
			.remitTo {
				padding-left:25%;
			}
			.text-center {
				text-align:center;
			}
			.text-left {
				text-align:left;
			}
			.viewAction {
				color:#000000;
				text-decoration:none;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function(){
        	
	        ;DIVISIONADMIN = {
				
	    		init : function() {	
	    			//DIVISIONADMIN.getDivisionList();
	    			DIVISIONADMIN.doFunctionBinding();
	    			DIVISIONADMIN.makeModals();
	    			DIVISIONADMIN.showNew();
	    			DIVISIONADMIN.markValid();
	    			DIVISIONADMIN.makeDataTable();
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

	            
	            
				doDelete : function ($clickevent, $divisionId, $div) {					
					$clickevent.preventDefault();
					console.log("doDelete: " + $divisionId + " " + $div);
					$('#confirmDelete').attr("data-id",$divisionId);
					$('#delDivisionCode').html($div);
	             	$('#confirmDelete').dialog("open")
				},
						
						
						
				doFunctionBinding : function () {
					$('.editAction').off("click");
					$('.delAction').off("click");
					$('.viewAction').off("click");
				
					$('.editAction').bind("click", function($clickevent) {
						$divisionId = $(this).attr("data-id");
						var $rownum = $(this).attr("data-row");
						DIVISIONADMIN.doEdit($clickevent);
					});						
					
					$('.delAction').bind("click", function($clickevent) {
						var $divisionId = $(this).attr("data-id");
						var $div = $(this).attr("data-div");
						DIVISIONADMIN.doDelete($clickevent, $divisionId, $div);
					});		
					
					$('.viewAction').bind("click", function($clickevent) {
						var $divisionId = $(this).attr("data-id");
						DIVISIONADMIN.doView($clickevent, $divisionId);
					});		
				},
					
				
				
				doEdit : function ($clickevent) {
					$clickevent.preventDefault();
					var $divisionId = $clickevent.currentTarget.attributes["data-id"].value;
					console.log("doEdit: " + $divisionId);
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
				            	$("#addForm input[name='remitToName']").val($division.remitTo.name);
				            	$("#addForm input[name='remitToAddress1']").val($division.remitTo.address1);
				            	$("#addForm input[name='remitToAddress2']").val($division.remitTo.address2);
				            	$("#addForm input[name='remitToCity']").val($division.remitTo.city);
				            	$("#addForm select[name='remitToState']").val($division.remitTo.state);
				            	$("#addForm input[name='remitToZip']").val($division.remitTo.zip);
				            	$("#addForm input[name='remitToPhone']").val($division.remitTo.phone);
				            	$("#addForm input[name='remitToEmail']").val($division.remitTo.email);
				            	
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
	        	
				
	        	
				doView : function($clickevent, $divisionId) {
					$clickevent.preventDefault();
					console.log("doView: " + $divisionId);
					var $url = "division/" + $divisionId
					var $callbacks = {
						200 : DIVISIONADMIN.doViewSuccess,
						404 : DIVISIONADMIN.doViewNotFound,
					};
					ANSI_UTILS.makeServerCall("GET", $url, {}, $callbacks, {});
				},
				
				
				doViewSuccess : function($data, $passthru) {
					console.log("doViewSuccess");	
					var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>'
					var $yes = '<webthing:checkmark>Yes</webthing:checkmark>'
					var $no = '<webthing:ban>No</webthing:ban>'
					var $active = '<webthing:checkmark>Active</webthing:checkmark>'
					var $inactive = '<webthing:ban>Inactive</webthing:ban>'
					var $division=$data.data.divisionList[0];
					
					$("#viewForm .divisionId").html($division.divisionId);
	            	$("#viewForm .divisionCode").html($division.divisionCode);
	            	$("#viewForm .divisionNbr").html($division.divisionNbr);
	            	$("#viewForm .description").html($division.description);
	            	$("#viewForm .defaultDirectLaborPct").html($division.defaultDirectLaborPct);
	            	if ( $division.status == "Active" ) {
	            		$("#viewForm .status").html($active);
	            	} else {
	            		$("#viewForm .status").html($inactive);
	            	}
	            	$("#viewForm .maxRegHrsPerDay").html($division.maxRegHrsPerDay);
	            	$("#viewForm .maxRegHrsPerWeek").html($division.maxRegHrsPerWeek);
	            	if ( $division.minHourlyRate == null ) {
	            		$minHourlyRate = $unknown;
	            	} else {
	            		$minHourlyRate = $division.minHourlyRate.toFixed(2);
	            	}
	            	$("#viewForm .minHourlyRate").html($minHourlyRate);
	            	if ( $division.overtimeRate == null ) {
	            		$overtimeRate = $unknown;
	            	} else {
	            		$overtimeRate =$division.overtimeRate.toFixed(2);
	            	}
	            	$("#viewForm .overtimeRate").html($overtimeRate);
	            	if ( $division.weekendIsOt == null ) {
	            		$("#viewForm .weekendIsOt").html($unknown);
	            	} else if ( $division.weekendIsOT == 1){
	            		$("#viewForm .weekendIsOt").html($yes);
	            	} else {
	            		$("#viewForm .weekendIsOt").html($no);
	            	}
	            	if ( $division.hourlyRateIsFixed == null ) {
	            		$("#viewForm .hourlyRateIsFixed").html($unknown);
	            	} else if ( $division.hourlyRateIsFixed == 1){
	            		$("#viewForm .hourlyRateIsFixed").html($yes);
	            	} else {
	            		$("#viewForm .hourlyRateIsFixed").html($no);
	            	}
	            	$("#viewForm .remitToName").html($division.remitTo.name);
	            	$("#viewForm .remitToAddress1").html($division.remitTo.address1);
	            	$("#viewForm .remitToAddress2").html($division.remitTo.address2);
	            	$("#viewForm .remitToCity").html($division.remitTo.city);
	            	$("#viewForm .remitToState").html($division.remitTo.state);
	            	$("#viewForm .remitToZip").html($division.remitTo.zip);
	            	$("#viewForm .remitToPhone").html($division.remitTo.phone);
	            	$("#viewForm .remitToEmail").html($division.remitTo.email);
	            	
	             	$("#viewForm").dialog("option","title", "View Division").dialog("open");
				},
				
				
				doViewNotFound : function($data, $passthru) {
					console.log("doViewNotFound");
					$("#globalMsg").html("System Error: Invalid Division. Reload this page and try again").show().fadeOut(5000);
				},
				
				
					
				goDelete : function($clickevent) {
					$clickevent.preventDefault();
					var $divisionId = $("#confirmDelete").attr("data-id");
					console.log("goDelete: " + $divisionId);
	            	$url = 'division/' + $divisionId;
	            	
	            	var jqxhr = $.ajax({
	            	    type: 'delete',
	            	    url: $url,
	            	    //data: $outbound,
						statusCode: {
							200: function($data) {
		            	    	$("#globalMsg").html($data.responseHeader.responseMessage).fadeIn(10).fadeOut(6000);
								if ( $data.responseHeader.responseCode == 'SUCCESS') {									
									$('#confirmDelete').dialog("close");
									$('#division-lookup-table').DataTable().ajax.reload();
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
				},
						
				
				
				goUpdate : function () {
					var $divisionId = $("#addForm input[name='divisionId']").val();
					var $rownum = $('#addForm').data('rownum')

					if ( $divisionId == null || $divisionId == '') {
						$url = 'division/add';
					} else {
						$url = 'division/' + $divisionId;
					}
						
					var $outbound = {};				
					$.each( $("#addForm input"), function($index, $value) {
						$outbound[$value.name] = $($value).val();
					});
					$.each( $("#addForm select"), function($index, $value) {
						$outbound[$value.name] = $($value).val();
					});
					console.log("Outbound: " + JSON.stringify($outbound));

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
									$('#addForm').dialog("close");
			    					$("#globalMsg").html("Update Successful").show().fadeOut(10000);
			    					$('#division-lookup-table').DataTable().ajax.reload();
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
    					height: 700,
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
					
				
				
				
				makeDataTable : function() {
					var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>'
					var $yes = '<webthing:checkmark>Yes</webthing:checkmark>'
					var $no = '<webthing:ban>No</webthing:ban>'
					var $active = '<webthing:checkmark>Active</webthing:checkmark>'
					var $inactive = '<webthing:ban>Inactive</webthing:ban>'
					
					DIVISIONADMIN.dataTable = $('#division-lookup-table').DataTable( {
            			"aaSorting":		[[1,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'dt_RowId',
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        "pageLength":		100,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength', 'copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}}
            	        ],
            	        
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [1,2] },
            	            { className: "dt-center", "targets": [0,3,4,8,9,10] },
            	            { className: "dt-right", "targets": [5,6,7]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "divisionLookup",
    			        	"type": "GET",
    			        	"data": {}
    			        	},
    			        columns: [
    			            { width:"4%", title: "<bean:message key="field.label.divisionId" />", searchable:true, width:"5%", "defaultContent": $unknown, data: "division_id" },
    			            { width:"8%", title: "<bean:message key="field.label.div" />", searchable:true, width:"10%", "defaultContent": $unknown, data: "div"},
    			            { width:"25%", title: "<bean:message key="field.label.description" />", width:"25%", searchable:true, "defaultContent": $unknown, data: "description"},
    			            { width:"10%", title: "<bean:message key="field.label.defaultDirectLaborPctDL%" />", width:"5%", "defaultContent":$unknown, data: "default_direct_labor_pct"},
    			            { width:"10%", title: "<bean:message key="field.label.maxRegularHrs" />", width:"5%", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if ( row.max_reg_hrs_per_day == null ) {
    			            		$maxRegHrsDay = $unknown;
    			            	} else {
    			            		$maxRegHrsDay = row.max_reg_hrs_per_day;
    			            	}
    			            	if ( row.max_reg_hrs_per_week == null ) {
    			            		$maxRegHrsWk = $unknown;
    			            	} else {
    			            		$maxRegHrsWk = row.max_reg_hrs_per_week;
    			            	}
    			            	return $maxRegHrsDay + "/" + $maxRegHrsWk;
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.minimumHourlyPay" />", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if(row.minimum_hourly_pay != null){return (row.minimum_hourly_pay.toFixed(2));}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.overtimeRate" />", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if(row.overtime_rate != null){return (row.overtime_rate.toFixed(2));}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.weekendIsOT" />", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if ( row.weekend_is_ot == 1 ) {
    			            		$icon = $yes;
    			            	} else {
    			            		$icon = $no;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.fixedHourlyRate" />", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if ( row.hourly_rate_is_fixed == 1 ) {
    			            		$icon = $yes;
    			            	} else {
    			            		$icon = $no;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.status" />", "defaultContent": $unknown, data: function ( row, type, set ) {
    			            	if ( row.division_status == 1 ) {
    			            		$icon = $active;
    			            	} else {
    			            		$icon = $inactive;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
    			       			var $editLink = '<ansi:hasPermission permissionRequired="SYSADMIN_WRITE"><a href="#" class="editAction" data-id="' + row.division_id + '"><webthing:edit>Edit</webthing:edit></a></ansi:hasPermission>';
    			       			var $deleteLink = "";
    			       			if ( row.user_count == 0 ) {
    			       				$deleteLink = '<ansi:hasPermission permissionRequired="SYSADMIN_WRITE"><a href="#" class="delAction" data-id="' + row.division_id + '" data-div="'+row.div+'"><webthing:delete>Delete</webthing:delete></a></ansi:hasPermission>';
    			       			}
    			       			var $viewLink = '<a href="#" class="viewAction" data-id="'+row.division_id+'"><webthing:view>View</webthing:view></a>';
    			       			return $viewLink + $editLink + $deleteLink;

    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	var myTable = this;
    			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#division-lookup-table", DIVISIONADMIN.makeDataTable);
    			            },
    			            "drawCallback": function( settings ) {    			            	
    			            	console.log("drawCallback");
    			            	//$("#searching-modal").dialog("close");
    			            	DIVISIONADMIN.doFunctionBinding();
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
    								var $divisionId = $(this).attr("data-id");
    								DIVISIONADMIN.goDelete($event, $divisionId);
    							}
    						}	      	      
    					],
    					close: function() {
							DIVISIONADMIN.clearAddForm();
    						$("#confirmDelete").dialog( "close" );
    					}
    				});
	        		$('#goDelete').button('option', 'label', 'Confirm Delete');
	        		$('#closeDeleteModal').button('option', 'label', 'Cancel');
				},
						
				
				makeModals : function() {
	    			DIVISIONADMIN.makeAddForm();
	    			DIVISIONADMIN.makeDeleteModal();
	    			DIVISIONADMIN.makeViewModal();
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
					
	            
	            
	            
	            makeViewModal : function () {
    				$("#viewForm" ).dialog({
    					autoOpen: false,
    					height: 600,
    					width: 600,
    					modal: true,
        				closeOnEscape:true,
    					buttons: [
    						{
    							id: "closeViewForm",
    							click: function() {		
    								$("#viewForm").dialog( "close" );
    							}
    						}      	      
    					],
    					close: function() {
    						$("#viewForm").dialog( "close" );
    						//allFields.removeClass( "ui-state-error" );
    					}
    				});
    				$("#closeViewForm").button('option', 'label', 'Done');
				},
				
				
				
	            
        		showNew : function() {
        			$(".showNew").click(function($event) {
        				$('#goUpdate').data("rownum",null);
                		$('#goUpdate').button('option', 'label', 'Save');
                		$('#closeAddForm').button('option', 'label', 'Close');
                		
		            	$("#addForm input").val("");
		            	$("#addForm select").val("");
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

		<webthing:lookupFilter filterContainer="filter-container" />
    	<table id="division-lookup-table">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
	    </table>
    	
    	
    	
 		<webthing:scrolltop />
    	<ansi:hasPermission permissionRequired="SYSADMIN_WRITE">
   			<div class="addButtonDiv">
   				<input type="button" class="prettyWideButton showNew" value="New" />
	    	</div>
    	</ansi:hasPermission>
    	
    	<ansi:hasPermission permissionRequired="SYSADMIN_WRITE">
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
	    					<td><span class="required">*</span><span class="formLabel">Max Regular Hours Per Day:</span></td>
	    					<td><input type="text" name="maxRegHrsPerDay"/></td>
	    					<td><span class="err" id="maxRegHrsPerDayErr"></span></td>
	    				</tr>
	    				<tr>
	    					<td><span class="required">*</span><span class="formLabel">Max Regular Hours Per Week:</span></td>
	    					<td><input type="text" name="maxRegHrsPerWeek"/></td>
	    					<td><span class="err" id="maxRegHrsPerWeekErr"></span></td>
	    				</tr>
	    				<tr>
	    					<td><span class="required">*</span><span class="formLabel">Min Hourly Rate:</span></td>
	    					<td><input type="text" name="minHourlyRate"/></td>
	    					<td><span class="err" id="minHourlyRateErr"></span></td>
	    				</tr>
	    				<tr>
	    					<td><span class="required">*</span><span class="formLabel">Overtime Rate:</span></td>
	    					<td><input type="text" name="overtimeRate"/></td>
	    					<td><span class="err" id="overtimeRateErr"></span></td>
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
	    							<option value=""></option>
	    							<option value="1"><bean:message key="field.label.active" /></option>
	    							<option value="0"><bean:message key="field.label.inactive" /></option>
	    						</select>
	    					</td>
   							<td><span class="err" id="statusErr"></span></td>
	    				</tr>
	    				<tr>
		   					<td><span class="formLabel">Remit-To Address:</span></td>
		   					<td>&nbsp;</td>
		   					<td>&nbsp;</td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="required">*</span><span class="formLabel">Name:</span></td>
		   					<td><input type="text" name="remitToName" /></td>
		   					<td><span class="err" id="remitToNameErr"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="required">*</span><span class="formLabel">Address:</span></td>
		   					<td><input type="text" name="remitToAddress1" /></td>
		   					<td><span class="err" id="remitToAddress1Err"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="formLabel">Address2:</span></td>
		   					<td><input type="text" name="remitToAddress2" /></td>
		   					<td><span class="err" id="remitToAddress2Err"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="required">*</span><span class="formLabel">City:</span></td>
		   					<td><input type="text" name="remitToCity" /></td>
		   					<td><span class="err" id="remitToCityErr"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="required">*</span><span class="formLabel">State:</span></td>
		   					<td>
		   						<select name="remitToState">
		   							<option value=""></option>
		   							<webthing:states />
		   						</select>
	   						</td>
		   					<td><span class="err" id="remitToStateErr"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="required">*</span><span class="formLabel">Zip:</span></td>
		   					<td><input type="text" name="remitToZip" /></td>
		   					<td><span class="err" id="remitToZipErr"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="formLabel">Phone:</span></td>
		   					<td><input type="text" name="remitToPhone" /></td>
		   					<td><span class="err" id="remitToPhoneErr"></span></td>
		   				</tr>
		   				<tr>
		   					<td class="remitTo"><span class="formLabel">Email:</span></td>
		   					<td><input type="text" name="remitToEmail" /></td>
		   					<td><span class="err" id="remitToEmailErr"></span></td>
		   				</tr>
	    			</table>
	    		</form>
	    	</div>
		    	
		</ansi:hasPermission>
    	
    	<div id="viewForm">
    		<h2 id="viewFormTitle"></h2>
   			<table>
   				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.divisionId" />:</span></td>
   					<td><span class="divisionId"></span></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.divisionNbrDA" />:</span></td>
   					<td><span class="divisionNbr"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.divisionCode" />:</span></td>
   					<td><span class="divisionCode"/></td>
   				</tr>
				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.description" />:</span></td>
   					<td><span class="description"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.defaultDirectLaborPctDefault" /> <bean:message key="field.label.defaultDirectLaborPctDL%" />:</span></td>
   					<td><span class="defaultDirectLaborPct"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Max Regular Hours Per Day:</span></td>
   					<td><span class="maxRegHrsPerDay"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Max Regular Hours Per Week:</span></td>
   					<td><span class="maxRegHrsPerWeek"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Min Hourly Rate:</span></td>
   					<td><span class="minHourlyRate"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Overtime Rate:</span></td>
   					<td><span class="overtimeRate"/></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Weekend is Overtime:</span></td>
   					<td><span class="weekendIsOt"></span></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Hourly Rate is Fixed:</span></td>
   					<td><span class="hourlyRateIsFixed"></span></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel"><bean:message key="field.label.status" />:</span></td>
   					<td><span class="status"></span></td>
   				</tr>
   				<tr>
   					<td><span class="formLabel">Remit-To Address:</span></td>
   					<td>&nbsp;</td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Name:</span></td>
   					<td><span class="remitToName"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Address:</span></td>
   					<td><span class="remitToAddress1"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Address2:</span></td>
   					<td><span class="remitToAddress2"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">City:</span></td>
   					<td><span class="remitToCity"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">State:</span></td>
   					<td><span class="remitToState"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Zip:</span></td>
   					<td><span class="remitToZip"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Phone:</span></td>
   					<td><span class="remitToPhone"></span></td>
   				</tr>
   				<tr>
   					<td class="remitTo"><span class="formLabel">Email:</span></td>
   					<td><span class="remitToEmail"></span></td>
   				</tr>
   			</table>
    	</div>
    </tiles:put>

</tiles:insert>

