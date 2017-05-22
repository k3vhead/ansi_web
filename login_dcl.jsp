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
			.forgotPassLink {
				color:#000000;
				text-decoration:none;
				font-style:italic;
				font-size:12px;
				font-family:times roman,serif;
			}
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
			
			.bigText {
				font-size:42px;
			}
			.almostAsBigText {
				font-size:22px;
			}
			input {
				font-size:18px;
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
	        }
			
        </style>
        <%--
        <script src="js/login.js"></script>
        --%>
        
        <script type="text/javascript">
        $(function() {
        	$("input[name=userid]").val($("input[name=userid]").data("default"));
        	$("input[name=password]").val($("input[name=password]").data("default"));
			$(".loginField").focus(function() {
				var $labelField = "#" + $(this).data("label");
				var $labelText = $labelField + " .labelText";
				$($labelField).removeClass("isNotVisible");
				$($labelField).addClass("isVisible");
				$($labelText).fadeIn(2000);
				if ( $(this).val() == $(this).data("default")) {
					$(this).val("");
				} else {
					$(this).select();
				}
			});
		});
		</script>        
    </tiles:put>
    
    
    <tiles:put name="content" type="string">
		<div style="width:1300px;">
			<div style="float:right; width:66%;">
				<div style="margin-top:150px; text-align:center; width:340px; padding-right:30px; padding-left:30px; border:solid 1px #000000; background-color:#CCCCCC; opacity:0.5">
					<span class="bigText">Welcome</span><br />
					<span class="almostAsBigText">to ANSI Scheduling</span>			
					<form method="post" action="#">
						<div id="userIdLabel" class="loginLabel isNotVisible"><span class="labelText">User ID</span></div>
						<div style="text-align:center; border-bottom:solid 1px #000000; margin-top:30px;">
							<input type="text" name="userid" class="loginField" data-label="userIdLabel" data-default="User ID" />		
							<i class="fa fa-user fa-3x" aria-hidden="true" style="float:left;"></i>
							<div style="clear:both;">&nbsp</div>
						</div>
						
						<div id="passwordLabel" class="loginLabel isNotVisible">Password</div>
						<div style="text-align:center; border-bottom:solid 1px #000000; margin-top:30px;">
							<input type="password" name="password" class="loginField"  data-label="passwordLabel" data-default="Password" />		
							<i class="fa fa-lock fa-3x" aria-hidden="true" style="float:left;"></i>
							<div style="clear:both;">&nbsp</div>
						</div>
					</form>
					
				</div>
			</div>
			<div style="float:left; width:33%;">
				&nbsp;
			</div>
		</div>
		<div id="bottomSpace">
			&nbsp;
		</div>
    </tiles:put>

</tiles:insert>

