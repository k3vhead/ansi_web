;QUOTE_PRINT_HISTORY = {
		init : function($modalName, $triggerName) {
			QUOTE_PRINT_HISTORY.modalName = $modalName;
			
			$( $modalName ).dialog({
				title:'Quote Print History',
				autoOpen: false,
				height: 400,
				width: 700,
				modal: true,
				closeOnEscape:true,
				//open: function(event, ui) {
				//	$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
				//},
				buttons: [
					{
						id: "closePrintHistory",
							click: function($event) {
							$($modalName).dialog("close");
						}
					}
				]
			});
			$('#closePrintHistory').button('option', 'label', 'Close');
			$($triggerName).click(function($event) {
				QUOTE_PRINT_HISTORY.getHistory();
				$($modalName).dialog("open");
			});
		},
		
		
		
		getHistory : function() {
			var $quoteId = QUOTEUTILS.getQuoteId();
			var $URL = "quotePrintHistory/" + $quoteId;
	        var jqxhr = $.ajax({
	        	type: 'GET',
	        	url: $URL,
	        	data: null,
	   			statusCode: {
	   				200: function($data) {   				
		   				QUOTE_PRINT_HISTORY.populateDataTable($data.data);
		   			},
		   			403: function($data) {
		   				$("#globalMsg").html($data.responseJSON.responseHeader.responseMessage).show();
		   			},
		   			404: function($data) {
		 				$("#globalMsg").html("System Error: Contact Support").show();
		   			},
		   			500: function($data) {
		   				$("#globalMsg").html("System Error: Contact Support").show();
		   			} 
		   		},
		   		dataType: 'json'
		   	});        	
		},
		
		
		populateDataTable : function($data) {
			var $tableSelector = QUOTE_PRINT_HISTORY.modalName + " .printHistoryTable";
			
			$($tableSelector).dataTable().fnDestroy();
			
	           
			var dataTable = null;
        	
			var dataTable = $($tableSelector).DataTable( {
				"bDestroy":			true,
	        	"processing": 		true,
	        	"autoWidth": 		false,
	        	"scrollCollapse": 	true,
	        	"scrollX": 			true,
	        	rowId: 				'dt_RowId',
	        	dom: 				'Bfrtip',
	        	"searching": 		true,
    	        //lengthMenu: [
    	        //    [ 10, 50, 100, 500, 1000, -1 ],
    	        //    [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows', 'Show All' ],
    	        //],
	        	buttons: [
	        		//	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();}},
	        	],
	        	"columnDefs": [
	        		{ "orderable": true, "targets": "_all" },
	        		{ className: "dt-left", "targets": [0,1,2] },
	        		//{ className: "dt-right", "targets": [3,4] },
	        		//{ className: "dt-center", "targets": [1,2,5] },
	        	],
	        	"paging": true,
	        	data: $data.history,
	        	columns: [
	        		{ title: "Print Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {	
	        			if(row.printDate != null){return (row.printDate+"");}
	        		} },
	        		{ title: "Quote Date", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
	        			if(row.quoteDate != null){return (row.quoteDate+"");}
	        		} },
	        		{ title: "Printed By", "defaultContent": "<i>N/A</i>", data: function ( row, type, set ) {
	        			var $value = row.firstName + " " + row.lastName;
	        			if(row.firstName != null || row.lastName != null){return ($value+"");}
	        		} }
	        	],
	        	"initComplete": function(settings, json) {
	        		//doFunctionBinding();
	        	},
	        	"drawCallback": function( settings ) {
	        		//doFunctionBinding();
	        	}
			} );
        	
			init();
        			
        	
			function init(){
	            //	$.each($('input'), function () {
				//	        $(this).css("height","20px");
				//	        $(this).css("max-height", "20px");
				//	    });
						
	            	//populateDataTable();
			};
	            
			function initComplete (){
				var $footSelector = QUOTE_PRINT_HISTORY.modalName + " .printHistoryTable tfoot tr";
				var $headSelector = QUOTE_PRINT_HISTORY.modalName + " .printHistoryTable thead";
				var r = $($footSelector);
				r.find('th').each(function(){
					$(this).css('padding', 8);
				});
				$($headSelector).append(r);
			}

		},
			
			


}