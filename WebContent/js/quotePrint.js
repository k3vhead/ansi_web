;QUOTE_PRINT = {
		init_modal : function($modalName) {
			$( $modalName ).dialog({
				title:'Print Quote',
				autoOpen: false,
				height: 200,
				width: 500,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "goPrint",
							click: function($event) {
							QUOTE_PRINT.goPrint($modalName);
						}
					}
				]
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