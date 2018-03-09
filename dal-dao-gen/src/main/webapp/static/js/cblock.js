/*! block.js | Copyright (c) 20013-2014 Link Wu | 3 clause BSD license */

(function(ctx) {
	var cblock = function(el) {
		$(el).block({
	        message: '<img src="/static/images/ajax-loading.gif" align="">',
	        // centerY: centerY != undefined ? centerY : true,
	        css: {
	            top: '10%',
	            border: 'none',
	            padding: '2px',
	            backgroundColor: 'none'
	        },
	        overlayCSS: {
	            backgroundColor: '#000',
	            opacity: 0.05,
	            cursor: 'wait'
	        }
    	});
	};

	var cunblock = function(el){
		$(el).unblock();
	};

	/**
	 * export to either browser or node.js
	 */
	ctx.cblock = cblock;
	ctx.cunblock = cunblock;
})(window);
