$(function(){
        
	
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
        var $gitBranch = $(this).attr("data-gitbranch");
    	alert("Web Build: " + $webDate + "\nCommon Build: " + $commonDate + "\nReport Build: " + $reportDate + "\nWeb Git Branch: " + $gitBranch);
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