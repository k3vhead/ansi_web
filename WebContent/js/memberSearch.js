	        $(function() {
	            $('#goButton').click(function($event) {
		            $event.preventDefault();
		            var $searchTerm = $("input[name='memberSearchTerm']").val();
		            $("#memberDisplay").fadeOut("slow");
		            doMemberCall({'searchTerm':$searchTerm});
	            });

	            $("#memberSearchTerm").focus().autocomplete("memberAutoComplete.html", {
	            	delay:200, 
	            	minChars:1,
	            	width:450,
	            	selectFirst:false,
	            	matchContains:true,
	            	extraParams:{src:'name'},
	            	max:500
	            });
	        });

