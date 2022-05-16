<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="/WEB-INF/theTagThing.tld" prefix="ansi" %>




<tiles:insert page="../layout.jsp" flush="true">

    <tiles:put name="title" type="string">
		Payroll Export
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
       	<link rel="stylesheet" href="css/lookup.css" />
    	<link rel="stylesheet" href="css/ticket.css" />
    	<link rel="stylesheet" href="css/document.css" />
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
    	<script type="text/javascript" src="js/addressUtils.js"></script>
    	<script type="text/javascript" src="js/lookup.js"></script> 
    	<script type="text/javascript" src="js/document.js"></script> 
    
        <style type="text/css">
        	#display-container {
        		width:100%; 
        		clear:both;
        		display:none;
        	}
        	#export-button {
        		display:none;
        	}
        	#select-container {
        		width:100%;
        	}
        	.form-header {
        		font-weight:bold;
        	}
        	.section-head {
        		width:100%;
        		background-color:#CCCCCC;
        		cursor:pointer;
        		border:solid 1px #000000;
        		padding:8px;
        	}
        
        
        
        	
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;PAYROLL_EXPORT = {
        		init : function() {
        			PAYROLL_EXPORT.makeClickers();
        		},
        		
        		
        		
        		doExport : function() {
        			console.log("doExport");
        			var $companyCode = $("#select-container select[name='companyCode']").val();
    				var $weekEnding = $("#select-container input[name='weekEnding']").val();
        			$("#export input[name='companyCode']").val($companyCode);
        			$("#export input[name='weekEnding']").val($weekEnding);
        			$("#export").submit();
        		},
        		
        		
        		
        		doPreview : function() {
        			console.log("doPreview");
        			var $companyCode = $("#select-container select[name='companyCode']").val();
    				var $weekEnding = $("#select-container input[name='weekEnding']").val();
    				var $outbound = {
    					"companyCode":$companyCode,
    					"weekEnding":$weekEnding,
    				}
    				var $callbacks = {
           				200 : PAYROLL_EXPORT.makePreview,
           				403 : PAYROLL_EXPORT.process403,
           			}
           			var $passthru = {}
           			ANSI_UTILS.makeServerCall("GET", "payroll/exportPreview", $outbound, $callbacks, $passthru);
        		},
        		
        		
        		
        		makeClickers : function() {
        			$("#select-container input[name='export']").click( function($event) {
        				PAYROLL_EXPORT.doExport();
        			});
        			
        			$("#select-container input[name='preview']").click( function($event) {
        				PAYROLL_EXPORT.doPreview();
        			});
        			
        			$("#select-container select[name='companyCode']").change( function($event) {
        				var $companyCode = $("#select-container select[name='companyCode']").val();
        				var $weekEnding = $("#select-container input[name='weekEnding']").val();
        				if ( $companyCode == null || $companyCode == "" ) {
        					$("#display-container").hide();
        					$("#export-button").hide();
        				} else {
	        				if ( $weekEnding != null && $weekEnding != "" ) {
	        					PAYROLL_EXPORT.doPreview();	
	        				}
        				}
        			});
        			
        			$(".section-head").click( function($event) {
        				var $displayContainer = "#" + $(this).attr("data-display");
        				$($displayContainer).toggle();
        			});
        		},
        		
        		
        		makePreview : function($data, $passthru) {
        			console.log("makePreview");
        			if ( $data.responseHeader.responseCode == "SUCCESS" ) {
        				$("#display-container").show();
        				$("#export-button").show();
        				PAYROLL_EXPORT.makeDisplayTable($data.data.regularHours, "#regularHours");
        				PAYROLL_EXPORT.makeDisplayTable($data.data.allHours, "#allHours");
        				PAYROLL_EXPORT.makeDisplayTable($data.data.preview, "#exportPreview");
        			} else if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
        				$("#display-container").hide();
        				$("#export-button").hide();
        				$("#globalMsg").html("Invalid selection").show().fadeOut(3000);
        			} else {
        				$("#display-container").hide();
        				$("#export-button").hide();
        				$("#globalMsg").html("Invalid response status " + $data.responseHeader.responseCode + ". Contact Support").show();
        			}
        		},
        		
        		
        		
        		makeDisplayTable : function($data, $destination) {
        			$($destination).DataTable( {
            			"aaSorting":		[[0,'asc'],[1,'asc'],[2,'asc'],[3,'asc'],[4,'asc']],
            			"processing": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        rowId: 				'rowId',
            	        destroy : 			true,		// this lets us reinitialize the table
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        buttons: [
           	        		'copy', 
           	        		'csv', 
           	        		'excel', 
           	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
           	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#employeeImport').draw();}},
           	        	],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,8] },
            	            { className: "dt-center", "targets": [7] },
            	            { className: "dt-right", "targets": [6]}
						],
            	        "paging": false,
						data: $data,
    			        columns: [
    			        	{ title: "Company", width:"5%", searchable:true, "defaultContent": "", data:'company_code' },
    			        	{ title: "Week Ending", width:"10%", searchable:true, "defaultContent": "", data:'week_ending' },
    			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "", data:'employee_code' },
    			        	{ title: "Last Name", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_last_name' },
    			        	{ title: "First Name", width:"15%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_first_name' },
    			        	{ title: "Division", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'division' }, 
    			        	{ title: "Hours", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", 
    			        		data: function ( row, type, set ) { 
    			            		return row.amount.toFixed(2);
    			            	}
    			        	}, 
    			        	{ title: "Type", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'type' },
    			        	{ title: "Message", width:"30%", searchable:true, "defaultContent": "", data:'error'},
   			      		],
   			      		"initComplete": function(settings, json) {
							var myTable = this;
    			           	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#employeeImport", EMPLOYEE_IMPORT.makeEmployeeTable);
						},
						drawCallback : function() {
						}    
    			    } );
        		},
        		
        		
        		
        		
        		
        		
        		
        		
        		process403 : function($data, $passthru) {
        			console.log("process403");
        			$("#globalMsg").html("Session has expired. Please login and try again").show();
        		},
        		
        	};
        	
        	PAYROLL_EXPORT.init();
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Export</h1> 
		<div id="select-container">
			<span class="form-header">Company: </span> 
			<select name="companyCode">
				<option value=""></option>
				<ansi:companyCodeSelect format="select" />
			</select>
			&nbsp;
			<span class="form-header">Week Ending: </span>
			<input type="date" name="weekEnding" />
			&nbsp;
			<input type="button" name="preview" value="Preview" />
			<input type="button" name="export" value="Export" id="export-button" />
		</div>
    	
		<div id="display-container">
		
			<div id="regularHoursHead" class="section-head" data-display="regularHoursDisplay">
				<span class="form-header">Excess Regular Hours</span>
			</div>
			<div id="regularHoursDisplay" class="sectionDisplay">
				<table id="regularHours">
				</table>
			</div>
			
			<div id="allHoursHead" class="section-head" data-display="allHoursDisplay">
				<span class="form-header">Excess Combined Hours (Regular, Holiday, Vacation hours)</span>
			</div>
			<div id="allHoursDisplay" class="sectionDisplay">
				<table id="allHours">
				</table>
			</div>
			
			<div id="previewHead" class="section-head" data-display="previewDisplay">
				<span class="form-header">Export Preview</span>
			</div>
			<div id="previewDisplay" class="sectionDisplay">
				<table id="exportPreview">
				</table>
			</div>
		
		</div>
		
		<div id="export-container">
			<form id="export" method="get" action="payroll/export">
				<input type="hidden" name="companyCode" />
				<input type="hidden" name="weekEnding" />
			</form>
		</div>
    </tiles:put>
		
</tiles:insert>

