;QUOTE_PRINT_HISTORY = {
		init : function($modalName, $triggerName) {
			console.debug($modalName);
			console.debug($triggerName);
			$( $modalName ).dialog({
				title:'Quote Print History',
				autoOpen: false,
				height: 300,
				width: 500,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "closePrintHistory",
							click: function($event) {
							$($modalName).dialog("close");
						}
					}
				]
			});
			$('#closePrintHistory').button('option', 'label', 'Close');
			$($triggerName).click(function($event) {
				$($modalName).dialog("open");
			});
		},
		
		
		showQuotePrint : function($modalName, $quoteId, $quoteNumber) {
			$quoteIdSelector = $modalName + ' input[name="quoteId"]'
			$quoteNumberSelector = $modalName + ' .quoteNumber';
			$quoteFormSelector = $modalName + ' form';
			$("#goPrint").button('option', 'label', 'Print');
			$($quoteIdSelector).val($quoteId);
			$($quoteNumberSelector).html($quoteNumber);
			$($quoteFormSelector).attr("action", "quotePrint/" + $quoteId);
			$($modalName).dialog("open");
		},
		
		
		goPrint : function($modalName) {
			$quoteIdSelector = $modalName + ' input[name="quoteId"]'
			$quoteDateSelector = $modalName + ' input[name="quoteDate"]'
			$quoteNumberSelector = $modalName + ' .quoteNumber';
			$quoteFormSelector = $modalName + ' form';
			var $quoteId = $($quoteIdSelector).val();
			var $quoteDate = $($quoteDateSelector).val();
			$($modalName).dialog("close");
			$($quoteFormSelector).submit();
		}

}