$(function(){
    ;MENU_SPACE = {
        	makeReportList : function() {
        		var jqxhr = $.ajax({
    				type: 'GET',
    				url: 'options',
    				data: 'REPORT_TYPE',
    				statusCode: {
    					200: function($data) {
    						$.each($data.data.reportType, function($index, $value){
    							$("#ansi_report_menu").append(
	    							$('<li>').append(
	    								$('<a>').attr('href','report.html?id='+$value.reportType).append(
	    									$value.display
	    								)
	    							)
	    						);
    						});
    					},
    					403: function($data) {
    						//$("#globalMsg").html("Session Timeout. Log in and try again").show();
    					}, 
    					404: function($data) {
    						$("#globalMsg").html("System Error 404 (report menu): Contact Support").show();
    					}, 
    					405: function($data) {
    						$("#globalMsg").html("System Error 405 (report menu): Contact Support").show();
    					}, 
    					500: function($data) {
    						$("#globalMsg").html("System Error 500 (report menu): Contact Support").show();
    					} 
    				},
    				dataType: 'json'
    			});
    	    }
        }
    
    
	MENU_SPACE.makeReportList();    
	
    $("ul.dropdown li").hover(function(){
    
        $(this).addClass("hover");
        $('ul:first',this).css('visibility', 'visible');
    
    }, function(){
    
        $(this).removeClass("hover");
        $('ul:first',this).css('visibility', 'hidden');
    
    });
    
    $("ul.dropdown li ul li:has(ul)").find("a:first").append(" &raquo; ");

    $("#buildDate").click(function($event) {
    	var $webDate = $(this).attr("data-webBuildDate");
    	var $commonDate = $(this).attr("data-commonBuildDate");
    	var $reportDate = $(this).attr("data-reportBuildDate");
    	alert("Web Build: " + $webDate + "\nCommon Build: " + $commonDate + "\nReport Build: " + $reportDate);
    });
    
    
    $( "#taskListModal" ).dialog({
        autoOpen: false,
        height: "auto",
        width: 780,
        modal: true,
        buttons: [{
        	id: 'taskListCloseButton',
        	click: function() {
        		$( "#taskListModal" ).dialog( "close" );
        	}
        }],
        close: function() {
        	$( "#taskListModal" ).dialog( "close" );
        }
    });

    
    
    $("#taskList").click(function($event) {
    	$.ajax({     
        	type: 'GET',
        	url: 'deployedTaskList.html',
        	data:  {},
        	success: function($data) {
            	$("#taskListModal").html($data);
                $("#taskListModal").dialog( "option", "title", "Latest Modifications" );
                $('#taskListCloseButton').button('option', 'label', 'Close');
                $("#taskListModal").dialog( "open" );
        	},
        	dataType: 'html'
   		});
    });

    

});