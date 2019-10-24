<template>
	<div class="container-fluid">
		<div class="row">
			<div class="col-9">
				<div class="mb-4">
					<div class="row mb-2">
						<div class="col">
							<h1 class="mb-0">Publisher</h1>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<div class="card card-borderless mb-2">
								<div class="list-group">
									<div class="list-group-item list-group-item-action c-pointer" v-for="(test, uuid) in tests" v-bind:key="uuid" v-on:click.self="changeVisibility(test)" v-bind:class="[test.result.equal ? 'bg-success' : 'bg-danger']">
										<div class="row mb-2" v-on:click="changeVisibility(test, test.visibility)">
											<h4 class="d-block float-left mr-4">{{test.name}}<h6>({{uuid}})</h6></h4>
										</div>
										<div class="card card-borderless" v-show="test.visibility">
											<!--<div class="card card-borderless mb-2">
												<h5 class="row col">
													UUID: {{uuid}}
												</h5>
											</div>-->
												<h5 class="645f6606-28dd-4889-b118-30b4d88b6eca">
													Description <h6>{{test.description}}</h6>
												</h5>
											<div class="list-group">
												<div class="list-group-item list-group-item-action c-pointer" v-on:click.self="changeInputVisibility(test)">
													<div class="row mb-2" v-on:click="changeInputVisibility(test)">
														<h5 class="d-block float-left mr-4">Input: {{inputs[test.input].name}}</h5>
													</div>
													<div class="card card-borderless mb-2" v-show="test.inputVis">
														<div class="row col">
															<json-builder v-bind:template="inputs[test.input].value" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
														</div>
													</div>
												</div>
											</div>
											<div class="card card-borderless mb-2">
												<div class="list-group">
													<div class="list-group-item list-group-item-action c-pointer" v-on:click.self="changeOutputVisibility(test)">
														<div class="row mb-2" v-on:click="changeOutputVisibility(test)">
															<h5 class="d-block float-left mr-4">Outputs:</h5>
														</div>
														<div class="card card-borderless mb-2" v-for="(output) in Object.values(test.result.outputs)" v-bind:key="output">
															<div class="list-group">
																<div class="list-group-item list-group-item-action c-pointer" v-bind:class="[output.equal ? 'bg-success' : 'bg-danger']">
																	<div class="row mb-2">
																		<h5 class="d-block float-left mr-4">{{output.expected.name}}</h5>
																	</div>
																	<div class="card card-borderless mb-2" v-show="test.outputVis">
																		<div class="card card-borderless mb-2">
																			<h5 class="row col">
																				Expected
																			</h5>
																			<div class="row col">
																				<json-builder v-bind:template="output.expected.value" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
																			</div>
																		</div>
																		<div class="card card-borderless mb-2">
																			<h5 class="row col">
																				Calculated
																			</h5>
																			<div class="row col">
																				<json-builder v-bind:template="output.calculated" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
																			</div>
																		</div>
																		<div class="card card-borderless mb-2" v-bind:class="[output.equal ? 'bg-success' : 'bg-danger']">
																			<h5 class="row col">
																				Equal
																			</h5>
																			<json-builder v-bind:template="output.equal" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="card card-borderless mb-2" v-bind:class="[test.result.equal ? 'bg-success' : 'bg-danger']">
													<h5 class="row col">
														Results
													</h5>
													<div class="row col">
														<json-builder v-bind:template="test.result.equal" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
													</div>
												</div>
											</div>
										</div>
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

<style>

</style>

<script>
	import Network from "../helpers/network";
	import DiffMatchPatch from 'diff-match-patch';
	import JSONBuilder from "../components/json/json-builder.vue";
	
	export default {
		async mounted() {
			await this.getInputs();
			await this.getOutputs();
			await this.getTests();
			await this.executeTests();
		},
		components: {
			"json-builder": JSONBuilder
		},
		data() {
			return {
				model: {},

				inputs: {},
				outputs: {},

				tests: [],

				test: {
					uuid: null,
					name: null,
					description: null,
					input: null,
					inputVis: false,
					outputs: [],
					outputVis: false,
					result: {
						outputs: [],
						equal: false
					},
					visibility: false
				}
			}
		},
		methods: {
			//
			// Inputs
			//
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},

			//
			// Outputs
			//
			async getOutputs() {
				this.outputs = await Network.getOutputs();
			},

			//
			// Tests
			//
			async getTests() {
				this.tests = await Network.getTests();
				for (const uuid in this.tests) {
					this.$set(this.tests[uuid],"visibility",false);
					this.$set(this.tests[uuid],"inputVis",false);
					this.$set(this.tests[uuid],"outputVis",false);
				}
			},
			async executeTests() {
				for (const uuid in this.tests) {
					const tempRes = await Network.executeTest(uuid)
					this.$set(this.tests[uuid], "result", tempRes);

					const tempEq = this.tests[uuid].result.equal;
					this.$delete(this.tests[uuid].result, "equal");

					this.$set(this.tests[uuid].result, "equal", tempEq);
				}
			},
			async changeVisibility(test) {
				test.visibility = !test.visibility;
			},
			async changeInputVisibility(test) {
				test.inputVis = !test.inputVis;
			},
			async changeOutputVisibility(test) {
				test.outputVis = !test.outputVis;
			},
			getDifference(expected, calculated) {
				return new DiffMatchPatch().diff_main(expected !== null ? JSON.stringify(expected.value, null, 3) : '', calculated !== null ? JSON.stringify(calculated.value, null, 3) : '');
			}
		}
	};
</script>