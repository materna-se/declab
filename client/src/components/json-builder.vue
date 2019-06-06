<template>
	<json-builder-table v-bind:value="values" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot"></json-builder-table>
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
			// Does not allow the addition of new key value pairs to the json
			fixed: {
				default: false
			},
			// Does not allow a different root type
			fixedRoot: {
				default: false
			},
		},
		data: function () {
			return {
				values: this.enrichTemplate(this.template)
			}
		},
		watch: {
			template: function (template) {
				this.values = this.enrichTemplate(template);
			},
			values: {
				handler: function (values) {
					const cleanedObject = Converter.clean(values);
					this.$emit('update:values', cleanedObject === undefined ? {} : cleanedObject)
				},
				deep: true
			}
		},
		methods: {
			enrichTemplate: function (template) {
				if (template === null) {
					return Converter.enrich({});
				}
				if (this.convert) {
					return Converter.enrich(template);
				}

				return template;
			}
		}
	};
</script>