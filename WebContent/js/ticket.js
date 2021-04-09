$(function() {    	
	;TICKETUTILS = {
		ticketStatusMap : {},


		doTicketViewModal : function($modalId,$ticketId) {
			console.log("PopulateTicketViewModal: " + $ticketId);

			var jqxhr = $.ajax({
	       		type: 'GET',
	       		url: "ticket/" + $ticketId,
	       		statusCode: {
	       			200: function($data) {
	       				if ( $data.responseHeader.responseCode=='SUCCESS') {
	       					TICKETUTILS.populateTicketModal($modalId,$ticketId,$data);
	       				} else {
	       					$($modalId).dialog().close();
	       					$("#globalMsg").html($data.responseHeader.responseMessage).show()
	       				}
	       			},
       				404: function($data) {
       					$($modalId).dialog().close();
        	    		$("#globalMsg").html("Invalid Ticket Number. Reload page and try again").show().fadeOut(6000);
        	    	},
					403: function($data) {
						$($modalId).dialog().close();
						$("#globalMsg").html("Session Timeout. Log in and try again").show();
					},
	       			500: function($data) {
            	    	$("#globalMsg").html("System Error 500: Contact Support").show();
            		},
	       		},
	       		dataType: 'json'
	       	});
		},
	   	
	   	populateOptionList : function ($data, $callback) {	
			$.each($data.ticketStatus, function($index, $value) {
				TICKETUTILS.ticketStatusMap[$value.code]=$value.display;
			});		        	
	   	},
			
			
			
			
		makeTicketViewModal : function($modalId) {
			ANSI_UTILS.getOptionList("TICKET_STATUS",TICKETUTILS.populateOptionList);
			
			
			var $cancelId = $modalId.substring(1)+"-cancel-button";
			$( $modalId ).dialog({
				title:'TicketView',
				autoOpen: false,
				height: 600,
				width: 950,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id:  $cancelId,
						click: function($event) {
							$($modalId).dialog("close");
						}
					}
				]
			});	
			$("#"+$cancelId).button('option', 'label', 'Cancel');
		},
		
		
		
		markInvalid : function($item) {
			$item.removeClass("far");
    		$item.removeClass("fa-check-square");
			$item.removeClass("inputIsValid");

			$item.addClass("fa");
    		$item.addClass("fa-ban");
			$item.addClass("inputIsInvalid");
		},
		
		
		
		markValid : function($item) {
    		$item.removeClass("fa");
    		$item.removeClass("fa-ban");
			$item.removeClass("inputIsInvalid");

			$item.addClass("far");
    		$item.addClass("fa-check-square");
			$item.addClass("inputIsValid");
		},
		
		
		
		
		populateInvoiceDetail : function($modalId, $data) {
			if ($data.invoiceDetail) {
				$($modalId + " .invoiceId").html($data.invoiceDetail.invoiceId);					
				$($modalId + " .sumInvPpc").html($data.invoiceDetail.sumInvPpc);
				$($modalId + " .sumInvPpcPaid").html($data.invoiceDetail.sumInvPpcPaid);
				$($modalId + " .sumInvTax").html($data.invoiceDetail.sumInvTax);
				$($modalId + " .sumInvTaxPaid").html($data.invoiceDetail.sumInvTaxPaid);
				$($modalId + " .invoiceBalance").html($data.invoiceDetail.balance);
                $($modalId + " .invoiceTable").show();
			}else{
				$($modalId + " .invoiceTable").hide();				
			}				
		},
		populateJobTags : function($modalId, $data) {
				$($modalId + " .longCode").html($data.jobTags.longCode);					
				$($modalId + " .abbrev").html($data.jobTags.abbrev);
				$($modalId + " .status").html($data.jobTags.status);
				$($modalId + " .statusDescription").html($data.jobTags.statusDescription);
				$($modalId + " .tagDescription").html($data.jobTags.tagDescription);
				$($modalId + " .tagId").html($data.jobTags.tagId);
				$($modalId + " .tagType").html($data.jobTags.tagType);
				$($modalId + " .tagTypeDescription").html($data.jobTags.tagTypeDescription);
			},
		
		
		populateSummary : function($modalId, $data) {
			$($modalId + " .status").html('<span class="tooltip">' + $data.ticketDetail.status + '<span class="tooltiptext">' + TICKETUTILS.ticketStatusMap[$data.ticketDetail.status]+ '</span></span>');
			$($modalId + " .divisionDisplay").html($data.ticketDetail.divisionDisplay);
			$($modalId + " .jobId").html( '<a class="joblink" href="jobMaintenance.html?id='+ $data.ticketDetail.jobId + '">' + $data.ticketDetail.jobId + '</a>');
			$($modalId + " .serviceDescription").html($data.ticketDetail.serviceDescription);
			$($modalId + " .jobFrequency").html('<span class="tooltip">' + $data.ticketDetail.jobFrequency + '<span class="tooltiptext">' + $data.ticketDetail.jobFrequencyDesc + '</span></span>');
			$($modalId + " .invoiceStyle").html($data.ticketDetail.invoiceStyle);
			$($modalId + " .poNumber").html($data.ticketDetail.actPoNumber);
		},
		
		
		
		populateTicketDetail : function($modalId, $data) {
			console.log("populateTicketDetail");
			console.log($data);
			$($modalId + " .ticketId").html($data.ticketDetail.ticketId);
			$($modalId + " .actPricePerCleaning").html($data.ticketDetail.actPricePerCleaning);
			$($modalId + " .totalVolPaid").html($data.ticketDetail.totalVolPaid);
			$($modalId + " .actTax").html($data.ticketDetail.actTax);
			$($modalId + " .totalTaxPaid").html($data.ticketDetail.totalTaxPaid);
			$($modalId + " .ticketBalance").html($data.ticketDetail.balance);
			
			$($modalId + " .completedRow").hide();
			if ( $data.ticketDetail.status=='N') {
				$($modalId + " .processNotesRow").hide();
			} else {
				$($modalId + " .processNotesRow").show();
				$($modalId + " .processNotesRow td").addClass("bottomRow");
				if ( $data.ticketDetail.status == 'R' ) {
					$processLabel = "Reject Date:";
				} else if ( $data.ticketDetail.status == 'D' ) {
					$processLabel = "Dispatch Date:";
				} else if ( $data.ticketDetail.status == 'V' ) {
					$processLabel = "Void Date:";
				} else if ( $data.ticketDetail.status == 'S' ) {
					$processLabel = "Skip Date:";
				} else if ( ($data.ticketDetail.status == 'C') || ($data.ticketDetail.status == 'I') || ($data.ticketDetail.status == 'P') ) {
					$processLabel = "Complete Date:";
					$($modalId + " .processNotesRow td").removeClass("bottomRow");
					$($modalId + " .completedRow").show();
					if ( $data.ticketDetail.customerSignature == true ) {
						TICKETUTILS.markValid($($modalId + " .customerSignature"));
					} else {
						TICKETUTILS.markInvalid($($modalId + " .customerSignature"));
					}
					if ( $data.ticketDetail.billSheet == true ) {
						TICKETUTILS.markValid($($modalId + " .billSheet"));
					} else {
						TICKETUTILS.markInvalid($($modalId + " .billSheet"));
					}
					if ( $data.ticketDetail.mgrApproval == true ) {
						TICKETUTILS.markValid($($modalId + " .managerApproval"));
					} else {
						TICKETUTILS.markInvalid($($modalId + " .managerApproval"));
					}
				} else if ( $data.ticketDetail.status == 'I' ) {
					$processLabel = "Invoice Date:";
				} else if ( $data.ticketDetail.status == 'P' ) {
					$processLabel = "Paid Date:";
				} else {
					$processLabel = "Process Date (" + $data.ticketDetail.status + ")";
				}
				
				$($modalId + " .processDateLabel").html($processLabel);
				$($modalId + " .processDate").html($data.ticketDetail.processDate);
				$($modalId + " .processNotes").html($data.ticketDetail.processNotes);	
				
				$($modalId + " .jobTags").html("");
				$displayValue = "";
				
				
				$.each($data.ticketDetail.jobTags, function($key, $value) {
					$displayValue = $displayValue + $key + ": ";
					$.each($value, function($index, $jobTag) {
						if ( $value.tagType == $jobTag.name ){
							$displayValue = $displayValue + " " + '<span class="jobtag-display jobtag-selected tooltip">'+$jobTag.abbrev+'<span class="tooltiptext">'+ $jobTag.tagDescription +'</span></span>';
						} else {
							$displayValue = $displayValue + '<span class="formLabel">' + $jobTag.displayValue + ": </span>";
						}
					});
					$displayValue = $displayValue + "<br />";
				});	
				$($modalId + " .jobTags").html($displayValue);
			}
		},
		
		
		
		
		populateTicketModal : function($modalId, $ticketId, $data) {
			console.log("populateTicketModal");
			console.log($data);
   			TICKETUTILS.populateTicketDetail($modalId, $data.data);	       			
   			TICKETUTILS.populateSummary($modalId, $data.data);
			ADDRESS_UTILS.populateAddress($modalId + "-jobsite-address", $data.data.ticketDetail.jobSiteAddress);
			ADDRESS_UTILS.populateAddress($modalId + "-billto-address", $data.data.ticketDetail.billToAddress);
   			TICKETUTILS.populateInvoiceDetail($modalId, $data.data);	  					

		},
		
		xxxx : function() {
			//$.each($data.data.ticketList, function(index, value) {
			//	addRow(index, value);
			//});
   			//populatePanelSelect($data.data);
   			//populateDefaultValues($data.data);
			//$("#summaryTable").fadeIn(4000);
			//$("#selectPanel").fadeIn(4000);
			//$("#ticketTable").fadeIn(4000);
		},
	}
});