/*!
 * JQuery Spliter Plugin
 * Copyright (C) 2010-2013 Jakub Jankiewicz <http://jcubic.pl> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function($, undefined) {
    var count = 0;
    var splitter_id = null;
    var splitters = [];
    var current_splitter = null;
    $.fn.split = function(options) {
        var data = this.data('splitter');
        if (data) {
            return data;
        }
        var panel_1;
        var panel_2;
        var settings = $.extend({
            limit: 100,
            orientation: 'horizontal',
            position: '50%',
            onDragStart: $.noop,
            onDragEnd: $.noop,
            onDrag: $.noop
        }, options || {});
        this.settings = settings;
        var cls;
        var children = this.children();
        if (settings.orientation == 'vertical') {
            panel_1 = children.first().addClass('left_panel');
            panel_2 = panel_1.next().addClass('right_panel');
            cls = 'vsplitter';
        } else if (settings.orientation == 'horizontal') {
            panel_1 = children.first().addClass('top_panel')
            panel_2 = panel_1.next().addClass('bottom_panel');
            cls = 'hsplitter';
        }
        var width = this.width();
        var height = this.height();
        var id = count++;
        this.addClass('splitter_panel');
        var splitter = $('<div/>').addClass(cls).mouseenter(function() {
            splitter_id = id;
        }).mouseleave(function() {
            splitter_id = null;
        }).insertAfter(panel_1);
        var position;

        function get_position(position) {
            if (typeof position === 'number') {
                return position;
            } else if (typeof position === 'string') {
                var match = position.match(/^([0-9]+)(px|%)$/);
                if (match) {
                    if (match[2] == 'px') {
                        return +match[1];
                    } else {
                        if (settings.orientation == 'vertical') {
                            return (width * +match[1]) / 100;
                        } else if (settings.orientation == 'horizontal') {
                            return (height * +match[1]) / 100;
                        }
                    }
                } else {
                    //throw position + ' is invalid value';
                }
            } else {
                //throw 'position have invalid type';
            }
        }

        var self = $.extend(this, {
            refresh: function() {
                var new_width = this.width();
                var new_height = this.height();
                if (width != new_width || height != new_height) {
                    width = this.width();
                    height = this.height();
                    self.position(position);
                }
            },
            position: (function() {
                if (settings.orientation == 'vertical') {
                    return function(n, silent) {
                        if (n === undefined) {
                            return position;
                        } else {
                            position = get_position(n);
                            var sw = splitter.width()/2;
                            splitter.css('left', position-sw);
                            panel_1.width(position-sw);
                            panel_2.width(self.width()-position-sw);
                        }
                        if (!silent) {
                            self.find('.splitter_panel').trigger('splitter.resize');
                        }
                        return self;
                    };
                } else if (settings.orientation == 'horizontal') {
                    return function(n, silent) {
                        if (n === undefined) {
                            return position;
                        } else {
                            position = get_position(n);
                            var sw = splitter.height()/2;
                            splitter.css('top', position-sw);
                            panel_1.height(position-sw);
                            panel_2.height(self.height()-position-sw);
                        }
                        if (!silent) {
                            self.find('.splitter_panel').trigger('splitter.resize');
                        }
                        return self;
                    };
                } else {
                    return $.noop;
                }
            })(),
            orientation: settings.orientation,
            limit: settings.limit,
            isActive: function() {
                return splitter_id === id;
            },
            destroy: function() {
                self.removeClass('splitter_panel');
                splitter.unbind('mouseenter');
                splitter.unbind('mouseleave');
                if (settings.orientation == 'vertical') {
                    panel_1.removeClass('left_panel');
                    panel_2.removeClass('right_panel');
                } else if (settings.orientation == 'horizontal') {
                    panel_1.removeClass('top_panel');
                    panel_2.removeClass('bottom_panel');
                }
                self.unbind('splitter.resize');
                self.find('.splitter_panel').trigger('splitter.resize');
                splitters[id] = null;
                splitter.remove();
                var not_null = false;
                for (var i=splitters.length; i--;) {
                    if (splitters[i] !== null) {
                        not_null = true;
                        break;
                    }
                }
                //remove document events when no splitters
                if (!not_null) {
                    $(document.documentElement).unbind('.splitter');
                    $(window).unbind('resize.splitter');
                    self.data('splitter', null);
                    splitters = [];
                    count = 0;
                }
            }
        });
        self.bind('splitter.resize', function(e) {
            var pos = self.position();
            if (self.orientation == 'vertical' && 
                pos > self.width()) {
                pos = self.width() - self.limit-1;
            } else if (self.orientation == 'horizontal' && 
                       pos > self.height()) {
                pos = self.height() - self.limit-1;
            }
            if (pos < self.limit) {
                pos = self.limit + 1;
            }
            self.position(pos, true);
        });
        //inital position of splitter
        var pos;
        if (settings.orientation == 'vertical') {
            if (pos > width-settings.limit) {
                pos = width-settings.limit;
            } else {
                pos = get_position(settings.position);
            }
        } else if (settings.orientation == 'horizontal') {
            //position = height/2;
            if (pos > height-settings.limit) {
                pos = height-settings.limit;
            } else {
                pos = get_position(settings.position);
            }
        }
        if (pos < settings.limit) {
            pos = settings.limit;
        }
        self.position(pos, true);
        if (splitters.length == 0) { // first time bind events to document
            $(window).bind('resize.splitter', function() {
                $.each(splitters, function(i, splitter) {
                    splitter.refresh();
                });
            });
            $(document.documentElement).bind('mousedown.splitter', function(e) {
                if (splitter_id !== null) {
                    current_splitter = splitters[splitter_id];
                    $('<div class="splitterMask"></div>').insertAfter(current_splitter);
                    if (current_splitter.orientation == 'horizontal') {
                        $('body').css('cursor', 'row-resize');
                    } else if (current_splitter.orientation == 'vertical') {
                        $('body').css('cursor', 'col-resize');
                    }
                    current_splitter.settings.onDragStart(e);
                    return false;
                }
            }).bind('mouseup.splitter', function(e) {
                if (current_splitter) {
                    $('.splitterMask').remove();
                    $('body').css('cursor', 'auto');
                    current_splitter.settings.onDragEnd(e);
                    current_splitter = null;
                }
            }).bind('mousemove.splitter', function(e) {
                if (current_splitter !== null) {
                    var limit = current_splitter.limit;
                    var offset = current_splitter.offset();
                    if (current_splitter.orientation == 'vertical') {
                        var x = e.pageX - offset.left;
                        if (x <= current_splitter.limit) {
                            x = current_splitter.limit + 1;
                        } else if (x >= current_splitter.width() - limit) {
                            x = current_splitter.width() - limit - 1;
                        }
                        if (x > current_splitter.limit &&
                            x < current_splitter.width()-limit) {
                            current_splitter.position(x, true);
                            current_splitter.find('.splitter_panel').trigger('splitter.resize');
                            e.preventDefault();
                        }
                    } else if (current_splitter.orientation == 'horizontal') {
                        var y = e.pageY-offset.top;
                        if (y <= current_splitter.limit) {
                            y = current_splitter.limit + 1;
                        } else if (y >= current_splitter.height() - limit) {
                            y = current_splitter.height() - limit - 1;
                        }
                        if (y > current_splitter.limit &&
                            y < current_splitter.height()-limit) {
                            current_splitter.position(y, true);
                            current_splitter.trigger('splitter.resize');
                            e.preventDefault();
                        }
                    }
                    current_splitter.settings.onDrag(e);
                }
            });
        }
        splitters.push(self);
        self.data('splitter', self);
        return self;
    };
})(jQuery);
