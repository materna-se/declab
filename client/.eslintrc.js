module.exports = {
	extends: [
		// add more generic rulesets here, such as:
		// 'eslint:recommended',
		'plugin:vue/recommended' // Use this if you are using Vue.js 2.x.
	],
	rules: {
		"vue/html-indent": ["error", "tab", {
			"attribute": 1,
			"baseIndent": 1,
			"closeBracket": 0,
			"alignAttributesVertically": true,
			"ignores": []
		}],
		"vue/mustache-interpolation-spacing": ["error", "never"],
		"vue/max-attributes-per-line": ["off"],
		"vue/html-closing-bracket-spacing": ["error", {
			"startTag": "never",
			"endTag": "never",
			"selfClosingTag": "never"
		}],
		"vue/attributes-order": ["off"],
		"vue/singleline-html-element-content-newline": ["off"],
		"vue/multi-word-component-names": ["off"],
		"vue/v-on-style": ["error", "longform"],
		"vue/v-bind-style": ["error", "longform"],
		"vue/html-self-closing": ["off"],
		"vue/component-definition-name-casing": ["error", "kebab-case"],
		"vue/order-in-components": ["off"],
		"vue/component-tags-order": ["off"],
	}
}