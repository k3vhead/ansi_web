;QUOTE_PRINT = {
		init_modal : function($modalName) {
			QUOTE_PRINT['modalName'] = $modalName;
			
			$( $modalName ).dialog({
				title:'Print Quote',
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
			//$(window.location).attr('href', 'quoteMaintenance.html?id='+$quoteId);
			//location.reload();
			window.setTimeout(QUOTE_PRINT.reloadPage, 1000);
			//location.href='quoteMaintenance.html?id='+$quoteId			
		},
		
		

		reloadPage : function() {
			$quoteIdSelector = QUOTE_PRINT['modalName'] + ' input[name="quoteId"]'
			var $quoteId = $($quoteIdSelector).val();
			location.href='quoteMaintenance.html?id='+$quoteId
		}
}