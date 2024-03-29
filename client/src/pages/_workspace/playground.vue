<template>
	<div>
		<div class="d-flex align-items-center mb-2">
			<h3 class="mb-0 me-auto">Playground</h3>

			<div class="d-flex justify-content-between">
				<div class="me-2">
					<div class="input-group">
						<span class="input-group-text">Playground</span>
						<select class="form-select" ref="playground-select" @change="loadPlayground($event.target.value)" aria-label="Load playground">
							<option selected disabled hidden>Load playground...</option>
							<option v-for="(playground, uuid) in playgrounds" :key="uuid" :value="uuid">{{playground.name}}</option>
						</select>
					</div>
				</div>
				<input placeholder="Name..." class="form-control me-2" style="display:block;flex:1" :value="playground.name" @keyup="playground.name = $event.target.value">
				<input placeholder="Description..." class="form-control me-2" style="display:block;flex:1" :value="playground.description" @keyup="playground.description = $event.target.value">
				<button class="btn btn-outline-primary me-2" style="display:block" @click="savePlayground" aria-label="Save playground">
					<svg style="width:24px;height:24px" viewBox="0 0 24 24">
						<path fill="currentColor" d="M15,9H5V5H15M12,19A3,3 0 0,1 9,16A3,3 0 0,1 12,13A3,3 0 0,1 15,16A3,3 0 0,1 12,19M17,3H5C3.89,3 3,3.9 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V7L17,3Z"/>
					</svg>
				</button>
				<button class="btn btn-outline-primary" style="display:block" @click="resetPlayground" aria-label="Reset playground">
					<svg style="width:24px;height:24px" viewBox="0 0 24 24">
						<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="row mb-4">
			<div class="col-6">
				<h4 class="mb-2">Expression</h4>

				<div class="card mb-4">
					<div class="card-body">
						<literal-expression v-model="playground.expression" @input="executeRaw"/>
					</div>
				</div>

				<div class="d-flex align-items-center mb-2">
					<h4 class="mb-0 me-auto">Context</h4>
					<div>
						<div class="input-group">
							<span class="input-group-text">Template</span>
							<select class="form-select" @change="importInput(inputs[$event.target.value].value)" aria-label="Select template">
								<option selected disabled>Select template...</option>
								<option v-for="(input, uuid) in inputs" :key="uuid" :value="uuid">{{input.name}}</option>
							</select>
						</div>
					</div>
				</div>

				<div class="card">
					<div class="card-body">
						<json-builder :convert="true" :template="playground.context.template" @update:value="playground.context.value = $event; executeRaw();" :fixed-root="true"/>
					</div>
				</div>
			</div>
			<div class="col-6">
				<div class="d-flex align-items-center">
					<h4 class="mb-2 me-auto">Output</h4>
					<div>
						<div class="input-group mb-2">
							<span class="input-group-text">Engine</span>
							<select class="form-select" v-model="engine" @change="executeRaw" aria-label="Engine">
								<option value="DROOLS">Drools</option>
								<option value="CAMUNDA">Camunda</option>
								<option value="GOLDMAN">jDMN</option>
							</select>
						</div>
					</div>
				</div>

				<div class="row mb-2" v-if="alert.message !== null">
					<div class="col-12">
						<alert :alert="alert"/>
					</div>
				</div>
				<div class="card">
					<div class="card-body">
						<json-builder :template="playground.output.outputs" :convert="true" :fixed="true" :fixed-values="true"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";

	import LiteralExpression from "../../components/dmn/literal-expression.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import Alert from "../../components/alert/alert.vue";
	import AlertHelper from "../../components/alert/alert-helper";
	import Converter from "@/components/json/json-builder-converter";
	import base64 from "base-64";

	export default {
		head() {
			return {
				title: "declab - Playground",
			}
		},
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"literal-expression": LiteralExpression,
		},
		async mounted() {
			await this.getPlaygrounds();

			await this.getInputs();

			await this.executeRaw("");

			const context = this.$route.query.context;
			if (context !== undefined) {
				this.playground.context.template = JSON.parse(base64.decode(context));
			}
		},
		data() {
			return {
				alert: {
					message: null,
					state: null
				},

				engine: "DROOLS",

				playgrounds: [],
				playground: {
					uuid: null,
					name: null,
					description: null,
					expression: null,
					context: {
						value: {},
						template: {},
					},
					output: {
						outputs: null,
						messages: []
					},
				},

				inputs: {},
			}
		},
		methods: {
			//
			// Model
			//
			async executeRaw() {
				const response = await Network.executeRaw(this.playground.expression, this.playground.context.value, this.engine);
				if (response.status !== 200) {
					this.playground.output.outputs = null;
					this.displayAlert("The output could not be calculated.", "danger");
					return;
				}

				const result = await response.json();
				this.playground.output.outputs = result.outputs.main;
				if (result.messages.length > 0) {
					this.displayAlert(AlertHelper.buildList("The output was calculated, the following messages were returned:", result.messages), "warning");
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

			//
			// PLaygrounds
			//
			async getPlaygrounds() {
				this.playgrounds = await Network.getPlaygrounds(false);
			},

			async savePlayground() {
				//Do not allow saving if playground has no name
				if (this.playground.name === null || this.playground.name === "") {
					this.$store.commit("displayAlert", {
						message: "The playground must have a name.",
						state: "danger"
					});
					return;
				}


				const playground = {
					name: this.playground.name,
					description: this.playground.description,
					expression: this.playground.expression,
					context: this.playground.context.value,
				};

				//Create or edit, depending on if a saved playground is already loaded
				if (this.playground.uuid === null) {
					this.playground.uuid = await Network.addPlayground(playground);
				}
				else {
					await Network.editPlayground(this.playground.uuid, playground);
				}

				this.$store.commit("displayAlert", {
					message: "The playground was successfully saved.",
					state: "success"
				});

				//Update page
				this.playgrounds = await Network.getPlaygrounds();
			},

			async loadPlayground(uuid) {
				if (uuid !== null && uuid !== undefined) {
					const playground = this.playgrounds[uuid];
					this.playground.uuid = uuid;
					this.playground.name = playground.name;
					this.playground.description = playground.description;
					this.playground.expression = playground.expression;
					this.playground.context.template = playground.context;

					await this.executeRaw();
				}
			},

			resetPlayground() {
				this.playground.uuid = null;
				this.$refs["playground-select"].selectedIndex = 0;
			},


			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},


			importInput(input) {
				const currentInput = JSON.parse(JSON.stringify(this.playground.context.template));
				const templateInput = Converter.enrich(input);
				this.playground.context.template = Converter.clean(Converter.merge(currentInput, templateInput));
			},
		}
	};
</script>
