<template>
	<div>
		<div class="row mb-2">
			<div class="col-11">
				<h3 class="mb-0">Tests</h3>
			</div>
			<div class="col-1">
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
					<div class="col-6">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="executeTests">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M12 20c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 0 0 2 12a10 10 0 0 0 10 10 10 10 0 0 0 10-10A10 10 0 0 0 12 2m-2 14.5l6-4.5-6-4.5v9z" fill="currentColor"/>
							</svg>
						</button>
					</div>
					<div class="col-6">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="order = !order; getTests()">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M9.25 5l3.25-3.25L15.75 5h-6.5m-.36 9.3H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-if="order"/>
								<path d="M15.75 19l-3.25 3.25L9.25 19h6.5m-6.86-4.7H6L5.28 17H2.91L6 7h3l3.13 10H9.67l-.78-2.7m-2.56-1.62h2.23l-.63-2.12-.26-.97-.25-.96h-.03l-.22.97-.24.98-.6 2.1M13.05 17v-1.26l4.75-6.77v-.06h-4.3V7h7.23v1.34L16.09 15v.08h4.71V17h-7.75z" fill="currentColor" v-else/>
							</svg>
						</button>
					</div>
				</div>
				<div class="list-group">
					<template v-if="Object.keys(tests).length !== 0">
						<div class="list-group-item list-group-item-action c-pointer" style="display: flex; align-items: center" v-for="(test, uuid) in tests" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)" v-bind:class="[test.equal === undefined ? '' : (test.equal ? 'bg-success text-white' : 'bg-danger text-white')]">
							<span class="d-block" style="margin-right: auto" v-on:click="setViewMode(uuid)">{{test.name}}</span>
							<div>
								<span class="badge badge-light badge-md d-block float-left mr-2" v-if="test.tps !== undefined">{{test.tps}} / s</span>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
									<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"/>
								</svg>
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deleteTest(uuid)">
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
						<h4 class="mb-0">{{test.name}}</h4>
					</div>
					<div class="card-body">
						<div class="row mb-4" v-if="test.description">
							<div class="col-12">
								<h4 class="mb-2">Description</h4>
								<p class="mb-0">{{test.description}}</p>
							</div>
						</div>
						<div class="row mb-4">
							<div class="col-12">
								<h4 class="mb-2">Inputs</h4>
								<div class="card">
									<div class="card-body">
										<h5 class="mb-2">Name</h5>
										<p class="mb-4">{{inputs[test.input].name}}</p>
										<h5 class="mb-2">Input</h5>
										<json-builder v-bind:template="inputs[test.input].value" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<h4 class="mb-0">Output</h4>
								<div class="card mt-2 border-lg" v-for="output in test.result.outputs" v-bind:key="test.uuid + '-' + output.decision" v-bind:class="[output.equal ? 'border-success' : 'border-danger']">
									<div class="card-body">
										<h5 class="mb-2">Name</h5>
										<p class="mb-4">{{output.name}}</p>
										<h5 class="mb-2">Decision</h5>
										<p class="mb-4">{{output.decision}}</p>
										<h5 class="mb-2">Output</h5>
										<div style="white-space: pre">
											<span v-for="difference of getDifference(output.expected, output.calculated)">
												<code class="difference-minus" v-if="difference[0] === -1">{{difference[1]}}</code>
												<code class="difference-plus" v-else-if="difference[0] === 1">{{difference[1]}}</code>
												<code v-else>{{difference[1]}}</code>
											</span>
										</div>
										<div v-if="!output.equal">
											<button class="btn btn-block btn-outline-secondary mt-4" v-on:click="amendOutput(output)">Save as Correct</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="card" v-if="mode === 'ADD' || mode === 'EDIT'">
					<div class="card-body">
						<h5 class="mb-2">Name</h5>
						<input class="form-control mb-4" v-model="test.name">

						<h5 class="mb-2">Description</h5>
						<textarea rows="3" class="form-control mb-4" v-model="test.description"/>

						<h5 class="mb-2">Input</h5>
						<select id="form-inputs" class="form-control mb-4" v-model="test.input">
							<option v-for="(input, uuid) in inputs" v-bind:value="uuid">{{input.name}}</option>
						</select>

						<h5 class="mb-2">Output</h5>
						<select id="form-output" size="15" multiple class="form-control mb-4" v-model="test.outputs">
							<option v-for="(output, uuid) in outputs" v-bind:value="uuid">{{output.name}}</option>
						</select>

						<hr>

						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'ADD'" v-on:click="addTest">Save Test</button>
						<button class="btn btn-block btn-outline-secondary" v-if="mode === 'EDIT'" v-on:click="editTest(test.uuid)">Save Test</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style>
	.badge-md {
		padding: 0.5em .4em;
	}
