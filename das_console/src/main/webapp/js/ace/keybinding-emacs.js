/* ***** BEGIN LICENSE BLOCK *****
 * Distributed under the BSD license:
 *
 * Copyright (c) 2010, Ajax.org B.V.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Ajax.org B.V. nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL AJAX.ORG B.V. BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***** END LICENSE BLOCK ***** */

define('ace/keyboard/emacs', ['require', 'exports', 'module' , 'ace/lib/dom', 'ace/incremental_search', 'ace/commands/incremental_search_commands', 'ace/keyboard/hash_handler', 'ace/lib/keys'], function(require, exports, module) {


var dom = require("../lib/dom");
require("../incremental_search");
var iSearchCommandModule = require("../commands/incremental_search_commands");


var screenToTextBlockCoordinates = function(x, y) {
    var canvasPos = this.scroller.getBoundingClientRect();

    var col = Math.floor(
        (x + this.scrollLeft - canvasPos.left - this.$padding) / this.characterWidth
    );
    var row = Math.floor(
        (y + this.scrollTop - canvasPos.top) / this.lineHeight
    );

    return this.session.screenToDocumentPosition(row, col);
};

var HashHandler = require("./hash_handler").HashHandler;
exports.handler = new HashHandler();

exports.handler.isEmacs = true;
exports.handler.$id = "ace/keyboard/emacs";

var initialized = false;
var $formerLongWords;
var $formerLineStart;

exports.handler.attach = function(editor) {
    if (!initialized) {
        initialized = true;
        dom.importCssString('\
            .emacs-mode .ace_cursor{\
                border: 2px rgba(50,250,50,0.8) solid!important;\
                -moz-box-sizing: border-box!important;\
                -webkit-box-sizing: border-box!important;\
                box-sizing: border-box!important;\
                background-color: rgba(0,250,0,0.9);\
                opacity: 0.5;\
            }\
            .emacs-mode .ace_cursor.ace_hidden{\
                opacity: 1;\
                background-color: transparent;\
            }\
            .emacs-mode .ace_overwrite-cursors .ace_cursor {\
                opacity: 1;\
                background-color: transparent;\
                border-width: 0 0 2px 2px !important;\
            }\
            .emacs-mode .ace_text-layer {\
                z-index: 4\
            }\
            .emacs-mode .ace_cursor-layer {\
                z-index: 2\
            }', 'emacsMode'
        );
    }
    $formerLongWords = editor.session.$selectLongWords;
    editor.session.$selectLongWords = true;
    $formerLineStart = editor.session.$useEmacsStyleLineStart;
    editor.session.$useEmacsStyleLineStart = true;

    editor.session.$emacsMark = null; // the active mark
    editor.session.$emacsMarkRing = editor.session.$emacsMarkRing || [];

    editor.emacsMark = function() {
        return this.session.$emacsMark;
    }

    editor.setEmacsMark = function(p) {
        this.session.$emacsMark = p;
    }

    editor.pushEmacsMark = function(p, activate) {
        var prevMark = this.session.$emacsMark;
        if (prevMark)
            this.session.$emacsMarkRing.push(prevMark);
        if (!p || activate) this.setEmacsMark(p)
        else this.session.$emacsMarkRing.push(p);
    }

    editor.popEmacsMark = function() {
        var mark = this.emacsMark();
        if (mark) { this.setEmacsMark(null); return mark; }
        return this.session.$emacsMarkRing.pop();
    }

    editor.getLastEmacsMark = function(p) {
        return this.session.$emacsMark || this.session.$emacsMarkRing.slice(-1)[0];
    }

    editor.on("click", $resetMarkMode);
    editor.on("changeSession", $kbSessionChange);
    editor.renderer.screenToTextCoordinates = screenToTextBlockCoordinates;
    editor.setStyle("emacs-mode");
    editor.commands.addCommands(commands);
    exports.handler.platform = editor.commands.platform;
    editor.$emacsModeHandler = this;
    editor.addEventListener('copy', this.onCopy);
    editor.addEventListener('paste', this.onPaste);
};

exports.handler.detach = function(editor) {
    delete editor.renderer.screenToTextCoordinates;
    editor.session.$selectLongWords = $formerLongWords;
    editor.session.$useEmacsStyleLineStart = $formerLineStart;
    editor.removeEventListener("click", $resetMarkMode);
    editor.removeEventListener("changeSession", $kbSessionChange);
    editor.unsetStyle("emacs-mode");
    editor.commands.removeCommands(commands);
    editor.removeEventListener('copy', this.onCopy);
    editor.removeEventListener('paste', this.onPaste);
};

var $kbSessionChange = function(e) {
    if (e.oldSession) {
        e.oldSession.$selectLongWords = $formerLongWords;
        e.oldSession.$useEmacsStyleLineStart = $formerLineStart;
    }

    $formerLongWords = e.session.$selectLongWords;
    e.session.$selectLongWords = true;
    $formerLineStart = e.session.$useEmacsStyleLineStart;
    e.session.$useEmacsStyleLineStart = true;

    if (!e.session.hasOwnProperty('$emacsMark'))
        e.session.$emacsMark = null;
    if (!e.session.hasOwnProperty('$emacsMarkRing'))
        e.session.$emacsMarkRing = [];
}

var $resetMarkMode = function(e) {
    e.editor.session.$emacsMark = null;
}

var keys = require("../lib/keys").KEY_MODS,
    eMods = {C: "ctrl", S: "shift", M: "alt", CMD: "command"},
    combinations = ["C-S-M-CMD",
                    "S-M-CMD", "C-M-CMD", "C-S-CMD", "C-S-M",
                    "M-CMD", "S-CMD", "S-M", "C-CMD", "C-M", "C-S",
                    "CMD", "M", "S", "C"];
combinations.forEach(function(c) {
    var hashId = 0;
    c.split("-").forEach(function(c) {
        hashId = hashId | keys[eMods[c]];
    });
    eMods[hashId] = c.toLowerCase() + "-";
});

exports.handler.onCopy = function(e, editor) {
    if (editor.$handlesEmacsOnCopy) return;
    editor.$handlesEmacsOnCopy = true;
    exports.handler.commands.killRingSave.exec(editor);
    delete editor.$handlesEmacsOnCopy;
}

exports.handler.onPaste = function(e, editor) {
    editor.pushEmacsMark(editor.getCursorPosition());
}

exports.handler.bindKey = function(key, command) {
    if (!key)
        return;

    var ckb = this.commmandKeyBinding;
    key.split("|").forEach(function(keyPart) {
        keyPart = keyPart.toLowerCase();
        ckb[keyPart] = command;
        var keyParts = keyPart.split(" ").slice(0,-1);
        keyParts.reduce(function(keyMapKeys, keyPart, i) {
            var prefix = keyMapKeys[i-1] ? keyMapKeys[i-1] + ' ' : '';
            return keyMapKeys.concat([prefix + keyPart]);
        }, []).forEach(function(keyPart) {
            if (!ckb[keyPart]) ckb[keyPart] = "null";
        });
    }, this);
}

exports.handler.handleKeyboard = function(data, hashId, key, keyCode) {
    var editor = data.editor;
    if (hashId == -1) {
        editor.pushEmacsMark();
        if (data.count) {
            var str = Array(data.count + 1).join(key);
            data.count = null;
            return {command: "insertstring", args: str};
        }
    }

    if (key == "\x00") return undefined;

    var modifier = eMods[hashId];
    if (modifier == "c-" || data.universalArgument) {
        var prevCount = String(data.count || 0);
        var count = parseInt(key[key.length - 1]);
        if (typeof count === 'number' && !isNaN(count)) {
            data.count = parseInt(prevCount + count);
            return {command: "null"};
        } else if (data.universalArgument) {
            data.count = 4;
        }
    }
    data.universalArgument = false;
    if (modifier) key = modifier + key;
    if (data.keyChain) key = data.keyChain += " " + key;
    var command = this.commmandKeyBinding[key];
    data.keyChain = command == "null" ? key : "";
    if (!command) return undefined;
    if (command === "null") return {command: "null"};

    if (command === "universalArgument") {
        data.universalArgument = true;
        return {command: "null"};
    }
    var args;
    if (typeof command !== "string") {
        args = command.args;
        if (command.command) command = command.command;
        if (command === "goorselect") {
            command = editor.emacsMark() ? args[1] : args[0];
            args = null;
        }
    }

    if (typeof command === "string") {
        if (command === "insertstring" ||
            command === "splitline" ||
            command === "togglecomment") {
            editor.pushEmacsMark();
        }
        command = this.commands[command] || editor.commands.commands[command];
        if (!command) return undefined;
    }

    if (!command.readonly && !command.isYank)
        data.lastCommand = null;

    if (data.count) {
        var count = data.count;
        data.count = 0;
        if (!command || !command.handlesCount) {
            return {
                args: args,
                command: {
                    exec: function(editor, args) {
                        for (var i = 0; i < count; i++)
                            command.exec(editor, args);
                    }
                }
            };
        } else {
            if (!args) args = {}
            if (typeof args === 'object') args.count = count;
        }
    }

    return {command: command, args: args};
};

exports.emacsKeys = {
    "Up|C-p"      : {command: "goorselect", args: ["golineup","selectup"]},
    "Down|C-n"    : {command: "goorselect", args: ["golinedown","selectdown"]},
    "Left|C-b"    : {command: "goorselect", args: ["gotoleft","selectleft"]},
    "Right|C-f"   : {command: "goorselect", args: ["gotoright","selectright"]},
    "C-Left|M-b"  : {command: "goorselect", args: ["gotowordleft","selectwordleft"]},
    "C-Right|M-f" : {command: "goorselect", args: ["gotowordright","selectwordright"]},
    "Home|C-a"    : {command: "goorselect", args: ["gotolinestart","selecttolinestart"]},
    "End|C-e"     : {command: "goorselect", args: ["gotolineend","selecttolineend"]},
    "C-Home|S-M-,": {command: "goorselect", args: ["gotostart","selecttostart"]},
    "C-End|S-M-." : {command: "goorselect", args: ["gotoend","selecttoend"]},
    "S-Up|S-C-p"      : "selectup",
    "S-Down|S-C-n"    : "selectdown",
    "S-Left|S-C-b"    : "selectleft",
    "S-Right|S-C-f"   : "selectright",
    "S-C-Left|S-M-b"  : "selectwordleft",
    "S-C-Right|S-M-f" : "selectwordright",
    "S-Home|S-C-a"    : "selecttolinestart",
    "S-End|S-C-e"     : "selecttolineend",
    "S-C-Home"        : "selecttostart",
    "S-C-End"         : "selecttoend",

    "C-l" : "recenterTopBottom",
    "M-s" : "centerselection",
    "M-g": "gotoline",
    "C-x C-p": "selectall",
    "C-Down": {command: "goorselect", args: ["gotopagedown","selectpagedown"]},
    "C-Up": {command: "goorselect", args: ["gotopageup","selectpageup"]},
    "PageDown|C-v": {command: "goorselect", args: ["gotopagedown","selectpagedown"]},
    "PageUp|M-v": {command: "goorselect", args: ["gotopageup","selectpageup"]},
    "S-C-Down": "selectpagedown",
    "S-C-Up": "selectpageup",

    "C-s": "iSearch",
    "C-r": "iSearchBackwards",

    "M-C-s": "findnext",
    "M-C-r": "findprevious",
    "S-M-5": "replace",
    "Backspace": "backspace",
    "Delete|C-d": "del",
    "Return|C-m": {command: "insertstring", args: "\n"}, // "newline"
    "C-o": "splitline",

    "M-d|C-Delete": {command: "killWord", args: "right"},
    "C-Backspace|M-Backspace|M-Delete": {command: "killWord", args: "left"},
    "C-k": "killLine",

    "C-y|S-Delete": "yank",
    "M-y": "yankRotate",
    "C-g": "keyboardQuit",

    "C-w": "killRegion",
    "M-w": "killRingSave",
    "C-Space": "setMark",
    "C-x C-x": "exchangePointAndMark",

    "C-t": "transposeletters",
    "M-u": "touppercase",    // Doesn't work
    "M-l": "tolowercase",
    "M-/": "autocomplete",   // Doesn't work
    "C-u": "universalArgument",

    "M-;": "togglecomment",

    "C-/|C-x u|S-C--|C-z": "undo",
    "S-C-/|S-C-x u|C--|S-C-z": "redo", //infinite undo?
    "C-x r":  "selectRectangularRegion",
    "M-x": {command: "focusCommandLine", args: "M-x "}
};


exports.handler.bindKeys(exports.emacsKeys);

exports.handler.addCommands({
    recenterTopBottom: function(editor) {
        var renderer = editor.renderer;
        var pos = renderer.$cursorLayer.getPixelPosition();
        var h = renderer.$size.scrollerHeight - renderer.lineHeight;
        var scrollTop = renderer.scrollTop;
        if (Math.abs(pos.top - scrollTop) < 2) {
            scrollTop = pos.top - h;
        } else if (Math.abs(pos.top - scrollTop - h * 0.5) < 2) {
            scrollTop = pos.top;
        } else {
            scrollTop = pos.top - h * 0.5;
        }
        editor.session.setScrollTop(scrollTop);
    },
    selectRectangularRegion:  function(editor) {
        editor.multiSelect.toggleBlockSelection();
    },
    setMark:  {
        exec: function(editor, args) {
            if (args && args.count) {
                var mark = editor.popEmacsMark();
                mark && editor.selection.moveCursorToPosition(mark);
                return;
            }

            var mark = editor.emacsMark(),
                transientMarkModeActive = true;
            if (transientMarkModeActive && (mark || !editor.selection.isEmpty())) {
                editor.pushEmacsMark();
                editor.clearSelection();
                return;
            }

            if (mark) {
                var cp = editor.getCursorPosition();
                if (editor.selection.isEmpty() &&
                    mark.row == cp.row && mark.column == cp.column) {
                    editor.pushEmacsMark();
                    return;
                }
            }
            mark = editor.getCursorPosition();
            editor.setEmacsMark(mark);
            editor.selection.setSelectionAnchor(mark.row, mark.column);
        },
        readonly: true,
        handlesCount: true,
        multiSelectAction: "forEach"
    },
    exchangePointAndMark: {
        exec: function(editor, args) {
            var sel = editor.selection;
            if (args.count) {
                var pos = editor.getCursorPosition();
                sel.clearSelection();
                sel.moveCursorToPosition(editor.popEmacsMark());
                editor.pushEmacsMark(pos);
                return;
            }
            var lastMark = editor.getLastEmacsMark();
            var range = sel.getRange();
            if (range.isEmpty()) {
                sel.selectToPosition(lastMark);
                return;
            }
            sel.setSelectionRange(range, !sel.isBackwards());
        },
        readonly: true,
        handlesCount: true,
        multiSelectAction: "forEach"
    },
    killWord: {
        exec: function(editor, dir) {
            editor.clearSelection();
            if (dir == "left")
                editor.selection.selectWordLeft();
            else
                editor.selection.selectWordRight();

            var range = editor.getSelectionRange();
            var text = editor.session.getTextRange(range);
            exports.killRing.add(text);

            editor.session.remove(range);
            editor.clearSelection();
        },
        multiSelectAction: "forEach"
    },
    killLine: function(editor) {
        editor.pushEmacsMark(null);
        var pos = editor.getCursorPosition();
        if (pos.column == 0 &&
            editor.session.doc.getLine(pos.row).length == 0) {
            editor.selection.selectLine();
        } else {
            editor.clearSelection();
            editor.selection.selectLineEnd();
        }
        var range = editor.getSelectionRange();
        var text = editor.session.getTextRange(range);
        exports.killRing.add(text);

        editor.session.remove(range);
        editor.clearSelection();
    },
    yank: function(editor) {
        editor.onPaste(exports.killRing.get() || '');
        editor.keyBinding.$data.lastCommand = "yank";
    },
    yankRotate: function(editor) {
        if (editor.keyBinding.$data.lastCommand != "yank")
            return;
        editor.undo();
        editor.onPaste(exports.killRing.rotate());
        editor.keyBinding.$data.lastCommand = "yank";
    },
    killRegion: {
        exec: function(editor) {
            exports.killRing.add(editor.getCopyText());
            editor.commands.byName.cut.exec(editor);
        },
        readonly: true,
        multiSelectAction: "forEach"
    },
    killRingSave: {
        exec: function(editor) {
            exports.killRing.add(editor.getCopyText());
            setTimeout(function() {
                var sel = editor.selection,
                    range = sel.getRange();
                editor.pushEmacsMark(sel.isBackwards() ? range.end : range.start);
                sel.clearSelection();
            }, 0);
        },
        readonly: true
    },
    keyboardQuit: function(editor) {
        editor.selection.clearSelection();
        editor.setEmacsMark(null);
    },
    focusCommandLine: function(editor, arg) {
        if (editor.showCommandLine)
            editor.showCommandLine(arg);
    }
});

exports.handler.addCommands(iSearchCommandModule.iSearchStartCommands);

var commands = exports.handler.commands;
commands.yank.isYank = true;
commands.yankRotate.isYank = true;

exports.killRing = {
    $data: [],
    add: function(str) {
        str && this.$data.push(str);
        if (this.$data.length > 30)
            this.$data.shift();
    },
    get: function(n) {
        n = n || 1;
        return this.$data.slice(this.$data.length-n, this.$data.length).reverse().join('\n');
    },
    pop: function() {
        if (this.$data.length > 1)
            this.$data.pop();
        return this.get();
    },
    rotate: function() {
        this.$data.unshift(this.$data.pop());
        return this.get();
    }
};

});

