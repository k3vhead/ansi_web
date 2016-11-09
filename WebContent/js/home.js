var current = 0;
var next;
var $items;
var $brags;
var $nextbrag;
var $currentbrag = 0

$(function() {
    $items = $('#slideshowContainer .slideshowImageContainer');
    $brags = $('#slideshowContainer2 .slideshowBragContainer')

    setInterval( "nextSlide()", 12000 );
    $($brags.get(0)).show();
    setInterval( "nextBrag()", 8000 );
    
    
    $(".eventLink").click( function($event) {
        $event.preventDefault();
		                
		var $eventId = $event.currentTarget.id;
		
    	$.ajax({     
        	type: 'POST',
        	url: 'event.html',
        	data:  {'id':$eventId},
        	success: function($data) {
            	$("#eventDisplay").html($data);
        		$('#eventWindow').bPopup({
                    modalClose: false,
                    opacity: 0.6,
                    positionStyle: 'fixed' //'fixed' or 'absolute'
                });
        	},
        	dataType: 'html'
   		});
    });

    $("#closeEventButton").click( function($event) {
    	$('#eventWindow').bPopup().close();
    });
    
	$.ajax({     
    	type: 'GET',
    	url: 'survey.html',
    	data:  {'step':'display'},
    	success: function($data) {
    		$('#surveyDiv').html($data);
    		$('#surveyGo').bind("click", function($event) {
    			$.ajax({     
    				type: 'POST',
    				url: 'survey.html',
    				data: $('#surveyForm :input').serialize(),
    				success: function($data) {
    					$("#surveyDiv").html($data);    					
    				},
    				dataType: 'html'
    			});
    		});
    		$('#surveyResult').bind("click", function($event) {
    			$.ajax({     
    				type: 'POST',
    				url: 'surveyResults.html',
    				success: function($data) {
    					$("#surveyDiv").html($data);    					
    				},
    				dataType: 'html'
    			});
    		});
    	},
    	dataType: 'html'
	});

	$.ajax({     
    	type: 'GET',
    	url: 'facebookFeedPanel.html',
    	data:  {},
    	success: function($data) {
    		$fbMessage = "";
    		if ( $data.picture ) {
    			$fbMessage = '<img src="' + $data.picture + '" style="float:right; width:275px;" />';
    		}
    		$fbMessage = $fbMessage + ' ' + $data.message
    		$('#fbMessage').html($fbMessage);
    	},
    	dataType: 'json'
	});
});

function nextSlide() {
    next = current + 1;
    if ( next > $items.length - 1 ) {
        next = 0;
    } 
    $($items.get(current)).fadeOut(4000);
    setTimeout("doFadeIn()", 4000); // delay before fading in next pic
    current = next;
}

function doFadeIn() {
    $($items.get(next)).fadeIn(7000);
}

function nextBrag() {
    $nextbrag = $currentbrag + 1;
    if ( $nextbrag > $brags.length - 1 ) {
    	$nextbrag = 0;
    } 
    $($brags.get($currentbrag)).fadeOut(2000);
    setTimeout("doBragIn()", 2000); // delay before fading in next pic
    $currentbrag = $nextbrag;
}

function doBragIn() {
    $($brags.get($nextbrag)).fadeIn(2000);
}

