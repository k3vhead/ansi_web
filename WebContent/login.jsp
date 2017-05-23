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
        Login
    </tiles:put>
    
    
    <tiles:put name="headextra" type="string">
        <style type="text/css">
        	<%--
			#bottomSpace {
				clear:both; 
				width:100%;
				padding-top:50px;
			}
			#columnLeft {
				width:66%; 
				float:left; 
				margin-top:50px;
			}
			#columnRight {
				width:50%; 
				float:right;
			}
			#formDiv {
				border:solid 1px #000000; 
				padding:30px; 
				width:85%;
			} --%>
			.forgotPassLink {
				color:#000000;
				text-decoration:none;
				font-style:italic;
				font-size:12px;
				font-family:times roman,serif;
			}
			
			.bigText {
				font-size:42px;
				color:#000000;
				opacity:1.0;
			}
			.almostAsBigText {
				font-size:32px;
				color:#EEEEEE;
				opacity:1.0;
			}
			input {
				font-size:24px;
			}
			
	        .loginLabel {
	        	text-align:center;
	        	margin-top:20px;
	        	height:20px;
	        }
	        .loginField {
	        	text-align:center; 
	        	border:none; 
	        	background-color:#CCCCCC; 
	        	font-family:arial; 
	        	font-size:14px;
	        }
	        .isVisible {
	        	visibility:visible;
	        }
	        .isNotVisible {
	        	visibility:hidden;
	        }
	        .labelText {
	        	display:none;
	        	font-size:30px;
	        	color:#000000;
	        	opacity:1;
	        	text-decoration:underline;
	        }
			
        </style>
        <%--
        <script src="js/login.js"></script>
        --%>
        
        <script type="text/javascript">
        $(function() {
        	$("input[name=userid]").val($("input[name=userid]").data("default"));
        	$("input[name=password]").val($("input[name=password]").data("default"));
        	
        	$("input[name='userid']").focus(function() {
        		doLoginInput($(this));
		    	$("#validUser").addClass("fa");
		    	$("#validUser").addClass("fa-ban");
				$("#validUser").addClass("inputIsInvalid");
        	});
        	
        	$("input[name='password']").focus(function() {
        		doLoginInput($(this));
		    	$("#validPass").addClass("fa");
		    	$("#validPass").addClass("fa-ban");
				$("#validPass").addClass("inputIsInvalid");
        	});
        	
			//$(".loginField").focus(function() {
			function doLoginInput($field) {
				var $labelField = "#" + $field.data("label");
				var $labelText = $labelField + " .labelText";
				$($labelField).removeClass("isNotVisible");
				$($labelField).addClass("isVisible");
				$($labelText).fadeIn(2000);
				if ( $field.val() == $field.data("default")) {
					$field.val("");
				} else {
					$field.select();
				}


			}
			
			
		    $("#goButton").click(function($event) {
		    	$event.preventDefault();
		    	$userid = $("input[name='userid']").val();
		    	$password = $("input[name='password']").val();
		    	$outbound = JSON.stringify({'userid':$userid, 'password':$password});
		    	var jqxhr = $.ajax({
		    	     type: 'POST',
		    	     url: 'login',
		    	     data: $outbound,
		    	     statusCode: {
		    	    	200: function($data) {
		    	    		location.href="dashboard.html";
		    	     	},
		    	    	403: function($data) {
		    	    		$("#loginMsg").html($data.responseJSON.responseHeader.responseMessage);
		    	    		$("#loginMsg").fadeIn('fast');
		    	    		$("#loginMsg").fadeOut(6000);
		    	    	} 
		    	     },
		    	     dataType: 'json'
		    	});
		    });

		    
		    
		    $("input[name='userid']").on('input',function(e) {
		    	userid = $("input[name='userid']").val();
		    	var re = /.+\@.+\..+/;
		    	if ( re.test(userid) ) {
		    		$("#validUser").removeClass("fa-ban");
		    		$("#validUser").removeClass("inputIsInvalid");
		    		$("#validUser").addClass("fa-check-square-o");
		    		$("#validUser").addClass("inputIsValid");
		    	} else {
		    		$("#validUser").removeClass("fa-check-square-o");
		    		$("#validUser").removeClass("inputIsValid");
		    		$("#validUser").addClass("fa-ban");
		    		$("#validUser").addClass("inputIsInvalid");
		    	}
		    	
		    });
		    $("input[name='password']").on('input',function(e) {
		    	password = $("input[name='password']").val();
		    	var re = /.+/;
		    	if ( re.test(password) ) {
		    		$("#validPass").removeClass("fa-ban");
		    		$("#validPass").removeClass("inputIsInvalid");
		    		$("#validPass").addClass("fa-check-square-o");
		    		$("#validPass").addClass("inputIsValid");
		    	} else {
		    		$("#validPass").removeClass("fa-check-square-o");
		    		$("#validPass").removeClass("inputIsValid");
		    		$("#validPass").addClass("fa-ban");
		    		$("#validPass").addClass("inputIsInvalid");
		    	}    	
		    });
		});
		</script>
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
    	<div style="width:100%;clear:both;">&nbsp;</div>
		<div style="width:1300px;">
			<div style="float:right; width:75%;">
				<div style="margin-top:50px; text-align:center; width:50%; padding-right:30px; padding-left:30px; border:solid 1px #000000; background-color:#CCCCCC;">
					<span class="bigText" style="opacity:.99;">Welcome</span><br />
					<span class="almostAsBigText">to ANSI Scheduling</span>			
					<div>
						&nbsp;<span class="err" style="font-size:18px;" id="loginMsg"></span>&nbsp;
					</div>
					<form method="post" action="#">
						<div id="userIdLabel" class="loginLabel isNotVisible"><span class="labelText">User ID</span></div>
						<div style="text-align:center; border-bottom:solid 1px #000000; margin-top:30px;">
							<i class="fa fa-user fa-3x" aria-hidden="true" style="float:left;"></i>
							<input type="text" name="userid" class="loginField" style="width:320px; font-size:25px;" data-label="userIdLabel" data-default="User ID"></input>
							<i id="validUser" class="fa-2x" aria-hidden="true"></i>									
							<div style="clear:both;">&nbsp</div>
						</div>
						
						<div id="passwordLabel" class="loginLabel isNotVisible"><span class="labelText">Password</span></div>
						<div style="text-align:center; border-bottom:solid 1px #000000; margin-top:30px;">
							<i class="fa fa-lock fa-3x" aria-hidden="true" style="float:left;"></i>
							<input type="password" name="password" class="loginField" style="width:320px; font-size:25px;" data-label="passwordLabel" data-default="Password"></input>
							<i id="validPass" class="fa-2x" aria-hidden="true"></i>									
							<div style="clear:both;">&nbsp</div>
						</div>
						<div style="text-align:center; margin-top:30px; margin-bottom:30px;">
							<input type="submit" value="Login" id="goButton" class="prettyWideButton" />
						</div>
					</form>
						<span style="float:right;"><a href="#" class="forgotPassLink">Forgot My Password</a></span>					
				</div>
			</div>
			<div style="float:left; width:33%;">
				&nbsp;
			</div>
		</div>
    	<div style="width:100%;clear:both;">&nbsp;</div>
    </tiles:put>

</tiles:insert>

