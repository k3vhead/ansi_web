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
        	#clockBox {
        		width:100%; 
        		text-align:center; 
        		padding-left:25px;
        	}
			#helloBox {
				width:100%; 
				clear:both; 
				text-align:center;
			}
			#motd {
				padding-top:25px;
				padding-bottom:25px;
			}
			
			
			#clock {
	        	position: relative;
	        	width: 300px;
	        	height: 300px;
	        	margin: 10px auto 0 auto;
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
	        .perm { display:none; width:200px; } 
            .hilite { background-color: #404040;}
            .lowlite { background-color: #FFFFFF; }
            .funcArea { width: 300px; }
            #quickLink-container {
        		width: 33%;
			    margin-left:auto; 
			    margin-right:auto;
        	}        
        	#table-quickLink {
        		width: 33%;
			    margin-left:auto; 
			    margin-right:auto;
        	}
        	#column-container-a {	
        		width: 100%;
        	}
        	#column-container-b { 
        		 width:66%;
        		 float:right; 
        	}
        	#tableQuickLink {
				width:33%;
				float:left;
				
        	}
        	#division-description {
        		text-align:center;
        		font-weight:bold;
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
        	#lookup-container {
        		width:33%; 
        		float:left; 
        	}
        	#table-lookup {
				width:33%;
			    margin-left:auto; 
			    margin-right:auto;
			}
			
        	#ticket-modal {
				display:none;	
			}
        	#report-container {
				width:33%;
				float:right;
			}
			#table-report {
				width:33%;
			    margin-left:auto; 
			    margin-right:auto;
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
        </style>
        <script type="text/javascript" src="js/dashboard.js"></script>
        <script type="text/javascript" src="js/clock.js"></script>  
    
    
	
	
	<script type="text/javascript">
        $(function() {
            ;DASHBOARD = {
                init : function() {
                	DASHBOARD.getTotalList("report");
                	DASHBOARD.getTotalList("quickLink");
                	DASHBOARD.getTotalList("lookup");
                    
                },

                
                getTotalList : function(type) {
                	var jqxhr = $.ajax({
						type: 'GET',
						url: "user/dashboardFavorites?type=" + type,
						data: {},
						statusCode: {
							200: function($data) {
								DASHBOARD.makeTable(type, $data.data);
								DASHBOARD.makeClickers();
							},					
							403: function($data) {
								$("#globalMsg").html("Session Timeout. Log in and try again");
							},
							404: function($data) {
								$("#globalMsg").html("System Error Reorder 404. Contact Support");
							},
							500: function($data) {
								$("#globalMsg").html("System Error Reorder 500. Contact Support");
							}
						},
						dataType: 'json'
					});
                },
                
                
                
                makeTable : function(type, $data) {
                	var $funcAreaTable = $("<table>");
                	$funcAreaTable.attr("style","border:solid 1px #000000; margin-left:30px; margin-top:10px;margin-bottom:10px;");
                	
                	$.each($data.favoriteList, function($index, $value) {
                		var $funcAreaTR = $("<tr>");
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
                		
                		
                		$funcAreaTable.append($funcAreaTR)
                	});
                	$("#table-"+type).append($funcAreaTable);
                },
                
                
                makeClickers : function() {
                    $(".funcarea").click(function($event) {
                        var $id = $(this).attr("data-id");
                        $(".funcarea").removeClass("hilite");
                        $(this).addClass("hilite");
                        $(".perm").hide();
                        $selector = "." + $id;
                        $($selector).fadeIn(2000);
                    });

                    $(".perm").click(function($event) {
                        var $id = $(this).attr("data-id");
                        var $functionalArea = $(this).attr("data-funcarea");
                        if ( $(this).hasClass("hilite")) {
                            $(this).removeClass("hilite");
                        } else {
                            var $selector1 = "." + $functionalArea;
                            $($selector1).removeClass("hilite");
                            $(this).addClass("hilite");
                        }
                        
                    });
                }
            }

            DASHBOARD.init();
        });
        </script>
        
    
    
    
    </tiles:put>
    
    
    
    <tiles:put name="content" type="string">
		<h1>ANSI Scheduling Dashboard</h1>
    	<div id="helloBox">
    		<h4><span id="helloText">Hello</span> <c:out value="${com_ansi_scilla_session_data.user.firstName}" /></h4>
    		<div id="clockBox">
	    		<ul id="clock">	
				   	<li id="sec"></li>
				   	<li id="hour"></li>
					<li id="min"></li>
				</ul>
			</div>
    		<div id="motd"></div>
    	</div>
    
    
    <div id="column-container-a" style="border:1px solid black;">
			
			<div id="lookup-container" style="border:1px solid black;">
			<span class="formLabel">Lookup List:</span>
				<div id="table-lookup" style="border:1px solid black;"></div>
			</div>
	
			<div id="column-container-b style="border:1px solid black;">
			
	    		<div id="report-container" style="border:1px solid black;" >
				<span class="formLabel">Report List:</span>
				<div id="table-report" style="border:1px solid black;"></div>
				</div>
				<div id="quickLink-container" style="border:1px solid black;">
				<span class="formLabel">Quick Link List:</span>			
				<div id="table-quickLink" style="border:1px solid black;"></div>
				</div>
				<div class="spacer">&nbsp;</div>
			</div>
			<div class="spacer">&nbsp;</div>
			</div>
	
	
	
	</div>	
    	
    	
    	
	
    	<webthing:scrolltop />

		
   
   
    </tiles:put>
	
	
	

</tiles:insert>

