<template>
	<div>
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Outputs</h3>
			</div>
			<div class="col-1 mb-2">
				<button class="btn btn-block btn-outline-secondary" @click="setAddMode">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="row">
			<div class="col-3 mb-4">
				<div class="row mb-2">
					<div class="col-12">
						<button class="btn btn-block btn-outline-secondary mb-2" @click="order = !order; getOutputs()">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M9.25 5l3.25-3.25L15.75 5h-6.5m-.36 9.3H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-if="order"/>
								<path d="M15.75 19l-3.25 3.25L9.25 19h6.5m-6.86-4.7H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-else/>
							</svg>
						</button>
					</div>
				</div>
				<div class="list-group">
					<template v-if="Object.keys(outputs).length !== 0">
						<div class="list-group-item list-group-item-action c-pointer" v-for="(output, uuid) in outputs" :key="uuid" @click.self="setViewMode(uuid)">
							<span class="d-block float-left mr-4" @click="setViewMode(uuid)">{{output.name}}</span>
							<div class="float-right">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" @click="setEditMode(uuid)">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" @click="setDuplicateMode(uuid)">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" @click="deleteOutput(uuid)">
									<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
								</svg>
							</div>
						</div>
					</template>
					<div class="list-group-item" v-else>
						<empty-collection/>
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
						<json-builder :template="output.value" :convert="true" :fixed="true" :fixed-values="true"/>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="output.name">

						<h5 class="mb-2">Decision</h5>
						<select id="form-parent" class="form-control mb-4" v-model="output.decision">
							<option v-for="decision of decisions" :key="decision">{{decision}}</option>
						</select>

						<h5 class="mb-2">Output</h5>
						<json-builder :template="output.template" :convert="true" @update:value="output.value = $event"/>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" @click="addOutput">Save Output</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" @click="editOutput">Save Output</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";

	import JSONBuilder from "../../components/json/json-builder.vue";
	import EmptyCollectionComponent from "../../components/empty-collection.vue";

	export default {
		head() {
			return {
				title: "declab - Outputs",
			}
		},
		components: {
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent
		},
		async mounted() {
			await this.getModel();
			await this.getOutputs();
		},
		data() {
			return {
				mode: "SELECT",

				model: {},
				decisions: [],

				order: false,
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
				const models = await Network.getModel();
				const decisions = [];
				for (let i = 0; i < models.length; i++) {
					const model = models[i];
					decisions.push(...model.decisions.map(decision => i === models.length -1 ? decision : model.namespace + "." + decision));
				}

				this.model = models;
				this.decisions = decisions;
			},

			//
			// Outputs
			//
			async getOutputs() {
				this.outputs = await Network.getOutputs(this.order);
				this.mode = "SELECT";
			},
			async addOutput() {
				await Network.addOutput({
					name: this.output.name,
					decision: this.output.decision,
					value: this.output.value
				});

				this.$store.commit("displayAlert", {
					message: "The output was successfully created.",
					state: "success"
				});

				await this.getOutputs();
			},
			async editOutput() {
				await Network.editOutput(this.output.uuid, {
					name: this.output.name,
					decision: this.output.decision,
					value: this.output.value
				});

				this.$store.commit("displayAlert", {
					message: "The output was successfully edited.",
					state: "success"
				});

				await this.getOutputs();
			},
			async deleteOutput(uuid) {
				await Network.deleteOutput(uuid);

				this.$store.commit("displayAlert", {
					message: "The output was successfully deleted.",
					state: "success"
				});

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
