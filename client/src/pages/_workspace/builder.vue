<template>
	<div class="container-fluid">
		<div class="d-flex align-items-center mb-2">
			<h3 class="mb-0 mr-auto">Builder</h3>
			<div class="mr-2">
				<button class="btn btn-block btn-outline-secondary px-4" v-on:click="modalVisible = true">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M15 9H5V5h10m-3 14a3 3 0 01-3-3 3 3 0 013-3 3 3 0 013 3 3 3 0 01-3 3m5-16H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V7l-4-4z" fill="currentColor"/>
					</svg>
				</button>
			</div>
			<div>
				<button class="btn btn-block btn-outline-secondary px-4" v-on:click="detachWorker">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="d-flex">
			<div class="mb-4" v-if="mode !== 2" style="flex: 1" v-bind:style="{'margin-right': mode === 0 ? '30px' : null}">
				<div class="d-flex align-items-center mb-2">
					<h4 class="mb-0 mr-auto">Input Context</h4>
					<div>
						<div class="input-group">
							<div class="input-group-prepend">
								<span class="input-group-text">Template</span>
							</div>
							<select class="form-control" v-on:change="importInput(inputs[$event.target.value].value)">
								<option selected disabled>Select template...</option>
								<option v-for="(input, key) in inputs" v-bind:value="key">{{input.name}}</option>
							</select>
						</div>
					</div>
				</div>
				<div class="card card-borderless mb-2">
					<div class="card-body p-0" style="overflow-x: auto">
						<json-builder v-bind:template="model.input.template" v-bind:fixed="true" v-bind:convert-after="false" v-on:update:value="model.input.value = $event; executeModel();" class="input-builder"/>
					</div>
				</div>
				<div class="card card-borderless mb-2">
					<div class="card-body p-0" style="overflow-x: auto">
						<json-builder v-bind:template="model.decisions.template" v-bind:fixed="true" v-bind:convert-after="false" v-on:update:value="model.decisions.value = $event; executeModel();" class="decision-builder"/>
					</div>
				</div>
			</div>
			<div class="mb-4" v-if="mode !== 1" style="flex: 1">
				<h4 class="mb-2">Output Context</h4>
				<div class="row mb-2" v-if="alert.message !== null">
					<div class="col-12">
						<alert v-bind:alert="alert"/>
					</div>
				</div>

				<div class="card mb-2" v-for="(output, key) in model.result.outputs">
					<div class="card-header" v-on:click="$set(model.result.visibleOutputs, key, model.result.visibleOutputs[key] !== true)">
						<h5 class="mb-0">{{key}}&ensp;<small>({{model.result.visibleOutputs[key] ? 'click to hide' : 'click to show'}})</small></h5>
					</div>
					<div class="card-body" v-if="model.result.visibleOutputs[key]">
						<h5 class="mb-2">Output</h5>
						<json-builder class="mb-0" v-bind:template="output" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

						<div class="mt-4" v-if="model.result.context[key] !== undefined && Object.keys(model.result.context[key]).length !== 0">
							<h5 class="mb-0" v-on:click="$set(model.result.visibleContexts, key, model.result.visibleContexts[key] !== true)">Context<small>&ensp;({{model.result.visibleContexts[key] ? 'click to hide' : 'click to show'}})</small></h5>
							<json-builder class="mt-2" v-if="model.result.visibleContexts[key]" v-bind:template="model.result.context[key]" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
						</div>
					</div>
				</div>

				<h4 class="mb-2 mt-3" v-on:click="accessLogVisible = !accessLogVisible">Access Log<small>&ensp;({{accessLogVisible ? 'click to hide' : 'click to show'}})</small></h4>
				<access-log v-if="accessLogVisible" v-bind:entries="model.result.accessLog"></access-log>
			</div>
		</div>

		<template v-if="modalVisible">
			<div class="modal-backdrop fade show"></div>
			<div class="modal fade show" style="display: block" v-on:click.self="modalVisible = false">
				<div class="modal-dialog modal-dialog-centered modal-lg">
					<div class="modal-content p-4">
						<div class="modal-body">
							<h4 class="mb-4">Save Entities</h4>
							<div class="mb-4" v-bind:class="[!hasTestName() ? 'input-disabled' : null]">
								<h5 class="mb-2">Test</h5>
								<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.test.name">
							</div>
							<div class="mb-4" v-bind:class="[!hasInputName() ? 'input-disabled' : null]">
								<h5 class="mb-2">Input</h5>
								<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.input.name">
							</div>
							<template v-if="Object.keys(model.result.outputs).length > 0">
								<h5 class="mt-4 mb-2">Outputs</h5>
								<div class="row">
									<div class="col-6 mb-2" v-for="(output, key) in model.result.outputs" v-bind:class="[!hasOutputName(key) ? 'input-disabled' : null]">
										<h6 class="mb-2">{{key}}</h6>
										<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.result.name[key]">
									</div>
								</div>
							</template>
							<button class="btn btn-outline-secondary mt-4 w-100" v-on:click="saveEntities">Save Entities</button>
						</div>
					</div>
				</div>
			</div>
		</template>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import AlertHelper from "../../components/alert/alert-helper";
	import Converter from "../../components/json/json-builder-converter";

	import Alert from "../../components/alert/alert.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import AccessLogComponent from "../../components/dmn/access-log.vue";

	export default {
		head() {
			return {
				title: "declab - Builder",
			}
		},
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"access-log": AccessLogComponent,
		},
		data() {
			return {
				alert: {
					message: null,
					state: null
				},

				modalVisible: false,

				accessLogVisible: false,

				inputs: {},

				model: {
					test: {
						name: null
					},

					input: {
						name: null,
						value: Converter.enrich({}),
						template: Converter.enrich({})
					},

					decisions: {
						value: Converter.enrich({}),
						template: Converter.enrich({})
					},

					result: {
						outputs: {},
						visibleOutputs: {},
						context: {},
						visibleContexts: {},
						accessLog: {},
						name: {}
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
						// executeModel will be received when the inputs are updated.
						case "executeModel": {
							vue.model.input.value = data.data;
							vue.executeModel();
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

			Network.addSocketListener(this.onSocket);
		},
		beforeDestroy() {
			Network.removeSocketListener(this.onSocket);
		},
		methods: {
			async onSocket(e) {
				const data = JSON.parse(e.data);
				if(data.type === "imported") {
					const modelInputs = await Network.getModelInputs();

					this.model.input.template = Converter.merge(this.model.input.value, {type: "object", value: modelInputs.inputs});
					this.model.decisions.template = Converter.merge(this.model.decisions.value, {type: "object", value: modelInputs.decisions});
				}
			},
			//
			// Model
			//
			async getModelInputs() {
				const modelInputs = await Network.getModelInputs();
				this.model.input.template = modelInputs.inputs;
				this.model.decisions.template = modelInputs.decisions;
			},
			async executeModel() {
				// If we have a detached worker, we don't want to calculate the results here.
				if (this.mode === 1) {
					this.worker.postMessage({
						type: "executeModel",
						data: this.model.input.value
					}, "*");
					return;
				}

				try {
					const input = Converter.clean(Converter.merge(this.model.input.value, this.model.decisions.value));
					const result = await Network.executeModel(input === undefined ? {} : input);
					this.model.result.outputs = result.outputs;
					this.model.result.context = result.context;
					this.model.result.accessLog = result.accessLog;
					if (result.messages.length > 0) {
						this.displayAlert(AlertHelper.buildList("The output was calculated, the following messages were returned:", result.messages), "warning");
						return;
					}

					this.displayAlert(null, null);
				}
				catch (e) {
					this.displayAlert("The output could not be calculated.", "danger");
				}
			},

			//
			// Inputs
			//
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},
			async addInput() {
				const uuid = await Network.addInput({
					name: this.model.input.name,
					value: Converter.clean(this.model.input.value)
				});
				await this.getInputs();
				return uuid;
			},

			//
			// Outputs
			//
			async addOutput(decision) {
				return await Network.addOutput({
					name: this.model.result.name[decision],
					decision: decision,
					value: this.model.result.outputs[decision]
				});
			},

			//
			// Outputs
			//
			async addTest(input, outputs) {
				return await Network.addTest({
					name: this.model.test.name,
					input: input,
					outputs: outputs
				});
			},


			//
			// Helpers
			//
			async saveEntities() {
				if (this.hasTestName() && !this.hasInputName()) {
					this.$store.commit("displayAlert", {
						message: "You need to enter an input name if you want to save a test.",
						state: "danger"
					});
					return;
				}

				let input = null;
				if (this.hasInputName()) {
					input = await this.addInput();
				}
				const outputs = [];
				for (const decision in this.model.result.outputs) {
					if (this.hasOutputName(decision)) {
						outputs.push(await this.addOutput(decision));
					}
				}

				let test = null;
				if (this.hasTestName()) {
					test = await this.addTest(input, outputs);
				}

				this.$store.commit("displayAlert", {
					message: ((input !== null ? 1 : 0) + " " + (input !== null ? "input" : "inputs") + ", " + outputs.length + " " + (outputs.length === 1 ? "output" : "outputs") + " and " + (test !== null ? 1 : 0) + " " + (test !== null ? "test" : "tests") + " were successfully saved."),
					state: "success"
				});
			},
			hasInputName() {
				return this.model.input.name !== null && this.model.input.name !== '';
			},
			hasOutputName(decision) {
				return this.model.result.name[decision] !== undefined && this.model.result.name[decision] !== '';
			},
			hasTestName() {
				return this.model.test.name !== null && this.model.test.name !== '';
			},
			displayAlert(message, state) {
				this.alert = {
					message: message,
					state: state
				}
			},
			detachWorker() {
				const vue = this;

				// We need to detach the output into another window.
				const worker = window.open(location.href);
				// This custom event will be received when vue has finished mounting.
				worker.addEventListener("aftermount", function () {
					vue.mode = 1;
					vue.displayAlert(null, null);
					vue.executeModel();

					// This event will be received when the opened window is closed or the user navigates away.
					// It can only be added *after* the opened window is loaded.
					worker.addEventListener("beforeunload", function () {
						vue.mode = 0;
						vue.executeModel();
					});
				});
				this.worker = worker;
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
	.card-borderless {
		border: none;
	}

	.input-disabled {
		opacity: 0.6;
	}
</style>
