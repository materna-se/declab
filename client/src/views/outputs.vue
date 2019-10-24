<template>
	<div class="container-fluid">
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Outputs</h3>
			</div>
			<div class="col-1 mb-2">
				<button class="btn btn-block btn-outline-secondary" v-on:click="setAddMode">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"></path>
					</svg>
				</button>
			</div>
		</div>
		<div class="row">
			<div class="col-3 mb-4">
				<div class="list-group">
					<div class="list-group-item list-group-item-action c-pointer" v-for="(output, uuid) in outputs" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)">
						<span class="d-block float-left mr-4" v-on:click="setViewMode(uuid)">{{output.name}}</span>
						<div class="float-right">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
								<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"></path>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
								<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"></path>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deleteOutput(uuid)">
								<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"></path>
							</svg>
						</div>
					</div>
				</div>
			</div>
			<div class="col-9 mb-4">
				<div class="card" v-if="mode === 'VIEW'">
					<div class="card-header">
						<h4 class="mb-0">{{output.name}}</h4>
					</div>
					<div class="card-body">
						<h5 class="mb-2">Decision</h5>
						<p class="mb-4"><code>{{output.decision}}</code></p>

						<h5 class="mb-2">Output</h5>
						<json-builder v-bind:template="output.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"></json-builder>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="output.name">

						<h5 class="mb-2">Decision</h5>
						<select id="form-parent" class="form-control mb-4" v-model="output.decision">
							<option v-for="model of model.decisions">{{model}}</option>
						</select>

						<h5 class="mb-2">Output</h5>
						<json-builder v-bind:template="output.template" v-bind:convert="true" v-on:update:value="output.value = $event"></json-builder>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" v-on:click="addOutput">Save Output</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" v-on:click="editOutput">Save Output</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";

	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		components: {
			"json-builder": JSONBuilder
		},
		async mounted() {
			await this.getModel();
			await this.getOutputs();
		},
		data() {
			return {
				mode: "SELECT",

				model: {},

				outputs: {},
				output: {
					uuid: null,
					name: null,
					decision: null,
					value: null,
					template: null
				}
			}
		},
		methods: {
			//
			// Model
			//
			async getModel() {
				this.model = await Network.getModel();
			},

			//
			// Outputs
			//
			async getOutputs() {
				this.outputs = await Network.getOutputs();
				this.mode = "SELECT";
			},
			async addOutput() {
				await Network.addOutput({
					name: this.output.name,
					decision: this.output.decision,
					value: this.output.value
				});
				await this.getOutputs();
			},
			async editOutput() {
				await Network.editOutput(this.output.uuid, {
					name: this.output.name,
					decision: this.output.decision,
					value: this.output.value
				});
				await this.getOutputs();
			},
			async deleteOutput(uuid) {
				await Network.deleteOutput(uuid);
				await this.getOutputs();
			},


			//
			// View Modes
			//
			setViewMode(uuid) {
				const output = this.outputs[uuid];

				this.output = {
					uuid: uuid,
					name: output.name,
					decision: output.decision,
					value: output.value,
					template: output.value
				};

				this.mode = "VIEW";
			},
			setAddMode() {
				this.output = {
					uuid: null,
					name: null,
					decision: null,
					value: null,
					template: null
				};

				this.mode = "ADD";
			},
			setEditMode(uuid) {
				const output = this.outputs[uuid];

				this.output = {
					uuid: uuid,
					name: output.name,
					decision: output.decision,
					value: output.value,
					template: output.value
				};

				this.mode = "EDIT";
			},
			setDuplicateMode(uuid) {
				const output = this.outputs[uuid];

				this.output = {
					uuid: uuid,
					name: output.name,
					decision: output.decision,
					value: output.value,
					template: output.value
				};

				this.mode = "ADD";
			}
		}
	};
</script>

<style>
</style>
