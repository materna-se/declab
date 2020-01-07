<template>
	<div>
		<div class="row">
			<div class="col-6 mb-4">
				<h3 class="mb-2">Expression</h3>
				<div class="card">
					<div class="card-body">
						<feel-editor v-on:update:value="expression = $event; getRawResult();"/>
					</div>
				</div>
			</div>
			<div class="col-6 mb-4">
				<h3 class="mb-2">Output</h3>
				<div class="row mb-4" v-if="alert.message !== null">
					<div class="col-12">
						<alert v-bind:alert="alert"/>
					</div>
				</div>
				<div class="card">
					<div class="card-body">
						<json-builder v-bind:template="model.output.outputs" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 mb-4">
				<h3 class="mb-2">Context</h3>
				<div class="card">
					<div class="card-body">
						<json-builder v-bind:template="model.input.template" v-on:update:value="model.input.value = $event; getRawResult();"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import Converter from "../components/json/json-builder-converter";

	import FEELEditor from "../components/dmn/dmn-editor.vue";
	import JSONBuilder from "../components/json/json-builder.vue";
	import Alert from "../components/alert/alert.vue";
	import AlertHelper from "../components/alert/alert-helper";

	export default {
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"feel-editor": FEELEditor,
		},
		mounted() {
			this.getRawResult();
		},
		data() {
			return {
				alert: {
					message: null,
					state: null
				},

				model: {
					input: {
						value: {},
						template: Converter.enrich({}),
					},
					output: {
						outputs: null,
						messages: []
					},
				},

				expression: null
			}
		},
		methods: {
			//
			// Model
			//
			async getRawResult() {
				const response = await Network.getRawResult(this.expression, this.model.input.value);
				if (response.status !== 200) {
					this.model.output.outputs = null;
					this.displayAlert("The output can't be calculated.", "danger");
					return;
				}

				const result = await response.json();

				this.model.output.outputs = result.outputs.main.value;
				if (result.messages.length > 0) {
					this.displayAlert(AlertHelper.buildList("The output was calculated, but the following warnings have occurred:", result.messages), "warning");
					return;
				}
				this.displayAlert(null, null);
			},

			//
			// Helpers
			//
			displayAlert(message, state) {
				this.alert = {
					message: message,
					state: state
				}
			},
		}
	};
</script>
