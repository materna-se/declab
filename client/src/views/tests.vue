<template>
	<div class="container-fluid">
		<div class="row mb-2">
			<div class="col-10">
				<h3 class="mb-0">Tests</h3>
			</div>
			<div class="col-2">
				<button class="btn btn-block btn-outline-secondary" v-on:click="setAddMode">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"></path>
					</svg>
				</button>
			</div>
		</div>
		<div class="row">
			<div class="col-4 mb-4">
				<div class="row mb-2">
					<div class="col-6">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="executeTests">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M12 20c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 0 0 2 12a10 10 0 0 0 10 10 10 10 0 0 0 10-10A10 10 0 0 0 12 2m-2 14.5l6-4.5-6-4.5v9z" fill="currentColor"></path>
							</svg>
						</button>
					</div>
					<div class="col-6">
						<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="filterTests">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
								<path d="M15 19.88c.04.3-.06.62-.29.83a.996.996 0 0 1-1.41 0L9.29 16.7a.989.989 0 0 1-.29-.83v-5.12L4.21 4.62a1 1 0 0 1 .17-1.4c.19-.14.4-.22.62-.22h14c.22 0 .43.08.62.22a1 1 0 0 1 .17 1.4L15 10.75v9.13M7.04 5L11 10.06v5.52l2 2v-7.53L16.96 5H7.04z" fill="currentColor"></path>
							</svg>
						</button>
					</div>
				</div>
				<div class="list-group">
					<div class="list-group-item list-group-item-action c-pointer" v-for="(test, uuid) in tests" v-bind:key="uuid" v-on:click.self="setViewMode(uuid)" v-bind:class="[test.equal === undefined ? '' : (test.equal ? 'bg-success text-white' : 'bg-danger text-white')]">
						<span class="d-block float-left mr-4" v-on:click="setViewMode(uuid)">{{test.name}}</span>
						<div class="float-right">
							<span class="badge badge-light badge-md d-block float-left mr-2" v-if="test.tps !== undefined">{{test.tps}} / s</span>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setEditMode(uuid)">
								<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"></path>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left mr-2" v-on:click="setDuplicateMode(uuid)">
								<path d="M11 17H4a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h12v2H4v12h7v-2l4 3-4 3v-2m8 4V7H8v6H6V7a2 2 0 0 1 2-2h11a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2v-2h2v2h11z" fill="currentColor"></path>
							</svg>
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left" v-on:click="deleteTest(uuid)">
								<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"></path>
							</svg>
						</div>
					</div>
				</div>
			</div>
			<div class="col-8 mb-4">
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
										<p class="mb-4"><code>{{inputs[test.input].name}}</code></p>
										<h5 class="mb-2">Input</h5>
										<pre class="mb-0"><code>{{JSON.stringify(inputs[test.input].value, null, 3)}}</code></pre>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<h4 class="mb-0">Outputs</h4>
								<div class="card mt-2 border-lg" v-for="(output, decision) in test.result.outputs" v-bind:key="test.uuid + '-' + decision" v-bind:class="[output.equal ? 'border-success' : 'border-danger']">
									<div class="card-body">
										<h5 class="mb-2">Name</h5>
										<p class="mb-4"><code>{{output.expected.name}}</code></p>
										<h5 class="mb-2">Decision</h5>
										<p class="mb-4"><code>{{decision}}</code></p>
										<h5 class="mb-2">Output</h5>
										<div style="white-space: pre">
											<span v-for="difference of getDifference(output.expected, output.calculated)">
												<code class="difference-minus" v-if="difference[0] === -1">{{difference[1]}}</code>
												<code class="difference-plus" v-else-if="difference[0] === 1">{{difference[1]}}</code>
												<code v-else>{{difference[1]}}</code>
											</span>
										</div>
										<div v-if="!output.equal">
											<button class="btn btn-block btn-outline-secondary mt-4" v-on:click="amendOutput(output.expected, output.calculated)">Save as Correct</button>
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
						<textarea rows="3" class="form-control mb-4" v-model="test.description"></textarea>

						<h5 class="mb-2">Input</h5>
						<select id="form-inputs" class="form-control mb-4" v-model="test.input">
							<option v-for="(input, uuid) in inputs" v-bind:value="uuid">{{input.name}}</option>
						</select>

						<h5 class="mb-2">Outputs</h5>
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

	.difference-minus {
		background: yellow;
		text-decoration: line-through;
	}

	.difference-plus {
		background: yellow;
		text-decoration: underline;
	}
</style>

<script>
	import Network from "../helpers/network";
	import DiffMatchPatch from 'diff-match-patch';

	export default {
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
			async amendOutput(expected, calculated) {
				await Network.editOutput(expected.uuid, {
					name: expected.name,
					decision: expected.decision,
					value: calculated.value
				});
				await this.setViewMode(this.test.uuid);
			},

			//
			// Tests
			//
			async getTests() {
				this.tests = await Network.getTests();
				this.mode = "SELECT";
			},
			async addTest() {
				await Network.addTest({
					name: this.test.name,
					description: this.test.description,
					input: this.test.input,
					outputs: this.test.outputs
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
				this.getTests();
			},
			async deleteTest(uuid) {
				await Network.deleteTest(uuid);
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
				return new DiffMatchPatch().diff_main(expected !== null ? JSON.stringify(expected.value, null, 3) : '', calculated !== null ? JSON.stringify(calculated.value, null, 3) : '');
			}
		}
	};
</script>