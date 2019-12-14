$(function() {    	
	;CALLNOTE = {
		callTypeList : null,	
		callNoteAccordion : false,
		
		
		init : function() {
			ANSI_UTILS.getCodeList('call_log','contact_type',CALLNOTE.makeCallTypeList); 
			CALLNOTE.makeAutoComplete();
		},
		
		
		
		clearForm : function($xrefType, $xrefId) {
			console.log("clearForm " + $xrefType + " " + $xrefId);
			$("#note-crud-form .err").html("");
			
			$.each( $("#note-crud-form input"), function($index, $value) {        				
				var $selector = '#note-crud-form input[name="' + $value.name + '"]';
				$($selector).val("");        				
			});
			
			$("#crud-modal-notes").val("");
			
			var $select = $("#note-crud-form select[name='contactType']");
			$('option', $select).remove();

			$select.append(new Option("",""));
			$.each(CALLNOTE.callTypeList, function(index, val) {
				console.log(val);
			    $select.append(new Option(val.displayValue, val.value));
			});			
			$("#note-crud-form input[name='xrefType']").val($xrefType);
			$("#note-crud-form input[name='xrefId']").val($xrefId);
		},
		
		
		
		// when the call notes button is in a datatables lookup table, call this function
		// in "drawcallback". (the same place you do function binding)
		lookupLink : function() {
			$(".call-note-action-link").click(function($event) {
				var $xrefId = $(this).attr("data-xrefid");
				var $xrefType = $(this).attr("data-xreftype");
				var $url = "callNote/callNote/" + $xrefType + "/" + $xrefId;
				
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							//$callback(200, $data);
							if ( $data.data.noteList.length == 0 ) {
								CALLNOTE.openNoteForm($xrefType, $xrefId)
							} else {
								CALLNOTE.showListModal($data.data);
							}
							console.log($data);
						},
						403: function($data) {
							$callback(403, $data);
						},
						404: function($data) {
							$callback(404, $data);
						},
						405: function($data) {
							$callback(405, $data);
						},
						500: function($data) {
							$callback(500, $data);
						} 
					},
					dataType: 'json'
				});	
			});
		},
		
		
		
		makeAutoComplete : function() {
			var $addressDisplaySelector = '#note-crud-form input[name="address"]';
    		$( $addressDisplaySelector ).autocomplete({
				'source':"addressTypeAhead?term=",
				position:{my:"left top", at:"left bottom",collision:"none"},
				appendTo:"#node-crud-form",
				select: function( event, ui ) {
					$('#note-crud-form input[name="addressId"]').val(ui.item.id);							
					if ( ui.item.value == null || ui.item.value.trim() == "" ) {
						$($contactDisplaySelector).val(ui.item.label)
					}
			      	},
			      	response: function(event, ui) {
					if (ui.content.length === 0) {
						$('#note-crud-form input[name="addressId"]').val("");	
						$("#new-address-modal input").val("");
						$("#new-address-modal .err").html("");
						$("#new-address-modal input[name='name']").val($($addressDisplaySelector).val());
						$("#new-address-modal").dialog("open");
					}
				}
		 	});
    		
    		
    		var $contactDisplaySelector = '#note-crud-form input[name="contact"]';
    		$( $contactDisplaySelector ).autocomplete({
				'source':"contactTypeAhead?term=",
				position:{my:"left top", at:"left bottom",collision:"none"},
				appendTo:"#node-crud-form",
				select: function( event, ui ) {
					$('#note-crud-form input[name="contactId"]').val(ui.item.id);
					if ( ui.item.value == null || ui.item.value.trim() == "" ) {
						$($contactDisplaySelector).val(ui.item.label);
					}
			      	},
			      	response: function(event, ui) {
					if (ui.content.length === 0) {
						$('#note-crud-form input[name="contactId"]').val("");
						$('#new-contact-modal input[type="text"]').val("");
						$("#new-contact-modal .err").html("");
						var $name = $($contactDisplaySelector).val().split(" ");
						$("#new-contact-modal input[name='firstName']").val($name[0]);
						if ( $name.length > 1 ) {
							$("#new-contact-modal input[name='lastName']").val($name[1]);
						}
						$("#new-contact-modal").dialog("open");
					}
				}
		 	});
    		
    		
    		var $ansiDisplaySelector = '#note-crud-form input[name="ansi"]';
    		$( $ansiDisplaySelector ).autocomplete({
				'source':"ansiUserAutoComplete?",
				position:{my:"left top", at:"left bottom",collision:"none"},
				appendTo:"#node-crud-form",
				select: function( event, ui ) {
					$('#note-crud-form input[name="userId"]').val(ui.item.id);
					if ( ui.item.value == null || ui.item.value.trim() == "" ) {
						$($contactDisplaySelector).val(ui.item.label);
					}
			      	},
			      	response: function(event, ui) {
					if (ui.content.length === 0) {
						alert("Nobody here by that name");
						$('#note-crud-form input[name="userId"]').val("");
					}
				}
		 	});
		},
		
		
		// this is a list of call types from the code table
		// we "makeModal" here to ensure that we have a list of call types before continuing
		makeCallTypeList : function($data) {
			console.log("makeCallTypeList");
			CALLNOTE.callTypeList = $data.codeList;  
			CALLNOTE.makeModal();
		},
		
		
		
		makeModal : function() {
			console.log("makeModal");
			$( "#note-crud-form" ).dialog({
				title:'Call Note',
				autoOpen: false,
				height: 500,
				width: 700,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "note-cancel-button",
						click: function($event) {
							$( "#note-crud-form" ).dialog("close");
						}
					},{
						id: "note-save-button",
						click: function($event) {
							CALLNOTE.saveNote();
						}
					}
				]
			});	
			$("#note-save-button").button('option', 'label', 'Save');
			$("#note-cancel-button").button('option', 'label', 'Cancel');
			
			
			
			
			$( "#new-address-modal" ).dialog({
				title:'New Address',
				autoOpen: false,
				height: 450,
				width: 600,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "address-cancel-button",
						click: function($event) {
							$( "#new-address-modal" ).dialog("close");
						}
					},{
						id: "address-save-button",
						click: function($event) {
							CALLNOTE.saveAddress();
						}
					}
				]
			});	
			$("#address-save-button").button('option', 'label', 'Save');
			$("#address-cancel-button").button('option', 'label', 'Cancel');
			
			
			
			
			
			
			$( "#new-contact-modal" ).dialog({
				title:'New Contact',
				autoOpen: false,
				height: 500,
				width: 700,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "contact-cancel-button",
						click: function($event) {
							$( "#new-contact-modal" ).dialog("close");
						}
					},{
						id: "contact-save-button",
						click: function($event) {
							CALLNOTE.saveContact();
						}
					}
				]
			});	
			$("#contact-save-button").button('option', 'label', 'Save');
			$("#contact-cancel-button").button('option', 'label', 'Cancel');
			
			
			
			
			$( "#call-note-list-modal" ).dialog({
				title:'Call Note History',
				autoOpen: false,
				height: 500,
				width: 700,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "call-note-list-cancel-button",
						click: function($event) {
							$( "#call-note-list-modal" ).dialog("close");
						}
					},{
						id: "call-note-list-new-button",
						click: function($event) {
							$( "#call-note-list-modal" ).dialog("close");
							CALLNOTE.openNoteForm($("#call-note-list-modal").attr("data-xreftype"),$("#call-note-list-modal").attr("data-xrefid"));
						}
					}
				]
			});	
			$("#call-note-list-cancel-button").button('option', 'label', 'Cancel');
			$("#call-note-list-new-button").button('option', 'label', 'New');
			
		},
		
		
		
		openNoteForm : function($xrefType, $xrefId) {
			CALLNOTE.clearForm($xrefType, $xrefId);
			$( "#note-crud-form" ).dialog("open");
		},
		
		
		
		saveAddress : function() {
			$outbound = {};
			$.each( $("#new-address-modal input"), function($index, $value) {
				$outbound[$($value).attr("name")] = $($value).val();
			});
			
			
			var jqxhr = $.ajax({
				type: 'POST',
				url: "address/add",
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						console.log($data);	
						$("#new-address-modal .err").html("");
						if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
							$.each( $data.data.webMessages, function($index, $value) {
								var $selector = "#new-address-modal ." + $index + "Err";
								console.log($selector + " " + $value[0]);	
								$($selector).html($value[0]).show();
							});							
						} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
							$("#globalMsg").html("Success").show().fadeOut(6000);
							$("#new-address-modal").dialog("close");
							$("#note-crud-form input[name='addressId']").val($data.data.address.addressId);
							$("#note-crud-form input[name='address']").val($data.data.address.name);
						} else {
							$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
							$("#new-address-modal").dialog("close");
							$('html, body').animate({scrollTop: 0}, 800);
						}
					},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Login and try again").show();
						$("#new-address-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					404: function($data) {
						$("#globalMsg").html("Invalid system state. Reload this page and try again").show();
						$("#new-address-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					405: function($data) {
						$("#globalMsg").html("System Error 405. Contact Support").show();
						$("#new-address-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
						$("#new-address-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					} 
				},
				dataType: 'json'
			});	
		},
		
		
		
		
		saveContact : function() {
			$outbound = {};
			$outbound['firstName'] = $("#new-contact-modal input[name='firstName']").val();
			$outbound['lastName'] = $("#new-contact-modal input[name='lastName']").val();
			$outbound['preferredContact'] = $("#new-contact-modal input[name='preferredContact']").val();
			$outbound['email'] = $("#new-contact-modal input[name='email']").val();
			$phoneType = $("#new-contact-modal select[name='phone_type']").val();
			$outbound[$phoneType] = $("#new-contact-modal input[name='phone']").val();
			
			console.log($outbound);
						
			var jqxhr = $.ajax({
				type: 'POST',
				url: "contact/add",
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						console.log($data);	
						$("#new-contact-modal .err").html("");
						if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
							$.each( $data.data.webMessages, function($index, $value) {
								var $selector = "#new-contact-modal ." + $index + "Err";
								console.log($selector + " " + $value[0]);	
								$($selector).html($value[0]).show();
							});							
						} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
							$("#globalMsg").html("Success").show().fadeOut(6000);
							$("#new-contact-modal").dialog("close");
							$("#note-crud-form input[name='contactId']").val($data.data.contact.contactId);
							$("#note-crud-form input[name='contact']").val($data.data.contact.firstName + " " + $data.data.contact.lastName);
						} else {
							$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
							$("#new-contact-modal").dialog("close");
							$('html, body').animate({scrollTop: 0}, 800);
						}
					},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Login and try again").show();
						$("#new-contact-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					404: function($data) {
						$("#globalMsg").html("Invalid system state. Reload this page and try again").show();
						$("#new-contact-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					405: function($data) {
						$("#globalMsg").html("System Error 405. Contact Support").show();
						$("#new-contact-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
						$("#new-contact-modal").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					} 
				},
				dataType: 'json'
			});	
		},
		
		
		saveNote : function() {
			var $xrefType = $("#note-crud-form input[name='xrefType']").val();
			var $xrefId = $("#note-crud-form input[name='xrefId']").val();
			var $url = "callNote/callNote/" + $xrefType + "/" + $xrefId;
			
			$outbound = {};
			$.each( $("#note-crud-form input"), function($index, $value) {
				$outbound[$($value).attr("name")] = $($value).val();
			});
			$.each( $("#note-crud-form select"), function($index, $value) {
				$outbound[$($value).attr("name")] = $($value).val();
			});
			$outbound["notes"] = $("#crud-modal-notes").val();
			
			var jqxhr = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) {
						console.log($data);	
						$("#note-crud-form .err").html("");
						if ( $data.responseHeader.responseCode == "EDIT_FAILURE" ) {
							$.each( $data.data.webMessages, function($index, $value) {
								var $selector = "#note-crud-form ." + $index + "Err";
								console.log($selector + " " + $value[0]);	
								$($selector).html($value[0]).show();
							});							
						} else if ( $data.responseHeader.responseCode == "SUCCESS" ) {
							$("#globalMsg").html("Success").show().fadeOut(6000);
							$("#note-crud-form").dialog("close");
							$('html, body').animate({scrollTop: 0}, 800);
						} else {
							$("#globalMsg").html("Invalid Response Code " + $data.responseHeader.responseCode + ". Contact Spport").show();
							$("#note-crud-form").dialog("close");
							$('html, body').animate({scrollTop: 0}, 800);
						}
					},
					403: function($data) {
						$("#globalMsg").html("Session Timeout. Login and try again").show();
						$("#note-crud-form").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					404: function($data) {
						$("#globalMsg").html("Invalid system state. Reload this page and try again").show();
						$("#note-crud-form").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					405: function($data) {
						$("#globalMsg").html("System Error 405. Contact Support").show();
						$("#note-crud-form").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
						$("#note-crud-form").dialog("close");
						$('html, body').animate({scrollTop: 0}, 800);
					} 
				},
				dataType: 'json'
			});	
		},
		
		
		
		
		
		showListModal : function($data) {
			console.log($data);
			$("#call-note-list-modal .note-list").html("");
			$("#call-note-list-modal").attr("data-xreftype", $data.xrefType);
			$("#call-note-list-modal").attr("data-xrefid", $data.xrefId);
			$.each($data.noteList, function($index, $value) {
				var $li = $('<li>');
				var $h4 = $('<h4>');
				var $div = $('<div>');
				$h4.append($value.contactName + ' ' + $value.startTime + ' ' + $value.summary);
				$li.append($h4);
				$div.append($value.content);
				$li.append($div);
				$("#call-note-list-modal .note-list").append($li);
			});
			if ( CALLNOTE.callNoteAccordion == true ) {
				$('#call-note-list-modal .note-list').accordion("refresh");
			} else {
				$('#call-note-list-modal .note-list').accordion({
					//autoHeight: true,
					heightStyle: "content",
					alwaysOpen: true,
					header: 'h4',
					fillSpace: false,
					collapsible: false,
					active: true
				});
				CALLNOTE.callNoteAccordion = true;
			}
			$("#call-note-list-modal").dialog("open");
		},

	}
	
	

});