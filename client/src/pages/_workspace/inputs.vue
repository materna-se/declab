<template>
	<div>
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Inputs</h3>
			</div>
			<div class="col-1 mb-2">
				<button class="btn btn-block btn-outline-primary" @click="setAddMode">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>
			</div>
		</div>
		<div class="row">
			<div class="d-flex">
				<div class="mb-4 me-4" style="width: 25%; min-width: 25%;">
					<button class="btn btn-block btn-outline-primary mb-2" @click="order = !order; getInputs()">
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
							<path d="M9.25 5l3.25-3.25L15.75 5h-6.5m-.36 9.3H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-if="order"/>
							<path d="M15.75 19l-3.25 3.25L9.25 19h6.5m-6.86-4.7H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-else/>
						</svg>
					</button>
					<div class="list-group">
						<template v-if="Object.keys(inputs).length !== 0">
							<div class="list-group-item list-group-item-action c-pointer d-flex align-items-center" v-for="(input, uuid) in inputs" :key="uuid" @click.self="setViewMode(uuid)">
								<span class="me-auto" @click="setViewMode(uuid)"><b>{{input.name}}</b></span>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="ms-4 me-2" @click="setEditMode(uuid)">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="me-2" @click="setDuplicateMode(uuid)">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" @click="deleteInput(uuid)">
									<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
								</svg>
							</div>
						</template>
						<div class="list-group-item" v-else>
							<empty-collection/>
						</div>
					</div>
				</div>
				<div class="flex-grow-1 mb-4">
					<div class="card card-sticky" v-if="mode === 'VIEW'">
						<div class="card-header">
							<h4 class="mb-0">{{input.name}}</h4>
						</div>
						<div class="card-body">
							<div v-if="input.parent !== null">
								<h5 class="mb-2">Parent</h5>
								<p class="mb-4">{{inputs[input.parent].name}}</p>
							</div>

							<h5 class="mb-2">Input</h5>
							<json-builder v-if="input.parent === null" :template="input.value" :convert="true" :fixed="true" :fixed-values="true"/>
							<json-builder v-else :template="input.enrichedValue" :convert="true" :fixed="true" :fixed-values="true"/>
						</div>
					</div>
					<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
						<div class="card-body">
							<h5 class="mb-2">Name</h5>
							<input class="form-control mb-4" v-model="input.name">

							<h5 class="mb-2">Parent</h5>
							<select class="form-control mb-4" :value="input.parent" @change="setParent($event.target.value)">
								<option value="" selected/>
								<option v-for="(input, uuid) in inputs" :key="uuid" :value="uuid">{{input.name}}</option>
							</select>

							<h5 class="mb-2">Input</h5>
							<json-builder :template="input.template" :fixed-root="true" @update:value="input.value = $event"/>

							<hr>

							<button class="btn btn-block btn-outline-primary" v-if="mode === 'ADD'" @click="addInput">Save Input</button>
							<button class="btn btn-block btn-outline-primary" v-if="mode === 'EDIT'" @click="editInput">Save Input</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import Converter from "../../components/json/json-builder-converter";

	import JSONBuilder from "../../components/json/json-builder.vue";
	import EmptyCollectionComponent from "../../components/empty-collection.vue";

	export default {
		head() {
			return {
				title: "declab - Inputs",
			}
		},
		components: {
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent
		},
		async mounted() {
			await this.getInputs();
		},
		data() {
			return {
				mode: "SELECT",

				order: false,
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
				this.inputs = await Network.getInputs(false, this.order);
				this.mode = "SELECT";
			},
			async addInput() {
				await Network.addInput({
					name: this.input.name,
					parent: this.input.parent,
					value: this.input.value
				});

				this.$store.commit("displayAlert", {
					message: "The input was successfully created.",
					state: "success"
				});

				await this.getInputs();
			},
			async editInput() {
				await Network.editInput(this.input.uuid, {
					name: this.input.name,
					parent: this.input.parent,
					value: this.input.value
				});

				this.$store.commit("displayAlert", {
					message: "The input was successfully edited.",
					state: "success"
				});

				await this.getInputs();
			},
			async deleteInput(uuid) {
				await Network.deleteInput(uuid);

				this.$store.commit("displayAlert", {
					message: "The input was successfully deleted.",
					state: "success"
				});

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
				if (parent === "") {
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