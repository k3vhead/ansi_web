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

<%@ page import="com.ansi.scilla.web.common.actionForm.MessageForm" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Invoice Generation (Div)
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <script type="text/javascript" src="js/jobMaintenance.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
        	#invoiceGenPanel {
        		width:80%;
        		padding-left:40px;
        	}
			#goButton {
				display:none;
			}
			#divisionTable .even { 
				border:solid 1px #404040;
				background-color:#DEDEDE;
			}
        </style>
        
        <script type="text/javascript">    
        $( document ).ready(function() {
	        ;INVOICE_GEN = {
	        
	        	init : function() {
	        		INVOICE_GEN.makeDivisionList();
	            	
	                $('.dateField').datepicker({
	                    prevText:'&lt;&lt;',
	                    nextText: '&gt;&gt;',
	                    showButtonPanel:true
	                });

	                $("input[name='selectAll']").click( function($event) {
	                	console.log("selectAll");
	                	var $checked = $("input[name='selectAll']").prop("checked");
	                	$.each( $("input[name='divisionId']"), function($index, $value) {							
							$($value).prop("checked",$checked);
	                	});
	                });
	                
	                
	            	$("#goButton").click(function($event){
	                	var $invoiceDate = $("#invoiceDate").val();
	                	var $monthlyFlag = $("#monthlyFlag").prop('checked');
	                	var $divisionId = []
	                	$.each( $("input[name='divisionId']"), function($index, $value) {							
							if ( $($value).prop("checked") ) {
								$divisionId.push($($value).val());
							}
	                	});
	                	var $outbound = {'invoiceDate':$invoiceDate, 'monthlyFlag':$monthlyFlag, 'divisionId':$divisionId};
	                	console.log( JSON.stringify($outbound));
	                    var jqxhr = $.ajax({
	            			type: 'POST',
	            			url: 'invoiceGeneration/',
	            			data: JSON.stringify($outbound),
	            			statusCode: {
	            				200: function($data) {
	            					if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
		            					$.each($data.data.webMessages, function (key, value) {
		            						var $selectorName = "#" + key + "Err";
		            						$($selectorName).show();
		            						$($selectorName).html(value[0]).fadeOut(4000);
		            					});
		            				} else {
		            					//$("#globalMsg").html($data.responseHeader.responseMessage).fadeOut(4000);
		            		        	//$("#invoiceDate").val("");
		            		        	//$("#monthlyFlag").prop('checked', false);
		            		        	console.debug("Invoices genned");
		            		        	$("#printForm input[name=message]").val("Success! Invoices Generated");
		            					console.debug("form submit");
		            				    $("#printForm").submit();
		            		        	
		            				}
	            				},
	            				403: function($data) {
	            					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
	            				},
	            				500: function($data) {
	                 	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
	                 	    	} 
	            			},
	            			dataType: 'json'
	            		});        	
	                });

	        	},
	        	
	        	
	        	
	        	makeDivisionList : function() {
	        		var dataTable =  $('#divisionTable').DataTable( {
        				"aaSorting":		[[1,'asc']],
	        			"processing": 		true,
	        	        "serverSide": 		true,
	        	        "autoWidth": 		false,
	        	        "deferRender": 		true,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        "searching": 		false,
	        	        "searchDelay":		800,
	        	        "bInfo":			false, // hide the record count
	        	        "pageLength":		1000,
	        	        //lengthMenu: [
	        	        //	[ 10, 50, 100, 500, 1000 ],
	        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
	        	        //],
	        	        buttons: [
	        	        //	'pageLength',
	        	        //	'copy', 
	        	        //	'csv', 
	        	        //	'excel', 
	        	        //	{extend: 'pdfHtml5', orientation: 'landscape'}, 
	        	        //	'print',
	        	        //	{extend: 'colvis',	label: function () {INVOICELOOKUP.doFunctionBinding();}}
	        	        ],
	        	        "columnDefs": [
	//         	            { "orderable": false, "targets": -1 },  // Need to re-add this when we add the action column back in
	        	            { className: "dt-left", "targets": [1,2] },
	        	            { className: "dt-center", "targets": [0] },
	        	            { className: "dt-right", "targets": []}
	        	         ],
	        	        "paging": false,
				        "ajax": {
				        	"url": "divisionList",
				        	"type": "GET",
				        	"data":{},
				        	"dataSrc":"data.divisionList"
				        	},
				        columns: [
				            { title: "", width:"5%", searchable:true, orderable:false, "defaultContent": "<i>N/A</i>", data:function ( row, type, set ) {
				            	return '<input type="checkbox" name="divisionId" value="'+row.division_id+'" />'
				            } },
				            { title: "Division", width:"10%", searchable:true, orderable:true, "defaultContent": "<i>N/A</i>", data: "div"},
				            { title: "Description", width:"40%", searchable:true, orderable:true, "defaultContent": "<i>N/A</i>", data: "description"},
				            { title: "", width:"45%", searchable:false, orderable:false, "defaultContent": ""}
				            ],
				            "initComplete": function(settings, json) {
				            	//console.log(json);
				            	//INVOICELOOKUP.doFunctionBinding();
				            	//var myTable = this;
    			            	//LOOKUPUTILS.makeFilters(myTable, "#filter-container", "#divisionTable", INVOICELOOKUP.createTable);
				            },
				            "drawCallback": function( settings ) {
				            	//INVOICELOOKUP.doFunctionBinding();
				            	//CALLNOTE.lookupLink();
				            	$("input[name='divisionId']").off("click");
				            	$("input[name='divisionId']").click( function($event) {
				            		var $checked = $(this).prop("checked");
				            		if ( ! $checked ) {
				            			$("input[name='selectAll']").prop("checked",false);
				            		}
				            	});
				            	$("#goButton").show();
				            },				            				            
				    } );
	        	}
	        };
	        
	        INVOICE_GEN.init();
        
      });

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Invoice Generation</h1>
    	
    	<div id="invoiceGenPanel">
			<form action="#">
				<table>
					<tr>
						<td class="formLabel">Invoice Date: </td>
						<td><input type="date" id="invoiceDate" /> <!-- <input type="text" class="dateField" id="invoiceDate"/> --></td>
						<td><span class="err" id="invoiceDateErr"></span></td>
					</tr>
					<tr>
						<td class="formLabel">Monthly: </td>
						<td><input type="checkbox" value="monthly" id="monthlyFlag" /></td>
						<td><span class="err" id="monthlyFlagErr"></span></td>
					</tr>
					<tr>
						<td class="formLabel">All Divisions:</td>
						<td><input type="checkbox" name="selectAll" /></td>
						<td><span class="err" id="divisionIdErr"></span></td>
					</tr>
					<tr>
						<td colspan="2"><br /><input type="button" value="Generate Invoices" id="goButton" /></td>
					</tr>
				</table>
				<table id="divisionTable" style="margin-top:12px;">
				</table>
			</form>
    	</div>
    	
    	<html:form action="invoicePrint" styleId="printForm">
    		<html:hidden property="<%= MessageForm.MESSAGE %>" />
    	</html:form>
    </tiles:put>

</tiles:insert>

