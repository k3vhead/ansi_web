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
			.delAction {
				text-decoration:none;
			}
			.editAction {
				text-decoration:none;
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
					
					$("#addForm .divisionId").html($division.divisionId);
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
					$outbound['minHourlyRate'] =  $("#addForm input[name='minHourlyRate']").val();
						
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
    			            { width:"4%", title: "<bean:message key="field.label.divisionId" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {	
    			            	if(row.division_id != null){return row.division_id;}
    			            } },
    			            { width:"4%", title: "<bean:message key="field.label.div" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return row.div;}
    			            } },
    			            { width:"25%", title: "<bean:message key="field.label.description" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if(row.description != null){return row.description;}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.defaultDirectLaborPctDL%" />", "defaultContent":$unknown, searchable:true, data: function ( row, type, set ) {
    			            	if(row.default_direct_labor_pct != null){return row.default_direct_labor_pct;}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.maxRegularHrs" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
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
    			            { width:"5%", title: "<bean:message key="field.label.minimumHourlyPay" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if(row.minimum_hourly_pay != null){return (row.minimum_hourly_pay.toFixed(2));}
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.overtimeRate" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if(row.overtime_rate != null){return (row.overtime_rate.toFixed(2));}
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.weekendIsOT" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if ( row.weekend_is_ot == 1 ) {
    			            		$icon = $yes;
    			            	} else {
    			            		$icon = $no;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.fixedHourlyRate" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if ( row.hourly_rate_is_fixed == 1 ) {
    			            		$icon = $yes;
    			            	} else {
    			            		$icon = $no;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"5%", title: "<bean:message key="field.label.status" />", "defaultContent": $unknown, searchable:true, data: function ( row, type, set ) {
    			            	if ( row.division_status == 1 ) {
    			            		$icon = $active;
    			            	} else {
    			            		$icon = $inactive;
    			            	}
    			            	return $icon;
    			            } },
    			            { width:"10%", title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {
    			       			var $editLink = '<a href="#" class="editAction" data-id="' + row.division_id + '"><webthing:edit>Edit</webthing:edit></a>';
    			       			var $deleteLink = "";
    			       			if ( row.user_count == 0 ) {
    			       				$deleteLink = '<a href="#" class="delAction" data-id="' + row.division_id + '" data-div="'+row.div+'"><webthing:delete>Delete</webthing:delete></a>';
    			       			}
    			       			var $viewLink = '<a href="#" class="viewAction" data-id="'+row.division_id+'"><webthing:view>View</webthing:view></a>';
    			       			return $viewLink + $editLink + $deleteLink;

    			            } }],
    			            "initComplete": function(settings, json) {
    			            	console.log("initComplete");
    			            	//console.log(json);
    			            	//doFunctionBinding();
    			            	//var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#document-lookup-table", DOCUMENTLOOKUP.makeTable);
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
    					height: 450,
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
                		
                		$("#addForm input[name='divisionId']").val("");
                		$("#addForm input[name='divisionCode']").val("");
                		$("#addForm input[name='divisionNbr']").val("");
                		$("#addForm input[name='description']").val("");
		            	$("#addForm input[name='defaultDirectLaborPct']").val("");
		            	$("#addForm select[name='status']").val("");
		            	$("#addForm input[name='maxRegHrsPerDay']").val("");
		            	$("#addForm input[name='maxRegHrsPerWeek']").val("");
		            	$("#addForm input[name='overtimeRate']").val("");
		            	$("#addForm select[name='weekendIsOt']").val("");
		            	$("#addForm select[name='hourlyRateIsFixed']").val("");
		            	$("#addForm input[name='minHourlyRate']").val("");
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
    	<!--  
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
    		<thead>
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
    		</thead>
    		<tbody id="tableBody">
    		</tbody>
    	</table>
    	 -->
    	
    	
    	<table id="division-lookup-table" class="display" cellspacing="0" style="table-layout: fixed; font-size:9pt;min-width:1300px; max-width:1300px;width:1300px;">
		        <thead></thead>
		        <tbody></tbody>
		        <tfoot></tfoot>
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
		    							<option value="1"><bean:message key="field.label.active" /></option>
		    							<option value="0"><bean:message key="field.label.inactive" /></option>
		    						</select>
		    					</td>
    							<td><span class="err" id="statusErr"></span></td>
		    				</tr>
		    			</table>
		    		</form>
		    	</div>
		    	
		    	
	    	</ansi:hasWrite>
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
   			</table>
    	</div>
    </tiles:put>

</tiles:insert>