define('ace/incremental_search', ['require', 'exports', 'module' , 'ace/lib/oop', 'ace/range', 'ace/search', 'ace/search_highlight', 'ace/commands/incremental_search_commands', 'ace/lib/dom', 'ace/commands/command_manager', 'ace/editor', 'ace/config'], function(require, exports, module) {


var oop = require("./lib/oop");
var Range = require("./range").Range;
var Search = require("./search").Search;
var SearchHighlight = require("./search_highlight").SearchHighlight;
var iSearchCommandModule = require("./commands/incremental_search_commands");
var ISearchKbd = iSearchCommandModule.IncrementalSearchKeyboardHandler;
function IncrementalSearch() {
    this.$options = {wrap: false, skipCurrent: false};
    this.$keyboardHandler = new ISearchKbd(this);
}

oop.inherits(IncrementalSearch, Search);

;(function() {

    this.activate = function(ed, backwards) {
        this.$editor = ed;
        this.$startPos = this.$currentPos = ed.getCursorPosition();
        this.$options.needle = '';
        this.$options.backwards = backwards;
        ed.keyBinding.addKeyboardHandler(this.$keyboardHandler);
        this.$mousedownHandler = ed.addEventListener('mousedown', this.onMouseDown.bind(this));
        this.selectionFix(ed);
        this.statusMessage(true);
    }

    this.deactivate = function(reset) {
        this.cancelSearch(reset);
        this.$editor.keyBinding.removeKeyboardHandler(this.$keyboardHandler);
        if (this.$mousedownHandler) {
            this.$editor.removeEventListener('mousedown', this.$mousedownHandler);
            delete this.$mousedownHandler;
        }
        this.message('');
    }

    this.selectionFix = function(editor) {
        if (editor.selection.isEmpty() && !editor.session.$emacsMark) {
            editor.clearSelection();
        }
    }

    this.highlight = function(regexp) {
        var sess = this.$editor.session,
            hl = sess.$isearchHighlight = sess.$isearchHighlight || sess.addDynamicMarker(
                new SearchHighlight(null, "ace_isearch-result", "text"));
        hl.setRegexp(regexp);
        sess._emit("changeBackMarker"); // force highlight layer redraw
    }

    this.cancelSearch = function(reset) {
        var e = this.$editor;
        this.$prevNeedle = this.$options.needle;
        this.$options.needle = '';
        if (reset) {
            e.moveCursorToPosition(this.$startPos);
            this.$currentPos = this.$startPos;
        } else {
            e.pushEmacsMark && e.pushEmacsMark(this.$startPos, false);
        }
        this.highlight(null);
        return Range.fromPoints(this.$currentPos, this.$currentPos);
    }

    this.highlightAndFindWithNeedle = function(moveToNext, needleUpdateFunc) {
        if (!this.$editor) return null;
        var options = this.$options;
        if (needleUpdateFunc) {
            options.needle = needleUpdateFunc.call(this, options.needle || '') || '';
        }
        if (options.needle.length === 0) {
            this.statusMessage(true);
            return this.cancelSearch(true);
        };
        options.start = this.$currentPos;
        var session = this.$editor.session,
            found = this.find(session);
        if (found) {
            if (options.backwards) found = Range.fromPoints(found.end, found.start);
            this.$editor.moveCursorToPosition(found.end);
            if (moveToNext) this.$currentPos = found.end;
            this.highlight(options.re)
        }

        this.statusMessage(found);

        return found;
    }

    this.addChar = function(c) {
        return this.highlightAndFindWithNeedle(false, function(needle) {
            return needle + c;
        });
    }

    this.removeChar = function(c) {
        return this.highlightAndFindWithNeedle(false, function(needle) {
            return needle.length > 0 ? needle.substring(0, needle.length-1) : needle;
        });
    }

    this.next = function(options) {
        options = options || {};
        this.$options.backwards = !!options.backwards;
        this.$currentPos = this.$editor.getCursorPosition();
        return this.highlightAndFindWithNeedle(true, function(needle) {
            return options.useCurrentOrPrevSearch && needle.length === 0 ?
                this.$prevNeedle || '' : needle;
        });
    }

    this.onMouseDown = function(evt) {
        this.deactivate();
        return true;
    }

    this.statusMessage = function(found) {
        var options = this.$options, msg = '';
        msg += options.backwards ? 'reverse-' : '';
        msg += 'isearch: ' + options.needle;
        msg += found ? '' : ' (not found)';
        this.message(msg);
    }

    this.message = function(msg) {
        if (this.$editor.showCommandLine) {
            this.$editor.showCommandLine(msg);
            this.$editor.focus();
        } else {
            console.log(msg);
        }
    }

}).call(IncrementalSearch.prototype);


exports.IncrementalSearch = IncrementalSearch;

var dom = require('./lib/dom');
dom.importCssString && dom.importCssString("\
.ace_marker-layer .ace_isearch-result {\
  position: absolute;\
  z-index: 6;\
  -moz-box-sizing: border-box;\
  -webkit-box-sizing: border-box;\
  box-sizing: border-box;\
}\
div.ace_isearch-result {\
  border-radius: 4px;\
  background-color: rgba(255, 200, 0, 0.5);\
  box-shadow: 0 0 4px rgb(255, 200, 0);\
}\
.ace_dark div.ace_isearch-result {\
  background-color: rgb(100, 110, 160);\
  box-shadow: 0 0 4px rgb(80, 90, 140);\
}", "incremental-search-highlighting");
var commands = require("./commands/command_manager");
(function() {
    this.setupIncrementalSearch = function(editor, val) {
        if (this.usesIncrementalSearch == val) return;
        this.usesIncrementalSearch = val;
        var iSearchCommands = iSearchCommandModule.iSearchStartCommands;
        var method = val ? 'addCommands' : 'removeCommands';
        this[method](iSearchCommands);
    };
}).call(commands.CommandManager.prototype);
var Editor = require("./editor").Editor;
require("./config").defineOptions(Editor.prototype, "editor", {
    useIncrementalSearch: {
        set: function(val) {
            this.keyBinding.$handlers.forEach(function(handler) {
                if (handler.setupIncrementalSearch) {
                    handler.setupIncrementalSearch(this, val);
                }
            });
            this._emit('incrementalSearchSettingChanged', {isEnabled: val});
        }
    }
});

});

