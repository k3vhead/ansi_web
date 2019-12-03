$(function() {    	
	;CALLNOTES = {
		// when the call notes button is in a datatables lookup table, call this function
		// in "drawcallback". (the same place you do function binding)
		lookupLink : function() {
			$(".call-note-action-link").click(function($event) {
				alert("you clicked a note\n" + $(this).attr("data-xrefid") + " ");
			});
		}
	}

});