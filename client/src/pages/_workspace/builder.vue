<template>
	<div>
		<div class="d-flex align-items-center mb-2">
			<h3 class="mb-0 me-auto">Builder</h3>
			<div class="me-2">
				<button class="btn btn-block btn-outline-primary px-4" @click="modalVisible = true">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M15 9H5V5h10m-3 14a3 3 0 01-3-3 3 3 0 013-3 3 3 0 013 3 3 3 0 01-3 3m5-16H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V7l-4-4z" fill="currentColor"/>
					</svg>
				</button>
			</div>
			<div>
				<button class="btn btn-block btn-outline-primary px-4" @click="detachWorker">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="d-flex">
			<div class="mb-4" v-if="mode !== 2" style="flex: 1" :style="{'margin-right': mode === 0 ? '30px' : null, 'max-width': mode === 0 ? '60%' : null}">
				<div class="d-flex align-items-center mb-2">
					<h4 class="mb-0 me-auto">Input Context</h4>
					<div class="d-flex align-items-center">
						<div class="me-2">
							<button class="btn btn-block btn-outline-primary" @click="model.input.hideEmptyFields = !model.input.hideEmptyFields" v-tooltip="{content: model.input.hideEmptyFields ? 'Show empty fields' : 'Hide empty fields'}">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
									<path v-bind:d="model.input.hideEmptyFields ? 'M2 5.27 3.28 4 20 20.72 18.73 22l-3.08-3.08c-1.15.38-2.37.58-3.65.58-5 0-9.27-3.11-11-7.5.69-1.76 1.79-3.31 3.19-4.54L2 5.27M12 9a3 3 0 0 1 3 3 3 3 0 0 1-.17 1L11 9.17A3 3 0 0 1 12 9m0-4.5c5 0 9.27 3.11 11 7.5a11.79 11.79 0 0 1-4 5.19l-1.42-1.43A9.862 9.862 0 0 0 20.82 12 9.821 9.821 0 0 0 12 6.5c-1.09 0-2.16.18-3.16.5L7.3 5.47c1.44-.62 3.03-.97 4.7-.97M3.18 12A9.821 9.821 0 0 0 12 17.5c.69 0 1.37-.07 2-.21L11.72 15A3.064 3.064 0 0 1 9 12.28L5.6 8.87c-.99.85-1.82 1.91-2.42 3.13Z' : 'M12 9a3 3 0 0 1 3 3 3 3 0 0 1-3 3 3 3 0 0 1-3-3 3 3 0 0 1 3-3m0-4.5c5 0 9.27 3.11 11 7.5-1.73 4.39-6 7.5-11 7.5S2.73 16.39 1 12c1.73-4.39 6-7.5 11-7.5M3.18 12a9.821 9.821 0 0 0 17.64 0 9.821 9.821 0 0 0-17.64 0Z'" fill="currentColor"/>
								</svg>
							</button>
						</div>
						<div class="input-group">
							<span class="input-group-text">Template</span>
							<select class="form-select" @change="importInput(inputs[$event.target.value].value)">
								<option selected disabled>Select template...</option>
								<option v-for="(input, uuid) in inputs" :key="uuid" :value="uuid">{{input.name}}</option>
							</select>
						</div>
					</div>
				</div>
				<div class="card card-borderless mb-2">
					<div class="card-body p-0" style="overflow-x: auto">
						<json-builder :template="model.input.template" :fixed="true" :convert-after="false" :hide-empty="model.input.hideEmptyFields" @update:value="model.input.value = $event; executeModelLoading = true; debouncedExecuteModel();" class="input-builder"/>
					</div>
				</div>
				<div class="card card-borderless mb-2">
					<div class="card-body p-0" style="overflow-x: auto">
						<json-builder :template="model.decisions.template" :fixed="true" :convert-after="false" @update:value="model.decisions.value = $event; executeModelLoading = true; debouncedExecuteModel();" class="decision-builder"/>
					</div>
				</div>
			</div>
			<div class="output-context mb-4" v-if="mode !== 1" style="flex: 1" v-bind:class="{'output-context-loading': executeModelLoading}">
				<h4 class="mb-2">Output Context</h4>
				<div class="row mb-2" v-if="alert.message !== null">
					<div class="col-12">
						<alert :alert="alert"/>
					</div>
				</div>

				<div class="card mb-2" v-for="(output, decisionName) in model.result.outputs" :key="decisionName">
					<div class="card-header" @click="$set(model.result.visibleOutputs, decisionName, model.result.visibleOutputs[decisionName] !== true)">
						<h5 class="mb-0">{{decisionName}}&ensp;<small>({{model.result.visibleOutputs[decisionName] ? 'click to hide' : 'click to show'}})</small></h5>
					</div>
					<div class="card-body" v-if="model.result.visibleOutputs[decisionName]">
						<h5 class="mb-2">Output</h5>
						<json-builder class="mb-0" :template="output" :convert="true" :fixed="true" :fixed-values="true"/>

						<div class="mt-4" v-if="model.result.context[decisionName] !== undefined && Object.keys(model.result.context[decisionName]).length !== 0">
							<h5 class="d-flex align-items-center mb-0" @click="$set(model.result.visibleContexts, decisionName, model.result.visibleContexts[decisionName] !== true)">
								Context
								<small class="me-auto">&ensp;({{model.result.visibleContexts[decisionName] ? 'click to hide' : 'click to show'}})</small>
								<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" class="d-block c-pointer" @click="copyToPlayground(model.result.context[decisionName])" v-tooltip="{content: 'Copy to Playground'}">
									<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
								</svg>
							</h5>
							<json-builder class="mt-2" v-if="model.result.visibleContexts[decisionName]" :template="model.result.context[decisionName]" :convert="true" :fixed="true" :fixed-values="true"/>
						</div>
					</div>
				</div>

				<h4 class="mb-2 mt-3" @click="toggleAccessLog">Access Log<small>&ensp;({{accessLogVisible ? 'click to hide' : 'click to show'}})</small></h4>
				<access-log v-if="accessLogVisible" :entries="model.result.accessLog"></access-log>
			</div>
		</div>

		<template v-if="modalVisible">
			<div class="modal-backdrop fade show"></div>
			<div class="modal fade show" style="display: block" @click.self="modalVisible = false">
				<div class="modal-dialog modal-dialog-centered modal-lg">
					<div class="modal-content p-4">
						<div class="modal-body">
							<h4 class="mb-4">Save Entities</h4>
							<div class="mb-4" :class="[!hasTestName() ? 'input-disabled' : null]">
								<h5 class="mb-2">Test</h5>
								<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.test.name">
							</div>
							<div class="mb-4" :class="[!hasInputName() ? 'input-disabled' : null]">
								<h5 class="mb-2">Input</h5>
								<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.input.name">
							</div>
							<template v-if="Object.keys(model.result.outputs).length > 0">
								<h5 class="mt-4 mb-2">Outputs</h5>
								<div class="row">
									<div class="col-6 mb-2" v-for="(output, uuid) in model.result.outputs" :key="uuid" :class="[!hasOutputName(uuid) ? 'input-disabled' : null]">
										<h6 class="mb-2">{{uuid}}</h6>
										<input type="text" class="form-control" placeholder="Enter Name..." v-model="model.result.name[uuid]">
									</div>
								</div>
							</template>
							<button class="btn btn-outline-primary mt-4 w-100" @click="saveEntities">Save Entities</button>
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
	import base64 from "base-64";
	import {throttle} from "lodash";

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
						hideEmptyFields: false,
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
				worker: null,

				debouncedExecuteModel: () => {
				},
				executeModelLoading: false,
			}
		},
		async mounted() {
			const vue = this;

			this.debouncedExecuteModel = throttle(this.executeModel, 1000);

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
				if (data.type === "imported") {
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
					const result = await Network.executeModel(input === undefined ? {} : input, this.accessLogVisible);
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
				finally {
					this.executeModelLoading = false;
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
				const mergedInput = Converter.merge({type: "object", value: currentInput}, templateInput);
				this.model.input.template = mergedInput.value;
			},
			copyToPlayground(context) {
				const url = this.$router.resolve({path: 'playground', query: {context: base64.encode(JSON.stringify(context))}});
				window.open(url.href, '_blank');
			},
			toggleAccessLog() {
				const newState = !this.accessLogVisible;
				this.accessLogVisible = newState;
				// If newState is true (so we want to debug the model), we need to execute the model explicitly.
				if(newState === true) {
					this.executeModel();
				}
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

	.output-context {
		transition: opacity .15s;
	}

	.output-context-loading {
		opacity: 0.5;
	}
</style>
