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
        Payments
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
    	<script type="text/javascript" src="js/ansi_utils.js"></script>
        <link rel="stylesheet" href="css/datepicker.css" type="text/css" />
        <style type="text/css">
        	#paymentSummary {
        		width:1300px;
        	}
        	#paymentSummary td {
        		text-align:center;
        	}
        	.black_border {
        		border:solid 1px #000000;
        	}
        	.spacer {
        		width:100%; 
        		clear:both; 
        		margin-top:15px;
        	}
        	#paymentModal {
        		display:none;
        	}
        	.formHdr {
        		font-weight:bold;
        	}
			.action-link {
				cursor:pointer;
			}
			.searchPmtRow {
				display:none;
			}
        </style>
        
        <script type="text/javascript">        
        $( document ).ready(function() {
        	var $paymentId = '<c:out value="${ANSI_PAYMENT_ID}" />';

			// set up the activate job modal window
			$( "#paymentModal" ).dialog({
				title:'Payment',
				autoOpen: false,
				height: 400,
				width: 500,
				modal: true,
				buttons: [
					{
						id: "closePmtModal",
						click: function() {
							$("#paymentModal").dialog( "close" );
						}
					},{
						id: "goPayment",
							click: function($event) {
							goPayment();
						}
					}
				],
				close: function() {
					$("#paymentModal").dialog( "close" );
	      	        //allFields.removeClass( "ui-state-error" );
				}
			});
			
			
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });
        	
        	if ( $paymentId == '' ) {
        		paymentModal();
        	} else {
        		$("#modalSearch").data("action","search");
        		$("#pmtSearchId").val($paymentId);
        		goPayment();
        	}
        	
        	$("#editPaymentIcon").click(function($event) {
        		paymentModal();
        		$("#modalSearch").click();
        	});

        	$("#modalSearch").click(function($event) {
        		$("#newPaymentDate").datepicker("hide");
        		$("#modalSearchBox").css('background-color','#CCCCCC');
        		$("#modalNewBox").css('background-color','#FFFFFF');
        		$(".searchPmtRow").show();
        		$(".newPmtRow").hide();
        		$("#modalSearch").data("action","search");
        		$("#pmtSearchId").focus();
        	})
        	
        	$("#modalNew").click(function($event) {
        		$("#modalSearchBox").css('background-color','#FFFFFF');
        		$("#modalNewBox").css('background-color','#CCCCCC');
        		$(".searchPmtRow").hide();
        		$(".newPmtRow").show();
        		$("#modalSearch").data("action","new");
        		$("#newPaymentDate").focus();
        	})
        	
        	function goPayment() {
        		var $action = $("#modalSearch").data("action");
        		if ( $action == "search") {
        			getPayment();
        		} else if ($action == "new"){
        			postPayment();
        		} else {
        			$("#globalMsg").html("System error. Reload and try again");
        		}
        	}
        	
        	function getPayment() {
        		var $paymentId = $("#pmtSearchId").val();
        		var $url = 'payment/' + $paymentId;
				var jqxhr = $.ajax({
	   				type: 'GET',
	   				url: $url,
	   				data: null,
	   				statusCode: {
		   				200: function($data) {   				
		   					//populateDataTable($data.data);
		   					populatePaymentSummary($data.data.paymentTotals);
		   					$("#paymentModal").dialog("close");
		   				},
	   					403: function($data) {
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {
		   					$("#pmtSearchIdErr").html("Invalid Payment Id").show().fadeOut(10000);
		   				},
		   				500: function($data) {
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10000);
	        	    	} 
		   			},
		   			dataType: 'json'
		   		});        	
        	}
        	
        	
        	
        	function populatePaymentSummary($paymentData) {
        		$.each($paymentData, function ($key, $value) {
					var $selector = "#paymentSummary ." + $key;
					if ( $value == null ) {
						$($selector).html("");
					} else {
						$($selector).html($value);
					}
				});
        	}
        	
        	
        	
            $( "#invoiceNbr" ).autocomplete({
                source: "invoiceTypeAhead",
                minLength: 4,
                appendTo: "#someElem",
                select: function( event, ui ) {
                  //alert( "Selected: " + ui.item.id + " aka " + ui.item.label + " or " + ui.item.value );
                  $("#invoiceNbr").val(ui.item.id);
                }
              });

        	
        	
        	
        	// ***********************************************************************
         
			$('#invoiceTable').dataTable().fnDestroy();
			
	           
			var dataTable = null;
        	
			function populateDataTable($data) {
				var dataTable = $('#invoiceTable').DataTable( {
	        			"bDestroy":			true,
	        			"processing": 		true,
	        			"autoWidth": 		false,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        "searching": 		true,
	        	        //lengthMenu: [
	        	        //    [ 10, 50, 100, 500, 1000, -1 ],
	        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
	        	        //],
	        	        buttons: [
	        	        //	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        ],
	        	        "columnDefs": [
	        	        	{ "orderable": true, "targets": "_all" },
	        	            { className: "dt-left", "targets": [0] },
	        	            { className: "dt-right", "targets": [3,4] },
	        	            { className: "dt-center", "targets": [1,2,5] },
	        	         ],
	        	        "paging": false,
	        	        data: $data.invoiceList,
				        columns: [
				            { title: "Division", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.div != null){return (row.div+"");}
				            } },
				            { title: "Invoices", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.invoiceCount != null){return (row.invoiceCount+"");}
				            } },
				            { title: "Tickets", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.ticketCount != null){return (row.ticketCount+"");}
				            } },
				            { title: "Tax",  "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.taxTotal != null){return (row.taxTotal+"");}
				            } },
				            { title: "PPC", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.invoiceTotal != null){return (row.invoiceTotal+"");}
				            } },
				            { title: "Action", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.invoiceCount == "0"){
				            		return "";
				            	} else {
				            		var $dataDiv = 'data-division="' + row.divisionId + '"';
				            		var $listLink = '<i class="action-link fa fa-list" aria-hidden="true" data-action="list" ' + $dataDiv + '></i>';
				            		var $printLink = '<i class="action-link fa fa-print" aria-hidden="true" data-action="print" ' + $dataDiv + '></i>';
				            		return $listLink + ' | ' + $printLink;
				            	}
				            } }
				            ],
				            "initComplete": function(settings, json) {
				            	doFunctionBinding();
				            },
				            "drawCallback": function( settings ) {
				            	doFunctionBinding();
				            }
				    } );
	        	}
        	
        		init();
        			
        	
	            function init(){
	            //	$.each($('input'), function () {
				//	        $(this).css("height","20px");
				//	        $(this).css("max-height", "20px");
				//	    });
						
	            	//populateDataTable();
	            };
	            
	            function initComplete (){
					var r = $('#invoiceTable tfoot tr');
					r.find('th').each(function(){
						$(this).css('padding', 8);
					});
					$('#invoiceTable thead').append(r);
	            }
            
				function doFunctionBinding() {
					$( ".action-link" ).on( "click", function($clickevent) {
			        	var $divisionId = $clickevent.currentTarget.attributes['data-division'].value;
			        	var $action = $clickevent.currentTarget.attributes['data-action'].value;
			        	if ( $action=='list') {
			        		location.href="invoiceLookup.html?id=" + $divisionId;
			        	} else if ( $action=='print') {
			        		paymentModal($divisionId);
			        	} else {
			        		$("#globalMessage").html("Invalid action. Reload the page and start again");
			        	}
					});
				}

				
				
				function paymentModal() {
    				$("#paymentModal .err").html("");
					$("#paymentModal input").val("");
	        		$('#goPayment').button('option', 'label', 'Go');
	        		$('#closePmtModal').button('option', 'label', 'Close');
	        	    $('#paymentModal').dialog( "open" );
				}
				
				

		
				
				function printInvoices() {
        			var $divisionId = $("#goPrint").data('divisionId');
					var $printDate = $("#printDate").val();
					var $dueDate = $("#dueDate").val();
					
		        	var $outbound = {'divisionId':$divisionId,'printDate':$printDate,'dueDate':$dueDate};
		            var jqxhr = $.ajax({
		    			type: 'POST',
		    			url: 'invoicePrint/',
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
			    					$("#paymentModal").dialog("close");
			    					var a = document.createElement('a');
			    					var linkText = document.createTextNode("Download");
			    					a.appendChild(linkText);
			    					a.title = "Download";
			    					a.href = $data.data.invoiceFile;
			    					a.target = "_new";   // open in a new window
			    					document.body.appendChild(a);
			    					a.click();
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
		        }
        
			});

		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">    	
    	<h1>Payments</h1>
    	
    	<table id="paymentSummary">
	    	<colgroup>
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:20%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	        	<col style="width:8%;" />
	   		</colgroup> 
    		<tr>
    			<td class="formHdr">Payment Id</td>
    			<td class="formHdr">Date Paid</td>
    			<td class="formHdr">Type</td>
    			<td class="formHdr">Check Number</td>
    			<td class="formHdr">Check Date</td>
    			<td class="formHdr">Amount</td>
    			<td class="formHdr">Payment Notes</td>
    			<td class="formHdr">Applied Amt</td>
    			<td class="formHdr">Applied Tax</td>
    			<td class="formHdr">Applied Total</td>
    			<td class="formHdr">Available</td>
    		</tr>
    		<tr>
    			<td class="black_border">
    				<span class="paymentId"></span>
    				<span style="float:right;" id="editPaymentIcon" class="action-link green fa fa-pencil" ari-hidden="true"></span>
    			</td>
    			<td class="black_border"><span class="paymentDate"></span></td>
    			<td class="black_border"><span class="paymentType"></span></td>
    			<td class="black_border"><span class="checkNbr"></span></td>
    			<td class="black_border"><span class="checkDate"></span></td>
    			<td class="black_border"><span class="paymentAmount"></span></td>
    			<td class="black_border"><span class="paymentNote"></span></td>
    			<td class="black_border"><span class="appliedAmount"></span></td>
    			<td class="black_border"><span class="appliedTaxAmt"></span></td>
    			<td class="black_border"><span class="appliedTotal"></span></td>
    			<td class="black_border"><span class="available"></span></td>
    		</tr>
    	</table>
    	
    	<div class="spacer"><br /></div>

    	<div style="float:right; margin-right:350px;">
    		<table>
    			<tr class="formHdr"><td>To Pay:</td><td class="black_border"><span id="toPay"></span></td></tr>
    			<tr class="formHdr"><td>Fee Amount:</td><td class="black_border"><input type="text" id="feeAmount" /></td></tr>
    			<tr class="formHdr"><td>Excess Cash:</td><td class="black_border"><input type="text" id="excessCash" /></td></tr>
    			<tr class="formHdr"><td>Unapplied Amount:</td><td class="black_border"><span id="unappliedAmount"></span></td></tr>
    		</table>
    	</div>
    	
    	<table style="width:500px;">
    		<tr>
    			<td class="formHdr">Bill To:</td>
    			<td>Invoice Number: <input type="text" id="invoiceNbr" /></td>
    		</tr>
    		<tr>
    			<td class="formHdr">Name:</td>
    			<td class="black_border"><span id="billToName"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">Address:</td>
    			<td class="black_border"><span id="billToAddress"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">Address2:</td>
    			<td class="black_border"><span id="billToAddress2"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">City/State/Zip:</td>
    			<td class="black_border">
    				<span id="billToCity"></span>,
    				<span id="billToState"></span>
    				<span id="billToZip"></span>
    			</td>
    		</tr>
    	</table>
    	
    	<div class="spacer"></div>
    	
    	
    	
    	
    	<table id="invoiceTable" style="table-layout: fixed" class="display" cellspacing="0" width="100%" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	        	<col style="width:10%;" />
	   		</colgroup>        
	        <thead>
	            <tr>
	    			<td class="formHdr">Div</td>
	    			<td class="formHdr">Ticket</td>
	    			<td class="formHdr">Completed</td>
	    			<td class="formHdr">Inv Date</td>
	    			<td class="formHdr">Inv Amt</td>
	    			<td class="formHdr">Inv Paid</td>
	    			<td class="formHdr">Balance</td>
	    			<td class="formHdr">Pay Inv</td>
	    			<td class="formHdr">Pay Tax</td>
	    			<td class="formHdr">Write Off</td>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	    			<td class="formHdr">Div</td>
	    			<td class="formHdr">Ticket</td>
	    			<td class="formHdr">Completed</td>
	    			<td class="formHdr">Inv Date</td>
	    			<td class="formHdr">Inv Amt</td>
	    			<td class="formHdr">Inv Paid</td>
	    			<td class="formHdr">Balance</td>
	    			<td class="formHdr">Pay Inv</td>
	    			<td class="formHdr">Pay Tax</td>
	    			<td class="formHdr">Write Off</td>
	            </tr>
	        </tfoot>
	    </table>
    
    
    	<div id="paymentModal">
			<form action="#">
				<table style="width:450px;">
					<colgroup>
			        	<col style="width:50%;" />
			        	<col style="width:50%;" />
					</colgroup>
					<tr>
						<td id="modalNewBox" style="text-align:center; border:solid 1px #000000; padding:5px; background-color:#CCCCCC;"><i id="modalNew"    class="action-link fa fa-plus-square fa-lg" aria-hidden="true"></i></td>
						<td id="modalSearchBox" style="text-align:center; border:solid 1px #000000; padding:5px; background-color:#FFFFFF;"><i id="modalSearch" class="action-link fa fa-search-plus fa-lg" aria-hidden="true"></i></td>
						<td>&nbsp;</td>
					</tr>
				</table>
				<table style="width:450px;">
					<colgroup>
			        	<col style="width:25%;" />
			        	<col style="width:30%;" />
			        	<col style="width:45%;" />
					</colgroup>
					<tr class="searchPmtRow">
						<td class="formHdr">Payment Id: </td>
						<td><input type="text" id="pmtSearchId"/></td>
						<td><span class="err" id="pmtSearchIdErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Payment Id: </td>
						<td>New</td>
						<td>&nbsp;</td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Date Paid: </td>
						<td><input type="text" class="dateField" id="newPaymentDate"/></td>
						<td><span class="err" id="newPaymentDateErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Check Nbr: </td>
						<td><input type="text" id="newCheckNbr"/></td>
						<td><span class="err" id="newCheckNbrErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Check Date: </td>
						<td><input type="text" class="dateField" id="newCheckDate"/></td>
						<td><span class="err" id="newCheckDateErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Amount: </td>
						<td><input type="text" id="newPaymentAmount"/></td>
						<td><span class="err" id="newPaymentAmountErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Notes: </td>
						<td><input type="text" id="newPaymentNote"/></td>
						<td><span class="err" id="newPaymentNoteErr"></span></td>
					</tr>
				</table>
			</form>
    	</div>
    </tiles:put>

</tiles:insert>

