<html>
    <head>
        <title>Dashboard Favorites</title>
        <script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
    	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
        <style type="text/css">
            .perm { display:none; width:200px; } 
            .hilite { background-color: #404040;}
            .lowlite { background-color: #FFFFFF; }
            .funcArea { width: 300px; }
            #quickLink-container {
           		width:49%;
        		float:right; 
        	}        
        	#action-button-container {
        		width:100%;
        		text-align:center;
        		display:none;
        	}
        	#column-container-a {	
        		display:none;
        	}
        	#column-container-b { 
        		 width:66%;
        		 float:left; 
        	}
        	#tableQuickLink {
				width:100%;
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
        		width:49%; 
        		float:left; 
        	}
        	#tableLookup {
				width:100%;
			}
			
        	#ticket-modal {
				display:none;	
			}
        	#report-container {
				width:33%;
				float:right;
			}
			#tableReport {
				width:100%;
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
								DASHBOARD.makeTable($data.data);
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
                
                
                
                makeTable : function($data) {
                	var $funcAreaTable = $("<table>");
                	$funcAreaTable.attr("style","border:solid 1px #000000; margin-left:30px; margin-top:10px;margin-bottom:10px;");
                	
                	$.each($data.user, function($index, $value) {
                		var $funcAreaTR = $("<tr>");
                		var $funcAreaTD = $("<td>");
                		$funcAreaTD.attr("class","funcarea");
                		
                		$link = $("<a>");
                		$link.attr("href", $value.link);
                		$link.append($value.text);
                		
                		
                		
                		
                		$funcAreaTD.attr("data-id",$value[0].favoriteList);
                		$funcAreaTD.append($value[0].favoriteList);
                		console.log($value[0].favoriteList);
                		
                		$funcAreaTR.append($funcAreaTD);    
                		$funcAreaTR.attr("class","funcarea");
                		$funcAreaTR.attr("data-id",$value[0].permissionIsActive);
                		$funcAreaTR.append($value[0].permissionIsActive);
                		console.log($value[0].permissionIsActive);
                		
                		
                		$funcAreaTable.append($funcAreaTR)
                	});
                	$("#tableReport").append($funcAreaTable);
                	$("#tableQuickLink").append($funcAreaTable);
                	$("#tableLookUp").append($funcAreaTable);
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
    </head>
    <tiles:put name="content" type="string">
    	<h1>Dashboard Favorites</h1>
    	
    	
    	
    	<%--
    	 -----------------------------------------------------------------------------------------------------
    	 | column-container-a                                                                                |
		 | ---------------------------------------------------------------  -------------------------------- |
		 | | column-container-b                                          |  | user-list-container          | |
		 | | ------------------------- --------------------------------  |  | --------------------------   | |
		 | | | ticket-list-container | | action-container             |  |  | | user-list-table        |   | |	
    	 | | | ------------------    | | ---------------------------  |  |  | |                        |   | |
    	 | | | | ticket-table   |    | | | action-button-container |  |  |  | |                        |   | |
    	 | | | |                |    | | ---------------------------  |  |  | |                        |   | |
    	 | | | |                |    | | ---------------------------  |  |  | |                        |   | |
    	 | | | |                |    | | | action-list-container   |  |  |  | |                        |   | |
    	 | | | ------------------    | | ---------------------------  |  |  | --------------------------   | |
    	 | | ------------------------- --------------------------------  |  |                              | |
    	 | ---------------------------------------------------------------  -------------------------------- |
    	 -----------------------------------------------------------------------------------------------------
    	 --%>
    	<div id="column-container-a">
    		<div id="report-container" >
    			<table id="tableReport" style="width:100%;">
    				<thead style="width:100%;"></thead>
    				<tbody style="width:100%;"></tbody>
    				<tfoot style="width:100%;"></tfoot>
    			</table>
			</div>
			<div id="column-container-b">
				<div id="quickLink-container">
					<table id="tableQuickLink">					
    				<thead style="width:100%;"></thead>
    				<tbody style="width:100%;"></tbody>
    				<tfoot style="width:100%;"></tfoot>
					</div>
				</div>
				<div id="lookup-container">
					<table id="tableLookup style="width:100%;">
						<thead style="width:100%;"></thead>
						<tbody style="width:100%;"></tbody>
						<tfoot style="width:100%;"></tfoot>
					</table>
				</div>
				<div class="spacer">&nbsp;</div>
			</div>
			<div class="spacer">&nbsp;</div>
    	</div>
    	
    	
	
    	<webthing:scrolltop />

		
   
   
    </tiles:put>
</html>