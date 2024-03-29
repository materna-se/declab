<template>
	<div :name="this.elementName" id="monaco-container"></div>
</template>

<style>
	#monaco-container,
	.monaco-editor {
		width: 100%;
		height: 500px;
	}
</style>

<script>
	import * as monaco from 'monaco-editor'

	export default {
		props: {
			value: {
				default: null
			},
			readonly: {
				default: false
			},
			elementName: {
				default: "literal-expression-main"
			}
		},
		data() {
			return {
				editor: null,
			}
		},
		watch: {
			value: {
				handler: function (value) {
					if (value === this.editor.getValue()) {
						return;
					}

					//The editor silently does nothing if you try to set its value to null
					if(value === null) {
						value = "";
					}

					this.editor.setValue(value);
				},
				deep: true
			},
		},
		mounted() {
			const vue = this;

			// Initialize language
			monaco.languages.register({id: 'feel-language'});
			monaco.languages.setMonarchTokensProvider('feel-language', {
				tokenizer: {
					root: [
						[/\/\/(.*)/, "feel-comment"],
						[/(?:true|false)/, "feel-boolean"],
						[/(?:[0-9]+)/, "feel-numeric"],
						[/(?:"(?:.*?)")/, "feel-string"],
						[/(?:(?:[a-z ]+\()|(?:\()|(?:\)))/, "feel-function"],
						[/(?:if|then|else)/, "feel-keyword"],
						[/(?:for|in|return)/, "feel-keyword"],
					],
				}
			});
			monaco.languages.registerCompletionItemProvider('feel-language', {
				provideCompletionItems: function () {
					const suggestions = [];

					const suggestionTypes = {
						Snippet: [
							["if", "if $1 then\n\t$0\nelse\n\t"],
							["for", "for element in $1 return\n\t$0"],
						],
						Function: [
							// Boolean
							["not(negand)", "not($1)"],

							// String
							["substring(string, start position, length?)", "substring($1, $2, $3)"],
							["string length(string)", "string length($1)"],
							["upper case(string)", "upper case($1)"],
							["lower case(string)", "lower case($1)"],
							["substring before(string, match)", "substring before($1, $2)"],
							["substring after(string, match)", "substring after($1, $2)"],
							["replace(input, pattern, replacement, flags?)", "replace($1, $2, $3, $4)"],
							["contains(string, match)", "contains($1, $2)"],
							["starts with(string, match)", "starts with($1, $2)"],
							["ends with(string, match)", "ends with($1, $2)"],
							["matches(input, pattern, flags?)", "matches($1, $2, $3)"],
							["split(string, delimiter)", "split($1, $2)"],
							["string join(list, delimiter) ", "string join($1, $2)"],
							["string join(list)", "string join($1)"],

							// List
							["list contains(list, element)", "list contains($1, $2)"],
							["count(list)", "count($1)"],
							["min(list)", "min($1)"],
							["max(list)", "max($1)"],
							["sum(list)", "sum($1)"],
							["mean(list)", "mean($1)"],
							["all(list)", "all($1)"],
							["any(list)", "any($1)"],
							["sublist(list, start position, length?)", "sublist($1, $2, $3)"],
							["append(list, item...)", "append($1, $2)"],
							["concatenate(list...)", "concatenate($1)"],
							["insert before(list, position, newItem)", "insert before($1, $2, $3)"],
							["remove(list, position)", "remove($1, $2)"],
							["reverse(list)", "remove($1)"],
							["index of(list, match)", "index of($1, $2)"],
							["union(list...)", "union($1)"],
							["distinct values(list)", "distinct values($1)"],
							["flatten(list)", "flatten($1)"],
							["product(list)", "product($1)"],
							["median(list)", "median($1)"],
							["stddev(list)", "stddev($1)"],
							["mode(list)", "mode($1)"],

							// Number
							["decimal(n, scale)", "decimal($1, $2)"],
							["floor(n)", "floor($1)"],
							["ceiling(n)", "ceiling($1)"],
							["round up(n, scale)", "round up($1, $2)"],
							["round down(n, scale)", "round down($1, $2)"],
							["round half up(n, scale)", "round half up($1, $2)"],
							["round half down(n, scale)", "round half down($1, $2)"],
							["abs(n)", "abs($1)"],
							["modulo(dividend, divisor)", "modulo($1, $2)"],
							["sqrt(number)", "sqrt($1)"],
							["log(number)", "log($1)"],
							["exp(number)", "exp($1)"],
							["odd(number)", "odd($1)"],
							["even(number)", "even($1)"],

							// Date and Time
							["is(first, second)", "is($1, $2)"],

							// Range
							["before(first, second)", "before($1, $2)"],
							["after(first, second)", "after($1, $2)"],
							["meets(first, second)", "meets($1, $2)"],
							["met by(first, second)", "met by($1, $2)"],
							["overlaps(first, second)", "overlaps($1, $2)"],
							["overlaps before(first, second)", "overlaps before($1, $2)"],
							["overlaps after(first, second)", "overlaps after($1, $2)"],
							["finishes(first, second)", "finishes($1, $2)"],
							["finished by(first, second)", "finished by($1, $2)"],
							["includes(first, second)", "includes($1, $2)"],
							["during(first, second)", "during($1, $2)"],
							["starts(first, second)", "starts($1, $2)"],
							["started by(first, second)", "started by($1, $2)"],
							["coincides(first, second)", "coincides($1, $2)"],

							// Temporal
							["day of year(date)", "day of year($1)"],
							["day of week(date)", "day of week($1)"],
							["month of year(date)", "month of year($1)"],
							["week of year(date)", "week of year($1)"],

							// Sort
							["sort(list, precedes)", "sort(list: $1, precedes: function($2) $3)"],

							// Context
							["get value(m, key)", "get value($1, $2)"],
							["get entries(m)", "get entries($1)"],
							["context(entries)", "context($1)"],
							["context put(context, key, value)", "context put($1, $2, $3)"],
							["context merge(first, second)", "context merge($1, $2)"],

							// Miscellaneous
							["now()", "now()"],
							["today()", "today()"],
						]
					};
					for (const key in suggestionTypes) {
						for (const suggestion of suggestionTypes[key]) {
							suggestions.push({
								kind: monaco.languages.CompletionItemKind[key],
								insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
								label: suggestion[0],
								insertText: suggestion[1]
							});
						}
					}

					return {
						suggestions: suggestions
					};
				}
			});

			// Initialize theme
			monaco.editor.defineTheme('feel-theme', {
				base: 'vs',
				inherit: false, // We don't want to inherit rules
				rules: [
					{token: 'feel-keyword', foreground: 'ec5b69', fontStyle: 'bold'}, // Lighter foreground color to counteract the bold font
					{token: 'feel-numeric', foreground: '005cc5'},
					{token: 'feel-boolean', foreground: 'd73a49'},
					{token: 'feel-string', foreground: '22863a'},
					{token: 'feel-function', foreground: '6f42c1'},
					{token: 'feel-comment', foreground: 'a7aab6'},
				],
				colors: {
					'editorLineNumber.foreground': '#000000',
				}
			});

			// Use getElementsByName to allow multiple feel-editors per page
			const editor = monaco.editor.create(document.getElementsByName(this.elementName)[0], {
				language: 'feel-language',
				theme: 'feel-theme',

				fontSize: 15,
				contextmenu: false,

				lineNumbersMinChars: 1,

				overviewRulerBorder: false,
				scrollBeyondLastLine: false,
				scrollbar: {
					useShadows: false
				},

				minimap: {
					enabled: false
				},

				value: this.value,
				readOnly: this.readonly
			});
			this.editor = editor;

			let previousValue = "";

			editor.onKeyUp(function () {
				const value = editor.getValue();
				if(value === previousValue) {
					return;
				}
				previousValue = value;
				vue.$emit('input', value);
			});
		}
	}
</script>
