<template>
	<div>
		<textarea class="form-control w-100 mb-1" v-if="developerMode === true" v-bind:value="JSON.stringify(cleanedValue)" v-on:input="importValue($event.target.value)"></textarea>
		<json-builder-table v-if="value !== null" v-bind:value="value" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot" v-bind:fixed-values="fixedValues"></json-builder-table>
		<json-builder-selector v-if="!fixed && !fixedRoot" v-bind:value="value" v-bind:mode="'edit'"></json-builder-selector>
	</div>
</template>

<script>
	import Configuration from "../../helpers/configuration";

	import Converter from "./json-builder-converter";
	import JSONBuilderTable from "./json-builder-table.vue";
	import JSONBuilderSelector from "./json-builder-selector.vue";

	export default {
		components: {
			"json-builder-table": JSONBuilderTable,
			"json-builder-selector": JSONBuilderSelector
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
				developerMode: Configuration.getDeveloperMode(),

				value: null,
				cleanedValue: null,
			}
		},
		mounted() {
			this.value = this.convert ? this.enrichTemplate(this.template) : this.template;
			this.exportValue(this.value);
		},
		watch: {
			template: {
				handler: function (template) {
					this.value = this.convert ? this.enrichTemplate(template) : template;
					this.exportValue(this.value);
				},
				deep: true
			},
			value: {
				handler: function (value) {
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
				this.value = Converter.merge(this.value, this.enrichTemplate(JSON.parse(value)));
				this.exportValue(this.value);
			},
			exportValue(value) {
				this.cleanedValue = this.cleanValue(value);
				this.$emit('update:value', this.cleanedValue);
			}
		}
	};
</script>