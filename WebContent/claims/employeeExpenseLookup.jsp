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
        Employee Expense Lookup
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
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
			
        </style>
        
        <script type="text/javascript">
        
        $(document).ready(function() {
        	;EMPLOYEEEXPENSELOOKUP = {
        		datatable : null,
        		
        		init : function() {
        			EMPLOYEEEXPENSELOOKUP.createTable();
        			EMPLOYEEEXPENSELOOKUP.makeModal();
        			EMPLOYEEEXPENSELOOKUP.makeOptionList('EXPENSE_TYPE', EMPLOYEEEXPENSELOOKUP.populateOptionList);
        			EMPLOYEEEXPENSELOOKUP.makeClickers();
        			EMPLOYEEEXPENSELOOKUP.makeDivisionList();
        			EMPLOYEEEXPENSELOOKUP.makeAutoComplete();
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
            	            { className: "dt-left", "targets": [0] },
            	            { className: "dt-center", "targets": [1,2,3,4,5,6] },
            	            { className: "dt-right", "targets": []}
            	         ],
            	        "paging": true,
    			        "ajax": {
    			        	"url": "claims/employeeExpenseLookup",
    			        	"type": "GET"
    			        	},
    			        columns: [
    			        	
    			            { title: "Name", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
    			            	if(row.lastName != null || row.firstName != null){return (row.lastName+", "+row.firstName);}
    			            } },
    			            { title: "Week", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.week != null){return (row.week+"");}
    			            } },
    			            { title: "Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.date != null){return (row.date+"");}
    			            } },
    			            { title: "Expense Type", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.expenseType != null){return (row.expenseType+"");}
    			            } },
//    			            { title: "Hrs Type" , "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
//    			            	return '<span class="tooltip">' + row.hoursType + '<span class="tooltiptext">' + row.hoursDescription + '</span></span>'
//    			            } },
    			            { title: "Amount", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.amount != null){return (row.amount+"");}
    			            } },
    			            { title: "Notes",  "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
    			            	if(row.notes != null){return (row.notes+"");}
    			            } },			            
    			            { title: "<bean:message key="field.label.action" />",  data: function ( row, type, set ) {	
    			            	{
    				            	var $edit = '<a href="#" class="editAction" data-id="'+row.laborId+'"><webthing:edit>Edit</webthing:edit></a>';
    			            		return "<ansi:hasPermission permissionRequired='CLAIMS_WRITE'>"+$edit+"</ansi:hasPermission>";
    			            	}
    			            	
    			            } }],
    			            "initComplete": function(settings, json) {
    			            	EMPLOYEEEXPENSELOOKUP.doFunctionBinding();
    			            },
    			            "drawCallback": function( settings ) {
    			            	EMPLOYEEEXPENSELOOKUP.doFunctionBinding();
    			            }
    			    } );
            		//new $.fn.dataTable.FixedColumns( dataTable );
            	},
        		
        		
            	
            	doFunctionBinding : function () {
					$( ".editAction" ).on( "click", function($clickevent) {
						var $laborId = $(this).attr("data-id");
						EMPLOYEEEXPENSELOOKUP.doGetLabor($laborId);
					});
				},
            	
            	
            	
				
				doGetLabor : function($laborId) {
					console.log("getting labor: " + $laborId)
					var $url = 'claims/employeeExpenseLookup/' + $laborId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						statusCode: {
							200 : function($data) {
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
					var $url = 'claims/employeeExpenseLookup/' + $laborId;
        			
            		var $outbound = {};
            		$.each( $("#ndl-crud-form input"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
            		$.each( $("#ndl-crud-form select"), function($index, $value) {
            			$outbound[$($value).attr("name")] = $($value).val();
            		});
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
								$("#globalMsg").html("System Error Division 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Division 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
            		
            		
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
            			EMPLOYEEEXPENSELOOKUP.clearForm();
            			$( "#ndl-crud-form ").attr("data-laborid","add")
            			$( "#ndl-crud-form" ).dialog("open");
            		});
            		
            		$('.dateField').datepicker({
                        prevText:'&lt;&lt;',
                        nextText: '&gt;&gt;',
                        showButtonPanel:true
                    });
            		
            		
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
						title:'Employee Expense',
						autoOpen: false,
						height: 500,
						width: 700,
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
									EMPLOYEEEXPENSELOOKUP.doPost();
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
	    			var $select = $("#ndl-crud-form select[name='expenseType']");
					$('option', $select).remove();
					$select.append(new Option("",""));
					$.each($data.expenseType, function(index, val) {
					    $select.append(new Option(val.display, val.code));
					});
	    		},
	    		
        	}
      	  	

        	EMPLOYEEEXPENSELOOKUP.init();
        	
        });
        </script>        
    </tiles:put>
    
   <tiles:put name="content" type="string">
    	<h1>Employee Expense</h1>
    	
 	<table id="displayTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
       	<colgroup>
        	<col style="width:10%;" />
        	<col style="width:5%;" />
    		<col style="width:5%;" />    		
    		<col style="width:10%;" />
    		<col style="width:10%;" />
    		<col style="width:50%;" />
    		<col style="width:10%;" />
   		</colgroup>
        <thead>
            <tr>
                <th>Name</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Expense Type</th>
    			<th>Amount</th>
    			<th>Notes</th>
    			<th>Action</th>    			
            </tr>
        </thead>
        <tfoot>
            <tr>
                <th>Name</th>
                <th>Week</th>
    			<th>Date</th>
    			<th>Expense Type</th>
    			<th>Amount</th>
    			<th>Notes</th>   
    			<th>Action</th>  			
            </tr>
        </tfoot>
    </table>
    <input type="button" value="New" class="prettyWideButton" id="new-NDL-button" />
    
    <webthing:scrolltop />
    
    <div id="ndl-crud-form">
    	<table>
    		<tr>
    			<td><span class="formLabel">Name</span></td>
    			<td><input name="washerName" /><input type="hidden" name="washerId" /></td>
    			<td><span id="washerIdErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Date</span></td>
    			<td><input type="text" name="date" class="dateField" /></td>
    			<td><span id="dateErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Expense Type</span></td>
    			<td><select name="expenseType" /></td>
    			<td><span id="expenseTypeErr" class="err"></span></td>
    		</tr>
    		<tr>
    			<td><span class="formLabel">Amount</span></td>
    			<td><input type="text" name="amount" /></td>
    			<td><span id="amountErr" class="err"></span></td>
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

