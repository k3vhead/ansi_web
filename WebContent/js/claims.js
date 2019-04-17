$(document).ready(function() {
	;CLAIMSUTILS={
			directLaborTable : null,
			passtrhuExpenseTable : null,

			makeDirectLaborLookup : function($destination, $ticketId) {
				var $url = "claims/directLaborLookup/" + $ticketId;
				CLAIMSUTILS.directLaborTable = $($destination).DataTable( {    				
        			"aaSorting":		[[0,'asc']],
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        "searchDelay":		800,
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
        	        ],
        	        "columnDefs": [
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-left", "targets": [1,5] },
        	            { className: "dt-center", "targets": [0] },
        	            { className: "dt-right", "targets": [2,3,4]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": $url,
			        	"type": "GET"
			        	},
			        columns: [
			        	{ title: "Date", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.work_date != null){return (row.work_date+"");}
			            } },
			            { width:"23%", title: "Washer", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.washer_first_name != null){return (row.washer_last_name+", "+row.washer_first_name);}
			            } },
			            { title: "Volume", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.volume != null){return (row.volume.toFixed(2)+"");}
			            } },
			            { title: "DL $", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.dl_amt != null){return (row.dl_amt.toFixed(2)+"");}
			            } },
			            { title: "Hrs", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.hours != null){return (row.hours.toFixed(2)+"");}
			            } },
			            { title: "Notes", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.notes != null){return (row.notes);}
			            } }
			            ],
			            "initComplete": function(){},
			            "drawCallback": function(){},
			            "footerCallback": function(row, data, start, end, display) {
			            	var api = this.api(), data;
			            	$(api.column(2).footer()).html(CLAIMSUTILS.makeColumnTotal(api, 2));
			            	$(api.column(3).footer()).html(CLAIMSUTILS.makeColumnTotal(api, 3));
			            	$(api.column(4).footer()).html(CLAIMSUTILS.makeColumnTotal(api, 4));
			            }
			    } );
        		//new $.fn.dataTable.FixedColumns( dataTable );
        	},
        	
        	
        	
        	
        	
        	makePassthruExpenseLookup : function($destination, $ticketId) {
				var $url = "claims/passthruExpenseLookup/" + $ticketId;
				CLAIMSUTILS.directLaborTable = $($destination).DataTable( {    				
        			"aaSorting":		[[0,'asc']],
        			"processing": 		true,
        	        "serverSide": 		true,
        	        "autoWidth": 		false,
        	        "deferRender": 		true,
        	        "scrollCollapse": 	true,
        	        "scrollX": 			true,
        	        rowId: 				'dt_RowId',
        	        dom: 				'Bfrtip',
        	        "searching": 		true,
        	        "searchDelay":		800,
        	        lengthMenu: [
        	        	[ 10, 50, 100, 500, 1000 ],
        	            [ '10 rows', '50 rows', '100 rows', '500 rows', '1000 rows' ]
        	        ],
        	        buttons: [
        	        	'pageLength','copy', 'csv', 'excel', {extend: 'pdfHtml5', orientation: 'landscape'}, 'print',{extend: 'colvis',	label: function () {doFunctionBinding();$('#displayTable').draw();}}
        	        ],
        	        "columnDefs": [
         	            { "orderable": false, "targets": -1 },
        	            { className: "dt-left", "targets": [3,4] },
        	            { className: "dt-center", "targets": [0,1] },
        	            { className: "dt-right", "targets": [2]}
        	         ],
        	        "paging": true,
			        "ajax": {
			        	"url": $url,
			        	"type": "GET"
			        	},
			        columns: [
			        	{ title: "Date", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.work_date != null){return (row.work_date+"");}
			            } },
			            { title: "Type", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.passthru_expense_type != null){return (row.passthru_expense_type+"");}
			            } },
			            { title: "Volume $", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.passthru_expense_volume != null){return (row.passthru_expense_volume.toFixed(2)+"");}
			            } },			            
			            { title: "Washer", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {	
			            	if(row.washer_first_name != null){return (row.washer_last_name+", "+row.washer_first_name);}
			            } },
			            { title: "Notes", "defaultContent": "<i>N/A</i>", searchable:true, data: function ( row, type, set ) {
			            	if(row.notes != null){return (row.notes);}
			            } }
			            ],
			            "initComplete": function(){},
			            "drawCallback": function(){},
			            "footerCallback": function(row, data, start, end, display) {
			            	var api = this.api(), data;
			            	$(api.column(2).footer()).html(CLAIMSUTILS.makeColumnTotal(api, 2));
			            }
			    } );
        		//new $.fn.dataTable.FixedColumns( dataTable );
        	},
            	
            	
        		
        	makeColumnTotal : function(api, idx) {
        		total = api.column(idx).data().reduce( function(a,b) {
        			return parseFloat(a) + parseFloat(b);
        		},0);
        		return total.toFixed(2);
        	},
            
            
            
				
	};
});
				