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
			td.highlight {
    			background-color: whitesmoke !important;
			}
			.red {
  				color:#FF0000;
  				font-weight:bold;
			}
			.background {
				background-color: yellow;
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
        				if ( $companyCode == null || $companyCode == "") {
        					$("#prompt-div .companyCodeErr").html("Required Value");
        				} else {
        					EXCEPTION_REPORT.currentCompanyCode = $companyCode;
        					$("#report-label").html($("#prompt-div select[name='companyCode'] option:selected").text());
        					EXCEPTION_REPORT.makeExceptionTable();
        					EXCEPTION_REPORT.makeModals();
        				}
       				});
        			
        			$("#errorsOnlyCheckbox").click(function($event) {
        				$("#prompt-div select[name='companyCode']").change(); //if the checkbox is clicked, act as if company has been selected
        			});
        			
        			$redText = EXCEPTION_REPORT.makeItRed("test text", ["test help 1","test help 2"]);
        			$("#testred").html($redText);
        		},
        		
        		
        		displayExceptionModal : function($myRow) {
        			console.log("displayExceptionModal: " + $myRow);
        			console.log(EXCEPTION_REPORT.exceptionMap[$myRow]);
        			
        			$("#exception-display input").val("");
        			$("#exception-display select").val("");
        			$("#exception-display .err").html("");

        			var $yes = '<webthing:checkmark>Yes</webthing:checkmark>';
        			var $no = '<webthing:ban>No</webthing:ban>';
        			var $noErrorFound = '<payroll:noErrorFound>No Error Found</payroll:noErrorFound>';
        			var $errorFound = '<payroll:errorFound>Error</payroll:errorFound>';
        			var $unknown = '<webthing:questionmark>Invalid</webthing:questionmark>';
        			
        			$("#exception-display input[name='employeeCode']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_code);
        			$("#exception-display input[name='div']").val(EXCEPTION_REPORT.exceptionMap[$myRow].div);
        			$("#exception-display input[name='weekEnding']").val(EXCEPTION_REPORT.exceptionMap[$myRow].week_ending);
        			$("#exception-display input[name='employeeName']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_name);
        			$("#exception-display select[name='employeeStatus']").val(EXCEPTION_REPORT.exceptionMap[$myRow].employee_status);
        			//$("#exception-display input[name='unionMember']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_member);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].union_member == 1 ) {
        				$("#exception-display .unionMember").html($yes);
        			} else {
        				$("#exception-display .unionMember").html($no);
        				
        			}
        			$("#exception-display input[name='unionCode']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_code);
        			$("#exception-display input[name='unionRate']").val(EXCEPTION_REPORT.exceptionMap[$myRow].union_rate);
        			//$("#exception-display input[name='underUnionMinPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].under_union_min_pay);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].under_union_min_pay == 1 ) {
        				$("#exception-display .underUnionMinPay").html($errorFound);
        			} else {
        				$("#exception-display .underUnionMinPay").html($noErrorFound);
        				
        			}
        			$("#exception-display input[name='minimumHourlyPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].minimum_hourly_pay.toFixed(2));
        			//$("#exception-display input[name='underGovtMinPay']").val(EXCEPTION_REPORT.exceptionMap[$myRow].under_govt_min_pay);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].under_govt_min_pay == 1 ) {
        				$("#exception-display .underGovtMinPay").html($errorFound);
        			} else {
        				$("#exception-display .underGovtMinPay").html($noErrorFound);
        				
        			}
        			//$("#exception-display input[name='excessExpensePct']").val(EXCEPTION_REPORT.exceptionMap[$myRow].excess_expense_pct);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].excess_expense_pct == 1 ) {
        				$("#exception-display .excessExpensePct").html($errorFound);
        			} else {
        				$("#exception-display .excessExpensePct").html($noErrorFound);
        				
        			}
        			//$("#exception-display input[name='excessExpenseClaim']").val(EXCEPTION_REPORT.exceptionMap[$myRow].excess_expense_claim);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].excess_expense_claim == 1 ) {
        				$("#exception-display .excessExpenseClaim").html($errorFound);
        			} else {
        				$("#exception-display .excessExpenseClaim").html($noErrorFound);
        				
        			}
        			//$("#exception-display select[name='ytdExcessExpensePct']").val(EXCEPTION_REPORT.exceptionMap[$myRow].ytd_excess_expense_pct);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].ytd_excess_expense_pct == 1 ) {
        				$("#exception-display .ytdExcessExpensePct").html($errorFound);
        			} else {
        				$("#exception-display .ytdExcessExpensePct").html($noErrorFound);
        				
        			}
        			//$("#exception-display input[name='ytdExcessExpenseClaim']").val(EXCEPTION_REPORT.exceptionMap[$myRow].ytd_excess_expense_claim);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].ytd_excess_expense_claim == 1 ) {
        				$("#exception-display .ytdExcessExpenseClaim").html($errorFound);
        			} else {
        				$("#exception-display .ytdExcessExpenseClaim").html($noErrorFound);
        				
        			}
        			$("#exception-display input[name='expensesSubmitted']").val(EXCEPTION_REPORT.exceptionMap[$myRow].expenses_submitted.toFixed(2));
        			$("#exception-display input[name='volume']").val(EXCEPTION_REPORT.exceptionMap[$myRow].volume.toFixed(2));
        			$("#exception-display input[name='directLabor']").val(EXCEPTION_REPORT.exceptionMap[$myRow].direct_labor.toFixed(2));
        			$("#exception-display input[name='ytdDirectLabor']").val(EXCEPTION_REPORT.exceptionMap[$myRow].ytd_direct_labor.toFixed(2));
        			//$("#exception-display input[name='foreignCompany']").val(EXCEPTION_REPORT.exceptionMap[$myRow].foreign_company);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].foreign_company == 1 ) {
        				$("#exception-display .foreignCompany").html($errorFound);
        			} else {
        				$("#exception-display .foreignCompany").html($noErrorFound);
        				
        			}
        			//$("#exception-display input[name='foreignDivision']").val(EXCEPTION_REPORT.exceptionMap[$myRow].foreign_division);
        			if (EXCEPTION_REPORT.exceptionMap[$myRow].foreign_division == 1 ) {
        				$("#exception-display .foreignDivision").html($errorFound);
        			} else {
        				$("#exception-display .foreignDivision").html($noErrorFound);
        				
        			}
        			
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
        			//EXCEPTION_REPORT.displayExceptionModal($myRow);
        		},
    		

        		
        		
        		displayReport : function($data) {
        			$("#prompt-div").hide();
        			$("#display-div").show();
        			
        			$("#display-div .companyCode").html($data.data.companyCode + " (" + $data.data.div + ")");
        			
        			EXCEPTION_REPORT.makeEmployeeTable();
        		},
        		
        		
        		
        		makeExceptionTable : function($companyCode) {
        			var $companyCode = EXCEPTION_REPORT.currentCompanyCode;
        			var $errorsOnly = $("#errorsOnlyCheckbox").prop('checked');
        			console.log($errorsOnly);
        			var $outbound = {"errorFilter":$errorsOnly};
        			
        			
        			
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
            	        language: {
            	            searchBuilder: {
            	                button: {
            	                    0: '<webthing:ban>No</webthing:ban>',
            	                    1: '<webthing:checkmark>Yes</webthing:checkmark>',
            	                }
            	            }
            	        },
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
	        	                        show: [ 0,1,2,3,7,9,15,16,18],
	        	                        hide: [ 4,5,6,8,10,11,12,13,14,17,19,20]
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
            	                        hide: [ 8,9,10,11,12,13,14,15,16,17 ]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Min Pay',
            	                        show: [ 0,1,2,3,4,7,8],
            	                        hide: [ 4,5,6,9,10,11,12,13,14,15,16,17]
            	                    },
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Expenses',
            	                        show: [ 0,1,2,3,9,10,11,12,13,14,15],
            	                        hide: [ 4,5,6,7,8,15,16,17]
            	                    },            	                    
            	                    {
            	                        extend: 'colvisGroup',
            	                        text: 'Out of Area',
            	                        show: [ 0,1,2,3,15,16],
            	                        hide: [ 4,5,6,7,8,10,11,12,13,14,17 ]
            	                    }     
            	                
            	        		
            	        		],
            	        "columnDefs": [
             	            { "orderable": true, "targets": -1 },
             	            { className: "dt-head-center", "targets":[]},
            	            { className: "dt-left", "targets": [3] },
            	            { className: "dt-center", "targets": [4,5,6,8,10,11,12,13,14,19,20,21] },
            	         	{ className: "dt-body-right", "targets": [0,1,2,7,9,15,16,17,18] },
            	            { "visible": false, "targets": [4,5,6,8,10,11,12,13,14,19,20]},
            	       		],
            	         	// "paging": true,
    			        "ajax": {
    			        	"url": "payroll/exceptionReport/" + $companyCode,
    			        	"type": "GET",
    			        	"data": $outbound,
    			        	},
    			        columns: [
        			        	{ title: "Emp Code", width:"5%", searchable:true, data: function ( row, type, set ) {
        			        		//"defaultContent": "<i>N/A</i>", data:'employee_code' }, 
	//        			        	"defaultContent": "<i>N/A</i>", data:'employee_name'  data: function ( row, type, set ) {
	        			        	if(row.employee_code != null){
	       			        			//{return (row.employee_name);{
	       			        			
	        			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
	        			        			//if(row.direct_labor != null){
	        			        				var $value = row.employee_code;
	        			        					//"$" + (parseFloat(row.expenses_submitted).toFixed(2));
	        			        				var $bubbleHelp = []
	        			        				//if ( row.foreign_division != null && row.foreign_division == 1 ) {
	        			        				//	$bubbleHelp.push("Foreign Division Flag");
	        			        				//}
	        			        				if ( row.foreign_company != null && row.foreign_company == 1 ) {
	        			        					$bubbleHelp.push("Non Home Company");
	        			        				}
	        			        				if ( $bubbleHelp.length > 0 ) {
	    			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
	        			        				}
	
	        			        				return $value;
	        			        			}
	    			        		
	    			        		} },
        			        	{ title: "Div", width:"5%", searchable:true, data: function ( row, type, set ) {
            			        	//	"defaultContent": "<i>N/A</i>", data:'employee_name'  data: function ( row, type, set ) {
            			        	if(row.div != null){
           			        			//{return (row.employee_name);{
           			        			
            			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
            			        			//if(row.direct_labor != null){
            			        				var $value = row.div;
            			        					//"$" + (parseFloat(row.expenses_submitted).toFixed(2));
            			        				var $bubbleHelp = []
            			        				if ( row.foreign_division != null && row.foreign_division == 1 ) {
            			        					$bubbleHelp.push("Non Home Division");
            			        				}
            			        			//	if ( row.foreign_company != null && row.foreign_company == 1 ) {
            			        			//		$bubbleHelp.push("Foreign Company Flag");
            			        			//	}
            			        				if ( $bubbleHelp.length > 0 ) {
        			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
            			        				}

            			        				return $value;
            			        			}
        			        		
        			        		} },
        			        		//"defaultContent": "<i>N/A</i>", data:'div' },
        			        	{ title: "Week Ending", width:"5%", searchable:true, searchFormat: "YYYY/MM/DD", "defaultContent": "", data:'formatted_week_ending' },
        			        	{ title: "Name", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_name'},
        			        	
        			        	{ title: "Status", width:"10%", searchable:true, "defaultContent": "<i>N/A</i>", data:'employee_status' },
        			        	{ title: "Union Flag", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent":$unknown,
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
        			        	{ title: "Union Rate", width:"10%", searchable:true, searchFormat: "#.##", "defaultContent": "",
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
    			        		
        			        	{ title: "Under Union Min Flag", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_union_min_pay != null ) {
    			        				if ( row.under_union_min_pay == 1 ) {
    			        					$value = $errorFound;
    			        					//$(row.under_union_min_pay).addClass("background");
    			        					//row.under_union_min_pay.style.backgroundColor = "yellow";
    			        					//$(row.under_union_min_pay).css('background-color','yellow');
        			        				//	$(row.under_union_min_pay).find('td').css('background-color', 'red');
    			        				}
    			        				if ( row.under_union_min_pay == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		{ title: "Minimum Hourly", width:"5%", searchable:true, searchFormat: "#.##", data: function ( row, type, set ) {
									var $value = $unknown;
       			        			if(row.minimum_hourly_pay != null){
										var $value = "$" + (parseFloat(row.minimum_hourly_pay).toFixed(2));
										var $bubbleHelp = []
										if ( row.under_union_min_pay != null && row.under_union_min_pay == 1 ) {
											$bubbleHelp.push("Under Union Min");
										}
										if ( row.under_govt_min_pay != null && row.under_govt_min_pay == 1 ) {
											$bubbleHelp.push("Under Govt Min");
										}
										if ( $bubbleHelp.length > 0 ) {
											$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
										}
									}
									return $value;
    			        		} },
    			        		{ title: "Under Govt Min", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.under_govt_min_pay != null ) {
    			        				if ( row.under_govt_min_pay == 1 ) {
    			        					$value = $errorFound;
    			        				}
    			        				if ( row.under_govt_min_pay == 0 ) {
    			        					$value = $noErrorFound;
    			        				}
    			        			}
    			        			return $value;
    			        			}
    			        		},
    			        		//{ title: "Expense Pct", width:"5%", searchable: true, searchFormat: "#%", 
    			        		//	data: function ( row, type, set ) {
       			        		//	if(row.excess_expense_pct != null){
       			        			//{return (parseFloat(row.excess_expense_pct).toFixed(2)) + "%";}
       			        			
        			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
        			        			//if(row.direct_labor != null){
        			        	//			var $value = row.expenses_submitted + "%";
        			        	//			var $bubbleHelp = []
        			        	//			if ( row.excess_expense_pct != null && row.excess_expense_pct == 1 ) {
        			        	//				$bubbleHelp.push("Expense Pct");
        			        	//			}
        			        	//			if ( row.ytd_excess_expense_pct != null && row.ytd_excess_expense_pct == 1 ) {
        			        	//				$bubbleHelp.push("YTD Expense Pct");
        			        	//			}
        			        	//			if ( $bubbleHelp.length > 0 ) {
    			        		//				$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
        			        	//			}

        			        	//			return $value;
        			        	//		}
    			        		
    			        	//	} },
    			        		{ title: "Expense Pct", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_pct != null ) {
    			        				if ( row.excess_expense_pct == 1 ) {
    			        					$value = $errorFound;
    			        					//$($errorFound).addClass("background");
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
    			        		//{ title: "< Union Min", width:"5%", searchable:true, "defaultContent": "<i>N/A</i>", data:'under_union_min_pay' },
        			        	{ title: "Expense Claim", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
    			        		data:function(row, type, set) {
    			        			var $value = $unknown;
    			        			if ( row.excess_expense_claim != null ) {
    			        				if ( row.excess_expense_claim == 1 ) {
    			        					$value = $errorFound;
    			        					//$($errorFound).addClass("background");
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
    			        		{ title: "YTD Expense Pct", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
        			        		data:function(row, type, set) {
        			        			var $value = $unknown;
        			        			if ( row.ytd_excess_expense_pct != null ) {
        			        				if ( row.ytd_excess_expense_pct == 1 ) {
        			        					$value = $errorFound;
	    			        				//	$($errorFound).addClass("background");
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
       			        		{ title: "YTD Expense Claim", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
           			        		data:function(row, type, set) {
           			        			var $value = $unknown;
           			        			if ( row.ytd_excess_expense_claim != null ) {
           			        				if ( row.ytd_excess_expense_claim == 1 ) {
           			        					$value = $errorFound;
	    			        					//$($errorFound).addClass("background");
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
    			        			if(row.expenses_submitted != null){
    			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
    			        			//if(row.direct_labor != null){
    			        				var $value = "$" + (parseFloat(row.expenses_submitted).toFixed(2));
    			        				var $bubbleHelp = []
    			        				if ( row.excess_expense_claim != null && row.excess_expense_claim == 1 ) {
    			        					$bubbleHelp.push("Expense Claim");
    			        				}
    			        				if ( row.ytd_excess_expense_claim != null && row.ytd_excess_expense_claim == 1 ) {
    			        					$bubbleHelp.push("YTD Expense Pct");
    			        				}
    			        				if ( $bubbleHelp.length > 0 ) {
			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
    			        				}

    			        				return $value;
    			        			}
    			        		} },
    			        		{ title: "Volume", width:"5%", searchable:true, "defaultContent": "",  searchFormat: "#.##", data: function ( row, type, set ){ 
    			        			//if(row.volume != null){return "$" + (parseFloat(row.volume).toFixed(2));}
    			        			if(row.volume != null){
           			        			//{return (parseFloat(row.excess_expense_pct).toFixed(2)) + "%";}
           			        			
            			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
            			        			//if(row.direct_labor != null){
            			        				var $value = "$" + (parseFloat(row.volume).toFixed(2));
            			        				var $bubbleHelp = []
            			        				if ( row.excess_expense_pct != null && row.excess_expense_pct == 1 ) {
            			        					$bubbleHelp.push("Expense Pct");
            			        				}
            			        				if ( row.ytd_excess_expense_pct != null && row.ytd_excess_expense_pct == 1 ) {
            			        					$bubbleHelp.push("YTD Expense Pct");
            			        				}
            			        				if ( $bubbleHelp.length > 0 ) {
        			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
            			        				}

            			        				return $value;
            			        			}
    			        		
    			        		} },
        			        	{ title: "Direct Labor", width:"5%", searchable:true,  searchFormat: "#.##", data: function ( row, type, set ) {
    			        			if(row.direct_labor != null){
    			        				var $value = "$" + (parseFloat(row.direct_labor).toFixed(2));
    			        				var $bubbleHelp = []
    			        				if ( row.excess_expense_claim != null && row.excess_expense_claim == 1 ) {
    			        					$bubbleHelp.push("Expense Claim");
    			        				}
    			        				//if ( row.under_govt_min_pay != null && row.under_govt_min_pay == 1 ) {
    			        				//	$bubbleHelp.push("Under Government Min");
    			        				//}
    			        				if ( $bubbleHelp.length > 0 ) {
			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
    			        				}

    			        				return $value;
    			        			}
		            			} },
        			        	{ title: "YTD Direct Labor", width:"5%", searchable:true,  searchFormat: "#.##", data: function ( row, type, set ) {
    			        			//if(row.ytd_direct_labor != null){return "$" + (parseFloat(row.ytd_direct_labor).toFixed(2));}
    			        			if(row.ytd_direct_labor != null){
           			        			//{return (parseFloat(row.excess_expense_pct).toFixed(2)) + "%";}
           			        			
            			        			//{return "$" + (parseFloat(row.expenses_submitted).toFixed(2));}
            			        			//if(row.direct_labor != null){
            			        				var $value = "$" + (parseFloat(row.ytd_direct_labor).toFixed(2));
            			        				var $bubbleHelp = []
            			        				if ( row.ytd_excess_expense_claim != null && row.ytd_excess_expense_claim == 1 ) {
            			        					$bubbleHelp.push("YTD Expense Claim");
            			        				}
            			        				//if ( row.ytd_excess_expense_pct != null && row.ytd_excess_expense_pct == 1 ) {
            			        				//	$bubbleHelp.push("YTD Expense Pct");
            			        			//	}
            			        				if ( $bubbleHelp.length > 0 ) {
        			        						$value = EXCEPTION_REPORT.makeItRed($value, $bubbleHelp);
            			        				}

            			        				return $value;
            			        			}
        			        		
        			        		} },
        			        	
        			        	{ title: "Non Home Company", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "", 
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
        			        	{ title: "Non Home Division", width:"5%", searchable:true, searchFormat: "0|1", "defaultContent": "",
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
        			        	{ title: "Action",  width:"5%", searchable:false,  orderable: false,
        			            	data: function ( row, type, set ) { 
        			            		var $viewLink = '<span class="action-link view-link" data-id="'+ row.row_id +'"><webthing:view>Detail</webthing:view></span>';
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
        			            	
        			            	
        			            //	$(".view-link").off("click");
        			            //	$(".view-link").click(function($clickevent) {
        			            //		var $myRow = $(this).attr("data-id");
        			            //		console.log("exception row id: " + $myRow);        			            		
        			        	//		EXCEPTION_REPORT.displayExceptionModal($myRow);
        			            //	});   
        			            },
        			            "drawCallback": function( settings ) {
        			            	//CALL_NOTE.lookupLink();


        			            	$(".view-link").off("click");
        			            	$(".view-link").click(function($clickevent) {
        			            		var $myRow = $(this).attr("data-id");
        			            		console.log("exception row id: " + $myRow);        			            		
        			        			EXCEPTION_REPORT.displayExceptionModal($myRow);
        			            	});    
        			            },
        			            
        			            
        			    } );
                	},
        		
        		getReport : function($companyCode) {
        			var $url = "payroll/exceptionReport/" + $companyCode;
        			ANSI_UTILS.makeServerCall("GET", $url, {$companyCode}, {200:EXCEPTION_REPORT.getReportSuccess}, {});
        		},
        		
        		
        		
        		
        		
        		getReportSuccess : function($data, $passThru) {
        			console.log("getReportSuccess");
        			//if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
        				EXCEPTION_REPORT.displayReport($data);
        			//} else {
        			//	$("#prompt-div .companyCodeErr").html($data.data.webMessages['companyCode'][0]);
        			//}
        		},
        		
        	//	errorFound : function()
        		
        		makeItRed : function($value, $bubbleHelp) {
        			var $helptext = $bubbleHelp.join("<br />");
        			return '<span class="red tooltip"><span class="tooltiptext">' + $helptext + '</span>' + $value + '</span>';
        		}

        		
        	};
        	
        	EXCEPTION_REPORT.init();
        	
        	
        	
        	
        	
        });
        		
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Payroll Exception Report</h1> 
    	
    	
		<div id="prompt-div">
			<span class="err companyCodeErr"></span><br />
	    	<select name="companyCode">
				<option value=""></option>
				<ansi:selectOrganization type="COMPANY" active="true" />
			</select>
			<input type="checkbox" id="errorsOnlyCheckbox" /><label for="errorsOnly">Exceptions Only</label>
			
			<div style="clear:both; width:100%;font-size:1px;">&nbsp;</div>	
    		<webthing:lookupFilter filterContainer="filter-container" />
			<h3 id="report-label"></h3>			
			<table id="exceptionReportTable" class="row-border hover order-column">
			</table>
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
					<td><span class="unionMember" />
						<input type="hidden" name="selectedUnionMember" />
					<td><span class="err unionMemberErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Union Code:</td>
					<td><input type="text" name="unionCode" readonly="true" />
						<input type="hidden" name="selectedUnionCode" />
					<td><span class="err unionCodeErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Union Rate:</td>
					<td><input type="text" name="unionRate" readonly="true" />
						<input type="hidden" name="selectedUnionRate" />
					<td><span class="err unionRateErr"></span></td>
				</tr>	
				<tr>
					<td class="form-label">Under Union Min Pay:</td>
					<td><span class="underUnionMinPay" />
						<input type="hidden" name="selectedUnderUnionMinPay" />
					<td><span class="err underUnionMinPayErr"></span></td>
				</tr>		
				<tr>
					<td class="form-label">Minimum Hourly Pay:</td>
					<td><input type="select" name="minimumHourlyPay" readonly="true" />
						<input type="hidden" name="selectedMinimumHourlyPay" />
					<td><span class="err minimumHourlyPayErr"></span></td>
				</tr>		
				<tr>
					<td class="form-label">Excess Expense Pct:</td>
					<td><span class="excessExpensePct" />
						<input type="hidden" name="selectedExcessExpensePct" />
					<td><span class="err excessExpensePctErr"></span></td>
				</tr>			
				<tr>
					<td class="form-label">Excess Expense Claim:</td>
					<td><span class="excessExpenseClaim" />
						<input type="hidden" name="selectedExcessExpenseClaim" />
					<td><span class="err excessExpenseClaimErr"></span></td>
				</tr>		
				<tr>
					<td class="form-label">YTD Excess Expense Pct:</td>
					<td><span class="ytdExcessExpensePct" />
						<input type="hidden" name="selectedYtdExcessExpensePct" />
					<td><span class="err ytdExcessExpensePctErr"></span></td>
				</tr>			
				<tr>
					<td class="form-label">YTD Excess Expense Claim:</td>
					<td><span class="ytdExcessExpenseClaim" />
						<input type="hidden" name="selectedYtdExcessExpenseClaim" />
					<td><span class="err ytdExcessExpenseClaimErr"></span></td>
				</tr>		
				<tr>
					<td class="form-label">Expenses Submitted:</td>
					<td><input type="text" name="expensesSubmitted" readonly="true" />
						<input type="hidden" name="selectedExpensesSubmitted" />
						</td>
					<td><span class="err expensesSubmittedErr"></span></td>
				</tr>							
				<tr>
					<td class="form-label">Volume:</td>
					<td><input type="text" name="volume" readonly="true" />
						<input type="hidden" name="selectedVolume" />
						</td>
					<td><span class="err volumeErr"></span></td>
				</tr>						
				<tr>
					<td class="form-label">Direct Labor:</td>
					<td><input type="text" name="directLabor" readonly="true" />
						<input type="hidden" name="selectedDirectLabor" />
						</td>
					<td><span class="err directLaborErr"></span></td>
				</tr>						
				<tr>
					<td class="form-label">YTD Direct Labor:</td>
					<td><input type="text" name="ytdDirectLabor" readonly="true" />
						<input type="hidden" name="selectedYtdDirectLabor" />
						</td>
					<td><span class="err ytdDirectLaborErr"></span></td>
				</tr>						
				<tr>
					<td class="form-label">Non Standard Company:</td>
					<td><span class="foreignCompany" />
						<input type="hidden" name="selectedForeignCompany" />
						</td>
					<td><span class="err departmentErr"></span></td>
				</tr>
				<tr>
					<td class="form-label">Non Standard Division:</td>
					<td>
						<span class="foreignDivision" />
						<input type="hidden" name="selectedForeignDivision" />
					</td>
					<td><span class="err foreignDivisionErr"></span></td>
				</tr>
			</table>
		</div>
		
		
    </tiles:put>
		
</tiles:insert>

