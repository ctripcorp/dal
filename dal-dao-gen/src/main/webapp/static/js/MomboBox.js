/*
 * MomboBox
 * https://github.com/luv2code/MomboBox
 *
 * Copyright (c) 2012 Matthew Taylor
 * Licensed under the MIT license.
 */

(function($, window) {
    var defaults = {
        data: [],
        templates: {
            buttonTemplate: '<button class="mombobutton">↓</button>',
            itemTemplate: '<a class="item">{item}</a>',
            menuTemplate: '<div class="mombomenu"></div>',
            render: function(tmpl, value, token) {
                return tmpl.replace('{'+token+'}', value);
            }
        },
        cssClasses: {
            matchingItem: 'match',
            selectedItem: 'selected',
            item: 'item',
            menu: 'mombomenu'
        },
        flags: {
            customItems:true,//if false, behaves like a style-able select
            addCustomItems:true,//Allows entries not in the list; but doesn't add them.
            hideUnmatchedItems:true,
            prependCustom:true
        }
    },
        define = window.define;
    // Collection method.
    $.fn.momboBox = function(options) {
        return this.each(function() {
            if(this.nodeName !== 'INPUT') {
                throw new Error("the momboBox plugin only works on input elements");
            }
            var momboBox = this.momboBox = $.extend(true, {}, defaults, options),
                menuContent = '',
                $items,
                $input = $(this),
                origValue = $input.val(),
                origValGet = $input.val,
                $button = $(momboBox.templates.buttonTemplate).insertAfter($input),
                $menu,
                renderMenu = function () {
                    menuContent = '';
                    $.each(momboBox.data, function (index, item) {
                        menuContent+= momboBox.templates.render(momboBox.templates.itemTemplate, item, 'item');
                    });
                    $menu.empty();
                    $items = $(menuContent).appendTo($menu);
                },
                setMatching = function () {
                    var $match, rgx = new RegExp('('+$input.val()+')', 'i');
                    if(!!$input.val()) {
                        $items.each(function (i, item) {
                            var $item = $(item), text = $item.text();
                            if(rgx.test(text)) {
                                $item.addClass(momboBox.cssClasses.matchingItem);
                                if(text.toLowerCase() === $input.val().toLowerCase()) {
                                    $match = $item;
                                }
                            } else {
                                $item.removeClass(momboBox.cssClasses.matchingItem);
                            }
                        });
                        $match = $match || $items.siblings('.' + momboBox.cssClasses.matchingItem).first();
                        if($match.length > 0) {
                            $menu.scrollTop($match.position().top);
                            $match.addClass(momboBox.cssClasses.selectedItem);
                        }
                    } else {
                        $items.removeClass(momboBox.cssClasses.matchingItem);
                    }
                    return $match;
                },
                addCustomItem = function (text) {
                    var insertMethod = momboBox.flags.prependCustom ? 'unshift' : 'push';
                    momboBox.data[insertMethod](text);
                    momboBox.update();
                };

            momboBox.update = function () {
                renderMenu();
                setMatching();
            };

            momboBox.getPosition = function () {
                var offset = $input.offset(),
                    top = $input.outerHeight() + offset.top,
                    left = offset.left;
                return { top: top, left: left };
            };

            $input.val = function (value, soft) {
                if(typeof value === 'string') {
                    origValue = !soft ? value : origValue;
                    return origValGet.call($input, value);
                } else {
                    return origValGet.call($input);
                }
            };

            if(!momboBox.flags.customItems && !!momboBox.data && momboBox.data.length > 0) {
               $input.val(momboBox.data[0]);
            }

            //set up the elements
            $menu = $(momboBox.templates.menuTemplate)
                .insertAfter($button)
                .offset(momboBox.getPosition())
                .on('click', '.' + momboBox.cssClasses.item, function (ev) {
                    $input.val($(ev.target).text());
                    $menu.fadeOut('fast');
                    $items.removeClass(momboBox.cssClasses.matchingItem);
                    $input.trigger('changed', $input.val());
                })
                .on('mouseover', '.' + momboBox.cssClasses.item, function (ev) {
                    $items.removeClass(momboBox.cssClasses.selectedItem);
                    $(ev.target).addClass(momboBox.cssClasses.selectedItem);
                })
                .hide();

            renderMenu();

            //event bindings
            $(document).on('click',function () {
                if(!(
                    $input.is(':momboFocus') ||
                    $button.is(':momboFocus')
                )) {
                    $menu.fadeOut('fast');
                }
            });
            $button.on('click', function () {
                $input.focus();
            });

            $input
                .on('added-custom', function (ev, item) {
                    if(!ev.isPropagationStopped() && momboBox.flags.addCustomItems){
                        //prevent this be adding a handler to the input before momboBox is attached, and calling ev.stopPropagation()
                        addCustomItem(item);
                    }
                })
                .on('click', function (){
                    $input.focus();
                })
                .on('focus', function () {
                    $menu.show();
                    $menu.offset(momboBox.getPosition());
                    window.setTimeout(function () {
                        $input.select();
                    }, 0);
                })
                .on('blur', function () {
                    $menu.fadeOut('fast');
                })
                .on('keydown', function (ev) {
                    var $selected = $items.siblings('.' + momboBox.cssClasses.selectedItem),
                        index = 0,
                        last = $items.length - 1,
                        value,
                        top,
                        $match;
                    if(ev.which !== 27) {
                        $menu.show();
                    }
                    $selected.toggleClass(momboBox.cssClasses.selectedItem + ' ' + momboBox.cssClasses.matchingItem);
                    switch(ev.which) {
                        case 38 : //up arrow key
                            if($selected.length > 0) {
                                $items.each(function (i, item) {
                                    if(item === $selected.get(0)) {
                                        index = i === 0 ? last : index;
                                        value = $($items[index]).addClass(
                                            momboBox.cssClasses.selectedItem + ' ' +
                                            momboBox.cssClasses.matchingItem
                                        ).text();
                                        top = $($items[index]).position().top;
                                    }
                                    index = i;
                                });
                            } else {
                                value = $items.last().addClass(
                                    momboBox.cssClasses.selectedItem + ' ' +
                                    momboBox.cssClasses.matchingItem
                                ).text();
                                top = $items.last().position().top;
                            }
                            $input.val(value, true);
                            break;
                        case 40 : //down arrow key
                            if($selected.length > 0) {
                                $items.each(function (i, item) {
                                    index = i + 1;
                                    if(item === $selected.get(0)) {
                                        index = i === last ? 0 : index;
                                        value = $($items[index]).addClass(
                                            momboBox.cssClasses.selectedItem + ' ' +
                                            momboBox.cssClasses.matchingItem
                                        ).text();
                                        top = $($items[index]).position().top;
                                    }
                                });
                            } else {
                                value = $items.last().addClass(
                                    momboBox.cssClasses.selectedItem + ' ' +
                                    momboBox.cssClasses.matchingItem
                                ).text();
                                top = $items.first().position().top;
                            }
                            $input.val(value, true);
                            break;
                        case 27 : //escape
                            $menu.fadeOut('fast');
                            $input.val(origValue);
                            break;
                        case 9 : //tab
                            $menu.fadeOut('fast');
                            break;
                        case 13 :
                            $match = setMatching();
                            if($match.length > 0) {
                                $input.val($match.first().text());
                            } else {
                                if(!momboBox.flags.customItems) {
                                    $input.val(origValue);
                                } else {
                                    $input.trigger('added-custom', [$input.val()]);
                                }
                            }
                            $input.trigger('changed', $input.val());
                            $menu.fadeOut('fast');
                            break;

                    }
                })
                .on('keyup', function (ev) {
                    var $matching;
                    setMatching();
                    //TODO: fix this code for hiding missing umatched values
//                    if(momboBox.flags.hideUnmatchedItems) {
//                        $matching = $items.siblings('.' + momboBox.cssClasses.matchingItem);
//                        if($matching.length > 0) {
//                            $items.siblings(':not(.' + momboBox.cssClasses.matchingItem + ')').hide();
//                            $matching.show();
//                        } else {
//                            $items.show();
//                        }
//                    }
                });
        });
    };
    $.expr[':'].momboFocus = function(elem) {
        return elem === document.activeElement && (elem.type || elem.href);
    };

    if ( typeof define === "function" && define.amd && define.amd.jQuery ) {
        define(function () { } ); //just signal that we're loaded.
    }
}(jQuery, window));
