$(function() {    	
	;DOCUTILS = {
		docTypeList : [],
		lookupTableSelect : null,
		
		init : function($lookupTableSelect) {
			DOCUTILS.makeModals();
			ANSI_UTILS.getOptionList("DOCUMENT_TYPE",DOCUTILS.makeDocTypeList);
			DOCUTILS.makeClickers();
			DOCUTILS.lookupTableSelect = $lookupTableSelect;
		},
		
		
		
		// when the document view button is in a datatables lookup table, call this function
		// in "drawcallback". (the same place you do function binding)
		documentLink : function() {
			$(".document-view-action-link").click(function($event) {
				var $xrefId = $(this).attr("data-id");
				var $url = "documentViewer.html?id=" + $xrefId;
				
				var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					statusCode: {
						200: function($data) {
							$("#documentViewer").html($data);
							$("#documentViewer").dialog("open");
						},
						403: function($data) {
							$("#globalMsg").html("Session expired. Log in and try again").show();
						},
						404: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 404. Contact Support").show();
						},
						405: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 405. Contact Support").show();
						},
						500: function($data) {
							$("#globalMsg").html("System Error: Document Viewer 500. Contact Support").show();
						} 
					},
					dataType: 'html'
				});	
			});
			
			
			
			
			$(".document-edit-action-link").on("click", function($clickevent) {
				console.log("action link");
				$clickevent.preventDefault();
				var $documentId = $(this).attr("data-id");
				var $url = "document/validate/" + $documentId
				console.log($url);
				
		        var jqxhr = $.ajax({
					type: 'GET',
					url: $url,
					data: null, //$outbound,
					statusCode: {
						200: function($data) { 
							if ( $data.responseHeader.responseCode == "SUCCESS") {
								console.log("form load");
								$("#new-document-form .documentId").html($data.data.documentId);
								$("#new-document-form input[name='documentId']").val($data.data.documentId);
								$("#new-document-form input[name='description']").val($data.data.description);
								$("#new-document-form input[name='documentDate']").val($data.data.documentDate);
								$("#new-document-form input[name='expirationDate']").val($data.data.expirationDate);
								DOCUTILS.showEditModal();
							} else {
								$("#globalMsg").html("System error: Invalid response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
							}
						},
						403: function($data) {
							$("#new-document-modal").dialog("close");
							$("#globalMsg").html("Session has expired. Login and try again").show();
						},
						404: function($data) {
							$("#new-document-modal").dialog("close");
							$("#globalMsg").html("System Error 404. Contact Support").show();
						},
						405: function($data) {
							$("#new-document-modal").dialog("close");
							$("#globalMsg").html("System Error 405. Contact Support").show();
						},
						500: function($data) {
							$("#new-document-modal").dialog("close");
							$("#globalMsg").html("System Error 500. Contact Support").show();
						} 
					},
					dataType: 'json'
				});	
								
			});
			
			
			
			
			$(".document-delete-action-link").on("click", function($clickevent) {
				console.log("delete link");
				$clickevent.preventDefault();
				var $documentId = $(this).attr("data-id");
				$("#documentConfirmation").attr("documentId", $documentId);
				$("#documentConfirmation").dialog("open");
			});
		},
		
		
		doDeleteDocument : function($documentId) {
			console.log("Yep - deleting " + $documentId);
			var $url = "documentUpload/" + $documentId;
			var jqxhr = $.ajax({
				type: 'DELETE',
				url: $url,
				data: null, 
				statusCode: {
					200: function($data) { 
						if ( $data.responseHeader.responseCode == "SUCCESS") {
							$("#globalMsg").html("Success").show().fadeOut(6000);
							console.log()
							$(DOCUTILS.lookupTableSelect).DataTable().ajax.reload();
						} else {
							$("#globalMsg").html("System error: Invalid response code: " + $data.responseHeader.responseCode + ". Contact Support").show();
						}
					},
					403: function($data) {
						$("#globalMsg").html("Session has expired. Login and try again").show();
					},
					404: function($data) {
						$("#globalMsg").html("System Error 404. Contact Support").show();
					},
					405: function($data) {
						$("#globalMsg").html("System Error 405. Contact Support").show();
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
					} 
				},
				dataType: 'json'
			});	
		},
		
		
		
		doSaveDocument : function() {
			console.log("doSaveDocument");
	
			// step 1 - validate input
			var $outbound = {};
			$.each( $("#new-document-modal input"), function($index, $value) {
					console.log($value.name);
					$outbound[$value.name] = $value.value;	
			});
	        $.each( $("#new-document-modal select"), function($index, $value) {
				console.log($value.name);
				$outbound[$value.name] = $value.value;	
			});					
			console.log($outbound);
			var $url = "document/validate";
			if ( $("#new-document-modal input[name='documentId']").val() != null ) {
				$url = $url + "/" + $("#new-document-modal input[name='documentId']").val();
			}
	        var jqxhr = $.ajax({
				type: 'POST',
				url: $url,
				data: JSON.stringify($outbound),
				statusCode: {
					200: function($data) { 
						if ( $data.responseHeader.responseCode == "EDIT_FAILURE") {
							console.log("Edit fail");
							DOCUTILS.showUploadMessages($data.data);
						} else if ( $data.responseHeader.responseCode == "SUCCESS") {
							console.log("form submit");
							$("#new-document-form").submit();
						} else {
							$("#globalMsg").html("System error: Invalid response code. Contact Support").show();
						}
					},
					403: function($data) {
						$("#new-document-modal").dialog("close");
						$("#globalMsg").html("Session has expired. Login and try again").show();
					},
					404: function($data) {
						$("#new-document-modal").dialog("close");
						$("#globalMsg").html("System Error 404. Contact Support").show();
					},
					405: function($data) {
						$("#new-document-modal").dialog("close");
						$("#globalMsg").html("System Error 405. Contact Support").show();
					},
					500: function($data) {
						$("#new-document-modal").dialog("close");
						$("#globalMsg").html("System Error 500. Contact Support").show();
					} 
				},
				dataType: 'json'
			});	
		},
		
		
		
		
		makeAddressComplete : function() {
			console.log("makeAddressComplete");
			var $quoteComplete = $( "#new-document-form input[name='xrefName']" ).autocomplete({
				'source':"addressTypeAhead?",
				select: function( event, ui ) {
					$( "#new-document-form input[name='xrefId']" ).val(ui.item.id);
		      	},
				response: function(event, ui) {
					if (ui.content.length === 0) {
						$( "#new-document-form input[name='xrefId']" ).val(-1); //server side will see this and mark it invalid
						$("#document-upload-message-modal" ).html("No Matching Addresses");
						$("#document-upload-message-modal" ).dialog("open");
					}
				}
			}).data('ui-autocomplete');
			
			$quoteComplete._renderMenu = function( ul, items ) {
				var that = this;
				$.each( items, function( index, item ) {
					that._renderItemData( ul, item );
				});
				if ( items.length == 1 ) {
					$( "#new-document-form input[name='xrefName']" ).val(items[0].value);
					$( "#new-document-form input[name='xrefId']" ).val(items[0].id);
				}
			};	
		},
		
		
		
		
		makeClickers : function() {
			console.log("makeClickers");
			$("#new-document-form select[name='documentType']").change(function() {
				$autoComplete = $( "#new-document-form input[name='xrefName']" ).autocomplete("instance");
				if ( $autoComplete != null ) {
					$( "#new-document-form input[name='xrefName']" ).autocomplete("destroy");
				}
				var $selectedType = $("#new-document-form select[name='documentType']").val();
				if ( $selectedType == 'SIGNED_CONTRACT') {
					DOCUTILS.makeQuoteComplete();
				} else if ( $selectedType == 'TAX_EXEMPT') {
					DOCUTILS.makeAddressComplete();
				}
			});
		},
		
		
		
		
		makeDocTypeList : function($data) {
			console.log("makeDocTypeList");
			var $select = $("#new-document-modal select[name='documentType']");
			$('option', $select).remove();
			$select.append(new Option("",""));
			$.each($data.documentType, function(index, val) {
			    $select.append(new Option(val.display, val.code));
			});	
		},
		
		
		
		makeModals : function() {
			$("#new-document-modal" ).dialog({
				title:'New Document',
				autoOpen: false,
				height: 400,
				width: 600,
				modal: true,
				buttons: [
					{
						id: "new-document-cancel-button",
						click: function() {
							$("#new-document-modal .err").html("");
							$("#new-document-modal").dialog( "close" );
						}
					},{
						id: "new-document-go-button",
						click: function($event) {
							console.log("Saving a new document");
							$("#new-document-modal .err").html("");
							DOCUTILS.doSaveDocument();
						}
					}	      	      
				],
				close: function() {
					$("#new-document-modal").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#new-document-cancel-button').button('option', 'label', 'Cancel');
			$('#new-document-go-button').button('option', 'label', 'Save');
			
			
			
			
			$("#document-upload-message-modal" ).dialog({
				title:'System Message',
				autoOpen: false,
				height: 200,
				width: 300,
				modal: true,
				buttons: [
					{
						id: "document-message-cancel-button",
						click: function() {
							$("#document-upload-message-modal").dialog( "close" );
						}
					}	      	      
				],
				close: function() {
					$("#document-upload-message-modal").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#document-message-cancel-button').button('option', 'label', 'OK');
			
			
			
			$("#documentViewer" ).dialog({
				title:'Document Viewer',
				autoOpen: false,
				height: 800,
				width: 1000,
				modal: true,
				buttons: [
					{
						id: "document-viewer-cancel-button",
						click: function() {
							$("#documentViewer").dialog( "close" );
						}
					}	      	      
				],
				close: function() {
					$("#documentViewer").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#document-viewer-cancel-button').button('option', 'label', 'OK');
			
			
			
			
			$("#documentConfirmation" ).dialog({
				title:'Confirm Delete',
				autoOpen: false,
				height: 200,
				width: 300,
				modal: true,
				buttons: [
					{
						id: "document-confirmation-cancel-button",
						click: function() {
							$("#documentConfirmation").dialog( "close" );
						}
					},{
						id: "document-confirmation-confirm-button",
						click: function() {
							console.log("confirm delete");
							$("#documentConfirmation").dialog( "close" );
							$documentId = $("#documentConfirmation").attr("documentId");
							DOCUTILS.doDeleteDocument($documentId);
						}
					}	      	      
				],
				close: function() {
					$("#documentViewer").dialog( "close" );
					//allFields.removeClass( "ui-state-error" );
				}
			});        			
			$('#document-confirmation-cancel-button').button('option', 'label', 'No');
			$('#document-confirmation-confirm-button').button('option', 'label', 'Yes');
		},
	
		
		
		makeQuoteComplete : function() {
			console.log("makeQuoteComplete");
			var $quoteComplete = $( "#new-document-form input[name='xrefName']" ).autocomplete({
				'source':"quoteAutoComplete?",
				select: function( event, ui ) {
					$( "#new-document-form input[name='xrefId']" ).val(ui.item.id);
		      	},
				response: function(event, ui) {
					if (ui.content.length === 0) {
						$( "#new-document-form input[name='xrefId']" ).val(-1); //server side will see this and mark it invalid
						$("#document-upload-message-modal" ).html("No Matching Quotes");
						$("#document-upload-message-modal" ).dialog("open");
					}
				}
			}).data('ui-autocomplete');
			
			$quoteComplete._renderMenu = function( ul, items ) {
				var that = this;
				$.each( items, function( index, item ) {
					that._renderItemData( ul, item );
				});
				if ( items.length == 1 ) {
					$( "#new-document-form input[name='xrefName']" ).val(items[0].value);
					$( "#new-document-form input[name='xrefId']" ).val(items[0].id);
				}
			};	
		},

		
		
		showAddModal : function() {
			$("#new-document-modal").dialog("option", "title", "New Document");
			$("#new-document-modal .addRow").show();
			$("#new-document-modal .documentId").html("");
			$("#new-document-modal input").val("");
			$("#new-document-modal select").val("");
			DOCUTILS.showModal();
		},
		
		
		showEditModal : function() {
			$("#new-document-modal").dialog("option", "title", "Update Document Attributes");
			$("#new-document-modal .addRow").hide();
			DOCUTILS.showModal();
		},
		
		
		showModal : function() {
			$("#new-document-modal .err").html("");
			$("#new-document-modal input[name='step']").val("validate");
			$("#new-document-modal").dialog("open");			
		},
		
		
		
		showUploadMessages : function($data) {
    		$.each($data.webMessages, function($index, $value) {
    			var $selector = "#new-document-modal ." + $index + "-err";
    			$($selector).html($value[0]);
    		});
    		
    	},
    	
    	
    	
    	
	}
});