<template>
	<div>
		<h3 class="mb-2">Challenger</h3>
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

						<hr>
						<template v-if="challenge.type === 'FEEL'">
							<h4 style="text-align:center">FEEL</h4>

							<div class="card">
								<div class="card-body">
									<literal-expression v-model="expression" v-on:input="executeRaw"/>
								</div>
							</div>
						</template>
						<template v-if="challenge.type === 'DMN_MODEL'">
							<h4 style="text-align:center">DMN</h4>

							<div class="card-body">
								<model-select v-model="dmnSolution"/>
							</div>
						</template>
					</div>

					<div class="col-6 mb-4">
						<!-- Hints/Solution Header -->
						<h4 class="mb-2">Hints</h4>

						<!-- Button to show hints / solution -->
						<div class="mb-4" v-if="hint === -1">
							<button class="btn btn-block btn-outline-primary" style="text-align:center" v-on:click="hint = 0" v-if="challenge.hints.length > 0">Show hint</button>
							<button class="btn btn-block btn-outline-primary" style="text-align:center" v-on:click="hint = 0; showSolution()" v-else-if="challenge.type !== 'DMN_MODEL'">Show solution</button>
						</div>

						<!-- If hints exist, add left/right buttons to iterate over all hints -->
						<!-- Once the last hint is reached, change right button to a solution button -->
						<div class="row mb-4" v-if="hint >= 0 && hint < challenge.hints.length && challenge.hints.length > 0">
							<div class="container" style="display: flex">
								<!-- Left button -->
								<div class="me-2" v-if="hint > 0">
									<button class="btn btn-block btn-outline-primary" style="text-align:center" v-on:click="hint -= 1">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
											<path d="M20,9V15H12V19.84L4.16,12L12,4.16V9H20Z" fill="currentColor"/>
										</svg>
									</button>
								</div>

								<!-- Hint -->
								<div style="flex: 1">
									<textarea readonly class="form-control" style="resize:none;max-height:40px" wrap="soft" v-model="challenge.hints[hint]"/>
								</div>

								<!-- Right button -->
								<div class="ms-2" v-if="hint < challenge.hints.length">
									<button class="btn btn-block btn-outline-primary" style="text-align: center" v-if="hint >= 0 && hint < challenge.hints.length - 1" v-on:click="hint += 1">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
											<path d="M4,15V9H12V4.16L19.84,12L12,19.84V15H4Z" fill="currentColor"/>
										</svg>
									</button>
									<button class="btn btn-block btn-outline-primary" style="text-align: center" v-else-if="challenge.type !== 'DMN_MODEL'" v-on:click="hint += 1; showSolution()">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
											<path d="M10 3H14V14H10V3M10 21V17H14V21H10Z" fill="currentColor"/>
										</svg>
									</button>
								</div>
							</div>
						</div>

						<!-- Add button to revert to original FEEL expression if solution is being shown -->
						<div class="mb-4" v-if="hint === challenge.hints.length">
							<button class="btn btn-block btn-outline-primary" style="text-align:center" v-on:click="hint -= 1; showOriginalExpression()">
								<div>Undo solution</div>
							</button>
						</div>

						<h4 class="mb-2">Scenarios</h4>

						<div class="progress mb-4">
							<div class="progress-bar" v-bind:class="[progress === 1 ? 'bg-success' : 'bg-primary']" v-bind:style="{width: progress * 100 + '%'}"></div>
						</div>

						<!-- eslint-disable-next-line vue/require-v-for-key -->
						<div class="card mb-4" v-for="scenario of challenge.scenarios">
							<div class="card-header d-flex align-items-center" v-bind:class="[scenario.output.equal ? 'text-success' : 'text-danger']">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="me-2" v-if="scenario.output.equal">
									<path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2m0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m4.59-12.42L10 14.17l-2.59-2.58L6 13l4 4 8-8-1.41-1.42z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="me-2" v-else>
									<path d="M12 20c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2m2.59 6L12 10.59 9.41 8 8 9.41 10.59 12 8 14.59 9.41 16 12 13.41 14.59 16 16 14.59 13.41 12 16 9.41 14.59 8z" fill="currentColor"/>
								</svg>
								<h5 class="mb-0">{{scenario.name}}</h5>
							</div>
							<div class="card-body" v-if="!scenario.output.equal">
								<h5 class="mb-2">Input</h5>
								<json-builder class="mb-4" v-bind:template="scenario.input.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

								<h5 class="mb-2">Output</h5>
								<h6 class="mb-2">Expected</h6>
								<json-builder class="mb-4" v-bind:template="scenario.output.value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

								<h6 class="mb-2">Calculated</h6>
								<div class="row mb-2" v-if="scenario.output.alert.message !== null">
									<div class="col-12">
										<alert v-bind:alert="scenario.output.alert"/>
									</div>
								</div>
								<json-builder v-bind:template="scenario.output.calculated" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
							</div>
						</div>
						<div class="list-group-item" v-if="challenge.scenarios.length === 0">
							<empty-collection/>
						</div>
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
	import EmptyCollectionComponent from "../../components/empty-collection.vue";
	import Model from './model.vue';
	import ModelSelect from "../../components/model-select.vue";

	export default {
		head() {
			return {
				title: "declab - Challenger",
			}
		},
		components: {
			"alert": Alert,
			"json-builder": JSONBuilder,
			"literal-expression": LiteralExpression,
			"empty-collection": EmptyCollectionComponent,
			"model": Model,
			"model-select": ModelSelect
		},
		async mounted() {
			await this.getChallenges();
		},
		watch: {
			dmnSolution: {
				handler: function (solution) {
					//console.log(solution);
					if (solution === null) {
						return;
					}

					this.dmnSolution.models = solution.models;
					this.dmnSolution.decisionService = solution.decisionService;
					this.executeRaw();
				},
				deep: true
			}
		},
		data() {
			return {
				mode: "SELECT",
				order: false,
				progress: 0,

				expression: "",
				expressionBackup: "",

				dmnSolution: {
					models: [],
					decisionService: null
				},

				challenge: null,
				challenges: {},

				hint: -1,
			}
		},
		methods: {
			async setViewMode(uuid) {
				this.challenge = this.challenges[uuid];

				this.expression = "";
				this.expressionBackup = "";

				this.dmnSolution.models = [];
				this.dmnSolution.decisionService = null;

				this.mode = "VIEW";

				this.hint = -1;

				await this.executeRaw();
			},

			async getChallenges() {
				this.challenges = await Network.getChallenges(this.order);

				//Initialize values not provided by the server
				//The existence of these values is required for the executeRaw function
				for (let [uuid, challenge] of Object.entries(this.challenges)) {
					for (const scenario of challenge.scenarios) {
						this.$set(scenario.output, "alert", {message: null, status: null});
						this.$set(scenario.output, "equal", false);
						this.$set(scenario.output, "calculated", null);
					}
				}

				this.mode = "SELECT";
			},

			async executeRaw() {
				//Keep track of progress
				let testsCompleted = 0;

				//Erase all previous results
				for (const scenario of this.challenge.scenarios) {
					scenario.output.calculated = null;
					scenario.output.equal = false;
				}

				this.progress = 0;

				let response;
				let challengeResults;

				// If DMN challenge, evaluate all scenarios using a single decision session
				// to avoid the overhead of sending and importing models for every scenario
				if (this.challenge.type === "DMN_MODEL") {
					if (this.dmnSolution.models.length === 0) {
						return;
					}

					let directInputs = []

					for (const scenario of this.challenge.scenarios) {
						directInputs.push(scenario.input.value);
					}

					response = await Network.executeModelChallengeList(this.dmnSolution.models, this.dmnSolution.decisionService, directInputs);

					challengeResults = await response.json();
				}

				//Calculate results
				let i = 0;

				for (const scenario of this.challenge.scenarios) {
					scenario.output.equal = false;

					let result;

					try {
						if (this.challenge.type === "FEEL") {
							if (this.expression === null || this.expression === "") {
								return;
							}
							response = await Network.executeRaw(this.expression, scenario.input.value);

							result = await response.json();
						} else if (this.challenge.type === "DMN_MODEL") {
							result = challengeResults[i];
						}

						if (response.status !== 200) {
							throw new Error();
						}
					}
					catch (e) {
						//console.log(e);
						scenario.output.calculated = null;
						scenario.output.equal = false;
						this.displayAlert(scenario, "The output could not be calculated.", "danger");
						continue;
					}

					if (this.challenge.type === "FEEL") {
						scenario.output.calculated = result.outputs.main;
					} else if (this.challenge.type === "DMN_MODEL") {
						scenario.output.calculated = result.outputs;
					}

					if (result.messages.length > 0) {
						scenario.output.equal = false;
						this.displayAlert(scenario, AlertHelper.buildList("The output was calculated, the following messages were returned:", result.messages), "warning");
						continue;
					}

					scenario.output.equal = JSON.stringify(scenario.output.value) === JSON.stringify(scenario.output.calculated);
					if (scenario.output.equal) {
						testsCompleted += 1;
					}

					this.displayAlert(scenario, null, null);

					i++;
				}

				this.progress = testsCompleted / this.challenge.scenarios.length;
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

			showSolution() {
				this.expressionBackup = JSON.parse(JSON.stringify(this.expression));
				this.expression = this.challenge.solution;
				this.executeRaw();
			},

			showOriginalExpression() {
				this.expression = JSON.parse(JSON.stringify(this.expressionBackup));
				this.executeRaw();
			},
		}
	};
</script>
