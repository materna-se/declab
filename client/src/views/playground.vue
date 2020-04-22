<template>
	<div>
		<div class="container-fluid mb-4">
			<div class="row justify-content-md-center">
				<div class="col-4">
					<div class="input-group">
						<div class="input-group-prepend">
							<span class="input-group-text">Playground</span>
						</div>
						<select class="form-control" ref="playground_select">
							<option selected disabled hidden>{{playgroundName}}</option>
							<option selected disabled hidden>Unsaved Playground...</option>
							<option selected disabled hidden>Select Playground...</option>
							<option v-for="(playground, key) in playgrounds" v-bind:value="key" v-on:click="loadPlayground(key)">{{playground.name}}</option>
						</select>
					</div>
				</div>
				<div class="col-3">
					<div>
						<div><input placeholder="Name..." class="form-control" v-bind:value="name" v-on:keyup="name = $event.target.value"></div>
					</div>
				</div>
				<div class="col-3">
					<div>
						<div><input placeholder="Description..." class="form-control" v-bind:value="description" v-on:keyup="description = $event.target.value"></div>
					</div>
				</div>
				<div class="col-1">
					<div>
						<button class="btn btn-block btn-outline-secondary" v-on:click="savePlayground()">
							<svg style="width:24px;height:24px" viewBox="0 0 24 24">
    							<path fill="currentColor" d="M15,9H5V5H15M12,19A3,3 0 0,1 9,16A3,3 0 0,1 12,13A3,3 0 0,1 15,16A3,3 0 0,1 12,19M17,3H5C3.89,3 3,3.9 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V7L17,3Z" />
							</svg>
						</button>
					</div>
				</div>
				<div class="col-1">
					<div>
						<button class="btn btn-block btn-outline-secondary" v-on:click="uuid = null; name = null; resetPlaygroundSelect()">
							<svg style="width:24px;height:24px" viewBox="0 0 24 24">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
							</svg>
						</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-6 mb-4">
				<h3 class="mb-2">Expression</h3>
				<div class="card">
					<div class="card-body">
						<feel-editor ref="expression_editor" v-on:update:value="expression = $event; executeRaw();"/>
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
						<json-builder ref="context_editor" v-bind:template="model.input.template" v-on:update:value="model.input.value = $event; executeRaw();"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import Converter from "../components/json/json-builder-converter";

	import FEELEditor from "../components/dmn/feel-editor.vue";
	import JSONBuilder from "../components/json/json-builder.vue";
	import Alert from "../components/alert/alert.vue";
	import AlertHelper from "../components/alert/alert-helper";

	import Playgrounds from "../views/playgrounds.vue";

	export default {
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"feel-editor": FEELEditor,
		},
		async mounted() {
			await this.getPlaygrounds();
			this.executeRaw();
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

				expression: null,

				playgrounds: null,
				playground: {
					name: null,
					description: null,
					expression: null,
					context: null
				},
				uuid: null,
				name: null,
				playgroundName: null,
				description: null,
			}
		},
		methods: {
			//
			// Model
			//
			async executeRaw() {
				const response = await Network.executeRaw(this.expression, this.model.input.value);
				if (response.status !== 200) {
					this.model.output.outputs = null;
					this.displayAlert("The output can't be calculated.", "danger");
					return;
				}

				const result = await response.json();

				this.model.output.outputs = result.outputs.main;
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

			//
			// PLaygrounds
			//
			async getPlaygrounds() {
				this.playgrounds = await Network.getPlaygrounds(false);
			},

			async savePlayground() {
				//Do not allow saving if playground has no name
				if(this.name === null || this.name == "") {
					this.$root.displayAlert("The playground must have a name.", "danger");
					return;
				}

				//Transfer playground data to local playground object
				this.playground.name = this.name;
				this.playground.description = this.description;
				this.playground.expression = this.expression;
				this.playground.context = this.model.input.value;

				//Create or edit, depending on if a saved playground is already loaded
				if(this.uuid === null) {
					const uuid = await Network.addPlayground(this.playground);
					this.uuid = uuid;
				} else {
					await Network.editPlayground(this.uuid, this.playground);
				}

				this.$root.displayAlert("The playground was successfully saved.", "success");

				//Update page
				this.playgrounds = await Network.getPlaygrounds();
				this.playgroundName = this.name;
				this.selectCurrentPlayground();
			},

			async loadPlayground(uuid) {
				if(uuid !== null && uuid !== undefined) {
					const playground = this.playgrounds[uuid];
					this.name = playground.name;
					this.description = playground.description;

					//Set feel-editor content
					this.$refs.expression_editor.editor.setValue(playground.expression);
					this.expression = playground.expression;

					//Set json-builder content
					this.$refs.context_editor.value = Converter.enrich(playground.context);
					this.model.input.value = playground.context;

					this.uuid = uuid;

					await this.executeRaw();
				}
			},

			resetPlaygroundSelect() {
				//Unsaved Playground...
				this.$refs.playground_select.selectedIndex = 1;
			},

			selectCurrentPlayground() {
				//{{this.playgroundName}}
				this.$refs.playground_select.selectedIndex = 0;
			}
		}
	};
</script>
