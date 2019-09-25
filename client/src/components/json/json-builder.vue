<template>
	<div>
		<textarea class="form-control w-100 mb-2" v-bind:value="JSON.stringify(cleanedValue)" v-on:input="importValue($event.target.value)"></textarea>
		<json-builder-table v-bind:value="value" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot" v-bind:fixed-values="fixedValues" v-if="value !== null"></json-builder-table>
	</div>
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
				value: null,
				cleanedValue: null,
			}
		},
		mounted() {
			this.value = this.convert ? this.enrichTemplate(this.template) : this.template;
			this.exportValue(this.value);
		},
		watch: {
			template: function (template) {
				this.value = this.convert ? this.enrichTemplate(template) : template;
				this.exportValue(this.value);
			},
			value: {
				handler: function (value) {
					console.info("handler", value);
					this.exportValue(value);
				},
				deep: true
			}
		},
		methods: {
			enrichTemplate(template) {
				return Converter.enrich(template);
			},
			cleanValue(value) {
				const cleanedObject = Converter.clean(value);
				return cleanedObject === undefined ? {} : cleanedObject;
			},
			importValue(value) {
				this.value = this.enrichTemplate(JSON.parse(value));
				this.exportValue(this.value);
			},
			exportValue(value) {
				this.cleanedValue = this.cleanValue(value);
				this.$emit('update:value', this.cleanedValue);
			}
		}
	};
</script>