;INVOICE_PRINT = {
		callback : null,
		modalName : null, 
		
		init : function($modalName, $callbackFunction) {
			INVOICE_PRINT.modalName = $modalName;
			INVOICE_PRINT.callback = $callbackFunction;	

			// set up the activate modal window
			var $modalSelector = "#" + $modalName;
			$( $modalSelector ).dialog({
				title:'Print Invoices',
				autoOpen: false,
				height: 300,
				width: 500,
				modal: true,
				buttons: [
					{
						id: "closePrint",
						click: function() {
							$($modalSelector).dialog( "close" );
						}
					},{
						id: "goPrint",
						click: function($event) {
							INVOICE_PRINT.printInvoices();
						}
					}
				],
				close: function() {
					$($modalSelector).dialog( "close" );
				}
			});
		},
		

		
		printModal : function($divisionId) {
			var $modalSelector = "#" + INVOICE_PRINT.modalName
			$("#printDate").val("");
			$("#dueDate").val("");
			$("#printDateErr").html("");
			$("#dueDateErr").html("");
			$("#hangOn").hide();

    		$('#goPrint').data('divisionId',$divisionId);
    		$('#goPrint').button('option', 'label', 'Print Invoices');
    		$('#closePrint').button('option', 'label', 'Close');
    	    $($modalSelector).dialog( "open" );
		},
		
		

		reprintInvoice : function($invoiceId) {
			var $url = "invoiceReprint/" + $invoiceId;
            var jqxhr = $.ajax({
    			type: 'GET',
    			url: $url,
    			data: {},
    			statusCode: {
	    			200: function($data) {
	    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
	    					$("#globalMsg").html($data.data.webMessages['GLOBAL_MESSAGE'].value[0]).show().fadeOut(4000);
	    				} else {
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
    					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
    				},
    				404: function($data) {
    					$("#globalMsg").html("Invoice " + $invoiceId + " does not exist or has never been printed").show().fadeOut(4000);
    				},
    				500: function($data) {
         	    		$("#globalMsg").html("System Error: Contact Support").show();
         	    	} 
    			},
    			dataType: 'json'
    		});       
		},
		
		
		printInvoices : function() {
			var $modalSelector = "#" + INVOICE_PRINT.modalName; 
			$("#hangOn").show();
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
	    				$("#hangOn").hide();
	    				if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
	    					$.each($data.data.webMessages, function (key, value) {
	    						var $selectorName = "#" + key + "Err";
	    						$($selectorName).show();
	    						$($selectorName).html(value[0]).fadeOut(4000);
	    					});
	    				} else {
	    					$($modalSelector).dialog("close");
	    					var a = document.createElement('a');
	    					var linkText = document.createTextNode("Download");
	    					a.appendChild(linkText);
	    					a.title = "Download";
	    					a.href = $data.data.invoiceFile;
	    					a.target = "_new";   // open in a new window
	    					document.body.appendChild(a);
	    					a.click();
	    					if ( INVOICE_PRINT.callback != null ) {
	    						INVOICE_PRINT.callback();
    						}
	    				}
	    			},
    				403: function($data) {
    					$("#hangOn").hide();
    					$("#globalMsg").html("Session timeout. Please log in and try again").show();
    					$($modalSelector).dialog("close");
    					$('html, body').animate({scrollTop: 0}, 800);
    				},
    				500: function($data) {
    					$("#hangOn").hide();
         	    		$("#globalMsg").html("System Error: Contact Support").show();
    					$($modalSelector).dialog("close");
    					$('html, body').animate({scrollTop: 0}, 800);
         	    	} 
    			},
    			dataType: 'json'
    		});        	
        }
			
			


}