define('ace/commands/incremental_search_commands', ['require', 'exports', 'module' , 'ace/config', 'ace/lib/oop', 'ace/keyboard/hash_handler', 'ace/commands/occur_commands'], function(require, exports, module) {

var config = require("../config");
var oop = require("../lib/oop");
var HashHandler = require("../keyboard/hash_handler").HashHandler;
var occurStartCommand = require("./occur_commands").occurStartCommand;
exports.iSearchStartCommands = [{
    name: "iSearch",
    bindKey: {win: "Ctrl-F", mac: "Command-F"},
    exec: function(editor, options) {
        config.loadModule(["core", "ace/incremental_search"], function(e) {
            var iSearch = e.iSearch = e.iSearch || new e.IncrementalSearch();
            iSearch.activate(editor, options.backwards);
            if (options.jumpToFirstMatch) iSearch.next(options);
        });
    },
    readOnly: true
}, {
    name: "iSearchBackwards",
    exec: function(editor, jumpToNext) { editor.execCommand('iSearch', {backwards: true}); },
    readOnly: true
}, {
    name: "iSearchAndGo",
    bindKey: {win: "Ctrl-K", mac: "Command-G"},
    exec: function(editor, jumpToNext) { editor.execCommand('iSearch', {jumpToFirstMatch: true, useCurrentOrPrevSearch: true}); },
    readOnly: true
}, {
    name: "iSearchBackwardsAndGo",
    bindKey: {win: "Ctrl-Shift-K", mac: "Command-Shift-G"},
    exec: function(editor) { editor.execCommand('iSearch', {jumpToFirstMatch: true, backwards: true, useCurrentOrPrevSearch: true}); },
    readOnly: true
}];
exports.iSearchCommands = [{
    name: "restartSearch",
    bindKey: {win: "Ctrl-F", mac: "Command-F"},
    exec: function(iSearch) {
        iSearch.cancelSearch(true);
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: "searchForward",
    bindKey: {win: "Ctrl-S|Ctrl-K", mac: "Ctrl-S|Command-G"},
    exec: function(iSearch, options) {
        options.useCurrentOrPrevSearch = true;
        iSearch.next(options);
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: "searchBackward",
    bindKey: {win: "Ctrl-R|Ctrl-Shift-K", mac: "Ctrl-R|Command-Shift-G"},
    exec: function(iSearch, options) {
        options.useCurrentOrPrevSearch = true;
        options.backwards = true;
        iSearch.next(options);
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: "extendSearchTerm",
    exec: function(iSearch, string) {
        iSearch.addChar(string);
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: "extendSearchTermSpace",
    bindKey: "space",
    exec: function(iSearch) { iSearch.addChar(' '); },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: "shrinkSearchTerm",
    bindKey: "backspace",
    exec: function(iSearch) {
        iSearch.removeChar();
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: 'confirmSearch',
    bindKey: 'return',
    exec: function(iSearch) { iSearch.deactivate(); },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: 'cancelSearch',
    bindKey: 'esc|Ctrl-G',
    exec: function(iSearch) { iSearch.deactivate(true); },
    readOnly: true,
    isIncrementalSearchCommand: true
}, {
    name: 'occurisearch',
    bindKey: 'Ctrl-O',
    exec: function(iSearch) {
        var options = oop.mixin({}, iSearch.$options);
        iSearch.deactivate();
        occurStartCommand.exec(iSearch.$editor, options);
    },
    readOnly: true,
    isIncrementalSearchCommand: true
}];

function IncrementalSearchKeyboardHandler(iSearch) {
    this.$iSearch = iSearch;
}

oop.inherits(IncrementalSearchKeyboardHandler, HashHandler);

;(function() {

    this.attach = function(editor) {
        var iSearch = this.$iSearch;
        HashHandler.call(this, exports.iSearchCommands, editor.commands.platform);
        this.$commandExecHandler = editor.commands.addEventListener('exec', function(e) {
            if (!e.command.isIncrementalSearchCommand) return undefined;
            e.stopPropagation();
            e.preventDefault();
            return e.command.exec(iSearch, e.args || {});
        });
    }

    this.detach = function(editor) {
        if (!this.$commandExecHandler) return;
        editor.commands.removeEventListener('exec', this.$commandExecHandler);
        delete this.$commandExecHandler;
    }

    var handleKeyboard$super = this.handleKeyboard;
    this.handleKeyboard = function(data, hashId, key, keyCode) {
        var cmd = handleKeyboard$super.call(this, data, hashId, key, keyCode);
        if (cmd.command) { return cmd; }
        if (hashId == -1) {
            var extendCmd = this.commands.extendSearchTerm;
            if (extendCmd) { return {command: extendCmd, args: key}; }
        }
        return {command: "null", passEvent: hashId == 0 || hashId == 4};
    }

}).call(IncrementalSearchKeyboardHandler.prototype);


exports.IncrementalSearchKeyboardHandler = IncrementalSearchKeyboardHandler;

});

define('ace/commands/occur_commands', ['require', 'exports', 'module' , 'ace/config', 'ace/occur', 'ace/keyboard/hash_handler', 'ace/lib/oop'], function(require, exports, module) {

var config = require("../config"),
    Occur = require("../occur").Occur;
var occurStartCommand = {
    name: "occur",
    exec: function(editor, options) {
        var alreadyInOccur = !!editor.session.$occur;
        var occurSessionActive = new Occur().enter(editor, options);
        if (occurSessionActive && !alreadyInOccur)
            OccurKeyboardHandler.installIn(editor);
    },
    readOnly: true
};

var occurCommands = [{
    name: "occurexit",
    bindKey: 'esc|Ctrl-G',
    exec: function(editor) {
        var occur = editor.session.$occur;
        if (!occur) return;
        occur.exit(editor, {});
        if (!editor.session.$occur) OccurKeyboardHandler.uninstallFrom(editor);
    },
    readOnly: true
}, {
    name: "occuraccept",
    bindKey: 'enter',
    exec: function(editor) {
        var occur = editor.session.$occur;
        if (!occur) return;
        occur.exit(editor, {translatePosition: true});
        if (!editor.session.$occur) OccurKeyboardHandler.uninstallFrom(editor);
    },
    readOnly: true
}];

var HashHandler = require("../keyboard/hash_handler").HashHandler;
var oop = require("../lib/oop");


function OccurKeyboardHandler() {}

oop.inherits(OccurKeyboardHandler, HashHandler);

;(function() {

    this.isOccurHandler = true;

    this.attach = function(editor) {
        HashHandler.call(this, occurCommands, editor.commands.platform);
        this.$editor = editor;
    }

    var handleKeyboard$super = this.handleKeyboard;
    this.handleKeyboard = function(data, hashId, key, keyCode) {
        var cmd = handleKeyboard$super.call(this, data, hashId, key, keyCode);
        return (cmd && cmd.command) ? cmd : undefined;
    }

}).call(OccurKeyboardHandler.prototype);

OccurKeyboardHandler.installIn = function(editor) {
    var handler = new this();
    editor.keyBinding.addKeyboardHandler(handler);
    editor.commands.addCommands(occurCommands);
}

OccurKeyboardHandler.uninstallFrom = function(editor) {
    editor.commands.removeCommands(occurCommands);
    var handler = editor.getKeyboardHandler();
    if (handler.isOccurHandler)
        editor.keyBinding.removeKeyboardHandler(handler);
}

exports.occurStartCommand = occurStartCommand;

});

define('ace/occur', ['require', 'exports', 'module' , 'ace/lib/oop', 'ace/range', 'ace/search', 'ace/edit_session', 'ace/search_highlight', 'ace/lib/dom'], function(require, exports, module) {


var oop = require("./lib/oop");
var Range = require("./range").Range;
var Search = require("./search").Search;
var EditSession = require("./edit_session").EditSession;
var SearchHighlight = require("./search_highlight").SearchHighlight;
function Occur() {}

oop.inherits(Occur, Search);

(function() {
    this.enter = function(editor, options) {
        if (!options.needle) return false;
        var pos = editor.getCursorPosition();
        this.displayOccurContent(editor, options);
        var translatedPos = this.originalToOccurPosition(editor.session, pos);
        editor.moveCursorToPosition(translatedPos);
        return true;
    }
    this.exit = function(editor, options) {
        var pos = options.translatePosition && editor.getCursorPosition();
        var translatedPos = pos && this.occurToOriginalPosition(editor.session, pos);
        this.displayOriginalContent(editor);
        if (translatedPos)
            editor.moveCursorToPosition(translatedPos);
        return true;
    }

    this.highlight = function(sess, regexp) {
        var hl = sess.$occurHighlight = sess.$occurHighlight || sess.addDynamicMarker(
                new SearchHighlight(null, "ace_occur-highlight", "text"));
        hl.setRegexp(regexp);
        sess._emit("changeBackMarker"); // force highlight layer redraw
    }

    this.displayOccurContent = function(editor, options) {
        this.$originalSession = editor.session;
        var found = this.matchingLines(editor.session, options);
        var lines = found.map(function(foundLine) { return foundLine.content; });
        var occurSession = new EditSession(lines.join('\n'));
        occurSession.$occur = this;
        occurSession.$occurMatchingLines = found;
        editor.setSession(occurSession);
        this.highlight(occurSession, options.re);
        occurSession._emit('changeBackMarker');
    }

    this.displayOriginalContent = function(editor) {
        editor.setSession(this.$originalSession);
    }
    this.originalToOccurPosition = function(session, pos) {
        var lines = session.$occurMatchingLines;
        var nullPos = {row: 0, column: 0};
        if (!lines) return nullPos;
        for (var i = 0; i < lines.length; i++) {
            if (lines[i].row === pos.row)
                return {row: i, column: pos.column};
        }
        return nullPos;
    }
    this.occurToOriginalPosition = function(session, pos) {
        var lines = session.$occurMatchingLines;
        if (!lines || !lines[pos.row])
            return pos;
        return {row: lines[pos.row].row, column: pos.column};
    }

    this.matchingLines = function(session, options) {
        options = oop.mixin({}, options);
        if (!session || !options.needle) return [];
        var search = new Search();
        search.set(options);
        return search.findAll(session).reduce(function(lines, range) {
            var row = range.start.row;
            var last = lines[lines.length-1];
            return last && last.row === row ?
                lines :
                lines.concat({row: row, content: session.getLine(row)});
        }, []);
    }

}).call(Occur.prototype);

var dom = require('./lib/dom');
dom.importCssString(".ace_occur-highlight {\n\
    border-radius: 4px;\n\
    background-color: rgba(87, 255, 8, 0.25);\n\
    position: absolute;\n\
    z-index: 4;\n\
    -moz-box-sizing: border-box;\n\
    -webkit-box-sizing: border-box;\n\
    box-sizing: border-box;\n\
    box-shadow: 0 0 4px rgb(91, 255, 50);\n\
}\n\
.ace_dark .ace_occur-highlight {\n\
    background-color: rgb(80, 140, 85);\n\
    box-shadow: 0 0 4px rgb(60, 120, 70);\n\
}\n", "incremental-occur-highlighting");

exports.Occur = Occur;

});
