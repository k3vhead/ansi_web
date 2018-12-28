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
                	DIVUSER.getTotalList(75);
                    
                },

                
                getTotalList : function($userId) {
                	var $url = "divisionUser/" + $userId;
                	var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
								DIVUSER.makeTable($data.data, $userId);
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
                
                
                
                makeTable : function($data, $userId) {
                	var $funcAreaTable = $("<table>");
                	$funcAreaTable.attr("style","border:solid 1px #000000; margin-left:30px; margin-top:10px;margin-bottom:10px;");
                	
                	
                	$.each($data.itemList, function($index, $value) {
                		var $funcAreaTR = $("<tr>");
                		
                		// this TD is the first column -- contains the functional areas
                		var $funcAreaTD = $("<td>");
                		if($value.active == true){
                			$funcAreaTD.addClass("hilite");
                		}
                		$funcAreaTD.addClass("funcarea");
                		$funcAreaTD.attr("data-id", $value.divisionId);
                		$funcAreaTD.attr("data-userid", $userId);
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
                        var $userId = $(this).attr("data-userid");
                        if ( $(this).hasClass("hilite")) {
                            $(this).removeClass("hilite");
                            DIVUSER.doPost($id, $userId, false);
                        } else {
                            $(this).addClass("hilite");
                            DIVUSER.doPost($id, $userId, true);
                        }
                        //$(this).addClass("hilite");
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
                           
                            $(this).addClass("hilite");
                        }
                        
                    });
                },
            
            
            	doPost : function($id, $userId, $active){
            		var $url = 'divisionUser/' + $userId;
					//console.log("YOU PASSED ROW ID:" + $rowid);
					$outbound = {"divisionId": $id, "active": $active};
					
					var jqxhr = $.ajax({
						type: 'POST',
						url: $url,
						data: JSON.stringify($outbound),
						success: function($data) {
							//console.log($data);
							
	        				$("#divisionId").val(($data.data.codeList[0]).divisionId);
	        				$("#div").val(($data.data.codeList[0]).div);
	        				$("#description").val(($data.data.codeList[0]).description);
	        				$("#active").val(($data.data.codeList[0]).active);
						},
						statusCode: {
							403: function($data) {
								$("#useridMsg").html("Session Timeout. Log in and try again");
							} 
						},
						dataType: 'json'
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