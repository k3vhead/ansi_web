<html>
    <head>
        <title>Permission Demo</title>
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
            ;PERMSTUFF = {
                init : function() {
                	PERMSTUFF.getTotalList();
                    
                },

                
                getTotalList : function() {
                	var jqxhr = $.ajax({
						type: 'GET',
						url: "permission/1158",
						data: {},
						statusCode: {
							200: function($data) {
								PERMSTUFF.makeTable($data.data);
								PERMSTUFF.makeClickers();
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
                	
                	$.each($data.permissionList, function($index, $value) {
                		var $funcAreaTR = $("<tr>");
                		var $funcAreaTD = $("<td>");
                		$funcAreaTD.attr("class","funcarea");
                		$funcAreaTD.attr("data-id",$value[0].permissionName);
                		$funcAreaTD.append($value[0].permissionName);
                		console.log($value[0].permissionName);
                		
                		$funcAreaTR.append($funcAreaTD);    
                		
                		
                		$funcAreaTable.append($funcAreaTR)
                	});
                	$("#tableGoesHere").append($funcAreaTable);
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

            PERMSTUFF.init();
        });
        </script>
    </head>
    <body>
		<div style="width:1024px; border:solid 1px #000000;">
            <table style="width: 600px; border:solid 1px #000000; margin-left:30px; margin-top:10px;margin-bottom:10px;">
            	
                <tr>
                    <td class="funcarea" data-id="fa1">Functional Area 1</td>                    
                    <td class="perm fa1" data-funcarea="fa1" data-id="perm1.1">Perm 1.1</td>
                    <td class="perm fa2" data-funcarea="fa2" data-id="perm1.1">Perm 2.1</td>
                    <td class="perm fa3" data-funcarea="fa3" data-id="perm1.1">Perm 3.1</td>
                    <td class="perm fa4" data-funcarea="fa4" data-id="perm1.1">Perm 4.1</td>
                </tr>
                <tr>
                    <td class="funcarea" data-id="fa2">Functional Area 2</td>                    
                    <td class="perm fa1" data-funcarea="fa1" data-id="perm1.1">Perm 1.2</td>
                    <td class="perm fa2" data-funcarea="fa2" data-id="perm1.1">Perm 2.2</td>
                    <td class="perm fa3" data-funcarea="fa3" data-id="perm1.1">Perm 3.2</td>
                    <td class="perm fa4" data-funcarea="fa4" data-id="perm1.1">Perm 4.2</td>
                </tr>
                <tr>
                    <td class="funcarea" data-id="fa3">Functional Area 3</td>                    
                    <td class="perm fa1" data-funcarea="fa1" data-id="perm1.1">Perm 1.3</td>
                    <td class="perm fa2" data-funcarea="fa2" data-id="perm1.1">Perm 2.3</td>
                    <td class="perm fa3" data-funcarea="fa3" data-id="perm1.1">Perm 3.3</td>
                    <td class="perm fa4" data-funcarea="fa4" data-id="perm1.1">Perm 4.3</td>
                </tr>
                <tr>
                    <td class="funcarea" data-id="fa4">Functional Area 4</td>                    
                    <td class="perm fa1" data-funcarea="fa1" data-id="perm1.1">Perm 1.4</td>
                    <td class="perm fa2" data-funcarea="fa2" data-id="perm1.1">Perm 2.4</td>
                    <td class="perm fa3" data-funcarea="fa3" data-id="perm1.1">Perm 3.4</td>
                    <td class="perm fa4" data-funcarea="fa4" data-id="perm1.1">Perm 4.4</td>
                </tr>
            </table>
		</div>
		<br />
		<div id="tableGoesHere"></div>
		EOF
    </body>
</html>