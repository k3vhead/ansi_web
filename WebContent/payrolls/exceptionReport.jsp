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
		Payroll Exception Report 
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
        	#display-div {
        		display:none;
        	}
        	#display-div .exception-report {
        		padding-top:12px;
        		width:100%;
        	}
        	#table-container {
        		width:100%;
        	}
			#filter-container {
        		width:402px;
        		float:right;
        	}			
        	#organization-display {
        		display:none;
        	}
        	#organization-display table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-display th {
        		text-align:left;
        	}
        	#organization-edit table {
        		width:100%;
        		border:solid 1px #404040;
        	}
        	#organization-edit th {
        		text-align:left;
        	}
        	.action-link {
        		text-decoration:none;
        	}
			.dataTables_wrapper {
				padding-top:10px;
			}	
			.form-label {
				font-weight:bold;
			}
			.org-status-change {
				display:none;
				cursor:pointer;
			}
			.view-link {
				color:#404040;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;EXCEPTION_REPORT = {
        		init : function() {
        			$("#prompt-div select[name='companyCode']").change(function() {
        				$("#prompt-div .companyCodeErr").html("");
        				var $companyCode = $("#prompt-div select[name='companyCode']").val();
        				if ( $companyCode == null ) {
        					$("#prompt-div .companyCodeErr").html("Required Value");
        				} else {
        					EXCEPTION_REPORT.getReport($companyCode);
        				}
       				});
        		},

        		
        		
        		displayReport : function($data) {
        			$("#prompt-div").hide();
        			$("#display-div").show();
        			
        			$("#display-div .companyCode").html($data.data.companyCode + " (" + $data.data.div + ")");
        			
        			EXCEPTION_REPORT.makeEmployeeTable();
        		},
        		
        		

        		
        		makeEmployeeTable : function() {
        			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $no = '<webthing:ban>No</webthing:ban>';
        			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
        			
        			$("#exceptionReportTable").DataTable( {
            			"aaSorting":		[[4,'asc'],[3,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	        "pageLength":		50,
            	        rowId: 				'dt_RowId',
            	        destroy : 			true,		// this lets us reinitialize the table
            	        dom: 				'Bfrtip',
            	        "searching": 		true,
            	        "searchDelay":		800,
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        		'pageLength',
            	        		'copy', 
            	        		'csv', 
            	        		'excel', 
            	        		{extend: 'pdfHtml5', orientation: 'landscape'}, 
            	        		'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#exceptionReportTable').draw();}},
            	        	],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,10,12] },
            	            { className: "dt-center", "targets": [9,13] },
            	            { className: "dt-right", "targets": [11]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "payroll/exceptionReport",
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
        			        	{ title: "Group_Name", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'group_name' }, 
        			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' }, 
        			        	{ title: "Company Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'company_code' }, 
        			        	{ title: "Division", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'division_id' },
        			        	{ title: "First Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_first_name' },
        			        	{ title: "Last Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_last_name' },
        			        	{ title: "Description", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'description' },
        			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
        			        	{ title: "Termination_Date", width:"10%", searchable:true, "defaultContent": "", data:'formatted_termination_date' },
        			        	{ title: "Union_Member", width:"10%", searchable:true, "defaultContent": "", data:'union_member' },
        			        	{ title: "Unions_Code", width:"10%", searchable:true, "defaultContent": "", data:'union_code' },
        			        	{ title: "Unsion_Rate", width:"10%", searchable:true, "defaultContent": "", data:'union_rate' },
        			        	{ title: "Process_Date", width:"10%", searchable:true, "defaultContent": "", data:'formatted_process_date' },
        			        	{ title: "Action",  width:"5%", searchable:false,  
        			            	data: function ( row, type, set ) { 
        			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.employee_code+'"><webthing:view>Exception_Report_Record</webthing:view></span>';
        			            		var $editLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link edit-link" data-id="'+row.employee_code+'"><webthing:edit>Edit</webthing:edit></span></ansi:hasPermission>';
        			            		var $deleteLink = '';
        			            		if ( row.timesheet_count == 0 ) {
        			            			$deleteLink = '<ansi:hasPermission permissionRequired="PAYROLL_WRITE"><span class="action-link delete-link" data-id="'+row.employee_code+'"><webthing:delete>Delete</webthing:delete></span></ansi:hasPermission>';
        			            		}
        			            		var $actionLink = $viewLink + $editLink + $deleteLink;
        			            		return $actionLink;
        			            	}
        			            }],
        			            "initComplete": function(settings, json) {
        			            	EXCEPTION_REPORT.doFunctionBinding();
        			            	var myTable = this;
        			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#exceptionReportTable", EXCEPTION_REPORT.createTable);
        			            },
        			            "drawCallback": function( settings ) {
        			            	CALLNOTE.lookupLink();
        			            }
        			    } );
                	},
        		
        		
        		
        		getReport : function($companyCode) {
        			var $url = "payroll/exceptionReport/" + $companyCode;
        			ANSI_UTILS.makeServerCall("GET", $url, {}, {200:EXCEPTION_REPORT.getReportSuccess}, {});
        		},
        		
        		
        		
        		
        		
        		getReportSuccess : function($data, $passThru) {
        			console.log("getReportSuccess");
        			//if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
        				EXCEPTION_REPORT.displayReport($data);
        			//} else {
        			//	$("#prompt-div .companyCodeErr").html($data.data.webMessages['companyCode'][0]);
        			//}
        		}
        		
        	};
        	
        	EXCEPTION_REPORT.init();
        	
        	
        	
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Exception Report</h1> 

		<div id="prompt-div">
	    	<select name="companyCode">
				<option value=""></option>
				<ansi:selectOrganization type="DIVISION" active="true" />
			</select>
			<span class="companyCodeErr err"></span>
		</div>

		<div id="display-div">
			<div>
				<span class="form-label">Company Code: </span>
				<span class="companyCode"></span>
			</div>
			
			<div class="exception-report">
			<table id="exceptionReportTable">
			</table>
			</div>
			
		</div>
    </tiles:put>
		
</tiles:insert>

