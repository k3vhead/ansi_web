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
        	#exception-display {
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
				cursor:pointer;
			}		
        </style>
        
        <script type="text/javascript">    
        
        $(document).ready(function(){
        	;EXCEPTION_REPORT = {
        		exceptionMap : {},
        		currentCompanyCode : null,
        		
        		init : function() {
        			$("#prompt-div select[name='companyCode']").change(function() {
        				$("#prompt-div .companyCodeErr").html("");
        				var $companyCode = $("#prompt-div select[name='companyCode']").val();
        				if ( $companyCode == null ) {
        					$("#prompt-div .companyCodeErr").html("Required Value");
        				} else {
        					EXCEPTION_REPORT.currentCompanyCode = $companyCode;
        					EXCEPTION_REPORT.makeExceptionTable();
        					EXCEPTION_REPORT.makeModals();
        				}
       				});
        		},
        		
        		
        		displayExceptionModal : function($myRow) {
        			console.log("displayExceptionModal: " + $myRow);
        			console.log(EXCEPTION_REPORT.exceptionMap[$myRow]);
        			$("#exception-display input").val("");
        			$("#exception-display select").val("");
        			$("#exception-display .err").html("");
        			$("#exception-display input[name='employeeCode']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_code);
        			$("#exception-display input[name='div']").val(EXCEPTION_REPORT.exceptionMap[$myRow].div);
        			$("#exception-display input[name='weekEnding']").val(EXCEPTION_REPORT.exceptionMap[$myRow].week_ending);
        			$("#exception-display input[name='employeeName']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_name);
        			$("#exception-display input[name='unionMember']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_member);
        			$("#exception-display input[name='unionRate']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_rate);
        			$("#exception-display input[name='unionCode']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_code);
        			$("#exception-display input[name='underUnionMinPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].under_union_min_pay);
        		//	$("#exception-display input[name='minimumHourlyPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].minimum_hourly_pay);
        			$("#exception-display input[name='underGovtMinPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].under_govt_min_pay);
        			$("#exception-display input[name='expensesPct']").val(EXCEPTION_REPORT.exceptionMap[$myRow].expenses_pct);
        			$("#exception-display input[name='expensesClaim']").val(EXCEPTION_REPORT.exceptionMap[$myRow].expenses_claim);
        			$("#exception-display select[name='ytdExpensesPct']").val(EXCEPTION_REPORT.exceptionMap[$myRow].ytd_expenses_pct);
        			$("#exception-display input[name='ytdExcessExpensesClaim']").val(EXCEPTION_REPORT.exceptionMap[$myRow].ytd_excess_expenses_claim);
        			$("#exception-display input[name='expensesSubmitted']").val(EXCEPTION_REPORT.exceptionMap[$myRow].expenses_submitted);
        			$("#exception-display input[name='volume']").val(EXCEPTION_REPORT.exceptionMap[$myRow].volume);
        			$("#exception-display input[name='directLabor']").val(EXCEPTION_REPORT.exceptionMap[$myRow].direct_labor);
        			$("#exception-display input[name='foreignCompany']").val(EXCEPTION_REPORT.exceptionMap[$myRow].foreign_company);
        			$("#exception-display input[name='foreignDivision']").val(EXCEPTION_REPORT.exceptionMap[$myRow].foreign_division);
        			$("#exception-display select[name='employeeStatus']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_status);
        			
        			$("#exception-display").dialog("open");
        		},
        		
        		makeModals : function() {
        			console.log("makeModals");
        			$( "#exception-display" ).dialog({
        				title:'View Exception Record',
        				autoOpen: false,
        				height: 500,
        				width: 600,
        				modal: true,
        				closeOnEscape:true,
        				//open: function(event, ui) {
        				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
        				//},
        				buttons: [
        					{
        						id:  "exception_display_cancel",
        						click: function($event) {
       								$( "#exception-display" ).dialog("close");
        						}
        					}
        				]
        			});	
        			$("#exception_display_cancel").button('option', 'label', 'Done');    
        			// EXCEPTION_REPORT.displayExceptionModal($myRow);
        		},
    		

        		
        		
        		displayReport : function($data) {
        			$("#prompt-div").hide();
        			$("#display-div").show();
        			
        			$("#display-div .companyCode").html($data.data.companyCode + " (" + $data.data.div + ")");
        			
        			EXCEPTION_REPORT.makeEmployeeTable();
        		},
        		
        		
        		
        		makeExceptionTable : function() {
        			var $companyCode = EXCEPTION_REPORT.currentCompanyCode;
        			
        			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $no = '<webthing:ban>No</webthing:ban>';
        			var $noErrorFound = '<payroll:noErrorFound>No Error Found</payroll:noErrorFound>';
        			var $errorFound = '<payroll:errorFound>Error</payroll:errorFound>';
        			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
        			
        			$("#exceptionReportTable").DataTable( {
            			"aaSorting":		[[3,'asc']],
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
	        	                        text: 'Default',
	        	                        show: [ 0,1,2,3,7,9,11,12,14,15,16,17,19],
	        	                        hide: [ 4,5,6,8,10,13,18]
	        	                    },
	        	                    {
	        	                        extend: 'colvisGroup',
	        	                        text: 'Show all',
	        	                        show: ':hidden'
	        	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Union',
            	                        show: [ 0,1,2,3,4,5,6],
            	                        hide: [ 8,9,10,11,12,13,14,15,16,17,18 ]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Min Pay',
            	                        show: [ 0,1,2,3,4,7,8],
            	                        hide: [ 4,5,6,9,10,11,12,13,14,15,16,17,18]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Expenses',
            	                        show: [ 0,1,2,3,9,10,11,12,13,14,15],
            	                        hide: [ 4,5,6,7,8,16,17,18]
            	                    },            	                    
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Out of Area',
            	                        show: [ 0,1,2,3,16,17],
            	                        hide: [ 4,5,6,7,8,10,11,12,13,14,15,18 ]
            	                    }     
            	                
            	        		
            	        		],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [3] },
            	            { className: "dt-center", "targets": [0,1,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19] },
            	         	{ "visible": false, "targets": [4,5,6,8,10,13]},
            	       		],
            	         	// "paging": true,
    			        "ajax": {
    			        	"url": "payroll/exceptionReport/" + $companyCode,
    			        	"type": "GET",
    			        	"data": {},
    			        	},
    			        columns: [
        			        	{ title: "Emp Code", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_code' }, 
        			        	{ title: "Div", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'div' },
        			        	{ title: "Week Ending", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'week_ending' },
        			        	{ title: "Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name' },
        			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
        			        	{ title: "Union", width:"5%", searchable:true, "defaultContent":$unknown,
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
        			        	{ title: "Union Rate", width:"10%", searchFormat: "#.##", "defaultContent": "",
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
    			        		//{ title: "< Union Min", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'under_union_min_pay' },
        			        	{ title: "Under Union Min", width:"5%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_union_min_pay != null ) {
    			        				if ( row.under_union_min_pay == 1 ) {
    			        					$value = $errorFound;
    			        					//row.under_union_min_pay.style.backgroundColor = "yellow";
    			        					//$(this).css('background-color','yellow');
        			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.under_union_min_pay == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        	//	{ title: "Minimum Hourly", width:"5%", searchFormat: "#.##", data: function ( row, type, set ) {
       			        	//		if(row.minimum_hourly_pay != null){return (parseFloat(row.minimum_hourly_pay).toFixed(2));}
   		            		//	} },
    			        		{ title: "Under Govt Min", width:"5%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_govt_min_pay != null ) {
    			        				if ( row.under_govt_min_pay == 1 ) {
    			        					$value = $errorFound;
    			        					//row.under_govt_min_pay.style.backgroundColor = "yellow";
    			        					//$(this).css('background-color','yellow');
        			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.under_govt_min_pay == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        	//	{ title: "Expenses Pct", width:"5%", searchFormat: "#.##", data: function ( row, type, set ) {
       			        	//		if(row.excess_expense_pct != null){return (parseFloat(row.excess_expense_pct).toFixed(2));}
   		            		//	} },
    			        		{ title: "Expenses Pct", width:"5%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_pct != null ) {
    			        				if ( row.excess_expense_pct == 1 ) {
    			        					$value = $errorFound;
    			        					//row.excess_expense_pct.style.backgroundColor = "yellow";
    			        					//$("excess_expense_pct").style.backgroundColor = "#90ee90";
        			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.excess_expense_pct == 0 ) {
    			        					$value = $noErrorFound;
    			        					//$("excess_expense_pct").hide();
    			        	
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "Expenses Claim", width:"5%", searchable:true, "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_claim != null ) {
    			        				if ( row.excess_expense_claim == 1 ) {
    			        					$value = $errorFound;
    			        					//row.excess_expense_claim.style.backgroundColor = "yellow";
    			        					//$("excess_expense_claim").style.backgroundColor = "#90ee90";
        			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.excess_expense_claim == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		//{ title: "YTD Expenses Pct", width:"5%", searchFormat: "#.##", data: function ( row, type, set ) {
      			        		//	if(row.ytd_excess_expense_pct != null){return (parseFloat(row.ytd_excess_expense_pct).toFixed(2));}
  		            			//} },
    			        		{ title: "YTD Expenses Pct", width:"5%", searchable:true, "defaultContent": "",
        			        		data:function(row, type, set) {
        			        			var $value = $unknown;
        			        			if ( row.ytd_excess_expense_pct != null ) {
        			        				if ( row.ytd_excess_expense_pct == 1 ) {
        			        					$value = $errorFound;
        			        					//row.ytd_excess_expense_pct.style.backgroundColor = "yellow";
    			        						//$(this).css('background-color','yellow');
            			        				//	$(this).find('td').css('background-color', 'red');
        			        				}
        			        				if ( row.ytd_excess_expense_pct == 0 ) {
        			        					$value = $noErrorFound;
        			        				}
        			        			}
        			        			return $value;
       			        			}
       			        		},
       			        		{ title: "YTD Expenses Claim", width:"5%", searchable:true, "defaultContent": "",
           			        		data:function(row, type, set) {
           			        			var $value = $unknown;
           			        			if ( row.ytd_excess_expense_claim != null ) {
           			        				if ( row.ytd_excess_expense_claim == 1 ) {
           			        					$value = $errorFound;
        			        					//row.ytd_excess_expense_claim.style.backgroundColor = "yellow";
        			        					//$(this).css('background-color','yellow');
            			        				//	$(this).find('td').css('background-color', 'red');
           			        				}
           			        				if ( row.ytd_excess_expense_claim == 0 ) {
           			        					$value = $noErrorFound;
           			        				}
           			        			}
           			        			return $value;
           			        			}
          			        	},
    			        		{ title: "Expenses Submitted", width:"5%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
    			        			if(row.expenses_submitted != null){return (parseFloat(row.expenses_submitted).toFixed(2));}
		            			} },
    			        		{ title: "Volume", width:"5%", searchable:true, "defaultContent": "",  searchFormat: "#.##", data: function ( row, type, set ) {
    			        			if(row.volume != null){return (parseFloat(row.volume).toFixed(2));}
		            			} },
        			        	{ title: "Direct Labor", width:"5%", searchable:true,  searchFormat: "#.##", data: function ( row, type, set ) {
    			        			if(row.direct_labor != null){return (parseFloat(row.direct_labor).toFixed(2));}
		            			} },
        			        	{ title: "Foreign Company", width:"5%", searchable:true, "defaultContent": "", 
        			        	data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.foreign_company != null ) {
    			        				if ( row.foreign_company == 1 ) {
    			        					$value = $errorFound;
    			        					//row.foreign_company.style.backgroundColor = "yellow";
    			        					//$(this).css('background-color','yellow');
    			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.foreign_company == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
        			        	{ title: "Foreign Division", width:"5%", searchable:true, "defaultContent": "",
        			        	data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.foreign_division != null ) {
    			        				if ( row.foreign_division == 1 ) {
    			        					$value = $errorFound;
    			        					//row.foreign_division.style.backgroundColor = "yellow";
    			        					//$(this).css('background-color','yellow');
        			        				//	$(this).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.foreign_division == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
        			        	{ title: "Action",  width:"5%", searchable:false,  
        			            	data: function ( row, type, set ) { 
        			            		var $viewLink = '<span class="action-link view-link" data-id="'+row.row_id+'"><webthing:view>Detail</webthing:view></span>';
        			            		return $viewLink;
        			            	}
        			            }],
        			            "initComplete": function(settings, json) {
        			            	var myTable = this;
        			            	LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#exceptionReportTable", EXCEPTION_REPORT.makeExceptionTable);
        			            	$.each(json.data, function($index, $myRow) {
        			            		EXCEPTION_REPORT.exceptionMap[$myRow.row_id]=$myRow;
        			            		console.log($myRow.row_id + " " + $myRow.employee_code +" " + $myRow.div +" " + $myRow.week_ending);	
        			            	});
        			            	
        			            	
        			            	$(".view-link").off("click");
        			            	$(".view-link").click(function($clickevent) {
        			            		var $myRow = $(this).attr("data-id");
        			            		console.log("exception row id: " + $myRow);        			            		
        			        			EXCEPTION_REPORT.displayExceptionModal($myRow);
        			            	});   
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
		<div id="exception-display" class="modal-window">
		<table>
				<tr>
					<td class="form-label">Employee Code:</td>
					<td>
						<input type="text" name="employeeCode" readonly="true" />
						<input type="hidden" name="selectedEmployeeCode" />
					</td>
					<td><span class="err employeeCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Division:</td>
					<td>
						<input type="text" name="div" readonly="true" />
						<input type="hidden" name="selectedDiv" />
					</td>
					<td><span class="err divErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Week Ending:</td>
					<td>
						<input type="text" name="weekEnding" readonly="true" />
						<input type="hidden" name="selectedWeekEnding" />
					</td>
					<td><span class="err weekEndingErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Name:</td>
					<td>
						<input type="text" name="employeeName" readonly="true" />
						<input type="hidden" name="selectedEmployeeName" />
					</td>
					<td><span class="err nameErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Union Member:</td>
					<td><input type="text" name="unionMember" readonly="true" />
						<input type="hidden" name="selectedUnionMember" />
					<td><span class="err unionMemberErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Union Rate:</td>
					<td><input type="select" name="unionRate" readonly="true" />
						<input type="hidden" name="selectedUnionRate" />
					<td><span class="err unionRateErr"></span></td>
				</tr>				
				<tr>
					<td class="form-label">Expenses Submitted:</td>
					<td><input type="text" name="expensesSubmitted" readonly="true" />
						<input type="hidden" name="selectedExpensesSubmitted" />
						</td>
					<td><span class="err expensesSubmittedErr"></span></td>
				</tr>						
				<tr>
					<td class="form-label">Direct Labor:</td>
					<td><input type="text" name="directLabor" readonly="true" />
						<input type="hidden" name="selectedDirectLabor" />
						</td>
					<td><span class="err directLaborErr"></span></td>
				</tr>				
				<tr>
					<td class="form-label">Foreign Company:</td>
					<td><input type="text" name="foreignCompany" readonly="true" />
						<input type="hidden" name="selectedForeignCompany" />
						</td>
					<td><span class="err departmentErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Foreign Division:</td>
					<td><input type="text" name="foreignDivision" readonly="true"/>
						<input type="hidden" name="selectedForeignDivision" />
						</td>
					<td><span class="err foreignDivisionErr"></span></td>
				</tr>
			</table>
		</div>
		
		
    </tiles:put>
		
</tiles:insert>

