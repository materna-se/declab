<template>
	<div class="container-fluid">
		<div class="row mb-2" v-if="alert.message !== null">
			<div class="col-12">
				<alert v-bind:alert="alert"></alert>
			</div>
		</div>
		<div class="row">
			<div class="col-8 mb-4">
				<div class="row">
					<div class="col-6 mb-2">
						<h3 class="mb-0">Input</h3>
					</div>
					<div class="col-6 mb-2">
						<div class="input-group">
							<div class="input-group-prepend">
								<span class="input-group-text">Template</span>
							</div>
							<select class="form-control" v-on:change="importInput(inputs[$event.target.value].value)">
								<option selected disabled>Select Template...</option>
								<option v-for="(input, key) in inputs" v-bind:value="key">{{input.name}}</option>
							</select>
						</div>
						<div class="input-group">
							<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.input.name">
							<div class="input-group-append">
								<button class="btn btn-outline-secondary" v-on:click="addInput">Save Input</button>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12 mb-4">
						<json-builder v-bind:template="model.template" v-bind:fixed="true" v-on:update:values="model.input.value = $event; getModelOutputs();"></json-builder>
					</div>
				</div>
			</div>
			<div class="col-4 mb-4">
				<h3 class="mb-2">Outputs</h3>
				<div class="card mb-2" v-for="(output, key) in model.outputs">
					<div class="card-header">
						<h4 class="mb-0">{{key}}</h4>
					</div>
					<div class="card-body">
						<pre class="mb-0"><code>{{output.value}}</code></pre>
					</div>
					<div class="card-footer">
						<div class="input-group">
							<input type="text" class="form-control" placeholder="Enter Name..." v-model="output.name">
							<div class="input-group-append">
								<button class="btn btn-outline-secondary" v-on:click="addOutput(key, output)">Save Output</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import Converter from "../components/json-builder-converter";

	import Alert from "../components/alert.vue";
	import JSONBuilder from "../components/json-builder.vue";

	export default {
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder
		},
		async mounted() {
			await this.getModelInputs();
			await this.getInputs();
		},
		data() {
			return {
				alert: {
					message: null,
					state: null
				},

				inputs: {},

				model: {
					input: {
						name: null,
						value: {}
					},
					outputs: {},

					template: {},
				}
			}
		},
		methods: {
			//
			// Model
			//
			async getModelInputs() {
				this.model.template = await Network.getModelInputs();
			},
			async getModelOutputs() {
				this.model.outputs = await Network.getModelOutputs(this.model.input.value);
			},

			//
			// Inputs
			//
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},
			async addInput() {
				await Network.addInput({
					name: this.model.input.name,
					value: this.model.input.value
				});
				await this.getInputs();
				this.alert = {
					message: "The input was successfully saved.",
					state: "success"
				};
			},

			//
			// Outputs
			//
			async addOutput(decision, output) {
				await Network.addOutput({
					name: output.name,
					decision: decision,
					value: output.value
				});
				this.alert = {
					message: "The output was successfully saved.",
					state: "success"
				};
			},

			//
			// Helpers
			//
			importInput: function (input) {
				const currentInput = JSON.parse(JSON.stringify(this.model.template));
				const templateInput = Converter.enrich(input);
				Converter.merge(currentInput, templateInput);

				this.model.template = currentInput;
			}
		}
	};
</script>

<style>
</style>
