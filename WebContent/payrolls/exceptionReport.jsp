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
<%@ taglib tagdir="/WEB-INF/tags/payroll" prefix="payroll" %>
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
        					EXCEPTION_REPORT.makeExceptionTable($companyCode);
        				}
       				});
        		},

        		
        		
        		displayReport : function($data) {
        			$("#prompt-div").hide();
        			$("#display-div").show();
        			
        			$("#display-div .companyCode").html($data.data.companyCode + " (" + $data.data.div + ")");
        			
        			EXCEPTION_REPORT.makeEmployeeTable();
        		},
        		
        		

        		
        		makeExceptionTable : function($companyCode) {
        			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $no = '<webthing:ban>No</webthing:ban>';
        			var $okay = '<payroll:okay>Okay</payroll:okay>';
        			var $exception = '<payroll:exception>Error</payroll:exception>';
        			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
        			
        			$("#exceptionReportTable").DataTable( {
            			"aaSorting":		[[4,'asc'],[3,'asc']],
            			"processing": 		true,
            	        "serverSide": 		true,
            	        "autoWidth": 		false,
            	        "deferRender": 		true,
            	        "scrollCollapse": 	true,
            	        "scrollX": 			true,
            	      //  "pageLength":		50,
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
            	        	
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Union',
            	                        show: [ 0,1,2,3,4,5,6,7,8],
            	                        hide: [ 9,10,11,12,13,14,15,16,17,18,19 ]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Min Pay',
            	                        show: [ 0,1,2,3,4,8,9],
            	                        hide: [ 5,6,7,10,11,12,13,14,15,16,17,18,19]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Expenses',
            	                        show: [ 0,1,2,3,4,10,11,12,13,14,15,16],
            	                        hide: [ 5,6,7,8,9,17,18,19]
            	                    },            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Out of Area',
            	                        show: [ 0,1,2,3,4,17,18],
            	                        hide: [ 5,6,7,8,10,11,12,13,14,15,16,19 ]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Show all',
            	                        show: ':hidden'
            	                    }
            	                
            	        		
            	        		],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [0,1,2,3,4,5,6,7,8,9,10,11] },
            	            { className: "dt-center", "targets": [12,13,14,15,16,17,18,19] },
            	         ],
            	       // "paging": true,
    			        "ajax": {
    			        	"url": "payroll/exceptionReport/" + $companyCode,
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
        			        	{ title: "Employee Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' }, 
        			        	{ title: "Company Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'company_name' }, 
        			        	{ title: "Division", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'division_id' },
        			        	{ title: "Employee Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name' },
        			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
        			        	{ title: "Union", width:"10%", searchable:true, "defaultContent":$unknown,
	        			        	data:function(row, type, set) {
	    			        			var $value = $unknown;
	    			        			if ( row.union_member != null ) {
	    			        				if ( row.union_member == 1 ) {
	    			        					$value = $yes;
	    			        				}
	    			        				if ( row.union_member == 0 ) {
	    			        					$value = $no;
	    			        				}
	    			        			}
	    			        			return $value;
	    			        		}
	    			        	},
        			        	{ title: "Union Code", width:"10%", searchable:true, "defaultContent": "", data:'union_code' },
        			        	{ title: "Union Rate", width:"10%", searchable:true, "defaultContent": "",
        			        	data:function(row, type, set) {
    			        			var $value = "";
    			        			if ( row.union_member == 1 ) {
    			        				$value = $unknown;
    			        				if ( row.union_rate != null ) {
    			        					$value = "$" + row.union_rate.toFixed(2);
    			        				}
    			        			}
    			        			return $value;
    			        		}
    			        	},
    			        		{ title: "< Union Min", width:"10%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_union_min_pay != null ) {
    			        				if ( row.under_union_min_pay == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.under_union_min_pay == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "< Gov Min", width:"10%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_govt_min_pay != null ) {
    			        				if ( row.under_govt_min_pay == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.under_govt_min_pay == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "Excess Expense %", width:"10%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_pct != null ) {
    			        				if ( row.excess_expense_pct == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.excess_expense_pct == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "Excess Expense Claim", width:"10%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_claim != null ) {
    			        				if ( row.excess_expense_claim == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.excess_expense_claim == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "YTD Excess Expense %", width:"10%", searchable:true, "defaultContent": "",
        			        		data:function(row, type, set) {
        			        			var $value = $unknown;
        			        			if ( row.ytd_excess_expense_pct != null ) {
        			        				if ( row.ytd_excess_expense_pct == 1 ) {
        			        					$value = $exception;
        			        				}
        			        				if ( row.ytd_excess_expense_pct == 0 ) {
        			        					$value = $okay;
        			        				}
        			        			}
        			        			return $value;
       			        			}
       			        		},
       			        		{ title: "YTD Excess Expense Claim", width:"10%", searchable:true, "defaultContent": "",
           			        		data:function(row, type, set) {
           			        			var $value = $unknown;
           			        			if ( row.ytd_excess_expense_pct_claim != null ) {
           			        				if ( row.ytd_excess_expense_claim == 1 ) {
           			        					$value = $exception;
           			        				}
           			        				if ( row.ytd_excess_expense_claim == 0 ) {
           			        					$value = $okay;
           			        				}
           			        			}
           			        			return $value;
           			        			}
          			        	},
    			        		{ title: "Expenses Submitted", width:"10%", searchable:true, "defaultContent": "", data:'expenses_submitted' },
        			        	{ title: "Volume", width:"10%", searchable:true, "defaultContent": "", data:'volume' },
        			        	{ title: "Direct Labor", width:"10%", searchable:true, "defaultContent": "", data:'direct_labor' },
        			        	{ title: "Foreign Company", width:"10%", searchable:true, "defaultContent": "", 
        			        	data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.foreign_company != null ) {
    			        				if ( row.foreign_company == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.foreign_company == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
        			        	{ title: "Foreign Division", width:"10%", searchable:true, "defaultContent": "",
        			        	data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.foreign_division != null ) {
    			        				if ( row.foreign_division == 1 ) {
    			        					$value = $exception;
    			        				}
    			        				if ( row.foreign_division == 0 ) {
    			        					$value = $okay;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
        			        	{ title: "Action",  width:"5%", searchable:false,  
        			            	data: function ( row, type, set ) { 
        			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.employee_code+'"><webthing:view>Exception_Report_Record</webthing:view></span>';
        			            		var $actionLink = $viewLink;
        			            		return $actionLink;
        			            	}
        			            }],
        			            "initComplete": function(settings, json) {
        			            	var myTable = this;
        			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#exceptionReportTable", EXCEPTION_REPORT.makeExceptionTable);
        			               
        			            	//EXCEPTION_REPORT.doFunctionBinding();
        			            	},
        			            "drawCallback": function( settings ) {
        			            	//CALL_NOTE.lookupLink();
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
    	
    	<webthing:lookupFilter filterContainer="filter-container" />
		<div id="prompt-div">
	    	<select name="companyCode">
				<option value=""></option>
				<ansi:selectOrganization type="COMPANY" active="true" />
				
			<table id="exceptionReportTable">
			</table>
			</select>
		</div>
    </tiles:put>
		
</tiles:insert>

