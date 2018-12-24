<html>
    <head>
        <title>Division User Demo</title>
        <script type="text/javascript" src="jQuery/jquery-3.1.1.min.js"></script>        
        <script type="text/javascript" src="jQuery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="jQuery/jcookie.js"></script>
    	<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
        <style type="text/css">
            .perm { display:none; width:200px; } 
            .hilite { background-color: #404040;}
            .lowlite { background-color: #FFFFFF; }
            .funcArea { width: 300px; }
        </style>
        <script type="text/javascript">
        $(function() {
            ;DIVUSER = {
                init : function() {
                	DIVUSER.getTotalList();
                    
                },

                
                getTotalList : function($userId) {
                	var $url = "divisionUser/" + $userId;
                	var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								DIVUSER.makeTable($data.data);
								DIVUSER.makeClickers();
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
                	
                	
                	$.each($data.divisionList, function($index, $value) {
                		var $funcAreaTR = $("<tr>");
                		
                		// this TD is the first column -- contains the functional areas
                		var $funcAreaTD = $("<td>");
                		$funcAreaTD.attr("class","funcarea");
                		$funcAreaTD.attr("data-id",$value.divisionId);
                		$funcAreaTD.append($value.div);
                		console.log($value.div);
                		$funcAreaTR.append($funcAreaTD);
                		
                		
                		
                		
                		$funcAreaTable.append($funcAreaTR);
                	});
                	$("#tableGoesHere").append($funcAreaTable);
                },
                
                
                makeClickers : function() {
                    $(".funcarea").click(function($event) {
                        var $id = $(this).attr("data-id");
                        $(".funcarea").removeClass("hilite");
                        $(this).addClass("hilite");
                        $(".div").hide();
                        $selector = "." + $id;
                        $($selector).show();
                    });

                    $(".div").click(function($event) {
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

            DIVUSER.init();
        });
        </script>
    </head>
    <body>
		<div style="width:1024px; border:solid 1px #000000;">
			<div id="tableGoesHere"></div>
		</div>
		
		EOF
    </body>
</html>