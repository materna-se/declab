<template>
	<div>
		<template v-if="developerMode === true">
			<textarea class="form-control w-100 mb-1" v-model="editedValue"/>
			<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="applyValue">Apply</button>
		</template>
		<json-builder-table v-if="value !== null" v-bind:value="value" v-bind:root="true" v-bind:fixed="fixed" v-bind:fixed-root="fixedRoot" v-bind:fixed-values="fixedValues"/>
		<json-builder-selector v-if="!fixed && !fixedRoot" v-bind:value="value" v-bind:mode="'edit'"/>
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
			// Converts a plain JSON object into the needed complex structure
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
				editedValue: null,

				value: null,
				cleanedValue: null,
			}
		},
		mounted() {
			this.value = this.importValue(this.template);
		},
		watch: {
			template: {
				handler: function (template) {
					this.value = this.importValue(template);
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
				if(this.convert) {
					return this.enrichTemplate(value);
				}

				if(value.type === "object") {
					return JSON.parse(JSON.stringify(value));
				}

				return {type: "object", value: JSON.parse(JSON.stringify(value))};
			},
			exportValue(value) {
				this.cleanedValue = this.cleanValue(value);
				this.$emit('update:value', this.cleanedValue);

				this.editedValue = JSON.stringify(this.cleanedValue);
			},
			applyValue() {
				this.value = this.enrichTemplate(JSON.parse(this.editedValue));
				this.exportValue(this.value);
			}
		}
	};
</script>