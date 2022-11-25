$(function() {    	
	;KNOWLEDGEBASE = {
		init : function() {
			console.log("KB init");
			var $div = $("<div>").attr("id","knowledgeDisplayModal");
			$("body").append($div)
			
			$("a.kbtag").off("click");
			$("a.kbtag").click(function($clickevent) {
				var $key = $(this).attr("data-key");
				console.log("kbtag click: " + $key);
				if ( ! $("#knowledgeDisplayModal").length ) {
					alert("Initialization issue: call support");
				}
				if ( ! $("#knowledgeDisplayModal").hasClass('ui-dialog-content') ) {
					KNOWLEDGEBASE.makeModal();
				}
				KNOWLEDGEBASE.showModal($key);
			});
		},
		
		
		makeModal : function() {
			console.log("KB makeModal");
			$("#knowledgeDisplayModal" ).dialog({
				autoOpen: false,
				title:"Knowledge",
				height: 400,
				width: 500,
				modal: true,
				buttons: [
					{
						id: "kbCancelButton",
						click: function() {
							$("#knowledgeDisplayModal").dialog( "close" );
						}
					}	      	      
				],
				close: function() {
					$("#knowledgeDisplayModal").dialog( "close" );
				}
			});
			$("#kbCancelButton").button('option', 'label', 'OK');  
		},
		
		
		
		showModal : function($key) {
			console.log("kb showModal: " + $key);
			var $url = "knowledgeBase/view/" + $key
			var jqxhr = $.ajax({
				type: "GET",
				url: $url,
				data: {},
				statusCode: {
					200: function($data) {
						$("#knowledgeDisplayModal").html($data);
						$("#knowledgeDisplayModal").dialog("open");
					},					
					403: function($data) {
						$("#globalMsg").html("Session has expired.").show();
					},
					404: function($data) {
						$("#globalMsg").html("System Error: " + $url + " 404. Contact Support").show();
					},
					405: function($data) {
						$("#globalMsg").html("Action Not Permitted").show();
					},
					500: function($data) {
						$("#globalMsg").html("System Error: " + $url + " 500. Contact Support").show();
					}
				},
				dataType: "html"
			});
		}
	}
	
	KNOWLEDGEBASE.init();

});