/* ***** BEGIN LICENSE BLOCK *****
 * Distributed under the BSD license:
 *
 * Copyright (c) 2012, Ajax.org B.V.
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
 *
 * Contributor(s):
 *
 *
 *
 * ***** END LICENSE BLOCK ***** */

define('ace/mode/prolog', ['require', 'exports', 'module' , 'ace/lib/oop', 'ace/mode/text', 'ace/tokenizer', 'ace/mode/prolog_highlight_rules', 'ace/mode/folding/cstyle'], function(require, exports, module) {


var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var Tokenizer = require("../tokenizer").Tokenizer;
var PrologHighlightRules = require("./prolog_highlight_rules").PrologHighlightRules;
var FoldMode = require("./folding/cstyle").FoldMode;

var Mode = function() {
    var highlighter = new PrologHighlightRules();
    this.foldingRules = new FoldMode();
    this.$tokenizer = new Tokenizer(highlighter.getRules());
};
oop.inherits(Mode, TextMode);

(function() {
    this.lineCommentStart = "/\\*";
    this.blockComment = {start: "/*", end: "*/"};
}).call(Mode.prototype);

exports.Mode = Mode;
});

define('ace/mode/prolog_highlight_rules', ['require', 'exports', 'module' , 'ace/lib/oop', 'ace/mode/text_highlight_rules'], function(require, exports, module) {


var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var PrologHighlightRules = function() {

    this.$rules = { start: 
       [ { include: '#comment' },
         { include: '#basic_fact' },
         { include: '#rule' },
         { include: '#directive' },
         { include: '#fact' } ],
      '#atom': 
       [ { token: 'constant.other.atom.prolog',
           regex: '\\b[a-z][a-zA-Z0-9_]*\\b' },
         { token: 'constant.numeric.prolog',
           regex: '-?\\d+(?:\\.\\d+)?' },
         { include: '#string' } ],
      '#basic_elem': 
       [ { include: '#comment' },
         { include: '#statement' },
         { include: '#constants' },
         { include: '#operators' },
         { include: '#builtins' },
         { include: '#list' },
         { include: '#atom' },
         { include: '#variable' } ],
      '#basic_fact': 
       [ { token: 
            [ 'entity.name.function.fact.basic.prolog',
              'punctuation.end.fact.basic.prolog' ],
           regex: '([a-z]\\w*)(\\.)' } ],
      '#builtins': 
       [ { token: 'support.function.builtin.prolog',
           regex: '\\b(?:\n\t\t\t\t\t\tabolish|abort|ancestors|arg|ascii|assert[az]|\n\t\t\t\t\t\tatom(?:ic)?|body|char|close|conc|concat|consult|\n\t\t\t\t\t\tdefine|definition|dynamic|dump|fail|file|free|\n\t\t\t\t\t\tfree_proc|functor|getc|goal|halt|head|head|integer|\n\t\t\t\t\t\tlength|listing|match_args|member|next_clause|nl|\n\t\t\t\t\t\tnonvar|nth|number|cvars|nvars|offset|op|\n\t\t\t\t\t\tprint?|prompt|putc|quoted|ratom|read|redefine|\n\t\t\t\t\t\trename|retract(?:all)?|see|seeing|seen|skip|spy|\n\t\t\t\t\t\tstatistics|system|tab|tell|telling|term|\n\t\t\t\t\t\ttime|told|univ|unlink_clause|unspy_predicate|\n\t\t\t\t\t\tvar|write\n\t\t\t\t\t)\\b' } ],
      '#comment': 
       [ { token: 
            [ 'punctuation.definition.comment.prolog',
              'comment.line.percentage.prolog' ],
           regex: '(%)(.*$)' },
         { token: 'punctuation.definition.comment.prolog',
           regex: '/\\*',
           push: 
            [ { token: 'punctuation.definition.comment.prolog',
                regex: '\\*/',
                next: 'pop' },
              { defaultToken: 'comment.block.prolog' } ] } ],
      '#constants': 
       [ { token: 'constant.language.prolog',
           regex: '\\b(?:true|false|yes|no)\\b' } ],
      '#directive': 
       [ { token: 'keyword.operator.directive.prolog',
           regex: ':-',
           push: 
            [ { token: 'meta.directive.prolog', regex: '\\.', next: 'pop' },
              { include: '#comment' },
              { include: '#statement' },
              { defaultToken: 'meta.directive.prolog' } ] } ],
      '#expr': 
       [ { include: '#comments' },
         { token: 'meta.expression.prolog',
           regex: '\\(',
           push: 
            [ { token: 'meta.expression.prolog', regex: '\\)', next: 'pop' },
              { include: '#expr' },
              { defaultToken: 'meta.expression.prolog' } ] },
         { token: 'keyword.control.cutoff.prolog', regex: '!' },
         { token: 'punctuation.control.and.prolog', regex: ',' },
         { token: 'punctuation.control.or.prolog', regex: ';' },
         { include: '#basic_elem' } ],
      '#fact': 
       [ { token: 
            [ 'entity.name.function.fact.prolog',
              'punctuation.begin.fact.parameters.prolog' ],
           regex: '([a-z]\\w*)(\\()(?!.*:-)',
           push: 
            [ { token: 
                 [ 'punctuation.end.fact.parameters.prolog',
                   'punctuation.end.fact.prolog' ],
                regex: '(\\))(\\.)',
                next: 'pop' },
              { include: '#parameter' },
              { defaultToken: 'meta.fact.prolog' } ] } ],
      '#list': 
       [ { token: 'punctuation.begin.list.prolog',
           regex: '\\[(?=.*\\])',
           push: 
            [ { token: 'punctuation.end.list.prolog',
                regex: '\\]',
                next: 'pop' },
              { include: '#comment' },
              { token: 'punctuation.separator.list.prolog', regex: ',' },
              { token: 'punctuation.concat.list.prolog',
                regex: '\\|',
                push: 
                 [ { token: 'meta.list.concat.prolog',
                     regex: '(?=\\s*\\])',
                     next: 'pop' },
                   { include: '#basic_elem' },
                   { defaultToken: 'meta.list.concat.prolog' } ] },
              { include: '#basic_elem' },
              { defaultToken: 'meta.list.prolog' } ] } ],
      '#operators': 
       [ { token: 'keyword.operator.prolog',
           regex: '\\\\\\+|\\bnot\\b|\\bis\\b|->|[><]|[><\\\\:=]?=|(?:=\\\\|\\\\=)=' } ],
      '#parameter': 
       [ { token: 'variable.language.anonymous.prolog',
           regex: '\\b_\\b' },
         { token: 'variable.parameter.prolog',
           regex: '\\b[A-Z_]\\w*\\b' },
         { token: 'punctuation.separator.parameters.prolog', regex: ',' },
         { include: '#basic_elem' },
         { token: 'invalid.illegal.invalidchar.prolog', regex: '[^\\s]' } ],
      '#rule': 
       [ { token: 'meta.rule.prolog',
           regex: '(?=[a-z]\\w*.*:-)',
           push: 
            [ { token: 'punctuation.rule.end.prolog',
                regex: '\\.',
                next: 'pop' },
              { token: 'meta.rule.signature.prolog',
                regex: '(?=[a-z]\\w*.*:-)',
                push: 
                 [ { token: 'meta.rule.signature.prolog',
                     regex: '(?=:-)',
                     next: 'pop' },
                   { token: 'entity.name.function.rule.prolog',
                     regex: '[a-z]\\w*(?=\\(|\\s*:-)' },
                   { token: 'punctuation.rule.parameters.begin.prolog',
                     regex: '\\(',
                     push: 
                      [ { token: 'punctuation.rule.parameters.end.prolog',
                          regex: '\\)',
                          next: 'pop' },
                        { include: '#parameter' },
                        { defaultToken: 'meta.rule.parameters.prolog' } ] },
                   { defaultToken: 'meta.rule.signature.prolog' } ] },
              { token: 'keyword.operator.definition.prolog',
                regex: ':-',
                push: 
                 [ { token: 'meta.rule.definition.prolog',
                     regex: '(?=\\.)',
                     next: 'pop' },
                   { include: '#comment' },
                   { include: '#expr' },
                   { defaultToken: 'meta.rule.definition.prolog' } ] },
              { defaultToken: 'meta.rule.prolog' } ] } ],
      '#statement': 
       [ { token: 'meta.statement.prolog',
           regex: '(?=[a-z]\\w*\\()',
           push: 
            [ { token: 'punctuation.end.statement.parameters.prolog',
                regex: '\\)',
                next: 'pop' },
              { include: '#builtins' },
              { include: '#atom' },
              { token: 'punctuation.begin.statement.parameters.prolog',
                regex: '\\(',
                push: 
                 [ { token: 'meta.statement.parameters.prolog',
                     regex: '(?=\\))',
                     next: 'pop' },
                   { token: 'punctuation.separator.statement.prolog', regex: ',' },
                   { include: '#basic_elem' },
                   { defaultToken: 'meta.statement.parameters.prolog' } ] },
              { defaultToken: 'meta.statement.prolog' } ] } ],
      '#string': 
       [ { token: 'punctuation.definition.string.begin.prolog',
           regex: '\'',
           push: 
            [ { token: 'punctuation.definition.string.end.prolog',
                regex: '\'',
                next: 'pop' },
              { token: 'constant.character.escape.prolog', regex: '\\\\.' },
              { token: 'constant.character.escape.quote.prolog',
                regex: '\'\'' },
              { defaultToken: 'string.quoted.single.prolog' } ] } ],
      '#variable': 
       [ { token: 'variable.language.anonymous.prolog',
           regex: '\\b_\\b' },
         { token: 'variable.other.prolog',
           regex: '\\b[A-Z_][a-zA-Z0-9_]*\\b' } ] }
    
    this.normalizeRules();
};

PrologHighlightRules.metaData = { fileTypes: [ 'plg', 'prolog' ],
      foldingStartMarker: '(%\\s*region \\w*)|([a-z]\\w*.*:- ?)',
      foldingStopMarker: '(%\\s*end(\\s*region)?)|(?=\\.)',
      keyEquivalent: '^~P',
      name: 'Prolog',
      scopeName: 'source.prolog' }


oop.inherits(PrologHighlightRules, TextHighlightRules);

exports.PrologHighlightRules = PrologHighlightRules;
});

