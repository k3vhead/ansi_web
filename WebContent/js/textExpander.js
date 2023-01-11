$(function() {    	
	;TEXTEXPANDER = {
		serviceDescription:{},
		
		init : function() {
			ANSI_UTILS.getCodeList("text_expander","service_description",TEXTEXPANDER.initServiceDescription)
		},
		
		
		
		initServiceDescription : function($data) {
			console.log("TEXTEXPANDER.initSuccess");
			console.log($data);
			$.each($data.codeList, function($index, $value) {
				console.log($value.value + " -- " + $value.displayValue);
				TEXTEXPANDER.serviceDescription[$value.value] = $value.displayValue;
			});
		},
		
		
		// Turn off text expander for a given field
		cancel : function($target) {
			$target.off("keyup");
			$target.off("blur");
		},
		
		keyup : function($event, $target) {			
			console.log("TEXTEXPANDER.keyup");
			if ( $event.keyCode == 32 ) {
				// if the spacebar was pressed
				var $text = $target.val();
				var $shortText = $text.substring(0, $text.length - 1).trim(); // length - 1 b/c the last key pressed was a space
				TEXTEXPANDER.doSubstitute($target, $shortText);
			}
		},
		
		
		blur : function($target) {
			console.log("TEXTEXPANDER.blur");
			var $text = $target.val();
			var $shortText = $text.substring(0, $text.length).trim(); 
			TEXTEXPANDER.doSubstitute($target, $shortText);
		},
		
		
		doSubstitute : function($target, $workText) {
			var $index = $workText.lastIndexOf(" ");
			var $keyword = $workText.substring($index).trim();
			console.log("[" + $keyword + "]");
			var $replacement = null;
			if ($keyword.toUpperCase() in TEXTEXPANDER.serviceDescription ) {
				$replacement = TEXTEXPANDER.serviceDescription[$keyword.toUpperCase()];				
			}
			if ( $replacement != null ) {
				var $replacementText = $workText.substring(0, $workText.length - $keyword.length).trim() + " " + $replacement.trim() + " ";
				$target.val($replacementText);
			}
		}
		
	};
});