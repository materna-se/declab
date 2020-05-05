<template>
	<div>
		<h3 class="mb-2">Challenges</h3>
		<div class="row">
			<div class="col-3 mb-4">
				<div class="list-group">
					<template v-if="Object.keys(challenges).length !== 0">
						<div class="list-group-item list-group-item-action c-pointer" v-for="(challenge, uuid) in challenges" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)">
							<span class="d-block" v-on:click="setViewMode(uuid)">{{challenge.name}}</span>
						</div>
					</template>
					<div class="list-group-item" v-else>
						<empty-collection/>
					</div>
				</div>
			</div>
			<div class="col-9 mb-4">
				<div class="row" v-if="mode === 'VIEW'">
					<div class="col-6 mb-4">
						<h4 class="mb-2">{{challenge.name}}</h4>
						<p class="mb-4">{{challenge.description}}</p>

						<div class="card">
							<div class="card-body">
								<feel-editor v-model="expression" v-on:input="executeRaw"/>
							</div>
						</div>
					</div>

					<div class="col-6 mb-4">
						<div class="progress mb-4">
							<div class="progress-bar" v-bind:class="[progress === 1 ? 'bg-success' : 'bg-primary']" v-bind:style="{width: progress * 100 + '%'}"></div>
						</div>

						<div class="card mb-4" v-for="scenario of challenge.scenarios">
							<div class="card-header d-flex align-items-center" v-bind:class="[scenario.output.equal ? 'text-success' : 'text-danger']">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="mr-2" v-if="scenario.output.equal">
									<path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2m0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m4.59-12.42L10 14.17l-2.59-2.58L6 13l4 4 8-8-1.41-1.42z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="mr-2" v-else>
									<path d="M12 20c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2m2.59 6L12 10.59 9.41 8 8 9.41 10.59 12 8 14.59 9.41 16 12 13.41 14.59 16 16 14.59 13.41 12 16 9.41 14.59 8z" fill="currentColor"/>
								</svg>
								<h5 class="mb-0">{{scenario.name}}</h5>
							</div>
							<div class="card-body" v-if="!scenario.output.equal">
								<h5 class="mb-2">Input</h5>
								<div class="card mb-4">
									<div class="card-body">
										<json-builder v-bind:template="scenario.input" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
									</div>
								</div>

								<h5 class="mb-2">Output</h5>
								<h6 class="mb-2">Expected</h6>
								<div class="card mb-4">
									<div class="card-body">
										<json-builder v-bind:template="scenario.output.expected" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
									</div>
								</div>

								<h6 class="mb-2">Calculated</h6>
								<div class="row mb-2" v-if="scenario.output.alert.message !== null">
									<div class="col-12">
										<alert v-bind:alert="scenario.output.alert"/>
									</div>
								</div>
								<div class="card">
									<div class="card-body">
										<json-builder v-bind:template="scenario.output.calculated" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";

	import FEELEditor from "../components/dmn/feel-editor.vue";
	import JSONBuilder from "../components/json/json-builder.vue";
	import Alert from "../components/alert/alert.vue";
	import AlertHelper from "../components/alert/alert-helper";
	import EmptyCollectionComponent from "../components/empty-collection.vue";

	export default {
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"feel-editor": FEELEditor,
			"empty-collection": EmptyCollectionComponent
		},
		mounted() {

		},
		data() {
			return {
				mode: "SELECT",

				progress: 0,
				expression: null,

				challenge: null,
				challenges: {
					test: {
						name: "Calculating the Length of a List",
						description: "This challenge involves creating a decision that calculates the length of a list. If the list is empty, the decision should return -1.",
						scenarios: [
							{
								name: "Empty List",
								input: {
									input: []
								},
								output: {
									alert: {
										message: null,
										state: null
									},
									expected: -1,
									calculated: null,
									equal: false
								}
							},
							{
								name: "List with One Element",
								input: {
									input: [1]
								},
								output: {
									alert: {
										message: null,
										state: null
									},
									expected: 1,
									calculated: null,
									equal: false
								}
							},
							{
								name: "List with Four Elements",
								input: {
									input: [1, 2, 3, 4]
								},
								output: {
									alert: {
										message: null,
										state: null
									},
									expected: 4,
									calculated: null,
									equal: false
								}
							}
						],
					}
				}
			}
		},
		methods: {
			async setViewMode(uuid) {
				this.challenge = this.challenges[uuid];

				this.mode = "VIEW";

				this.executeRaw();
			},

			//
			// Model
			//
			async executeRaw() {
				this.progress = 0;

				for (const scenario of this.challenge.scenarios) {
					let response;
					try {
						response = await Network.executeRaw(this.expression, scenario.input);
						if (response.status !== 200) {
							throw new Error();
						}
					}
					catch (e) {
						scenario.output.calculated = null;
						scenario.output.equal = false;
						this.displayAlert(scenario, "The output can't be calculated.", "danger");
						return;
					}

					const result = await response.json();
					scenario.output.calculated = result.outputs.main;
					if (result.messages.length > 0) {
						scenario.output.equal = false;
						this.displayAlert(scenario, AlertHelper.buildList("The output was calculated, but the following warnings have occurred:", result.messages), "warning");
						return;
					}

					scenario.output.equal = JSON.stringify(scenario.output.expected) === JSON.stringify(scenario.output.calculated);
					if (scenario.output.equal) {
						this.progress += 1 / this.challenge.scenarios.length;
					}
					this.displayAlert(scenario, null, null);
				}
			},

			//
			// Helpers
			//
			displayAlert(scenario, message, state) {
				scenario.output.alert = {
					message: message,
					state: state
				}
			},
		}
	};
</script>