</style>

<script>
	import Network from "../../helpers/network";
	import DiffMatchPatch from 'diff-match-patch';
	import JSONBuilder from "../../components/json/json-builder.vue";
	import EmptyCollectionComponent from "../../components/empty-collection.vue";

	export default {
		head() {
			return {
				title: "declab - Tests",
			}
		},
		components: {
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent
		},
		async mounted() {
			await this.getInputs();
			await this.getOutputs();
			await this.getTests();
		},
		data() {
			return {
				mode: "SELECT",

				model: {},
				inputs: {},
				outputs: {},

				order: false,
				tests: {},
				test: {
					uuid: null,
					name: null,
					description: null,
					input: null,
					outputs: [],
					result: {
						outputs: [],
						equal: false
					}
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
			async amendOutput(output) {
				await Network.editOutput(output.uuid, {
					name: output.name,
					decision: output.decision,
					value: output.calculated
				});
				await this.setViewMode(this.test.uuid);
			},

			//
			// Tests
			//
			async getTests() {
				this.tests = await Network.getTests(this.order);
				this.mode = "SELECT";
			},
			async addTest() {
				await Network.addTest({
					name: this.test.name,
					description: this.test.description,
					input: this.test.input,
					outputs: this.test.outputs
				});

				this.$store.commit("displayAlert", {
					message: "The test was successfully created.",
					state: "success"
				});

				this.getTests();
			},
			async editTest(uuid) {
				await Network.editTest(uuid, {
					name: this.test.name,
					description: this.test.description,
					input: this.test.input,
					outputs: this.test.outputs
				});

				this.$store.commit("displayAlert", {
					message: "The test was successfully edited.",
					state: "success"
				});

				this.getTests();
			},
			async deleteTest(uuid) {
				await Network.deleteTest(uuid);

				this.$store.commit("displayAlert", {
					message: "The test was successfully deleted.",
					state: "success"
				});

				this.getTests();
			},
			async executeTests() {
				for (const key in this.tests) {
					this.$delete(this.tests[key], "equal");
					this.$delete(this.tests[key], "tps");

					const result = await Network.executeTest(key);
					this.$set(this.tests[key], "equal", result.equal);
					this.$set(this.tests[key], "tps", result.tps);
				}
			},
			async filterTests() {
				for (const key in this.tests) {
					const test = this.tests[key];
					console.info(test);
				}
			},

			//
			// View Modes
			//
			async setViewMode(uuid) {
				const result = await Network.executeTest(uuid);

				const test = this.tests[uuid];
				this.test = {
					uuid: uuid,
					name: test.name,
					description: test.description,
					input: test.input,
					outputs: test.outputs,
					result: result
				};
				this.$set(this.tests[uuid], "equal", result.equal);
				this.$set(this.tests[uuid], "tps", result.tps);

				this.mode = "VIEW";
			},
			setAddMode() {
				this.test = {
					uuid: null,
					name: null,
					description: null,
					input: null,
					outputs: []
				};

				this.mode = "ADD";
			},
			setEditMode(uuid) {
				const test = this.tests[uuid];
				this.test = {
					uuid: uuid,
					name: test.name,
					description: test.description,
					input: test.input,
					outputs: test.outputs
				};

				this.mode = "EDIT";
			},
			setDuplicateMode(uuid) {
				const test = this.tests[uuid];
				this.test = {
					uuid: uuid,
					name: test.name,
					description: test.description,
					input: test.input,
					outputs: test.outputs
				};

				this.mode = "ADD";
			},


			//
			// Helper
			//
			getDifference(expected, calculated) {
				return new DiffMatchPatch().diff_main(expected !== null ? JSON.stringify(expected, null, 3) : '', calculated !== null ? JSON.stringify(calculated, null, 3) : '');
			}
		}
	};
</script>