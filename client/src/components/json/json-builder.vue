<template>
	<json-builder-table v-bind:value="values" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot" v-bind:fixed-values="fixedValues" v-if="values !== null"></json-builder-table>
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
				values: null
			}
		},
		mounted() {
			this.values = this.enrichTemplate(this.template);
			this.returnValue(this.values);
		},
		watch: {
			template: function (template) {
				this.values = this.enrichTemplate(template);
				this.returnValue(this.values);
			},
			values: {
				handler: function (values) {
					this.returnValue(values);
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
			returnValue(value) {
				const cleanedObject = Converter.clean(value);
				this.$emit('update:value', cleanedObject === undefined ? {} : cleanedObject)
			}
		}
	};
</script>