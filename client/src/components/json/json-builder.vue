<template>
	<json-builder-table v-bind:value="value" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot" v-bind:fixed-values="fixedValues" v-if="value !== null"></json-builder-table>
</template>

<script>
	import Converter from "./json-builder-converter";

	import JSONBuilderTable from "./json-builder-table.vue";

	export default {
		components: {
			"json-builder-table": JSONBuilderTable
		},
		props: {
			// Template, read-only
			template: {},
			// Converts normal json into the used complex structure
			convert: {
				default: false
			},
			// Does not allow the structure to be changed
			fixed: {
				default: false
			},
			// Does not allow the values to be changed
			fixedValues: {
				default: false
			},
			// Does not allow a different root type
			fixedRoot: {
				default: false
			},
		},
		data: function () {
			return {
				value: null
			}
		},
		mounted() {
			this.value = this.enrichTemplate(this.template);
			this.returnValue(this.value);
		},
		watch: {
			template: function (template) {
				this.value = this.enrichTemplate(template);
				this.returnValue(this.value);
			},
			value: {
				handler: function (value) {
					this.returnValue(value);
				},
				deep: true
			}
		},
		methods: {
			enrichTemplate(template) {
				if (this.convert) {
					return Converter.enrich(template);
				}

				return template;
			},
			cleanValue(value) {
				const cleanedObject = Converter.clean(value);
				return cleanedObject === undefined ? {} : cleanedObject;
			},
			returnValue(value) {
				/**
				 * Template muss irgendwie über <textarea> verändert werden...
				 */
				this.$emit('update:value', this.cleanValue(value));
			}
		}
	};
</script>