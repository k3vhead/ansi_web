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
        Budget Control
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	#filter-container {
        		width:402px;
        		float:right;
        	}
        	#filter-banner {
        		display:none;
        	}
        	#filter-banner .is-filtered {
        		display:none;
        	}
        	#filter-div {
        		width:402px;
        		border:solid 1px #000000;
        		display:none;
        	}
        	#ndl-crud-form {
        		display:none;
        		background-color:#FFFFFF;
				color:#000000;
				width:400px;
				padding:15px;
        	}
			
			
			.prettyWideButton {
				height:30px;
				min-height:30px;
			}
			.jobTitleRow {
				background-color:#404040; 
				cursor:pointer; 
				padding-left:4px;
				color:#FFFFFF;
				width:400px;
			}
			.jobTitleRow .panel-button-container .save-job {
				display:none;
			}
			.jobTitleRow .panel-button-container .cancel-job-edit {
				display:none;
			}
			.job-data-closed {
				color:#FFFFFF;
			}
			.job-data-open {
				color:#FFFFFF;
				display:none;
			}
			.job-header-job-div {
				display:inline; 
				margin-right:10px;
			}
			.panel-button-container {
				float:right; 
				margin-right:8px;
				width:6%; 
				background-color:#e5e5e5; 
				border:solid 1px #404040; 
				text-align:center;
				display:none;
			}
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;BUDGETCONTROL = {
        		datatable : null,
        		ticketFilter : '<c:out value="${BUDGET_CONTROL_TICKET_FILTER}" />',
        		
        		init : function() {
        			BUDGETCONTROL.createTable();
        			BUDGETCONTROL.makeModal();
        			BUDGETCONTROL.makeOptionList('WORK_HOURS_TYPE', BUDGETCONTROL.populateOptionList)
        			BUDGETCONTROL.makeClickers();
        			BUDGETCONTROL.makeDivisionList();
        			BUDGETCONTROL.makeAutoComplete();
        			
        			
        		},
        		
        		
        		
        		calculatePay : function() {
        			console.log("calculatePay");
					var $url = 'claims/calculateNdlPay';
        			
            		var $outbound = {};
            		$.each( $("#ndl-crud-form input"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		$.each( $("#ndl-crud-form select"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		console.log($outbound);
            		var jqxhr3 = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200:function($data) {
								$("#ndl-crud-form .err").html("");
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									$("#calcPayAmt").html($data.data.calculatedPay);
								} else {
									$("#calcPayAmt").html("ERR");
									//$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support").show();
									//$("#ndl-crud-form").dialog("close");
								}
							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error NDLPAY 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error NDLPAY 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
        		},
        		
        		
        		
        		clearForm : function() {
        			$("#ndl-crud-form select[name='divisionId']").val();
        			$.each( $("#ndl-crud-form input"), function($index, $value) {        				
        				var $selector = '#ndl-crud-form input[name="' + $value.name + '"]';
        				$($selector).val("");
        				var $errSelector = '#' + $value.name + "Err";
        				$($errSelector).html("");
        			});
        			$.each( $("#ndl-crud-form select"), function($index, $value) {        				
        				var $selector = '#ndl-crud-form select[name="' + $value.name + '"]';
        				$($selector).val("");
        				var $errSelector = '#' + $value.name + "Err";
        				$($errSelector).html("");
        			});
        		},
        		
        		
        		
        		createTable : function() {
        			var $url = "claims/budgetControlLookup";
        			if ( BUDGETCONTROL.ticketFilter != '' ) {
        				$url = $url + "/" + BUDGETCONTROL.ticketFilter;
        			}
        				
            		var dataTable = $('#displayTable').DataTable( {
            			"aaSorting":		[[0,'asc']],
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
            	        lengthMenu: [
            	        	[ 10, 50, 100, 500, 1000 ],
            	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
            	        ],
            	        buttons: [
            	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
            	        ],
            	        "columnDefs": [
             	            { "orderable": false, "targets": -1 },
            	            { className: "dt-left", "targets": [0,1,2] },
            	            { className: "dt-center", "targets": [3,14] },
            	            { className: "dt-right", "targets": [4,5,6,7,8,9,10,11,12,13]}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": $url,
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	{ title: "Div", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.div != null){return (row.div+"");}
    			            } },
    			            { width:"23%", title: "Account", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.job_site_name != null){return (row.job_site_name+"");}
    			            } },
    			            { title: "Ticket", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_id != null){return (row.ticket_id+"");}
    			            } },
    			            { title: "Status", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
    			            	if(row.ticket_status != null){return ('<span class="tooltip">' + row.ticket_status + '<span class="tooltiptext">' + row.ticket_status_description + '</span></span>');}
    			            } },
    			            { title: "Direct Labor", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_amt != null){return (row.claimed_weekly_dl_amt.toFixed(2));}
    			            } },
    			            { title: "+ Expenses", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_exp != null){return (row.claimed_weekly_dl_exp);}
    			            } },
    			            { title: "= Total", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_weekly_dl_total != null){return (row.claimed_weekly_dl_total.toFixed(2));}
    			            } },
    			            { title: "Total Volume" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.total_volume != null){return (row.total_volume.toFixed(2));}
    			            } },
    			            { title: "Volume Claimed" , "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_volume_total != null){return (row.claimed_volume_total.toFixed(2));}
    			            } },
    			            { title: "Volume Remaining", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.volume_remaining != null){return ( row.volume_remaining.toFixed(2));}
    			            } },
    			            { title: "Billed Amount",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.billed_amount != null){return ( row.billed_amount.toFixed(2));}
    			            } },			            
    			            { title: "Diff CLM/BLD",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.claimed_vs_billed != null){return ( row.claimed_vs_billed.toFixed(2));}
    			            } },
    			            { title: "Paid Amount",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.paid_amt != null){return ( parseFloat(row.paid_amt).toFixed(2));}
    			            } },
    			            { title: "Amount Due",  "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
    			            	if(row.amount_due != null){return ( parseFloat(row.amount_due).toFixed(2));}
    			            } },
    			            { 
    			            	title: "<bean:message key="field.label.action" />",
    			            	searchable:false,
    			            	data: function ( row, type, set ) {{
    				            	var $claim = '<a href="#" class="editAction" data-id="'+row.ticket_id+'"><webthing:invoiceIcon styleClass="green">Claim</webthing:invoiceIcon></a>';
    				            	var $expense = '<a href="#" class="editAction" data-id="'+row.ticket_id+'"><webthing:invoiceIcon styleClass="orange">Expense</webthing:invoiceIcon></a>';
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$claim+" "+$expense+"</ansi:hasPermission>";
    			            	}}
    			            }],
    			            "initComplete": BUDGETCONTROL.initComplete,
    			            "drawCallback": BUDGETCONTROL.drawCallback
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            		
            		
            		
            	},
            	
            	
        		
            	initComplete : function() {
            		var myTable = this;
	            	BUDGETCONTROL.doFunctionBinding();
	            	var $filterTable = $("<table>");
	            	
	            	var dataTable = $('#displayTable').DataTable();
	            	var columns = myTable.api().init().columns;

	            	$("#filter-div").append($filterTable);

	            	dataTable.columns().every( function(colIdx) {
	            		var $column = this;
				
	            		if ( columns[colIdx].searchable ) {
		            		var $filterRow = $("<tr>");
		            		var $titleCell = $("<td>");
		            		var $fieldCell = $("<td>");
		            		
		            		$titleCell.append($column.header().innerText);
		            		$inputField = $('<input type="text">')
		            		$fieldName = "columns["+colIdx+"][search][value]";
		            		$inputField.attr("name", $fieldName);
		            		$fieldCell.append($inputField);
		            		
		            		
		            		$filterRow.append($titleCell);
		            		$filterRow.append($fieldCell);
		            		$filterTable.append($filterRow);
		            		
		            		var $selector = '#filter-div input[name="' + $fieldName + '"]';
		            		$($selector).on('keyup change', function() {
	            				var dataTable = $('#displayTable').DataTable();
	            				myColumn = dataTable.columns(colIdx);
	            				console.log("searching " + this.value + " in " + colIdx);
	           					myColumn.search(this.value).draw();
	           					
	           					var isFiltered = false;
	           					$("#filter-div input").each(function(idx, field) {
	           						if ( $(field).val().length > 0 ) {
	           							isFiltered = true;
	           						}
	           					});
	           					if ( isFiltered == true ) {
	           						$("#filter-banner .is-filtered").show();
	           					} else {
	           						$("#filter-banner .is-filtered").hide();
	           					}
	            			});
	            		}
	            	});
	            	
	            	$("#filter-banner").show();
	            	$("#filter-banner .job-hider .job-data-closed").click(function($event) {
	            		$("#filter-banner .job-hider .job-data-closed").hide();
	            		$("#filter-banner .job-hider .job-data-open").show();
	            		$("#filter-div").fadeIn(1000);	            		
	            		$("#filter-banner .panel-button-container").fadeIn(1000); 
	            	});
	            	$("#filter-banner .job-hider .job-data-open").click(function($event) {
	            		$("#filter-banner .panel-button-container").fadeOut(1000); 
	            		$("#filter-banner .job-hider .job-data-open").hide();
	            		$("#filter-banner .job-hider .job-data-closed").show();
	            		$("#filter-div").fadeOut(1000);
	            	});
	            	$("#filter-banner .panel-button-container .clear-filter-button").click(function($event) {
	            		$("#filter-div input").val("");
	            		$("#filter-banner .is-filtered").hide();
	            		$("#filter-banner .job-hider .job-data-open").click();
	            		$('#displayTable').DataTable().ajax.reload();
	            	});

            	},
	            
	            
	            drawCallback : function(settings, json) {
	            	BUDGETCONTROL.doFunctionBinding();
	            },
            	
	            
	            
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $laborId = $(this).attr("data-id");
						BUDGETCONTROL.doGetLabor($laborId);
					});
				},
            	
            	
            	
				
				doGetLabor : function($laborId) {
					console.log("getting labor: " + $laborId)
					var $url = 'claims/nonDirectLabor/' + $laborId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
								BUDGETCONTROL.clearForm();
								$("#ndl-crud-form").attr("data-laborid",$laborId);
								$.each( $("#ndl-crud-form input"), function($index, $value) {
									var $name = $($value).attr("name");
									var $selectorName = '#ndl-crud-form input[name="' + $name + '"]';
									$($selectorName).val($data.data.item[$name]);
			            		});
								$('#ndl-crud-form input[name="washerName"]').val($data.data.item.firstName + " " + $data.data.item.lastName);
			            		$.each( $("#ndl-crud-form select"), function($index, $value) {
			            			var $name = $($value).attr("name");
									var $selectorName = '#ndl-crud-form select[name="' + $name + '"]';
									$($selectorName).val($data.data.item[$name]);
			            		});
								$("#ndl-crud-form").dialog("open");
							},
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("Invalid labor id. Reload and try again").show();
							},
							405: function($data) {
								$("#globalMsg").html("System Error 405. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error 500. Contact Support").show();
							},
						},
						dataType: 'json'
					});
				},
				
				
            	
            	doPost : function() {
            		console.log("doPost");
        			var $laborId = $( "#ndl-crud-form ").attr("data-laborid");
					var $url = 'claims/nonDirectLabor/' + $laborId;
        			
            		var $outbound = {};
            		$.each( $("#ndl-crud-form input"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		$.each( $("#ndl-crud-form select"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		console.log($outbound);
            		var jqxhr3 = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						statusCode: {
							200:function($data) {
								$("#ndl-crud-form .err").html("");
								if ( $data.responseHeader.responseCode == 'SUCCESS') {
									$("#displayTable").DataTable().ajax.reload();
									$("#globalMsg").html("Success").show().fadeOut(3000);
									$("#ndl-crud-form").dialog("close");
								} else if ($data.responseHeader.responseCode == 'EDIT_FAILURE') {
									$.each($data.data.webMessages, function($index, $value) {
										var $errSelector = '#' + $index + "Err";
				        				$($errSelector).html($value[0]);
									});
								} else {
									$("#globalMsg").html("Invalid response code " + $data.responseHeader.responseCode + ". Contact Support").show();
									$("#ndl-crud-form").dialog("close");
								}

							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error NDL 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error NDL 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
            		
            		
            	},
            	
            	
            	
            	formatPay : function($amt) {
            		return "$" + $amt.toFixed(2);
            	},
            	
            	
            	
            	makeAutoComplete : function() {
            		var $displaySelector = '#ndl-crud-form input[name="washerName"]';
            		var $idSelector = '#ndl-crud-form input[name="washerId"]';
            		var $washerAutoComplete = $($displaySelector).autocomplete({
						source: "washerTypeAhead?",
						select: function( event, ui ) {
							$( $idSelector ).val(ui.item.id);								
   				      	},
						response: function(event, ui) {
							if (ui.content.length === 0) {
								$("#washerIdErr").html("No Washer Found");
					        } else {
					        	$("#washerIdErr").html("");
					        }
						}
					});	
            	},
            	
            	
            	
            	
            	makeClickers : function() {
            		$('.ScrollTop').click(function() {
        				$('html, body').animate({scrollTop: 0}, 800);
              	  		return false;
              	    });
            		
            		$("#new-NDL-button").click(function($event) {
            			BUDGETCONTROL.clearForm();
            			$( "#ndl-crud-form ").attr("data-laborid","add")
            			$( "#ndl-crud-form" ).dialog("open");
            		});
            		
            		$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
            		
            		$('.calcPayTrigger').bind("change", BUDGETCONTROL.calculatePay);
            	},
            	
            	
            	
            	makeDivisionList : function() {
					console.log("makeDivisionList");
					var jqxhr3 = $.ajax({
						type: 'GET',
						url: 'division/list',
						data: {},
						statusCode: {
							200:function($data) {
								var $select = $("#ndl-crud-form select[name='divisionId']");
								$('option', $select).remove();

								$select.append(new Option("",""));
								$.each($data.data.divisionList, function(index, val) {
								    $select.append(new Option(val.divisionNbr + "-" + val.divisionCode, val.divisionId));
								});
							},
							403: function($data) {								
								$("#globalMsg").html("Session Expired. Log In and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Division 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Division 500. Contact Support").show();
							}
						},
						dataType: 'json',
						async:false
					});
				},
				
            	
            	
				makeModal : function() {
					$( "#ndl-crud-form" ).dialog({
						title:'Non-Direct Labor',
						autoOpen: false,
						height: 400,
						width: 500,
						modal: true,
						closeOnEscape:true,
						//open: function(event, ui) {
						//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//},
						buttons: [
							{
								id: "ndl-cancel-button",
								click: function($event) {
									$( "#ndl-crud-form" ).dialog("close");
								}
							},{
								id: "ndl-save-button",
								click: function($event) {
									BUDGETCONTROL.doPost();
								}
							}
						]
					});	
					$("#ndl-save-button").button('option', 'label', 'Save');
					$("#ndl-cancel-button").button('option', 'label', 'Cancel');
        		},
        		
        		
        		
        		
        		makeOptionList : function($optionList, $callBack) {
					console.log("getOptions");
	    			var $returnValue = null;
	    			var jqxhr1 = $.ajax({
	    				type: 'GET',
	    				url: 'options',
	    				data: $optionList,			    				
	    				statusCode: {
	    					200: function($data) {
	    						$callBack($data.data);		    						
	    					},			    				
	    					403: function($data) {
	    						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
	    					}, 
	    					404: function($data) {
	    						$("#globalMsg").html("System Error Option 404. Contact Support").show();
	    					}, 
	    					405: function($data) {
	    						$("#globalMsg").html("System Error Option 405. Contact Support").show();
	    					}, 
	    					500: function($data) {
	    						$("#globalMsg").html("System Error Option 500. Contact Support").show();
	    					}, 
	    				},
	    				dataType: 'json'
	    			});
	    		},
	    		
	    		
	    		
	    		populateOptionList : function($data) {
	    			var $select = $("#ndl-crud-form select[name='hoursType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each($data.workHoursType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
	    		},
	    		
        	}
      	  	

        	BUDGETCONTROL.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Budget Control</h1>
    	
    <div id="filter-container">
	    <div id="filter-banner" class="jobTitleRow">
	        <div class="panel-button-container"> 
	        	<webthing:ban styleClass="clear-filter-button red">Clear</webthing:ban>
			</div>
			<div class="job-hider">
				<span class="job-data-closed"><i class="fas fa-caret-right"></i></span>
				<span class="job-data-open"><i class="fas fa-caret-down"></i></span>
	            &nbsp;
	            <div class="job-header-job-div">
	            	<span class="formLabel">Filter</span>
	            	<webthing:checkmark styleClass="green is-filtered">Filter is Active</webthing:checkmark>
	            </div>
	        </div>
	   	</div>
	   	<div id="filter-div">
	   	</div>
   	</div>
   	
	<div  style="margin-bottom:5px; width:100%;">&nbsp;</div>

	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
        <thead>
        </thead>
        <tfoot>
        </tfoot>
    </table>
    <input type="button" value="New" class="prettyWideButton" id="new-NDL-button" />
    
    <webthing:scrolltop />
    
    <div id="ndl-crud-form">
    	<table>
    		<tr>
    			<td><span class="formLabel">Division</span></td>
    			<td><select name="divisionId" class="calcPayTrigger"></select></td>
    			<td><span id="divisionIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Date</span></td>
    			<td><input type="text" name="workDate" class="dateField calcPayTrigger" /></td>
    			<td><span id="workDateErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Washer</span></td>
    			<td><input type="text" name="washerName" class="calcPayTrigger" /><input type="hidden" name="washerId" /></td>
    			<td><span id="washerIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Hours</span></td>
    			<td><input type="text" name="hours" class="calcPayTrigger" /></td>
    			<td><span id="hoursErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Type</span></td>
    			<td><select  name="hoursType" class="calcPayTrigger"></select></td>
    			<td><span id="hoursTypeErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Calculated Pay</span></td>
    			<td><span  id="calcPayAmt"></span></td>
    			<td>&nbsp;</td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Actual Pay</span></td>
    			<td><input type="text" name="actPayoutAmt" /></td>
    			<td><span id="actPayAmtErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Notes</span></td>
    			<td><input type="text" name="notes" /></td>
    			<td><span id="notesErr" class="err"></span></td>
    		</tr>
    	</table>
    </div>
    </tiles:put>
		
</tiles:insert>

