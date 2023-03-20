$(function() {
	;LOGIN = {
		
		init : function() {
			LOGIN.makeClickers();
		},
		
		
		doReset : function() {
			console.log("doReset");	
		},
		
		
		makeClickers : function() {
			$("#goButton").click(function($event) {
		    	$(".working").show();
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
		    	    		$(".working").hide();
		    	    		$("#loginMsg").html($data.responseJSON.responseHeader.responseMessage);
		    	    		$("#loginMsg").fadeIn('fast');
		    	    		$("#loginMsg").fadeOut(6000);
		    	    	}, 
		    	    	404: function($data) {
		    	    		$(".working").hide();
		    	    		$("#loginMsg").html("System Error 404: Contact Support");
		    	    		$("#loginMsg").fadeIn('fast');
		    	    	}, 
		    	    	500: function($data) {
		    	    		$(".working").hide();
		    	    		$("#loginMsg").html("System Error 500: Contact Support");
		    	    		$("#loginMsg").fadeIn('fast');
		    	    	} 
		    	     },
		    	     dataType: 'json'
		    	});
		    });
		    
		    $("input[name='userid']").on('focus',function(e) {
		    	$("#validUser").addClass("fa");
		    	$("#validUser").addClass("fa-ban");
				$("#validUser").addClass("inputIsInvalid");
		    });
		    
		    $("input[name='password']").on('focus',function(e) {
		    	$("#validPass").addClass("fa");
		    	$("#validPass").addClass("fa-ban");
				$("#validPass").addClass("inputIsInvalid");
		    });
		    
		    $("input[name='userid']").on('input',function(e) {
		    	userid = $("input[name='userid']").val();
		    	var re = /.+\@.+\..+/;
		    	if ( re.test(userid) ) {
		    		$("#validUser").removeClass("fa");
		    		$("#validUser").removeClass("fa-ban");
		    		$("#validUser").removeClass("inputIsInvalid");
		    		$("#validUser").addClass("far");
		    		$("#validUser").addClass("fa-check-square");
		    		$("#validUser").addClass("inputIsValid");
		    	} else {
		    		$("#validUser").removeClass("far");
		    		$("#validUser").removeClass("fa-check-square");
		    		$("#validUser").removeClass("inputIsValid");
		    		$("#validUser").addClass("fa");
		    		$("#validUser").addClass("fa-ban");
		    		$("#validUser").addClass("inputIsInvalid");
		    	}
		    	
		    });
		    $("input[name='password']").on('input',function(e) {
		    	password = $("input[name='password']").val();
		    	var re = /.+/;
		    	if ( re.test(password) ) {
		    		$("#validPass").removeClass("fa");
		    		$("#validPass").removeClass("fa-ban");
		    		$("#validPass").removeClass("inputIsInvalid");
		    		$("#validPass").addClass("far");
		    		$("#validPass").addClass("fa-check-square");
		    		$("#validPass").addClass("inputIsValid");
		    	} else {
		    		$("#validPass").removeClass("far");
		    		$("#validPass").removeClass("fa-check-square");
		    		$("#validPass").removeClass("inputIsValid");
		    		$("#validPass").addClass("fa");
		    		$("#validPass").addClass("fa-ban");
		    		$("#validPass").addClass("inputIsInvalid");
		    	}    	
		    });
		    
		    $(".forgotPassLink").click( function($clickevent) {
				$clickevent.preventDefault();
				if ( ! $("#resetModal").hasClass('ui-dialog-content') ) {
					LOGIN.makeResetModal();
				}
				$("#resetModal input").prop('disabled', false);
				$("#resetModal input[type='text']").val("");
				$("#resetModal input[type='password']").val("");
				$("#resetModal .resetConfirm").hide();
				$("#resetModal .resetGo").hide();
				$("#resetModal .err").html("");
				$( "#resetModal" ).dialog("open");
				var $loginId = $("input[name='userid']").val();
				if ( $loginId.length > 0 ) {
					$("#resetModal input[name='resetUserId']").val($loginId);
				}
			});
		    
	    	$("input[name='userid']").focus();
		},
		
		
		makeResetModal : function() {
			console.log("makeResetModal");
			$( "#resetModal" ).dialog({
				title:'Password Reset',
				autoOpen: false,
				height: 400,
				width: 550,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id:  "reset_cancel",
						click: function($event) {
							$( "#resetModal" ).dialog("close");
						}
					}
//							ANSI_UTILS.makeServerCall("POST", "sysAdmin/appProperty", $outbound, $callbacks, {});
				]
			});	
			$("#reset_cancel").button('option', 'label', 'Cancel'); 
			
			$("#resetModal input[name='resetVerify']").click(function($event) {
				var $outbound = LOGIN.makeResetOutbound();
				$outbound['resetStep'] = 'VERIFY';
				ANSI_UTILS.makeServerCall("POST", "passwordReset", JSON.stringify($outbound), {200:LOGIN.resetVerifySuccess}, {});				
			});
			
			$("#resetModal input[name='resetConfirm']").click(function($event) {
				var $outbound = LOGIN.makeResetOutbound();
				$outbound['resetStep'] = 'CONFIRM';
				ANSI_UTILS.makeServerCall("POST", "passwordReset", JSON.stringify($outbound), {200:LOGIN.resetConfirmSuccess}, {});				
			});
			
			$("#resetModal input[name='resetGo']").click(function($event) {
				var $outbound = LOGIN.makeResetOutbound();
				$outbound['resetStep'] = 'GO';
				ANSI_UTILS.makeServerCall("POST", "passwordReset", JSON.stringify($outbound), {200:LOGIN.resetGoSuccess}, {});
			});
		},
		
		
		
		makeResetOutbound : function() {
			console.log("makeResetOutbound");
			var $outbound = {};
			$.each( $("#resetModal input"), function($index, $value) {
				if ( $value.type == 'text' || $value.type == 'password') {
					$outbound[$value.name] = $($value).val();
				}
			});
			return $outbound;
		},
		
		
		resetConfirmSuccess : function($data, $passthru) {
			console.log("resetConfirmSuccess");
			$("#resetModal .err").html("");
			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
				$(".resetGo").hide();
				LOGIN.showResetMessages($data.data.webMessages);
			} else if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
				$(".resetVerify input").prop('disabled', true);
				$(".resetConfirm input").prop('disabled', true);
				$(".resetGo").show();
			} else {
				$("#resetModal").dialog("close");
				$("#globalMsg").html("Unexpected response " + $data.responseHeader.responseCode + ". Contact Support.").show();
			}
		},
		
		
		resetGoSuccess : function($data, $passthru) {
			console.log("resetGoSuccess");
			$("#resetModal .err").html("");
			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
				LOGIN.showResetMessages($data.data.webMessages);
			} else if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
				$("#resetModal").dialog("close");
				$("#globalMsg").html("Success! You can log in with your new password").show();
			} else {
				$("#resetModal").dialog("close");
				$("#globalMsg").html("Unexpected response " + $data.responseHeader.responseCode + ". Contact Support.").show();
			}
		},
		
		
		resetVerifySuccess : function($data, $passthru) {
			console.log("resetVerifySuccess");
			$("#resetModal .err").html("");
			if ( $data.responseHeader.responseCode == 'EDIT_FAILURE') {
				$(".resetConfirm").hide();
				$(".resetGo").hide();
				LOGIN.showResetMessages($data.data.webMessages);
			} else if ( $data.responseHeader.responseCode == 'SUCCESS' ) {
				$(".resetVerify input").prop('disabled', true);
				$(".resetConfirm").show();
			} else {
				$("#resetModal").dialog("close");
				$("#globalMsg").html("Unexpected response " + $data.responseHeader.responseCode + ". Contact Support.");
			}
		},
		
		
		
		showResetMessages : function($webMessages) {
			$("#resetModal .err").html("");
			$.each($webMessages, function($index, $value) {
				var $selector = "#resetModal ." + $index + "Err";
				$($selector).html($value[0]);
			});
		}
	};
	
	LOGIN.init();
	
	
    

    
    
	

});