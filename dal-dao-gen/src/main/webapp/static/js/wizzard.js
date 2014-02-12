
(function( window, undefined ) {

	wizzard.prototype={

		next: function(current){
			//首先获取当前Grid选中的行
			var records = w2ui['grid'].getSelection();
        	var record = null;
        	if(records.length > 0)
            	record = w2ui['grid'].get(records[0]);
		},

	};

})( window );