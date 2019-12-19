<template>
	<div class="container-fluid">
		<div class="row">
			<div class="mb-4" v-if="mode !== 2" v-bind:class="{'col-6': mode === 0, 'col-12': mode === 1}">
				<div class="row mb-2">
					<div class="col-6">
						<h3 class="mb-0">Input</h3>
					</div>
					<div class="col-6">
						<div class="input-group">
							<div class="input-group-prepend">
								<span class="input-group-text">Template</span>
							</div>
							<select class="form-control" v-on:change="importInput(inputs[$event.target.value].value)">
								<option selected disabled>Select Template...</option>
								<option v-for="(input, key) in inputs" v-bind:value="key">{{input.name}}</option>
							</select>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<div class="card card-borderless mb-2">
							<div class="card-body p-0">
								<json-builder v-bind:template="model.input.template" v-bind:fixed="true" v-on:update:value="model.input.value = $event; getModelResult();"/>
							</div>
							<div class="card-footer card-footer-border">
								<div class="input-group">
									<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.input.name">
									<div class="input-group-append">
										<button class="btn btn-outline-secondary" v-on:click="addInput">Save Input</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="mb-4" v-if="mode !== 1" v-bind:class="{'col-6': mode === 0, 'col-12': mode === 2}">
				<div class="row">
					<div class="col-10">
						<h3 class="mb-2">Output</h3>
					</div>
					<div class="col-2">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="detachWorker">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
							</svg>
						</button>
					</div>
				</div>
				<div class="row mb-4" v-if="alert.message !== null">
					<div class="col-12">
						<alert v-bind:alert="alert"/>
					</div>
				</div>
				<div class="card mb-2" v-for="(output, key) in model.result.outputs">
					<div class="card-header">
						<div class="row">
							<div class="col-10">
								<h4 class="mb-0">{{key}}</h4>
							</div>
							<div class="col-2">
								<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" class="d-block float-right" style="cursor: pointer" v-on:click="$set(model.result.visible, key, model.result.visible[key] !== true)">
									<path d="M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6 1.41 1.41z" fill="currentColor" v-if="model.result.visible[key]"/>
									<path d="M7.41 8.58L12 13.17l4.59-4.59L18 10l-6 6-6-6 1.41-1.42z" fill="currentColor" v-else/>
								</svg>
							</div>
						</div>
					</div>
					<template v-if="model.result.visible[key]">
						<div class="card-body">
							<h5 class="mb-2">Output</h5>
							<json-builder class="mb-0" v-bind:template="output.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

							<div class="mt-4" v-if="Object.keys(model.result.context[key]).length !== 0">
								<h5 class="mb-2">Context</h5>
								<json-builder class="mb-0" v-bind:template="model.result.context[key]" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
							</div>
						</div>
						<div class="card-footer">
							<div class="input-group">
								<input type="text" class="form-control" placeholder="Enter Name..." v-model="output.name">
								<div class="input-group-append">
									<button class="btn btn-outline-secondary" v-on:click="addOutput(key, output)">Save Output</button>
								</div>
							</div>
						</div>
					</template>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import AlertHelper from "../components/alert-helper";
	import Converter from "../components/json/json-builder-converter";

	import Alert from "../components/alert.vue";
	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
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
						value: {},
						template: Converter.enrich({})
					},

					result: {
						outputs: {},
						context: {},
						visible: {}
					}
				},

				// 0: Both
				// 1: Input
				// 2: Output
				mode: 0,
				worker: null
			}
		},
		async mounted() {
			const vue = this;

			await this.getModelInputs();
			await this.getInputs();

			// If window.opener is not null, we are the worker attached to the main window.
			if (window.opener !== undefined && window.opener !== null) { // Workaround because Microsoft Edge is not compliant (window.opener is undefined).
				// Receive all messages that are sent by the main window.
				window.addEventListener("message", function (e) {
					const data = e.data;
					// We need to ignore messages from third parties (webpack, ...).
					if (data.type === undefined) {
						return;
					}

					switch (data.type) {
						// getModelResult will be received when the inputs are updated.
						case "getModelResult": {
							vue.model.input.value = data.data;
							vue.getModelResult();
						}
					}
				});
				// Listen to attempts to unload the window and switch back to mode 0.
				window.opener.addEventListener("unload", function () {
					window.close();
				});

				this.mode = 2;

				// After mounting vue, we fire an event so that the opener can send the current inputs.
				window.dispatchEvent(new Event('aftermount'));
			}
		},
		methods: {
			//
			// Model
			//
			async getModelInputs() {
				this.model.input.template = await Network.getModelInputs();
			},
			async getModelResult() {
				// If we have a detached worker, we don't want to calculate the results here.
				if (this.mode === 1) {
					this.worker.postMessage({
						type: "getModelResult",
						data: this.model.input.value
					}, "*");
					return;
				}

				try {
					const result = await Network.getModelResult(this.model.input.value);
					this.model.result.outputs = result.outputs;
					this.model.result.context = result.context;
					if (result.messages.length > 0) {
						this.displayAlert(AlertHelper.buildList("The output was calculated, but the following warnings have occurred:", result.messages), "warning");
						return;
					}

					this.displayAlert(null, null);
				}
				catch (e) {
					this.displayAlert("The output can't be calculated right now.", "danger");
				}
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

				this.$root.displayAlert("The input was successfully saved.", "success");
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

				this.$root.displayAlert("The output was successfully saved.", "success");
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
			detachWorker() {
				const vue = this;

				// We need to detach the output into another window.
				this.worker = window.open(location.href);
				// This custom event will be received when vue has finished mounting.
				this.worker.addEventListener("aftermount", function () {
					vue.mode = 1;
					vue.displayAlert(null, null);
					vue.getModelResult();
				});
				// This event will be received when the opened window is closed or the user navigates away.
				this.worker.addEventListener("beforeunload", function () {
					vue.mode = 0;
					vue.getModelResult();
				});
			},
			importInput(input) {
				const currentInput = JSON.parse(JSON.stringify(this.model.input.template));
				const templateInput = Converter.enrich(input);
				this.model.input.template = Converter.merge(currentInput, templateInput);
			},
		}
	};
</script>

<style scoped>
	.card-header-border {
		border: 1px solid #dee2e6;
		border-bottom: none;
	}

	.card-footer-border {
		border: 1px solid #dee2e6;
		border-top: none;
	}

	.card-borderless {
		border: none;
	}
</style>
