<template>
	<div>
		<div class="row">
			<div class="col-11 mb-2">
				<h3 class="mb-0">Challenges</h3>
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
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="order = !order; getChallenges()">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M9.25 5l3.25-3.25L15.75 5h-6.5m-.36 9.3H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-if="order"/>
								<path d="M15.75 19l-3.25 3.25L9.25 19h6.5m-6.86-4.7H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-else/>
							</svg>
						</button>
					</div>
				</div>
				<div class="list-group">
					<template v-if="Object.keys(challenges).length !== 0">
						<div class="list-group-item list-group-item-action c-pointer" v-for="(challenge, uuid) in challenges" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)">
							<span class="d-block float-left mr-4" v-on:click="setViewMode(uuid)">{{challenge.name}}</span>
							<div class="float-right">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deleteChallenge(uuid)">
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
						<h4 class="mb-0">{{challenge.name}}</h4>
					</div>
					<div class="card-body">
						<h5 class="mb-2">Description</h5>
						<input class="form-control mb-4" v-model="challenge.description" readonly>
						<h5 class="mb-2">Hints</h5>
						<div class="list-group mb-4">
							<template v-if="challenge.hints.length !== 0">
								<div class="list-group-item" v-for="hint in challenge.hints">
									<div class="mr-auto">
										<p class="mb-0">{{hint}}</p>
									</div>
								</div>
							</template>
							<div class="list-group-item" v-else>
								<empty-collection/>
							</div>
						</div>
						<h5 class="mb-2">Solution</h5>
						<div class="card mb-4">
							<div class="card-body">
								<feel-editor v-model="challenge.solution" readonly="true"/>
							</div>
						</div>


						<h5 class="mb-2">Scenarios</h5>
						<div class="list-group">
							<template v-if="challenge.scenarios.length !== 0">
								<div class="list-group-item" v-for="scenario in challenge.scenarios">
									<div class="mr-auto">
										<h5 class="mb-2">{{scenario.name}}</h5>
										<h6 class="mb-1">Input</h6>
										<json-builder v-bind:template="scenario.input.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
										<hr>
										<h6 class="mb-1">Output</h6>
										<json-builder v-bind:template="scenario.output.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
									</div>
								</div>
							</template>
							<div class="list-group-item" v-else>
								<empty-collection/>
							</div>
						</div>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="challenge.name">

						<h5 class="mb-2">Description</h5>
						<input class="form-control mb-4" v-model="challenge.description">

						<h5 class="mb-2">Hints</h5>
						<div class="list-group mb-2">
							<template v-if="challenge.hints.length !== 0">
								<div class="list-group-item" v-for="(hint, index) in challenge.hints">
									<div class="row mx-0 flex-row">
										<input class="form-control mb-0 mr-2" style="flex: 1" v-model="challenge.hints[index]">
										<button class="btn btn-outline-secondary" v-on:click="challenge.hints.splice(index, 1)">
											<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
												<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
											</svg>
										</button>
									</div>
								</div>
							</template>
							<div class="list-group-item" v-else>
								<empty-collection/>
							</div>
						</div>

						<button class="btn btn-block btn-outline-secondary mb-4" v-on:click="challenge.hints.push('')">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
							</svg>
						</button>

						<h5 class="mb-2">Solution</h5>
						<div class="card mb-4">
							<div class="card-body">
								<feel-editor v-model="challenge.solution"/>
							</div>
						</div>

						<h5 class="mb-2">Scenarios</h5>
						<div class="list-group">
							<template v-if="challenge.scenarios.length !== 0">
								<div class="list-group-item" v-for="(scenario, index) of challenge.scenarios">
									<div class="mr-auto">
										<h5 class="mb-2">Name</h5>
										<div>
											<input class="form-control mb-4" v-model="scenario.name">
										</div>
										<h6 class="mb-1">Input</h6>
										<json-builder v-bind:template="scenario.input.template" v-bind:convert="true" v-on:update:value="scenario.input.value = $event"/>
										<hr>
										<h6 class="mb-1">Output</h6>
										<json-builder v-bind:template="scenario.output.template" v-bind:convert="true" v-on:update:value="scenario.output.value = $event"/>
										<div class="float-right mt-2">
											<button class="btn btn-block btn-outline-secondary" v-on:click="challenge.scenarios.splice(index, 1)">
												<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
													<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
												</svg>
											</button>
										</div>
									</div>
								</div>
							</template>
							<div class="list-group-item" v-else>
								<empty-collection/>
							</div>
						</div>

						<button class="btn btn-block btn-outline-secondary mt-2" v-on:click="challenge.scenarios.push({'name':'New Scenario','input':{'value':{}, 'template':{}},'output':{'value':'', 'template':''}})">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
							</svg>
						</button>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" v-on:click="addChallenge">Save Challenge</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" v-on:click="editChallenge">Save Challenge</button>
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
	import EmptyCollectionComponent from "../components/empty-collection.vue";

	export default {
		components: {
			"feel-editor": FEELEditor,
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent,
		},
		async mounted() {
			await this.getChallenges();
		},
		data() {
			return {
				mode: "SELECT",

				hints: [],
				scenarios: [],

				order: false,
				challenges: {},
				challenge: {
					uuid: null,
					name: null,
					decision: null,
					hints: null,
					solution: null,
					scenarios: null
				}
			}
		},
		methods: {
			//
			// Challenges
			//
			async getChallenges() {
				this.challenges = await Network.getChallenges(this.order);

				//Initialize values not provided by the server
				//The existence of these templates is required for json-builders
				for (let [uuid, challenge] of Object.entries(this.challenges)) {
					for (const scenario of challenge.scenarios) {
						scenario.input.template = JSON.parse(JSON.stringify(scenario.input.value));
						scenario.output.template = JSON.parse(JSON.stringify(scenario.output.value));
					}
				}

				this.mode = "SELECT";
			},
			async addChallenge() {
				await Network.addChallenge({
					name: this.challenge.name,
					description: this.challenge.description,
                    hints: this.challenge.hints,
                    solution: this.challenge.solution,
                    scenarios: this.challenge.scenarios
				});

				this.$root.displayAlert("The challenge was successfully created.", "success");

				await this.getChallenges();
			},
			async editChallenge() {
				await Network.editChallenge(this.challenge.uuid, {
					name: this.challenge.name,
					description: this.challenge.description,
                    hints: this.challenge.hints,
                    solution: this.challenge.solution,
                    scenarios: this.challenge.scenarios
				});

				this.$root.displayAlert("The challenge was successfully edited.", "success");

				await this.getChallenges();
			},
			async deleteChallenge(uuid) {
				await Network.deleteChallenge(uuid);

				this.$root.displayAlert("The challenge was successfully deleted.", "success");

				await this.getChallenges();
			},

			//
			// View Modes
			//
			setViewMode(uuid) {
				const challenge = this.challenges[uuid];

				//Use JSON utils to do a deep copy of the entire object
				this.challenge = JSON.parse(JSON.stringify(challenge));
				this.challenge.uuid = uuid;

				this.mode = "VIEW";
			},
			setAddMode() {
				this.challenge = {
                    uuid: null,
					name: null,
					description: null,
                    hints: [],
                    solution: null,
                    scenarios: []
				};

				this.mode = "ADD";
			},
			setEditMode(uuid) {
				const challenge = this.challenges[uuid];

				//Use JSON utils to do a deep copy of the entire object
				this.challenge = JSON.parse(JSON.stringify(challenge));
				this.challenge.uuid = uuid;

				this.mode = "EDIT";
			},
			setDuplicateMode(uuid) {
				const challenge = this.challenges[uuid];

				//Use JSON utils to do a deep copy of the entire object
				this.challenge = JSON.parse(JSON.stringify(challenge));

				this.mode = "ADD";
			}
		}
	};
</script>

<style>
</style>