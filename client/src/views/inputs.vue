<template>
	<div>
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Inputs</h3>
			</div>
			<div class="col-1 mb-2">
				<button class="btn btn-block btn-outline-secondary" v-on:click="setAddMode">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="row">
			<div class="col-3 mb-4">
				<div class="list-group">
					<div class="list-group-item list-group-item-action c-pointer" v-for="(input, uuid) in inputs" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)">
						<span class="d-block float-left mr-4" v-on:click="setViewMode(uuid)">{{input.name}}</span>
						<div class="float-right">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
								<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
								<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deleteInput(uuid)">
								<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
							</svg>
						</div>
					</div>
				</div>
			</div>
			<div class="col-9 mb-4">
				<div class="card" v-if="mode === 'VIEW'">
					<div class="card-header">
						<h4 class="mb-0">{{input.name}}</h4>
					</div>
					<div class="card-body">
						<div v-if="input.parent !== null">
							<h5 class="mb-2">Parent</h5>
							<p class="mb-4">{{inputs[input.parent].name}}</p>
						</div>

						<h5 class="mb-2">Input</h5>
						<json-builder v-if="input.parent === null" v-bind:template="input.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
						<json-builder v-else v-bind:template="input.enrichedValue" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="input.name">

						<h5 class="mb-2">Parent</h5>
						<select class="form-control mb-4" v-bind:value="input.parent" v-on:change="setParent($event.target.value)">
							<option value="" selected/>
							<option v-for="(input, uuid) in inputs" v-bind:value="uuid">{{input.name}}</option>
						</select>

						<h5 class="mb-2">Input</h5>
						<json-builder v-bind:template="input.template" v-bind:fixed-root="true" v-on:update:value="input.value = $event"/>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" v-on:click="addInput">Save Input</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" v-on:click="editInput">Save Input</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import Converter from "../components/json/json-builder-converter";

	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		components: {
			"json-builder": JSONBuilder
		},
		async mounted() {
			await this.getInputs();
		},
		data() {
			return {
				mode: "SELECT",

				inputs: {},
				input: {
					uuid: null,
					name: null,
					parent: null,
					value: {},
					enrichedValue: {},
					template: {}
				}
			}
		},
		methods: {
			//
			// Inputs
			//
			async getInputs() {
				this.inputs = await Network.getInputs();
				this.mode = "SELECT";
			},
			async addInput() {
				await Network.addInput({
					name: this.input.name,
					parent: this.input.parent,
					value: this.input.value
				});
				await this.getInputs();
			},
			async editInput() {
				await Network.editInput(this.input.uuid, {
					name: this.input.name,
					parent: this.input.parent,
					value: this.input.value
				});
				await this.getInputs();
			},
			async deleteInput(uuid) {
				await Network.deleteInput(uuid);
				await this.getInputs();
			},

			//
			// View Modes
			//
			async setViewMode(uuid) {
				const input = this.inputs[uuid];
				const enrichedInput = await Network.getInput(uuid, true);

				this.input = {
					uuid: uuid,
					name: input.name,
					parent: input.parent,
					value: input.value,
					enrichedValue: enrichedInput.value
				};

				this.mode = "VIEW";
			},
			setAddMode() {
				this.input = {
					uuid: null,
					name: null,
					parent: null,
					value: {},
					template: Converter.enrich({})
				};

				this.mode = "ADD";
			},
			async setEditMode(uuid) {
				const input = this.inputs[uuid];
				const templateValue = await this.getTemplateValue(uuid, input.parent);

				this.input = {
					uuid: uuid,
					name: input.name,
					parent: input.parent,
					value: {},
					template: templateValue
				};

				this.mode = "EDIT";
			},
			async setDuplicateMode(uuid) {
				const input = this.inputs[uuid];
				const templateValue = await this.getTemplateValue(uuid, input.parent);

				this.input = {
					uuid: uuid,
					name: input.name,
					parent: input.parent,
					value: {},
					template: templateValue
				};

				this.mode = "ADD";
			},

			//
			// Helper
			//
			async setParent(parent) {
				if(parent === "") {
					parent = null;
				}

				this.input.parent = parent;
				this.input.template = parent === null ? Converter.enrich({}) : await this.getTemplateValue(parent, parent);
			},
			async getTemplateValue(uuid, parentUUID) {
				const mergedInput = await Network.getInput(uuid, true);

				if (parentUUID === undefined || parentUUID === null) {
					return Converter.enrich(mergedInput.value);
				}
				const mergedParentInput = await Network.getInput(parentUUID, true);

				const enrichedInput = Converter.enrich(mergedInput.value);
				const enrichedParentInput = Converter.enrich(mergedParentInput.value);
				Converter.addTemplate(enrichedInput, enrichedParentInput);

				return enrichedInput;
			}
		}
	};
</script>