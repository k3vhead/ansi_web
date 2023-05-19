;PAYROLL_UTILS = {
	timesheetFields : [
		{"label":"Regular", "name":"regular", hoursAndPay:true},
		{"label":"Expenses", "name":"expenses", hoursAndPay:false},
		{"label":"OT", "name":"ot", hoursAndPay:true},
		{"label":"Vacation", "name":"vacation", hoursAndPay:true},
		{"label":"Holiday", "name":"holiday", hoursAndPay:true},
		{"label":"Gross Pay", "name":"grossPay", hoursAndPay:false},
		{"label":"Expenses Submitted", "name":"expensesSubmitted", hoursAndPay:false},
		{"label":"Expenses Allowed", "name":"expensesAllowed", hoursAndPay:false},
		{"label":"Volume", "name":"volume", hoursAndPay:false},
		{"label":"Direct Labor", "name":"directLabor", hoursAndPay:false},
		//{"label":"Productivity", "name":"productivity", hoursAndPay:false},
	],
        		
        		
	initEditModal : function($modalName, $saveMethod) {
		console.log("initEditModal");
		
		var $tableSelector = $modalName + " .edit-form";
		$.each(PAYROLL_UTILS.timesheetFields, function($index, $value) {
			var $row = $("<tr>").addClass("employee-edit-row");
			if ( $value.hoursAndPay ) {
				$row.append( PAYROLL_UTILS.makeLabel($value.label + " Hours") );
				$row.append( PAYROLL_UTILS.makeInput($value.name + "Hours", false) );
				$row.append( PAYROLL_UTILS.makeErr($value.name + "Hours") );
				$row.append( PAYROLL_UTILS.makeLabel("Pay") );
				$row.append( PAYROLL_UTILS.makeInput($value.name + "Pay", true) );
				$row.append( PAYROLL_UTILS.makeErr($value.name + "Pay") );
			} else {
				$row.append( PAYROLL_UTILS.makeLabel($value.label) );
				$row.append( PAYROLL_UTILS.makeInput($value.name) );
				$row.append( PAYROLL_UTILS.makeErr($value.name) );
				$row.append( $("<td>").attr("colspan","3") );
			}			
			$($tableSelector).append($row);
		});

		var $totalPayRow = 	$("<tr>")
		$totalPayRow.append( $("<td>") );
		$totalPayRow.append( $("<td>") );
		$totalPayRow.append( $("<td>") );
		$totalPayRow.append( $("<td>").append($("<span>").addClass("form-label").append("Total Pay: ")   ));	
		$totalPayRow.append( $("<td>").attr("style","text-align:right;").append($("<span>").attr("class", "totalpay-display")));
		$($tableSelector).append($totalPayRow);
		
		$( $modalName ).dialog({
			title:'Timesheet Edit',
			autoOpen: false,
			height: 650,
			width: 675,
			modal: true,
			closeOnEscape:true,
			//open: function(event, ui) {
			//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
			//},
			buttons: [
				{
					id:  "edit-cancel",
					click: function($event) {
						$( $modalName ).dialog("close");
					}
				},{
					id:  "edit-save",
					click: function($event) {
						$saveMethod();
					}
				}
			]
		});	
		$("#edit-cancel").button('option', 'label', 'Cancel');  
		$("#edit-save").button('option', 'label', 'Save');
		
		$(".employee-edit-row").mouseover(function() { $(this).addClass("grayback"); });
		$(".employee-edit-row").mouseout(function() { $(this).removeClass("grayback"); });
		
		
		
		var $nameSelector = $modalName + " input[name='employeeName']";
		var $codeSelector = $modalName + " input[name='employeeCode']";
		var $divisionSelector = $modalName + " select[name='divisionId']";
		var $vendorCodeSelector = $modalName + " input[name='vendorEmployeeCode']";
		$( $nameSelector ).autocomplete({
			'source':function(request, response) {
				$.ajax({
					url:"payroll/employeeAutoComplete?",
					dataType:"json",
					data : {term:request.term,divisionId:$($divisionSelector).val()},
					success:function($data) {
						response($data);
					}		
				});
				
			},
			position:{my:"left top", at:"left bottom",collision:"none"},
			appendTo:$modalName,
			select: function( event, ui ) {
				$($nameSelector).val(ui.item.employeeName); //ui.item.label);
				$($codeSelector).val(ui.item.id); //ui.item.id);
				$($vendorCodeSelector).val(ui.item.vendorEmployeeCode); //ui.item.value);
				if ( ui.item.value == null || ui.item.value.trim() == "" ) {
					$($nameSelector).val("");
					$($codeSelector).val("");
					$($vendorCodeSelector).val("");
				}
	      	}
	 	});
	 	
	 	$( $nameSelector ).blur( function() {
			var $name = $($nameSelector).val();
			if ( $name == null || $name == "" ) {
				$($codeSelector).val("");
				$($vendorCodeSelector).val("");
			}
		});
		        			
		$( $codeSelector ).autocomplete({
			'source':"payroll/employeeCodeComplete?",
			position:{my:"left top", at:"left bottom",collision:"none"},
			appendTo:$modalName,
			select: function( event, ui ) {
				console.log(ui);
				console.log(event);
				$($nameSelector).val(ui.item.employeeName);
				//$($codeSelector).val(ui.item.label);
				if ( ui.item.value == null || ui.item.value.trim() == "" ) {
					$($nameSelector).val("")
					$($codeSelector).val("")
				}
	      	}
	 	});
		
		
		var $cityField = $modalName + " input[name='city']";
		var $stateField = $modalName + " select[name='state']";
		
		var $localeComplete = $( $cityField ).autocomplete({
			source: function(request,response) {
				term = $($cityField).val();
				localeTypeId = null; 
				stateName = null; 
				if ( $( $stateField ).val() != null ) {
					stateName = $( $stateField ).val();	
				}
				$.getJSON("localeAutocomplete", {"term":term, "localeTypeId":localeTypeId, "stateId":stateName}, response);
			},
            minLength: 2,
            //select: function( event, ui ) {
            	//$("#addLocaleForm input[name='parentId']").val(ui.item.id);
            //	console.log("Got it: " + ui.item.id)
            //},
            response: function(event, ui) {
                if (ui.content.length === 0) {
                	$($modalName + " .cityErr").html("No Matching Locale");
                	//$("#addLocaleForm input[name='parentId']").val("");
                } else {
                	$($modalName + " .cityErr").html("");
                }
            }
      	}).data('ui-autocomplete');	            	
        
		//$localeComplete._renderMenu = function( ul, items ) {
		//	var that = this;
		//	$.each( items, function( index, item ) {
		//		that._renderItemData( ul, item );
		//	});
		//	if ( items.length == 1 ) {
		//		$("#addLocaleForm input[name='parentId']").val(items[0].id);
		//		$($cityField).autocomplete("close");
		//	}
		//};
		
		$paySelector = $tableSelector + " input[data-payfield='1']";
		$($paySelector).blur( function($event) {
			PAYROLL_UTILS.calculateTotalPay($modalName);
		});
	},
	
	calculateTotalPay : function($modalName) {
		console.log("calculateTotalPay");
		var $totalPay = 0.0;
		$.each(PAYROLL_UTILS.timesheetFields, function($index, $value) {
			if ( $value.hoursAndPay == true ) {
				var $selector = $modalName + " input[name='"+$value.name+"Pay']";
				$payValue = $($selector).val();
				if ( $payValue != null && $payValue != "" ) {
					$totalPay = $totalPay + parseFloat($payValue);
				}
			}
		});
		$($modalName + " .totalpay-display").html( $totalPay.toFixed(2));
	},
	
	
	makeLabel : function($name) {
		return $("<td>").append( $("<span>").addClass("form-label").append($name + ": ")   );
	},
	makeInput : function($name, $payField) {
		if ( $payField == true ) {
			$dataPay = "1";
		} else {
			$dataPay = "0";
		}
		return $("<td>").append($("<input>").attr("type","number").attr("name", $name).attr("step",".01").attr("data-payfield",$dataPay).attr("class","payfield").attr("style","width:65px;").attr("placeholder","0.00"));
	},
	makeErr : function($name) {
		return $("<td>").append( $("<span>").addClass("err").addClass($name+"Err")  );
	},
	
};