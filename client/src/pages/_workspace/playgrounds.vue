<template>
	<div>
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Playgrounds</h3>
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
				<div class="row mb-2">
					<div class="col-12">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="order = !order; getPlaygrounds()">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M9.25 5l3.25-3.25L15.75 5h-6.5m-.36 9.3H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-if="order"/>
								<path d="M15.75 19l-3.25 3.25L9.25 19h6.5m-6.86-4.7H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-else/>
							</svg>
						</button>
					</div>
				</div>
				<div class="list-group">
					<template v-if="Object.keys(playgrounds).length !== 0">
						<div class="list-group-item list-group-item-action c-pointer" v-for="(playground, uuid) in playgrounds" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)">
							<span class="d-block float-left mr-4" v-on:click="setViewMode(uuid)">{{playground.name}}</span>
							<div class="float-right">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deletePlayground(uuid)">
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
						<h4 class="mb-0">{{playground.name}}</h4>
					</div>
					<div class="card-body">
						<h5 class="mb-2">Description</h5>
						<input class="form-control mb-4" v-model="playground.description" readonly>

						<h5 class="mb-2">Expression</h5>
						<div class="card mb-4">
							<div class="card-body">
								<feel-editor v-model="playground.expression" readonly="true"/>
							</div>
						</div>

						<h5 class="mb-2">Context</h5>
						<json-builder v-bind:template="playground.context" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="playground.name">
						
						<h5 class="mb-2">Description</h5>
						<input class="form-control mb-4" v-model="playground.description">
						
						<h5 class="mb-2">Expression</h5>
						<div class="card mb-4">
							<div class="card-body">
								<feel-editor v-model="playground.expression"/>
							</div>
						</div>

						<h5 class="mb-2">Context</h5>
						<json-builder v-bind:template="playground.template" v-bind:fixed-root="true" v-on:update:value="playground.context = $event"/>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" v-on:click="addPlayground">Save Playground</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" v-on:click="editPlayground">Save Playground</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import Converter from "../../components/json/json-builder-converter";
	import FEELEditor from "../../components/dmn/feel-editor.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import EmptyCollectionComponent from "../../components/empty-collection.vue";

	export default {
		head() {
			return {
				title: "declab - Playgrounds",
			}
		},
		components: {
			"feel-editor": FEELEditor,
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent
		},
		async mounted() {
			await this.getPlaygrounds();
		},
		data() {
			return {
				mode: "SELECT",

				order: false,
				playgrounds: {},
				playground: {
					uuid: null,
					name: null,
					description: null,
					expression: null,
                    context: null,
                    template: null
				}
			}
		},
		methods: {
			//
			// playgrounds
			//
			async getPlaygrounds() {
				this.playgrounds = await Network.getPlaygrounds(this.order);
				this.mode = "SELECT";
			},
			async addPlayground() {
				const id = await Network.addPlayground({
					name: this.playground.name,
					description: this.playground.description,
					expression: this.playground.expression,
					context: this.playground.context
				});

				this.$store.commit("displayAlert", {
					message: "The playground was successfully created.",
					state: "success"
				});

				await this.getPlaygrounds();
			},
			async editPlayground() {
				await Network.editPlayground(this.playground.uuid, {
					name: this.playground.name,
					description: this.playground.description,
					expression: this.playground.expression,
					context: this.playground.context
				});

				this.$store.commit("displayAlert", {
					message: "The playground was successfully edited.",
					state: "success"
				});

				await this.getPlaygrounds();
			},
			async deletePlayground(uuid) {
				await Network.deletePlayground(uuid);

				this.$store.commit("displayAlert", {
					message: "The playground was successfully deleted.",
					state: "success"
				});

				await this.getPlaygrounds();
			},

			//
			// View Modes
			//
			async setViewMode(uuid) {
				const playground = this.playgrounds[uuid];

				this.playground = {
					uuid: uuid,
					name: playground.name,
					description: playground.description,
					expression: playground.expression,
					context: playground.context
				};

				this.mode = "VIEW";
			},
			setAddMode() {
				this.playground = {
					uuid: null,
					name: null,
					description: null,
					expression: null,
                    context: null,
                    template: Converter.enrich({})
				};

				this.mode = "ADD";
			},
			async setEditMode(uuid) {
				const playground = this.playgrounds[uuid];

				this.playground = {
					uuid: uuid,
					name: playground.name,
					description: playground.description,
					expression: playground.expression,
                    context: playground.context,
                    template: Converter.enrich(playground.context)
				};

				this.mode = "EDIT";
			},
			async setDuplicateMode(uuid) {
				const playground = this.playgrounds[uuid];

				this.playground = {
					uuid: uuid,
					name: playground.name,
					description: playground.description,
					expression: playground.expression,
					context: playground.context,
					template: Converter.enrich(playground.context)
				};

				this.mode = "ADD";
			},
		}
	};
</script>