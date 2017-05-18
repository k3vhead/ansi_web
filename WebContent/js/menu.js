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
    	alert("Web Build: " + $webDate + "\nCommon Build: " + $commonDate);
    });
});