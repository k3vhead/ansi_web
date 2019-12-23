$( document ).ready(function() {
	;ANSI_UTILS = {
		getOptions: function($optionList) {
			var $returnValue = null;
			var jqxhr1 = $.ajax({
				type: 'GET',
				url: 'options',
				data: $optionList,
				success: function($data) {
					$returnValue = $data.data;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
		        async: false
			});
			return $returnValue;
		},
		
		getStatusList: function($ticketStatusList, $callback) {
			console.log("getStatusList");
			var $url = "ticket/" + $ticketStatusList;
			var jqxhr2 = $.ajax({
				type: 'GET',
				url: $url,
				data: {},							
				statusCode: {
					200: function($data) {
						$callback($data.data)
					},					
					403: function($data) {
						$("#globalMsg").html("Session Expired. Log In and try again").show();
					},
					404: function($data) {
						$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
					}
				},							
				dataType: 'json'
			});
		},
	
		// get a list of values from the codes table
		// this option could fail with asynchronous calls. Use getCodeList() instead
		getCodes: function($tableName, $fieldName) {
			var $returnValue = null;
			var $url = "code/" + $tableName;
			if ( $fieldName != null ) {
				$url = $url + "/" + $fieldName;
			}
			var jqxhr2 = $.ajax({
				type: 'GET',
				url: $url,
				data: {},
				success: function($data) {
					return $data.data;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json'
			});
		},
		
		
		
		getCodeList: function($tableName, $fieldName, $callback) {
			console.log("getCodeList");
			var $url = "code/" + $tableName;
			if ( $fieldName != null ) {
				$url = $url + "/" + $fieldName;
			}
			var jqxhr2 = $.ajax({
				type: 'GET',
				url: $url,
				data: {},							
				statusCode: {
					200: function($data) {
						$callback($data.data)
					},					
					403: function($data) {
						$("#globalMsg").html("Session Expired. Log In and try again").show();
					},
					404: function($data) {
						$("#globalMsg").html("Invalid quote").show().fadeOut(4000);
					},
					500: function($data) {
						$("#globalMsg").html("System Error 500. Contact Support").show();
					}
				},							
				dataType: 'json'
			});
		},
		
		populateCodeSelect : function($tableName, $fieldName, $selectorName, $keyField, $valueField ) {
			var $url = "code/" + $tableName;
			if ( $fieldName != null ) {
				$url = $url + "/" + $fieldName;
			}
			
			var jqxhr = $.ajax({
				type: 'GET',
				url: $url,
				data: {},
				statusCode: {
					200: function($data) {
						var $select = $($selectorName);
						$('option', $select).remove();
						$select.append(new Option("",""));
						$.each($data.data.codeList, function(index, val) {
						    $select.append(new Option(val[$valueField], val[$keyField]));
						});						
					},					
					403: function($data) {
						$("#globalMsg").html("Session expired. Login and try again").show();
					},
					404: function($data) {
						$("#globalMsg").html("Code Error 404. Contact Support").show();
					},
					500: function($data) {
						$("#globalMsg").html("Code Error 500. Contact Support").show();
					}
				},
				dataType: 'json'
			});
		},
		
		
		// get a list of divisions
		// this function can fail with asynchronous calls. Use makeDivisionList() instead
		getDivisionList: function() {
			var $returnValue = null;
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'division/list',
				data: {},
				success: function($data) {
					$returnValue = $data.data.divisionList;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
				async:false
			});
			return $returnValue;
		},
		
		
		makeDivisionList: function($successCallback, $failureCallback) {
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'division/list',
				data: {},
				statusCode: {
					200: function($data) {
						$successCallback($data);
					},					
					403: function($data) {
						$failureCallback($data);
					},
					404: function($data) {
						$failureCallback($data);
					},
					500: function($data) {
						$failureCallback($data);
					}
				},				
				dataType: 'json'
			});
		},
		
		
		// get User info
		getUser: function($userid) {
			var $returnValue = null;
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'user/'+$userid,
				data: {},
				success: function($data) {
					$returnValue = $data.data.userList;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					} 
				},
				dataType: 'json',
				async:false
			});
			return $returnValue;
		},
		
		
		makeBuildingTypeList:function() {							
			var $returnValue = null;
			var jqxhr3 = $.ajax({
				type: 'GET',
				url: 'code/job/building_type',
				data: {},
				success: function($data) {
					$returnValue = $data.data.codeList;
				},
				statusCode: {
					403: function($data) {
						$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
					},
					404: function($data) {
						$returnValue = {};
					},
					500: function($data) {
						
					}
				},
				dataType: 'json',
				async:false
			});
			return $returnValue;

		},

		
		// displays a red "ban" (crossed-off circle) icon
		markInvalid: function($selector) {
			$($selector).removeClass("far");
    		$($selector).removeClass("fa-check-square");
    		$($selector).removeClass("inputIsValid");
    		$($selector).addClass("fa");
    		$($selector).addClass("fa-ban");
    		$($selector).addClass("inputIsInvalid");
		},
		
		
		// displays a green checkmark
		markValid : function($selector) {
			$($selector).removeClass("fa");
    		$($selector).removeClass("fa-ban");
    		$($selector).removeClass("inputIsInvalid");
    		$($selector).addClass("far");
    		$($selector).addClass("fa-check-square-o");
    		$($selector).addClass("inputIsValid");
		},
		
		
		
		// Set the values in an html select tag
		setOptionList: function($selectorName, $optionList, $selectedValue) {
		
			var $select = $($selectorName);
			$('option', $select).remove();

			$select.append(new Option("",""));
			$.each($optionList, function(index, val) {
			    $select.append(new Option(val.display, val.abbrev));
			});
			
			if ( $selectedValue != null ) {
				$select.val($selectedValue);
			}
			//$select.selectmenu({ width : '175px', maxHeight: '400 !important', style: 'dropdown'});

		},
		
		
		getFieldValue: function($namespace, $field) {
			$selectorName = "#" + $namespace + "_" + $field;
			return $($selectorName).val();
			
		},
		
		
		removeFromArray : function(arr) {
			var what, a=arguments, L = a.length, ax;
			while ( L > 1 && arr.length ) {
				what = a[--L];
				while ((ax = arr.indexOf(what)) !== -1 ) {
					arr.splice(ax, 1);
				}
			}
			return arr;
		},
		
		
		
		setCheckbox: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).prop('checked', $value);
		},
				
		setFieldValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).val($value);
			
		},
		
		setTextValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).html($value);
		},
		
		setSelectValue: function($namespace, $field, $value) {
			$selectorName = "#" + $namespace + "_" + $field;
			$($selectorName).val($value);
//			$($selectorName).selectmenu('refresh');
		}
		
		
	}	
	
	;ADDRESSPANEL = {
		init: function($namespace, $countryList, $selectedCountry) {
			$.each($('input'), function () {
		        $(this).css("height","20px");
		        $(this).css("max-height", "20px");
		    });
			//$("#"+$namespace+"_address select[name='"+$namespace+"_state']").selectmenu({ width : '150px', maxHeight: '400 !important', style: 'dropdown'});
			$("select[name='"+$namespace+"_city']").addClass("ui-corner-all");
			$("select[name='"+$namespace+"_zip']").addClass("ui-corner-all");
			//$("#"+$namespace+"_address select[name='"+$namespace+"_country']").selectmenu({ width : '80px', maxHeight: '400 !important', style: 'dropdown'});
	
			 $( "input[name='"+$namespace+"_name']" ).autocomplete({
			     'source':"addressTypeAhead?term=",
			      select: function( event, ui ) {
			    	  ADDRESSPANEL.clearAddress($namespace);
			    	  var data = ADDRESSPANEL.getAddress(ui.item.id);
			    	  ADDRESSPANEL.setAddress($namespace,data[0]);
			      },
			      response: function(event, ui) {
			          if (ui.content.length === 0) {
			        	  alert("No Matching Address")
			          }
			      }
			 });
				 
			 $( "input[name='"+$namespace+"_jobContactName']" ).autocomplete({
				 'source':"contactTypeAhead?term=",
				 select: function( event, ui ) {
					 var data = ADDRESSPANEL.getContact(ui.item.id);
					 var id = ADDRESSPANEL.setContact($namespace+"_job",data);
					 $("input[name='"+$namespace+"_Con1id']").val(id);
				 },
				 response: function(event, ui) {
					 if (ui.content.length === 0) {
						 alert("No Matching Address")
					 }
		              }
			 	 });
				 $( "input[name='"+$namespace+"_siteContactName']" ).autocomplete({
				     'source':"contactTypeAhead?term=",
				      select: function( event, ui ) {
				        var data = ADDRESSPANEL.getContact(ui.item.id);
				        var id = ADDRESSPANEL.setContact($namespace+"_site",data);
				        $("input[name='"+$namespace+"_Con2id']").val(id);
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matching Address")
		            	  }
		              }
				    });
				 $( "input[name='"+$namespace+"_contractContactName']" ).autocomplete({
				     'source':"contactTypeAhead?term=",
				      select: function( event, ui ) {
				        var data = ADDRESSPANEL.getContact(ui.item.id);
				        var id = ADDRESSPANEL.setContact($namespace+"_contract",data);
				        $("input[name='"+$namespace+"_Con1id']").val(id);
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matching Address")
		            	  }
		              }
				    });
				 $( "input[name='"+$namespace+"_billingContactName']" ).autocomplete({
				     'source':"contactTypeAhead?term=",
				      select: function( event, ui ) {
				        var data = ADDRESSPANEL.getContact(ui.item.id);
				        var id = ADDRESSPANEL.setContact($namespace+"_billing",data);
				        $("input[name='"+$namespace+"_Con2id']").val(id);
				      },
		              response: function(event, ui) {
		            	  if (ui.content.length === 0) {
		            		  alert("No Matching Address")
		            	  }
		              }
				    });
				
				this.setCountryList($namespace, $countryList);
				this.setStateList($namespace, $countryList);
			}, 
			
			
			setCountryList: function($namespace, $countryList) {

				var $select = "#"+$namespace+"_address select[name='<%=namespace%>_country']";
				$select = $("select[name='"+$namespace+"_country']");
				$('option', $select).remove();
				$.each($countryList, function($index, $country) {
					$select.append(new Option($country.abbrev));
				});
				
				//$select.selectmenu();
			}, 
			
			
			setStateList: function($namespace, $countryList) {

				var $select = $("select[name='"+$namespace+"_state']");
				$('option', $select).remove();

				$select.append(new Option("",""));
				$.each($countryList, function($index, $val) {
					var group = $('<optgroup label="' + $val.abbrev + '" />');
						$.each($val.stateList, function(){
							//$('<option />').html(this.display).appendTo(group);
							$(group).append("<option value='"+this.abbreviation+"'>"+this.display+"</option>");
						});
						group.appendTo($select);
					});
				
				//$select.selectmenu();
			},
			
			
			getAddress:function($addressId) {						
				var $returnValue = null;
				var jqxhr4 = $.ajax({
					type: 'GET',
					url: 'address/'+$addressId,
					data: {},
					success: function($data) {
						$returnValue = $data.data.codeList;
					},
					statusCode: {
						403: function($data) {
							$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
						},
						404: function($data) {
							$returnValue = {};
						},
						500: function($data) {
							
						}
					},
					dataType: 'json',
					async:false
				});
				return $returnValue;

			},
			
			
			setError: function($namespace, $id){
				$("#"+$namespace+$id).addClass('error');
				setTimeout(function() {
					$("#"+$namespace+$id).removeClass('error');
			    }, 8000);
			},
			
			
			setAddress: function($namespace, $addressData) {
				console.log("Address Data;");
				console.log($addressData);
				if($addressData.name != null) {
					$("input[name='"+$namespace+"_name']").val($addressData.name);
				} 
				if($addressData.address1 != null) {
					$("input[name='"+$namespace+"_address1']").val($addressData.address1);
				} 
				if($addressData.address2 != null) {
					$("input[name='"+$namespace+"_address2']").val($addressData.address2);
				} 
				if($addressData.city != null) {
					$("input[name='"+$namespace+"_city']").val($addressData.city);
				} 
				if($addressData.state != null) {
					$("select[name='"+$namespace+"_state']").val($addressData.state);
					//$("select[name='"+$namespace+"_state']").selectmenu("refresh");
				} 
				if($addressData.zip != null) {
					$("input[name='"+$namespace+"_zip']").val($addressData.zip);
				} 
				if($addressData.county != null) {
					$("input[name='"+$namespace+"_county']").val($addressData.county);
				}
				if($addressData.country != null) {
					$("select[name='"+$namespace+"_country']").val($addressData.country);
					//$("select[name='"+$namespace+"_country']").selectmenu("refresh");
				}
				if($addressData.jobContactName != null) {
					$("input[name='"+$namespace+"_jobContactName']").val($addressData.jobContactName);
				}
				if($addressData.jobContactInfo != null) {
					$("input[name='"+$namespace+"_jobContactInfo']").val($addressData.jobContactInfo);
				}
				if($addressData.siteContactName != null) {
					$("input[name='"+$namespace+"_siteContactName']").val($addressData.siteContactName);
				}
				if($addressData.siteContactInfo != null) {
					$("input[name='"+$namespace+"_siteContactInfo']").val($addressData.siteContactInfo);
				}
				if($addressData.addressId != null) {
//					console.log("Address Id:" + $addressData.addressId);
					$("input[name='"+$namespace+"_id']").val($addressData.addressId);
//					console.log("Id Input:" + $("input[name='"+$namespace+"_id']").val());
				}
				//.selectmenu("refresh");
				
			},
			
			
			setContact: function($namespace, $contactData) {
//				console.log("Contact Data;");
//				console.log($contactData);
				if($contactData.preferredContact == "business_phone") {
					$("input[name='"+$namespace+"ContactInfo']").val($contactData.businessPhone);
				} 
				if($contactData.preferredContact == "email") {
					$("input[name='"+$namespace+"ContactInfo']").val($contactData.email);
				} 
				if($contactData.preferredContact == "mobile_phone") {
					$("input[name='"+$namespace+"ContactInfo']").val($contactData.mobilePhone);
				} 
				
				
					$("input[name='"+$namespace+"ContactName']").val($contactData.lastName + ", "+$contactData.firstName);
				
				
				
				return $contactData.contactId;
				
				//.selectmenu("refresh");
				
			}, 
			
			
			clearAddress: function($namespace){

				$("input[name='"+$namespace+"_name']").val("");
				$("input[name='"+$namespace+"_address1']").val("");
				$("input[name='"+$namespace+"_address2']").val("");
				$("input[name='"+$namespace+"_city']").val("");
				$("select[name='"+$namespace+"_state']").selectmenu("refresh");
				$("input[name='"+$namespace+"_zip']").val("");
				$("input[name='"+$namespace+"_county']").val("");
				$("select[name='"+$namespace+"_country']").selectmenu("refresh");
				$("input[name='"+$namespace+"_jobContactName']").val("");
				$("input[name='"+$namespace+"_jobContactInfo']").val("");
				$("input[name='"+$namespace+"_siteContactName']").val("");
				$("input[name='"+$namespace+"_siteContactInfo']").val("");
			},
			
			
			getContact: function($contactId){
				var $returnValue = [];
				if ( $contactId != null ) {
					var $url = "contact/"+$contactId;
					var jqxhr = $.ajax({
						type: 'GET',
						url: $url,
						data: {},
						statusCode: {
							200: function($data) {
//								console.log("Contact Data: ");
//								console.log($data);
								$returnValue = $data.data.contactList[0];
								
							},					
							403: function($data) {
								$("#useridMsg").html($data.responseJSON.responseHeader.responseMessage);
							},
							404: function($data) {
								$returnValue = {};
							},
							500: function($data) {
								
							}
						},
						dataType: 'json',
						async:false
					});
				//console.log($returnValue);
				}
				return $returnValue;
			}
		}

});