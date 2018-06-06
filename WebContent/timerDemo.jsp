<%@ page contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="WEB-INF/sql.tld" prefix="sql" %>
<%@ taglib uri="WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="WEB-INF/struts-html.tld"  prefix="html"  %>
<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="WEB-INF/struts-bean.tld"  prefix="bean"  %>
<%@ taglib uri="WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib tagdir="/WEB-INF/tags/webthing" prefix="webthing" %>
<%@ taglib uri="WEB-INF/theTagThing.tld" prefix="ansi" %>


<tiles:insert page="layout.jsp" flush="true">

    <tiles:put name="title" type="string">
        Timer Demo
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
		<style type="text/css">
			#data-container { display:none; }
		</style>
        
        <script type="text/javascript">        
        $(function() {  
        	; TIMERDEMO = {
				attempt : 0,        			
        		data_first : null,
        		data_second : null,
        		
        		init : function() {
        			TIMERDEMO.getFirstValue();
        			TIMERDEMO.getSecondValue();
        			
        			setTimeout(function() {
        				TIMERDEMO.populateSomething();
        			},1000);
        		},
        		
        		getFirstValue : function() {
        			console.debug("Get first");
        			var jqxhr = $.ajax({
                	    type: "GET",
                	    url: "division/list",
                	    data: {},
                	    
                	     statusCode: {
                	    	200: function($data) {
                	    		console.debug("Got first");
                     	    	var $value=JSON.stringify($data);
                     	    	TIMERDEMO.data_first = $value;
                     	    },
                	    	403: function($data) {
                    	    	alert("403");
                	    	}, 
    	         	    	404: function($data) {
    	            	    	alert("404");
    	        	    	}, 
                	    	401: function($data) {
                    	    	alert("401");
                	    	}, 
                	    	500: function($data) {
                    	    	alert("500");
                	    	} 
                	     },
                	     dataType: 'json'
                	});
        		},
        		
        		getSecondValue : function() {
        			console.debug("Get second");
        			var jqxhr = $.ajax({
                	    type: "GET",
                	    url: "userLookup",
                	    data: {},
                	    
                	     statusCode: {
                	    	200: function($data) {
                	    		console.debug("Got second");
                     	    	var $value=JSON.stringify($data);
                     	    	TIMERDEMO.data_second = $value;
                     	    },
                	    	403: function($data) {
                    	    	alert("403");
                	    	}, 
    	         	    	404: function($data) {
    	            	    	alert("404");
    	        	    	}, 
                	    	401: function($data) {
                    	    	alert("401");
                	    	}, 
                	    	500: function($data) {
                    	    	alert("500");
                	    	} 
                	     },
                	     dataType: 'json'
                	});
        		},
        		
        		populateSomething : function() {
        			console.debug("Populating " + TIMERDEMO.attempt);
        			if ( TIMERDEMO.data_first == null || TIMERDEMO.data_second == null ) {
        				TIMERDEMO.attempt = TIMERDEMO.attempt + 1;
        				setTimeout(function() {
            				TIMERDEMO.populateSomething();
            			},1000);
        			} else {
	        			$("#data-container").html(TIMERDEMO.data_first + "<br /><br />" + TIMERDEMO.data_second);
	        			$("#loading-container").hide();
	        			$("#data-container").show();
        			}
        		}
        	}
        	
        	TIMERDEMO.init();
            
        });
        </script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<h1>Timer Demo</h1>

		<div id="loading-container"><webthing:thinking style="width:100%" /></div>
		<div id="data-container">
		</div>
    </tiles:put>

</tiles:insert>

