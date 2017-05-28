;INVOICE_PRINT = {
		init : function($modalName, $triggerName) {
			INVOICE_PRINT.modalName = $modalName;
			

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
			$("#printDate").val("");
			$("#dueDate").val("");
			$("#printDateErr").html("");
			$("#dueDateErr").html("");
			$("#hangOn").hide();

    		$('#goPrint').data('divisionId',$divisionId);
    		$('#goPrint').button('option', 'label', 'Print Invoices');
    		$('#closePrint').button('option', 'label', 'Close');
    	    $('#printModal').dialog( "open" );
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
	    				}
	    			},
    				403: function($data) {
    					$("#hangOn").hide();
    					$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage);
    				},
    				500: function($data) {
    					$("#hangOn").hide();
         	    		$("#globalMsg").html("System Error: Contact Support").fadeIn(10);
         	    	} 
    			},
    			dataType: 'json'
    		});        	
        }
			
			


}