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
           	#column-container-b  #quickLink-container {
        		width: 49%;
				float: right;
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
				float: right;
        	}
        	#column-container-a {	
        		width: 1300px;
        	}
        	#column-container-a #column-container-b { 
        		 width:66%;
        		 float:right; 
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
        	#column-container-a #lookup-container {
        		width:33%; 
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
				 width: 100%;
			}
			
        	#ticket-modal {
				display:none;	
			}
        	#column-container-b #report-container {
				width:49%;
				float:left;
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
                    DASHBOARRD.makeIcons();
                },

                makeIcons : function() {
                	$(".edit-lookups").click(function() {
                		$(".checkboxstuff").toggle();
                		$(".not-a-favorite").toggle();
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
                	$funcAreaTable.attr("style","margin-left:30px; margin-top:10px;margin-bottom:10px;");
                	
                	$.each($data.favoriteList, function($index, $value) {
                		var $selected = "is-not-favorite";
                		if ( $value.selected == true ) {
                			$selected = "is-favorite";
                		}
                		var $funcAreaTR = $("<tr>");
                		$funcAreaTR.attr("class", $selected);
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
                		$checkbox.attr("name", $value.link);
                		$checkbox.attr("class","favorite-checkbox");
                		$checkboxTD.append($checkbox);
                		$funcAreaTR.append($checkboxTD);
                		
                		$funcAreaTable.append($funcAreaTR)
                	});
                	$("#table-"+type).append($funcAreaTable);
                },
                
                
                makeClickers : function() {
                /*	var checkBox = document.getElementById("myCheck");
                	var funcarea = document.getElementById("funcarea");
                	if (checkBox.checked == true){
                		funcarea.style.display = "block";
                	  	} else {
                	  	funcarea.style.display = "none";
                	  	}
                }   */
                	
                	
                	
                	
                	
                	
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
    	
    	
    	 <body>
			<div id="column-container-a">
		 	<div id="lookup-container" style="border:1px solid black;">
		 		<div style="width:100%; background-color:#000000;">
					<div style="float:right; width:10%; text-align:right;background-color:inherit; border:0;"><webthing:edit styleClass="edit-lookups">Edit Favorites</webthing:edit></div>
					<div style="float:left; width:89%;background-color:inherit;color:#FFFFFF; border:0;">Lookup</div>
				</div>
				<div id="table-lookup"></div>
			</div>
			<div id="column-container-b">
			<div id="report-container" style="border:1px solid black;">
			<h1>Report</h1>
			<div id="table-report""></div>
			</div>
			<div id="quickLink-container" style="border:1px solid black;">
			<h1>Quick Link</h1>		
			<div id="table-quickLink"></div>
			</div>
			</div>
		 </div>
		 
		 </body>
    	
    	
    	
	
    	<webthing:scrolltop />

		
   
   
    </tiles:put>
	
	
	

</tiles:insert>

