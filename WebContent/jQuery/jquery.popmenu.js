///////////////////////////////////////////////////////////////////////////////////////////
// PopMenu 1.0
// Version 1.0
// @requires jQuery v1.3.2
// 
// Copyright (c) 2009 Mark Ashley Bell
// Examples and docs at: http://markashleybell.com/jquery/jquery.popmenu.html
// 
// Dual licensed under the MIT and GPL licenses:
// http://www.opensource.org/licenses/mit-license.php
// http://www.gnu.org/licenses/gpl.html
///////////////////////////////////////////////////////////////////////////////////////////

(function($)
{
    $.fn.popMenu = function(settings)
    {
        var config = { 'delay': 300 };

        if (settings) $.extend(config, settings);
        
        this.each(function()
        {
            $(this).find('a').wrapInner('<span></span>').end()
            .find('ul').prev().addClass('stub').parent().bind('mouseover', function() {
                var o = $(this);
                if (o.attr('action')) clearTimeout(o.attr('action'));
                o.attr('action', setTimeout(function() {  $(o).find('ul:first').css('visibility', 'visible'); }, config['delay']));
            }).bind('mouseout', function() {
                var o = $(this);
                if (o.attr('action')) clearTimeout(o.attr('action'));
                o.attr('action', setTimeout(function() { $(o).find('ul:first').css('visibility', 'hidden'); }, config['delay']));
            });
        });

        return this;
    };

})(jQuery);