define('ace/mode/folding/cstyle', ['require', 'exports', 'module' , 'ace/lib/oop', 'ace/range', 'ace/mode/folding/fold_mode'], function(require, exports, module) {


var oop = require("../../lib/oop");
var Range = require("../../range").Range;
var BaseFoldMode = require("./fold_mode").FoldMode;

var FoldMode = exports.FoldMode = function(commentRegex) {
    if (commentRegex) {
        this.foldingStartMarker = new RegExp(
            this.foldingStartMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.start)
        );
        this.foldingStopMarker = new RegExp(
            this.foldingStopMarker.source.replace(/\|[^|]*?$/, "|" + commentRegex.end)
        );
    }
};
oop.inherits(FoldMode, BaseFoldMode);

(function() {

    this.foldingStartMarker = /(\{|\[)[^\}\]]*$|^\s*(\/\*)/;
    this.foldingStopMarker = /^[^\[\{]*(\}|\])|^[\s\*]*(\*\/)/;

    this.getFoldWidgetRange = function(session, foldStyle, row) {
        var line = session.getLine(row);
        var match = line.match(this.foldingStartMarker);
        if (match) {
            var i = match.index;

            if (match[1])
                return this.openingBracketBlock(session, match[1], row, i);

            return session.getCommentFoldRange(row, i + match[0].length, 1);
        }

        if (foldStyle !== "markbeginend")
            return;

        var match = line.match(this.foldingStopMarker);
        if (match) {
            var i = match.index + match[0].length;

            if (match[1])
                return this.closingBracketBlock(session, match[1], row, i);

            return session.getCommentFoldRange(row, i, -1);
        }
    };

}).call(FoldMode.prototype);

});
