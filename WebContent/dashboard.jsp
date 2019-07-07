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
        Dashboard
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	a {
        		color:black;
        		text-decoration:none;
        	}
        	td input[type="checkbox"] {
    			float: right;
			}
        	#clockBox {
        		width:100%; 
        		text-align:center; 
        	}
			#helloBox {
				width:100%; 
				clear:both; 
				text-align:center;
			}
			#motd {
				padding-bottom:25px;
			}
			
			#clock-container {
				float: right;
				width: 400px;
				
			}
			#clock {
	        	position: relative;
	        	width: 300px;
	        	height: 300px;
	        	background: url(images/clock/clockface.jpg);
	        	background-repeat: no-repeat;
	        	list-style: none;
	        	}
	        
	        #sec, #min, #hour {
	        	position: absolute;
	        	width: 15px;
	        	height: 300px;
	        	top: 0px;
	        	left: 142px;
	        	}
	        
	        #sec {
	        	background: url(images/clock/sechand.png);
	        	z-index: 3;
	           	}
	           
	        #min {
	        	background: url(images/clock/minhand.png);
	        	z-index: 2;
	           	}
	           
	        #hour {
	        	background: url(images/clock/hourhand.png);
	        	z-index: 1;
	           	}
	           	
	        p {
	            text-align: center; 
	            padding: 10px 0 0 0;
	            }
	        .edit-lookups { cursor:pointer; }
	        .perm { display:none; width:200px; } 
            .hilite { background-color: #404040;}
            .lowlite { background-color: #FFFFFF; }
            .funcArea { width: 300px; }
           	#column-container-b  #quickLink-container {
        		width: 49%;
				float: left;
        	}        
			#quickLink-container h1 {
				 color: white;
        		 background-color: black;
				 margin: 0;
				 padding: 0;
				 width: 100%;
				 height: 25px;
				 float: right;
			}
        	#quickLink-container #table-quickLink {
        		width: 100%;
				float: left;
        	}
        	#column-container-a {	
        		width: 1300px;
        	}
        	#column-container-a #column-container-b { 
        		 width:44%;
        		 float:left; 
        	}
        	#division-description {
        		text-align:center;
        		font-weight:bold;
        	}
        	.checkbox {
        		float: right;
        	}
        	#loading-container {
        		width:100%;
        		border:solid 1px #000000;
        		display:none;
        	}
        	#message-button-container {
        		width:100%;
        		text-align:center;
        		display:none;
        	}
        	#column-container-a #lookup-container {
        		width:21%; 
        		float:left; 
        	} 
        	#lookup-container h1 {
				 color: white;
        		 background-color: black;
				 margin: 0;
				 padding: 0;
				 width: 100%;
				 height: 25px;
				 float: left;
			}
        	#lookup-container #table-lookup {
				 width: 99%;
			}
			
        	#ticket-modal {
				display:none;	
			}
        	#column-container-b #report-container {
				width:50%;
				float:left;
			}
			h1 {
        		text-align:center;				 
			}
			#report-container h1 {
				 color: white;
        		 background-color: black;
				 margin: 0;
				 padding: 0;
				 width: 100%;
				 height: 25px;
				 float: left;
			}
			#report-container #table-report {
				width:100%;
				float:left;
			}
			.action-button {
				cursor:pointer;
			}
			.search-button {
				color:#000000;
				text-decoration:none;
			}
        	.ticket {
        		border:solid 1px #404040;
        		width:90%;
        	}
			.ticket-clicker {
				color:#000000;
			}
			.ui-droppable-hover {
				background-color:#CC6600;
			}
        	.user {

        	}
        	.is-not-favorite {
        		display:none;
        	}
        	.favorite-checkbox {
        		display: none;
        	}
        </style>
        <script type="text/javascript" src="js/dashboard.js"></script>
        <script type="text/javascript" src="js/clock.js"></script>  
    
    
	
	
	<script type="text/javascript">
        $(function() {
            ;DASHBOARD = {
           		donelist : {"report":false, "quickLink":false, "lookup":false},
           		
                init : function() {                	
                	DASHBOARD.getTotalList("report");
                	DASHBOARD.getTotalList("quickLink");
                	DASHBOARD.getTotalList("lookup");
                    DASHBOARD.makeIcons();
                },

                makeIcons : function() {
                	$(".edit-lookups").click(function(event) {
                		$('.favorite-checkbox').toggle();
                		$('.is-not-favorite').toggle();
                		$('.is-favorite').show();
                	});
                },
                
                getTotalList : function(type) {
                	var jqxhr = $.ajax({
						type: 'GET',
						url: "user/dashboardFavorites?type=" + type,
						data: {},
						statusCode: {
							200: function($data) {
								DASHBOARD.makeTable(type, $data.data);
							},					
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Reorder 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Reorder 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
                },
                
                
                
                makeTable : function(type, $data) {
                	var $funcAreaTable = $("<table>");
                	$funcAreaTable.attr("style","width: 96%; margin-top:10px;margin-bottom:10px;");
                	
                	$.each($data.favoriteList, function($index, $value) {
                		var $selected = "is-not-favorite";
                		if ( $value.selected == true ) {
                			$selected = "is-favorite";
                		}
                		var $funcAreaTR = $("<tr>");
                		$funcAreaTR.attr("class", $selected);
                		$funcAreaTR.attr("id",$value.menu);
                		var $funcAreaTD = $("<td>");
                		$funcAreaTD.attr("class","funcarea");
                		
                		$link = $("<a>");
                		$link.attr("href", $value.link);
                		$link.append($value.displayText);
                		
                		
                		
                		
                		//$funcAreaTD.attr("data-id",$value[0].favoriteList);
                		$funcAreaTD.append($link);
                		//console.log($value[0].favoriteList);
                		
                		$funcAreaTR.append($funcAreaTD);    
                		//$funcAreaTR.attr("class","funcarea");
                		//$funcAreaTR.attr("data-id",$value[0].permissionIsActive);
                		//$funcAreaTR.append($displayText);
                		//console.log($value[0].permissionIsActive);
                		
                		$checkboxTD = $("<td>");
                		$checkbox = $('<input type="checkbox">');
                		if ( $value.selected == true )  {
                			$checkbox.attr("checked","checked");
                		}
                		$checkbox.attr("name", $value.menu);
                		$checkbox.attr("class","favorite-checkbox");
                		$checkboxTD.append($checkbox);
                		$funcAreaTR.append($checkboxTD);
                		
                		$funcAreaTable.append($funcAreaTR)
                	});
                	$("#table-"+type).append($funcAreaTable);
                	DASHBOARD.donelist[type] = true;
                	
                	if ( DASHBOARD.donelist["report"] == true && DASHBOARD.donelist["quickLink"] == true && DASHBOARD.donelist["lookup"] == true) {
	                	$('.favorite-checkbox').click(function() {
	                		DASHBOARD.toggleFavorite( this.name );
	                	});
                	} 
                },
                
                
                toggleFavorite : function(menu) {
                	var jqxhr = $.ajax({
						type: 'POST',
						url: "user/dashboardFavorites",
						data: JSON.stringify({"menu":menu}),
						statusCode: {
							200: function($data) {
								$("#globalMsg").html("Success").show().fadeOut(3000);
								var selector = "input[name='" + menu + "']";
								if($(selector).is(":checked")) {
									$("#"+menu).addClass('is-favorite');
									$("#"+menu).removeClass('is-not-favorite');
								} else {
									$("#"+menu).removeClass('is-favorite');
									$("#"+menu).addClass('is-not-favorite').show();
								}
							},					
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again").show();
							},
							404: function($data) {
								$("#globalMsg").html("System Error Reorder 404. Contact Support").show();
							},
							500: function($data) {
								$("#globalMsg").html("System Error Reorder 500. Contact Support").show();
							}
						},
						dataType: 'json'
					});
                },
            }

            DASHBOARD.init();
        });
        </script>
        
    
    
    
    </tiles:put>
    
    
    
    <tiles:put name="content" type="string">
		<h1>ANSI Scheduling Dashboard</h1>
    	<div id="helloBox">
    		<h4><span id="helloText">Hello</span> <c:out value="${com_ansi_scilla_session_data.user.firstName}" /></h4>
    		<div id="motd"></div>
    	</div>
    	
    	
    	 <body>
			<div id="column-container-a">
		 	<div id="lookup-container" style="border:1px solid black;">
		 		<div style="width:100%; background-color:#000000;">					
					<div style="float:left; width:80%;background-color:inherit;color:#FFFFFF; border:0;text-indent: 3px;">Lookup</div>
					<div style="float:left; display:inline-block; overflow:hidden; width:20%; text-align: center;background-color:inherit; border:0;">
					<webthing:edit styleClass="edit-lookups">Edit Favorites</webthing:edit></div>
					
				</div>
				<div id="table-lookup"></div>
			</div>
			<div id="column-container-b">
			<div id="report-container" style="border:1px solid black;">
		 		<div style="width:100%; background-color:#000000;">
					<div style="float:left; width:80%;background-color:inherit;color:#FFFFFF; border:0;text-indent: 3px;">Report</div>
					<div style="float:left; display:inline-block; overflow:hidden; width:20%; text-align: center;background-color:inherit; border:0;">
					<webthing:edit styleClass="edit-lookups">Edit Favorites</webthing:edit></div>
					
				</div>
			<div id="table-report"></div>
			</div>
			<div id="quickLink-container" style="border:1px solid black;">
		 		<div style="width:100%; background-color:#000000;">
					<div style="float:left; width:80%;background-color:inherit;color:#FFFFFF; border:0;text-indent: 3px;">Quick Link</div>					
					<div style="float:left; display:inline-block; overflow:hidden; width:20%; text-align: center;background-color:inherit; border:0;">
					<webthing:edit styleClass="edit-lookups">Edit Favorites</webthing:edit></div>
					
				</div>
			<div id="table-quickLink"></div>
			</div>
			</div>
			<div id="clock-container">
    		<div id="clockBox">
	    		<ul id="clock">	
				   	<li id="sec"></li>
				   	<li id="hour"></li>
					<li id="min"></li>
				</ul>
			</div>
			</div>
		 </div>
		 
		 </body>
    	
    	
    	
	
    	<webthing:scrolltop />

		
   
   
    </tiles:put>
	
	
	

</tiles:insert>

