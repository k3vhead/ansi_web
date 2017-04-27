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
        	#ticketTable {
        		width:1300px;
        		max-width:1300px;
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
			.tfootTotal {
				border:sold 1px #000000;
        		font-weight:bold;
				text-align:right;
			}
			#applyButton {
				display:none;
			}
        </style>
        
        <script type="text/javascript">        
        $( document ).ready(function() {
        	
        	// see if we have a URL parm for payment ID
        	var $paymentId = '<c:out value="${ANSI_PAYMENT_ID}" />';
        	// set defaut modal action
        	var $paymentAction = "new";
        	// set up a variable to hold unapplied amount in a format we can use
        	var $unappliedAmount = 0;
        	
        	// populate payment method list
        	var $optionData = ANSI_UTILS.getOptions("PAYMENT_METHOD");
        	$('option', $("#paymentMethod")).remove();
        	$("#paymentMethod").append(new Option("",""));
			$.each($optionData.paymentMethod, function(index, val) {
				$("#paymentMethod").append(new Option(val.display, val.abbrev));
			});
        	

			// set up the activate job modal window
			$( "#paymentModal" ).dialog({
				title:'Payment',
				autoOpen: false,
				height: 400,
				width: 500,
				modal: true,
				closeOnEscape:false,
				open: function(event, ui) {
					$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				},
				buttons: [
					{
						id: "closePmtModal",
						click: function() {
							location.href="paymentLookup.html";
						}
					},{
						id: "goPayment",
							click: function($event) {
							goPayment();
						}
					}
				]
				//,close: function() {
				//	$("#paymentModal").dialog( "close" );
	      	    //    //allFields.removeClass( "ui-state-error" );
				//}
			});
			
			
        	$('.dateField').datepicker({
                prevText:'&lt;&lt;',
                nextText: '&gt;&gt;',
                showButtonPanel:true
            });
        	
        	if ( $paymentId == '' ) {
        		paymentModal();
        		$paymentAction = "new";
        		$("#modalNew").click();
        	} else {
        		$paymentAction = "search";
        		$("#pmtSearchId").val($paymentId);
        		goPayment();
        	}
        	
        	$("#editPaymentIcon").click(function($event) {
        		paymentModal();
        		$("#modalSearch").click();
        	});

        	$("#modalSearch").click(function($event) {
        		$paymentAction = "search";
        		$("#newPaymentDate").datepicker("hide");
        		$("#modalSearchBox").css('background-color','#CCCCCC');
        		$("#modalNewBox").css('background-color','#FFFFFF');
        		$(".searchPmtRow").show();
        		$(".newPmtRow").hide();
        		$("#modalSearch").data("action","search");
        		$("#pmtSearchId").focus();
        	})
        	
        	$("#modalNew").click(function($event) {
        		$paymentAction = "new";
        		$("#modalSearchBox").css('background-color','#FFFFFF');
        		$("#modalNewBox").css('background-color','#CCCCCC');
        		$(".searchPmtRow").hide();
        		$(".newPmtRow").show();
        		$("#newPaymentDate").focus();
        	})
        	
        	
        	$("#exitButton").click(function($event) {
        		location.href="paymentLookup.html";
        	});
        	
        	$("#clearButton").click(function($event) {
        		$(".ticketPmt").val("");
        		$(".totalField").val("$0.00");
        		$("#feeAmount").val("0.00");
        		$("#excessCash").val("0.00");
        	});
        	
        	$("#feeAmount").change(function($event) {
				$fixedValue = parseFloat($(this).val()).toFixed(2);
				$(this).val($fixedValue);
				calculateAllTotals();
        	});
        	
        	$("#feeAmount").focus(function($event) {
				$(this).select();
        	});
        	
        	$("#excessCash").change(function($event) {
				$fixedValue = parseFloat($(this).val()).toFixed(2);
				$(this).val($fixedValue);
				calculateAllTotals();
        	});
        	
        	$("#excessCash").focus(function($event) {
				$(this).select();
        	});
        	
        	function goPayment() {
        		if ( $paymentAction == "search") {
        			getPayment();
        		} else if ($paymentAction == "new"){
        			postPayment();
        		} else {
        			$("#globalMsg").html("System error. Reload and try again");
        		}
        		$("#invoiceNbr").focus();
        	}
        	
        	function getPayment() {
        		$("#clearButton").click();
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
        		$("#unappliedAmount").html($paymentData.available);
        		$unappliedAmount = $paymentData.available.replace("\$","").replace(",","");
        		$.each($paymentData, function ($key, $value) {
					var $selector = "#paymentSummary ." + $key;
					if ( $value == null ) {
						$($selector).html("");
					} else {
						$($selector).html($value);
					}
				});
        		
        	}
        	
        	
        	
            var $invoiceComplete = $( "#invoiceNbr" ).autocomplete({
				source: "invoiceTypeAhead",
                minLength: 2,
                appendTo: "#someInvoice",
                select: function( event, ui ) {
					$("#invoiceNbr").val(ui.item.id);
					var $ticketData = populateTicketData(ui.item.id);
                }
          	}).data('ui-autocomplete');
			$invoiceComplete._renderMenu = function( ul, items ) {
				var that = this;
				$.each( items, function( index, item ) {
					that._renderItemData( ul, item );
				});
				if ( items.length == 1 ) {
					$("#invoiceNbr").val(items[0].id);
					$("#invoiceNbr").autocomplete("close");
					var $ticketData = populateTicketData(items[0].id);
				}
			}
            
            
        	$("#pmtSearchId").autocomplete({
                source: "paymentTypeAhead",
                minLength: 2,
                appendTo: "#somePmt",
                select: function( event, ui ) {
                  //alert( "Selected: " + ui.item.id + " aka " + ui.item.label + " or " + ui.item.value );
                  $("#invoiceNbr").val(ui.item.id);
                }
        	});
        	
        	
        	$("#calcButton").click(function($event) {
        		var $outbound = {};
        		var $ticketList = [];
        		$outbound['paymentId'] = $("#paymentSummary .paymentId").html();
        		$outbound['invoiceId'] = $("#invoiceNbr").val();
        		$outbound['feeAmount'] = parseFloat($("#feeAmount").val());
        		$outbound['excessCash'] = parseFloat($("#excessCash").val());

        		
        		//return '<input tabIndex="'+ $tabIndex +'" type="text" data-ticketId="'+row.ticketId+'" data-balance="'+ row.totalBalance.replace("\$","").replace(",","") +'" class="ticketPayInv ticketPmt" style="width:80px;" />';
        		//return '<input tabIndex="' + $myTabIndex + '" type="text" data-ticketId="'+row.ticketId+'" data-balance="'+ row.totalBalance.replace("\$","").replace(",","") +'" class="ticketPayTax ticketPmt" style="width:80px;'+$display+'" />';

        		
        		
        		$.each( $('#ticketTable tr'), function($row, $value) {
        			var $inputs = $("input");
        			var $inputList = $(this).find($inputs);
        			
    				var $ticketItem = {};
    				var $useThisRow = false;
    				
        			$.each( $inputList, function($col, $input) {        				
        				var $ticketId = $(this).attr("data-ticketId");
        				if ( $ticketId != null && $ticketId != "" ) {
        					console.debug($ticketId);
        					$ticketItem["ticketId"] = $ticketId;
        					
	            			if ( $(this).hasClass("ticketPayInv") ) {
	            				var $payInvoice = $(this).val();
	            				if ( $payInvoice != null && $payInvoice != "" ) {
	            					$ticketItem["payInvoice"] = $payInvoice;
	                				$useThisRow = true;
	            				}
	            			}
	            			if ( $(this).hasClass("ticketPayTax") ) {
	            				var $payTax = $(this).val();
	            				if ( $payTax != null && $payTax != "" ) {
	            					$ticketItem["payTax"] = $payTax;
	                				$useThisRow = true;
	            				}
	            			}
        				}
        			});
        			if ( $useThisRow == true ) {
        				$ticketList.push($ticketItem);	
        			}
        			
        			//var $payInv = $inputList[0];
        			//var $payTax = $inputList[1];
        			//console.debug($row + ": PayInv")
        			//console.debug( $($payInv).val() );
        		});

				$outbound['ticketList'] = $ticketList;
				console.debug(JSON.stringify($outbound));
        	});
        	
        	function populateTicketData($invoiceId) {
        		var $url = 'invoice/' + $invoiceId;        		
				var jqxhr = $.ajax({
	   				type: 'GET',
	   				url: $url,
	   				data: null,
	   				statusCode: {
		   				200: function($data) {
		   					$(".billToField").html("");
		   					var $address = $data.data.address;
		   					$("#billToAddress1").html($address.address1);
		   					$("#billToAddress2").html($address.address2);
		   					$("#billToCity").html($address.city);
	   						$("#billToName").html($address.name);
	   						$("#billToState").html($address.state);
	   						$("#billToZip").html($address.zip);
		   					populateDataTable($data.data);
		   					$("#toPay").html("$0.00");
		   					$("#feeAmount").val("0.00");
		   					$("#excessCash").val("0.00");		   					
		   				},
	   					403: function($data) {
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {
		   					$("#pmtSearchIdErr").html("Invalid Payment Id").show().fadeOut(10000);
		   				},
		   				405: function($data) {
		   					$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
		   				},
		   				500: function($data) {
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
	        	    	} 
		   			},
		   			dataType: 'json'
		   		});        	
        	}
        	
			function paymentModal() {
				$("#paymentModal .err").html("");
				$("#paymentModal input").val("");
        		$('#goPayment').button('option', 'label', 'Go');
        		$('#closePmtModal').button('option', 'label', 'Exit');
        	    $('#paymentModal').dialog( "open" );
			}

        	
        	function postPayment() {
        		var $outbound = {}
        		$.each($('.newPmtRow input'), function(){
        			$outbound[this.id]=this.value
        		});
        		$.each($('.newPmtRow select'), function(){
        			$outbound[this.id]=this.value
        		});
        		
				var jqxhr = $.ajax({
	   				type: 'POST',
	   				url: 'payment/add',
	   				data: JSON.stringify($outbound),
	   				statusCode: {
		   				200: function($data) {
		   					if ( $data.responseHeader.responseCode=='EDIT_FAILURE') {
		   						doPaymentEditFailure($data.data);
		   					} else if ( $data.responseHeader.responseCode == 'SUCCESS') {
		   						populatePaymentSummary($data.data.paymentTotals);
		   						$(".newPaymentField").val("");
		   						$("#paymentModal").dialog("close");
		   					} else {
		   						$("#globalMsg").html($data.responseHeader.responseMessage);
		   						$("#paymentModal").dialog("close");
		   					}
		   				},
	   					403: function($data) {
	   						$("#paymentModal").dialog("close");
		   					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
		   				},
		   				404: function($data) {		   					
		   					$("#pmtSearchIdErr").html("Invalid Payment Id").show().fadeOut(10000);
		   				},
		   				405: function($data) {
		   					$("#paymentModal").dialog("close");
		   					$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
		   				},
		   				500: function($data) {
		   					$("#paymentModal").dialog("close");
	        	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(4000);
	        	    	} 
		   			},
		   			dataType: 'json'
		   		});        	
        	}
        	
        	
        	
        	function doPaymentEditFailure($data) {
        		$.each($data.webMessages, function(field, errList) {
        			var $selector = "#" + field + "Err";
        			if ( errList.length == 1 ) {
        				$($selector).html(errList[0]).show().fadeOut(8000);
        			} else {
        				var $errString = "";
        				$.each(errList, function(index, err) {
        					$errString = $errString + "<li>" + err + "</li>";
        				});
        				$($selector).html("<ul>" + errString + "</ul>").show().fadeOut(8000);
        			}
        		});
        	}
         
			$('#ticketTable').dataTable().fnDestroy();
			
	           
			var dataTable = null;
        	
			function populateDataTable($data) {
				
				$("#totalBalance").html($data.totalBalance);
				$("#totalPayInvoice").html($data.totalPayInvoice);
				$("#totalPayTax").html($data.totalPayTax);
				
				var $tabIndex = 0;
				$("#feeAmount").attr("tabindex", 2*$data.ticketList.length + 1);
				$("#excessCash").attr("tabindex", 2*$data.ticketList.length + 2);
				$("#calcButton").attr("tabindex", 2*$data.ticketList.length + 3);
				
				var dataTable = $('#ticketTable').DataTable( {
	        			"bDestroy":			true,
	        			"processing": 		true,
	        			"autoWidth": 		false,
	        	        "scrollCollapse": 	true,
	        	        "scrollX": 			true,
	        	        rowId: 				'dt_RowId',
	        	        dom: 				'Bfrtip',
	        	        "searching": 		false,
	        	        //lengthMenu: [
	        	        //    [ 10, 50, 100, 500, 1000, -1 ],
	        	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
	        	        //],
	        	        buttons: [
	        	        //	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	        	'copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',
	        	        ],
	        	        "columnDefs": [
	        	        	{ "orderable": true, "targets": "_all" },
	        	            { className: "dt-left", "targets": [0] },
	        	            { className: "dt-right", "targets": [4,5,6,7,8,9,10] },
	        	            { className: "dt-center", "targets": [0,1,2,3] },
	        	         ],
	        	        "paging": false,
	        	        data: $data.ticketList,
				        columns: [
				            { title: "Division", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
				            	if(row.divisionDisplay != null){return (row.divisionDisplay+"");}
				            } },
				            { title: "Ticket", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.ticketId != null){return (row.ticketId+"");}
				            } },
				            { title: "Completed", "defaultContent": "<i>0</i>", data: function ( row, type, set ) {
				            	if(row.processDate != null){return (row.processDate+"");}
				            } },
				            { title: "Inv. Date",  "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	if(row.invoiceDate != null){return (row.invoiceDate+"");}
				            } },
				            { title: "Inv. Amt.", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	if(row.actPricePerCleaning != null){return (row.actPricePerCleaning+"");}
				            } },
				            { title: "Inv. Paid", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	if(row.totalVolPaid != null){return (row.totalVolPaid+"");}
				            } },
				            { title: "Tax. Amt.", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	if(row.actTaxAmt != null){return (row.actTaxAmt+"");}
				            } },
				            { title: "Tax. Paid", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	if(row.totalTaxPaid != null){return (row.totalTaxPaid+"");}
				            } },
				            { title: "Balance", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	if(row.totalBalance != null){return (row.totalBalance+"");}
				            } },
				            { title: "Pay Inv", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	$tabIndex = $tabIndex+1;				            	
			            		return '<input tabIndex="'+ $tabIndex +'" type="text" data-ticketId="'+row.ticketId+'" data-balance="'+ row.totalBalance.replace("\$","").replace(",","") +'" class="ticketPayInv ticketPmt" style="width:80px;" />';
				            } },
				            { title: "Pay Tax", "defaultContent": "<i>0.00</i>", data: function ( row, type, set ) {
				            	$myTabIndex = $tabIndex + $data.ticketList.length;
				            	if ( row.actTaxAmt == "$0.00") {
				            		$display="display:none;";
				            	} else {
				            		$display="";
				            	}
			            		return '<input tabIndex="' + $myTabIndex + '" type="text" data-ticketId="'+row.ticketId+'" data-balance="'+ row.totalBalance.replace("\$","").replace(",","") +'" class="ticketPayTax ticketPmt" style="width:80px;'+$display+'" />';
				            } }
				            <%-- { title: "Write Off", "defaultContent": "<i></i>", data: function ( row, type, set ) {
				            	//if(row.invoiceTotal != null){return (row.invoiceTotal+"");}
				            	return "???";
				            } }, --%>
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
	            	$.each($('input[type="text"]'), function () {
				        $(this).css("height","20px");
				        $(this).css("max-height", "20px");
				    });
						
	            	//populateDataTable();
	            };
	            
	            function initComplete (){
					var r = $('#ticketTable tfoot tr');
					r.find('th').each(function(){
						$(this).css('padding', 8);
					});
					$('#ticketTable thead').append(r);
	            }
            
	            
				function calcTotalPayInv() {
					var $totalValue = 0.00
					$.each( $(".ticketPayInv"), function() {
						if ( $(this).val() != null && $(this).val() != "" ) {
							$totalValue = $totalValue + parseFloat($(this).val());
						}						
					});
					$("#totalPayInvoice").html( formatCurrency($totalValue));
					return $totalValue
				}
				
				function calcTotalPayTax() {
					var $totalValue = 0.00;
					$.each( $(".ticketPayTax"), function() {
						if ( $(this).val() != null && $(this).val() != "" ) {
							$totalValue = $totalValue + parseFloat($(this).val());
						}						
					});
					$("#totalPayTax").html( formatCurrency($totalValue));
					return $totalValue;
				}
				function calcToPay() {
					var $totalValue = calcTotalPayInv() + calcTotalPayTax();
					$("#toPay").html( formatCurrency($totalValue) );
					return $totalValue;
					
				}				
				function calcTotalPay() {
					var $feeAmount = parseFloat($("#feeAmount").val());
					var $excessCash = parseFloat($("#excessCash").val());
					var $toPay = calcToPay();
					return $feeAmount + $excessCash + $toPay;
				}
				
				function calculateAllTotals() {
					$totalPayInvoice = calcTotalPayInv();
					$totalPayTax = calcTotalPayTax();
					$toPay = calcToPay();					
					$totalPay = calcTotalPay();
					$available = $unappliedAmount - $totalPay;
					$("#unappliedAmount").html(formatCurrency($available));
				}
				
				
				function doFunctionBinding() {
					$( ".ticketPayInv").on("focus", function() {
						if ( $(this).val() == null || $(this).val() == "" ) {
							// figure default amount to apply
							$balance = parseFloat($(this).data("balance"));
							$totalPay = calcTotalPay();
							$available = $unappliedAmount - $totalPay;
							$toPay = Math.min($balance, $available)
							$(this).val($toPay.toFixed(2));
							// calculate totals
							calculateAllTotals();
							$(this).select();
						}
					});
					$( ".ticketPayInv").on("change", function() {
						if ( isNaN($(this).val()) ) {
							$fixedValue = 0;
						} else {
							$fixedValue = parseFloat($(this).val()).toFixed(2);
						}
						$(this).val($fixedValue);
						calculateAllTotals();
					});
					$( ".ticketPayTax").on("focus", function() {
						if ( $(this).val() == null || $(this).val() == "" ) {
							// figure default amount to apply
							$balance = parseFloat($(this).data("balance"));
							$totalPay = calcTotalPay();
							$available = $unappliedAmount - $totalPay;
							$toPay = Math.min($balance, $available)
							$(this).val($toPay.toFixed(2));
							// calculate totals
							calculateAllTotals();
							$(this).select();
						}
					});
					$( ".ticketPayTax").on("change", function() {
						if ( isNaN($(this).val()) ) {
							$fixedValue = 0;
						} else {
							$fixedValue = parseFloat($(this).val()).toFixed(2);
						}
						$(this).val($fixedValue);
						calculateAllTotals();
					});
				}
        

				function formatCurrency($amount) {
					return "$" + $amount.toFixed(2);
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
    			<td class="black_border"><span id="billToName" class="billToField"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">Address:</td>
    			<td class="black_border"><span id="billToAddress1" class="billToField"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">Address2:</td>
    			<td class="black_border"><span id="billToAddress2" class="billToField"></span></td>
    		</tr>
    		<tr>
    			<td class="formHdr">City/State/Zip:</td>
    			<td class="black_border">
    				<span id="billToCity" class="billToField"></span>,
    				<span id="billToState" class="billToField"></span>
    				<span id="billToZip" class="billToField"></span>
    			</td>
    		</tr>
    	</table>
    	
    	<div class="spacer"></div>
    	
    	
    	
    	
    	<table id="ticketTable" style="table-layout: fixed" class="display" cellspacing="0" style="font-size:9pt;max-width:1300px;width:1300px;">
	        <colgroup>
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
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
	    			<td class="formHdr">Tax Amt</td>
	    			<td class="formHdr">Tax Paid</td>
	    			<td class="formHdr">Balance</td>
	    			<td class="formHdr">Pay Inv</td>
	    			<td class="formHdr">Pay Tax</td>
	    			<%-- <td class="formHdr">Write Off</td> (this is for v2) --%>
	            </tr>
	        </thead>
	        <tfoot>
	        </tfoot>
        </table>
        <table style="width:1300px; margin-left:20px;">
	        <colgroup>
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:9%;" />
	        	<col style="width:10%;" />
	   		</colgroup>        
            <tr>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td class="formHdr">&nbsp;</td>
    			<td style="text-align:right; font-weight:bold;">Totals:</td>
    			<td style="text-align:right; border:solid 1px #000000; font-weight:bold;"><span class="totalField" id="totalBalance"></span></td>
    			<td style="text-align:right; border:solid 1px #000000; font-weight:bold;"><span class="totalField" id="totalPayInvoice"></span></td>
    			<td style="text-align:right; border:solid 1px #000000; font-weight:bold;"><span class="totalField" id="totalPayTax"></span></td>
    			<%-- <td class="formHdr">Write Off</td> (this is for v2) --%>
            </tr>
	    </table>
	    
	    
    	<div style="clear:both; width:1300px; text-align:right;  margin-top:20px;">
    		<input type="button" value="Clear" id="clearButton" />
    		<input type="button" value="Calculate Totals" id="calcButton" />
    		<input type="button" value="Apply Payments" id="applyButton" />
    		<input type="button" value="Exit" id="exitButton" />
    	</div>
    
    
    
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
						<td><input type="text" class="dateField newPaymentField" id="paymentDate"/></td>
						<td><span class="err" id="paymentDateErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Payment Method: </td>
						<td><select id="paymentMethod" class="newPaymentField"></select></td>
						<td><span class="err" id="paymentMethodErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Check Nbr: </td>
						<td><input type="text" id="checkNumber" class="newPaymentField" /></td>
						<td><span class="err" id="checkNumberErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Check Date: </td>
						<td><input type="text" class="dateField newPaymentField" id="checkDate"/></td>
						<td><span class="err" id="checkDateErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Amount: </td>
						<td><input type="text" id="paymentAmount" class="newPaymentField"/></td>
						<td><span class="err" id="paymentAmountErr"></span></td>
					</tr>
					<tr class="newPmtRow">
						<td class="formHdr">Notes: </td>
						<td><input type="text" id="paymentNote" class="newPaymentField" /></td>
						<td><span class="err" id="paymentNoteErr"></span></td>
					</tr>
				</table>
			</form>
    	</div>
    </tiles:put>

</tiles:insert>

